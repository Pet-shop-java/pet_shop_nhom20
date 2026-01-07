package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.Product_Image.CreateProductImageRequest;
import com.webpet_nhom20.backdend.dto.request.Product_Image.UpdateProductImageRequest;
import com.webpet_nhom20.backdend.dto.response.Cloudinary.CloudinaryResponse;
import com.webpet_nhom20.backdend.dto.response.ProductImage.ProductImageResponse;
import com.webpet_nhom20.backdend.entity.ProductImages;
import com.webpet_nhom20.backdend.entity.ProductVariantImage;
import com.webpet_nhom20.backdend.entity.Products;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.mapper.ProductImageMapper;
import com.webpet_nhom20.backdend.repository.ProductImageRepository;
import com.webpet_nhom20.backdend.repository.ProductRepository;
import com.webpet_nhom20.backdend.repository.ProductVariantImageRepository;
import com.webpet_nhom20.backdend.service.CloudinaryService;
import com.webpet_nhom20.backdend.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductVariantImageRepository productVariantImageRepository;
    @Autowired
    private ProductImageMapper mapper;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ProductRepository productRepository;

    @PreAuthorize("hasRole('SHOP')")
    @Override
    public List<ProductImageResponse> createProductImage(List<CreateProductImageRequest> requests) {
        int position = 0;
        List<ProductImageResponse> responses = new ArrayList<>();

        for (CreateProductImageRequest req : requests) {

            Products products = productRepository.findById(req.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            ProductImages image = new ProductImages();
            image.setProduct(products);
            image.setImageUrl(req.getImageUrl());
            image.setPosition(position++);

            ProductImages saved = productImageRepository.save(image);
            responses.add(mapper.toProductImageResponse(saved));
        }

        return responses;
    }

    @PreAuthorize("hasRole('SHOP')")
    @Override
    public List<ProductImageResponse> uploadProductImages(int productId, MultipartFile[] files, int[] positions) {

        Products product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        List<ProductImageResponse> responses = new ArrayList<>();
        
        if (files == null || files.length == 0) {
            throw new AppException(ErrorCode.FAIL_TO_UPLOAD_FILE);
        }

        // Validate số lượng positions phải khớp với số lượng files (nếu có gửi positions)
        if (positions != null && positions.length != files.length) {
            throw new AppException(ErrorCode.FAIL_TO_UPLOAD_FILE);
        }

        // Lấy position cao nhất hiện tại của product này (chỉ 1 lần)
        List<ProductImages> existingImages = productImageRepository.findByProductId(productId);
        int maxExistingPosition = existingImages.stream()
                .mapToInt(ProductImages::getPosition)
                .max()
                .orElse(-1); // Nếu chưa có ảnh nào thì -1
        
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            
            // Validate file
            if (file.isEmpty()) {
                continue; // Bỏ qua file rỗng
            }

            // Validate file size (2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                throw new AppException(ErrorCode.MAX_FILE_SIZE);
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new AppException(ErrorCode.FORMAT_FILE_VALID);
            }
            
            // Validate các định dạng ảnh được phép
            String[] allowedTypes = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"};
            boolean isValidType = false;
            for (String type : allowedTypes) {
                if (contentType.equals(type)) {
                    isValidType = true;
                    break;
                }
            }
            if (!isValidType) {
                throw new AppException(ErrorCode.FORMAT_FILE_VALID);
            }

            // Tính position: Nếu có positions từ frontend thì dùng, không thì tự động tăng
            int finalPosition;
            if (positions != null && positions.length > i) {
                // Sử dụng position từ frontend (không cộng thêm maxExistingPosition)
                finalPosition = positions[i];
            } else {
                // Nếu không gửi positions, tự động tăng dần từ maxExistingPosition + 1
                finalPosition = maxExistingPosition + i + 1;
            }

            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = "/" + productId + "_" + System.currentTimeMillis() + "_" + i + extension;
            
            // Upload lên Cloudinary
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(file, fileName);
            
            // Lưu vào database
            ProductImages image = new ProductImages();
            image.setProduct(product);
            image.setImageUrl(cloudinaryResponse.getUrl());
            image.setPosition(finalPosition);
            image.setIsDeleted("0");
            image.setIsPrimary(0); // Tất cả isPrimary đều là 0

            ProductImages saved = productImageRepository.save(image);
            responses.add(mapper.toProductImageResponse(saved));
        }

        return responses;
    }

    @PreAuthorize("hasRole('SHOP')")
    @Override
    public ProductImageResponse updateProductImage(int ImageId, UpdateProductImageRequest request) {
        ProductImages product_images = productImageRepository.findById(ImageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));
        
        // Kiểm tra nếu request muốn set isPrimary = 1
        if (request.getIsPrimary() != null && request.getIsPrimary().equals("1")) {
            // Tìm xem đã có ảnh primary khác cho product này chưa (không tính ảnh hiện tại)
            Optional<ProductImages> existingPrimaryImage = productImageRepository.findByProductIdAndIsPrimary(
                    product_images.getProduct().getId(), 1);
            
            // Nếu có ảnh primary và không phải là ảnh hiện tại đang update
            if (existingPrimaryImage.isPresent() && existingPrimaryImage.get().getId() != ImageId) {
                throw new AppException(ErrorCode.PRIMARY_IMAGE_ALREADY_EXISTS);
            }
        }
        
        // Kiểm tra logic xung đột giữa isDeleted và isPrimary
        if (request.getIsDeleted() != null && request.getIsPrimary() != null) {
            if (request.getIsDeleted().equals("1") && request.getIsPrimary().equals("1")) {
                throw new AppException(ErrorCode.IMAGE_IS_NOT_PRIMARY_AND_DELETE);
            }
        }
        
        // Kiểm tra nếu ảnh đã deleted mà muốn set primary
        if (product_images.getIsDeleted().equals("1") && request.getIsPrimary() != null && request.getIsPrimary().equals("1")) {
            throw new AppException(ErrorCode.IMAGE_IS_DELETE);
        }
        
        // Kiểm tra nếu ảnh đang là primary mà muốn delete
        if (product_images.getIsPrimary() == 1 && request.getIsDeleted() != null && request.getIsDeleted().equals("1")) {
            throw new AppException(ErrorCode.IMAGE_IS_PRIMARY);
        }
        
        // Update các trường
        mapper.updateProductImage(product_images, request);
        
        return mapper.toProductImageResponse(productImageRepository.save(product_images));
    }

    @Override
    public String deleteProductImage(int imageId) {
        ProductImages product_images = productImageRepository.findById(imageId).orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));
        Optional<ProductVariantImage> productVariantImage = productVariantImageRepository.findByImageId(imageId);
        if(productVariantImage.isPresent()){
            productVariantImageRepository.delete(productVariantImage.get());
        }
        product_images.setIsDeleted("1");
        productImageRepository.save(product_images);
        return "Xóa thành công";
    }
}
