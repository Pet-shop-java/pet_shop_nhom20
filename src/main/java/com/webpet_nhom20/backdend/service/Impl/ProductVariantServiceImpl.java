package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.Product_Variant.CreateProductVariantRequest;
import com.webpet_nhom20.backdend.dto.request.Product_Variant.UpdateProductVariantRequest;
import com.webpet_nhom20.backdend.dto.response.ProductVariant.ProductVariantResponse;
import com.webpet_nhom20.backdend.entity.ProductImages;
import com.webpet_nhom20.backdend.entity.ProductVariantImage;
import com.webpet_nhom20.backdend.entity.ProductVariants;
import com.webpet_nhom20.backdend.entity.Products;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.mapper.ProductVariantMapper;
import com.webpet_nhom20.backdend.repository.ProductImageRepository;
import com.webpet_nhom20.backdend.repository.ProductRepository;
import com.webpet_nhom20.backdend.repository.ProductVariantImageRepository;
import com.webpet_nhom20.backdend.repository.ProductVariantRepository;
import com.webpet_nhom20.backdend.service.ProductVariantService;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {
    @Autowired
    private ProductVariantRepository repository;
    @Autowired
    private ProductVariantMapper mapper;
    @Autowired
    private ProductVariantImageRepository productVariantImageRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductRepository productRepository;

    @PreAuthorize("hasRole('SHOP')")
    @Override
    public ProductVariantResponse createProductVariant(CreateProductVariantRequest request) {

        if(repository.existsByProductIdAndVariantName(request.getProductId(), request.getVariantName())){
            throw new AppException(ErrorCode.VARIANT_NAME_IS_EXISTED);
        }
        Products product = new  Products();
        product.setId(request.getProductId());

        ProductVariants variants = new ProductVariants();
        variants.setVariantName(request.getVariantName());
        variants.setProduct(product);
        variants.setStockQuantity(request.getStockQuantity());
        variants.setPrice(request.getPrice());
        variants.setWeight(request.getWeight());
        variants.setSoldQuantity(0);

        ProductVariants savedVariant = repository.save(variants);

        ProductImages productImages = productImageRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));

        // 5. Tạo và lưu bảng trung gian (ProductVariantImage)
        ProductVariantImage productVariantImage = new ProductVariantImage();
        productVariantImage.setVariant(savedVariant); // Gán đối tượng đã được lưu (có ID)
        productVariantImage.setImage(productImages);

        productVariantImageRepository.save(productVariantImage);

        // 6. Trả về kết quả
        return mapper.toProductVariantResponse(savedVariant);

    }
    @PreAuthorize("hasRole('SHOP')")
    @Override
    public ProductVariantResponse updateProductVariant(
            int variantId,
            UpdateProductVariantRequest request
    ) {
        // 1. Lấy variant
        ProductVariants variant = repository.findById(variantId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        // 2. Update thông tin cơ bản
        mapper.updateProductVariant(variant, request);

        // 3. Xử lý ảnh (giữ nguyên)
        if (request.getImageIds() != null) {
            List<ProductVariantImage> currentLinks = productVariantImageRepository.findByVariantId(variantId);
            List<Integer> currentImageIds = currentLinks.stream().map(pvi -> pvi.getImage().getId()).toList();
            List<Integer> newImageIds = request.getImageIds();

            // XÓA ảnh
            List<ProductVariantImage> toDelete = currentLinks.stream()
                    .filter(pvi -> !newImageIds.contains(pvi.getImage().getId()))
                    .toList();
            if (!toDelete.isEmpty()) {
                productVariantImageRepository.deleteAll(toDelete);
            }

            // THÊM ảnh
            List<Integer> imageIdsToAdd = newImageIds.stream()
                    .filter(id -> !currentImageIds.contains(id))
                    .toList();
            if (!imageIdsToAdd.isEmpty()) {
                List<ProductImages> imagesToAdd = productImageRepository.findAllById(imageIdsToAdd);
                List<ProductVariantImage> newLinks = imagesToAdd.stream()
                        .map(img -> {
                            ProductVariantImage pvi = new ProductVariantImage();
                            pvi.setVariant(variant);
                            pvi.setImage(img);
                            return pvi;
                        })
                        .toList();
                productVariantImageRepository.saveAll(newLinks);
            }
        }

        // 4. Lưu variant
        ProductVariants saved = repository.save(variant);

        // 5. LOGIC MỚI: Dùng Query DB để tránh lỗi ConcurrentModificationException
        Products product = saved.getProduct();
        if (product != null) {
            // Đếm xem còn variant nào KHÁC đang active không
            long otherActiveCount = repository.countOtherActiveVariants(product.getId(), variantId);

            // Kiểm tra trạng thái của variant HIỆN TẠI
            boolean currentIsActive = "0".equals(saved.getIsDeleted());

            // Nếu không còn variant nào khác active VÀ variant hiện tại cũng bị xóa
            if (otherActiveCount == 0 && !currentIsActive) {
                product.setIsDeleted("1");
            } else {
                // Ngược lại, nếu còn ít nhất 1 cái active (hoặc cái hiện tại active) -> Product active
                product.setIsDeleted("0");
            }
            productRepository.save(product);
        }

        return mapper.toProductVariantResponse(saved);
    }

    @Transactional
    @Override
    public String deleteProductVariant(int variantId) {
        ProductVariants product_variants = repository.findById(variantId).orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        if(product_variants.getIsDeleted().equals("1")){
            return "Sản phẩm đã bị xóa trước đó";
        }
        product_variants.setIsDeleted("1");
        repository.save(product_variants);
        Products product = product_variants.getProduct();
        if (product != null) {
            boolean allDeleted = product.getProduct_variants().stream()
                    .allMatch(v -> "1".equals(v.getIsDeleted()));
            if (allDeleted) {
                product.setIsDeleted("1");
                productRepository.save(product);
            }
        }
        return "Xóa thành công";
    }
}
