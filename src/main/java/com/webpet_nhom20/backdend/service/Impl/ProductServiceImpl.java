package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.Product.FullProductCreateRequest;
import com.webpet_nhom20.backdend.dto.request.Product.CreateProductRequest;
import com.webpet_nhom20.backdend.dto.request.Product.UpdateProductRequest;
import com.webpet_nhom20.backdend.dto.request.Product_Variant.VariantCreateDto;
import com.webpet_nhom20.backdend.dto.response.Product.BrandResponse;
import com.webpet_nhom20.backdend.dto.response.Product.FullProductCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Product.ProductResponse;
import com.webpet_nhom20.backdend.dto.response.ProductImage.ProductImageResponse;
import com.webpet_nhom20.backdend.dto.response.ProductVariant.ProductVariantResponse;
import com.webpet_nhom20.backdend.entity.*;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.mapper.ProductMapper;
import com.webpet_nhom20.backdend.repository.*;
import com.webpet_nhom20.backdend.service.ProductImageService;
import com.webpet_nhom20.backdend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantImageRepository productVariantImageRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductImageService productImageService;

    /**
     *
     * Lấy dữ liệu cho User dùng CACHE
     */
    @Override
    @Cacheable(value = "product_list",
            key = "'p:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #categoryId"
    )
    public Page<ProductResponse> getAllProduct(Pageable pageable, Integer categoryId, String search, Double minPrice, Double maxPrice, String animal, String brand, String isFeature, String isDelete) {
            Page<Products> productPage;

            // 1. Chuẩn hóa tham số
            boolean hasCategory = categoryId != null && categoryId > 0;
            boolean hasSearch = search != null && !search.trim().isEmpty();

            // animal và brand có thể null, query repository đã xử lý logic này (IS NULL OR ...)

            // Kiểm tra có lọc giá hay không (chỉ cần nhập min HOẶC max là tính có lọc)
            boolean hasPrice = minPrice != null || maxPrice != null;

            // Tự động điền giá trị thiếu:
            // - Nếu thiếu min -> coi như min = 0
            // - Nếu thiếu max -> coi như max = số cực lớn
            Double finalMin = (minPrice != null) ? minPrice : 0.0;
            Double finalMax = (maxPrice != null) ? maxPrice : Double.MAX_VALUE;

            // 2. Kiểm tra Sort
            boolean isSortByPrice = pageable.getSort().stream()
                    .anyMatch(order -> order.getProperty().equals("price"));

            if (isSortByPrice) {
                // === LOGIC SORT GIÁ (Dùng Native Query Custom) ===
                boolean isAsc = pageable.getSort().getOrderFor("price").isAscending();

                // Tạo Pageable mới KHÔNG CÓ SORT để tránh conflict với ORDER BY trong SQL
                Pageable unsortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

                if (isAsc) {
                    productPage = productRepository.findAllWithFiltersSortedByPriceAsc(
                            hasCategory ? categoryId : null,
                            animal, // Thêm tham số animal
                            brand,
                            isDelete,
                            isFeature,// Thêm tham số brand
                            hasSearch ? search.trim() : null,
                            finalMin, finalMax, unsortedPageable);
                } else {
                    productPage = productRepository.findAllWithFiltersSortedByPriceDesc(
                            hasCategory ? categoryId : null,
                            animal, // Thêm tham số animal
                            brand,
                            isDelete,
                            isFeature,// Thêm tham số brand
                            hasSearch ? search.trim() : null,
                            finalMin, finalMax, unsortedPageable);
                }
            } else {
                // === LOGIC THƯỜNG (Mặc định, Mới nhất, Bán chạy) ===

                // FIX LỖI: Map tên biến Java sang tên cột Database thủ công
                List<Sort.Order> dbOrders = pageable.getSort().stream()
                        .map(order -> {
                            String property = order.getProperty();
                            // Map createdDate -> created_time (hoặc created_date tùy DB của bạn)
                            if ("createdDate".equals(property)) return new Sort.Order(order.getDirection(), "created_date");
                            // Map soldQuantity -> sold_quantity
                            if ("soldQuantity".equals(property)) return new Sort.Order(order.getDirection(), "sold_quantity");
                            // Các trường khác giữ nguyên
                            return order;
                        })
                        .collect(Collectors.toList());

                // Tạo Pageable mới với tên cột DB chuẩn
                Pageable dbPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(dbOrders));

                productPage = productRepository.findAllWithFilters(
                        hasCategory ? categoryId : null,
                        animal, // Thêm tham số animal
                        brand,
                        isDelete,
                        isFeature,// Thêm tham số brand
                        hasSearch ? search.trim() : null,
                        finalMin, finalMax, dbPageable);
            }

            // 3. Map Response
            List<ProductResponse> productResponses = productPage.getContent().stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());

            return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
        }
    @Override
    @Cacheable(value = "product_detail", key = "#productId")
    public ProductResponse getProductById(int productId) {
        Products product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return mapToProductResponse(product);
    }

    @Override
    public List<BrandResponse> getBrand(){
        return productRepository.getBrand();
    }


    /**
     * Ch ADMIN dùng không cache
     */

    @PreAuthorize("hasRole('SHOP')")
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_IS_EXISTED);
        }
        if (categoryRepository.findById(request.getCategoryId()).isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        Products products = productMapper.toProduct(request);
        return productMapper.toProductResponse(productRepository.save(products));
    }

    @Override
    public Page<ProductResponse> getAllProductForAdmin(Pageable pageable, Integer categoryId, String search, Double minPrice, Double maxPrice, String animal, String brand, String isFeature, String isDelete) {
        Page<Products> productPage;

        // 1. Chuẩn hóa tham số
        boolean hasCategory = categoryId != null && categoryId > 0;
        boolean hasSearch = search != null && !search.trim().isEmpty();

        // animal và brand có thể null, query repository đã xử lý logic này (IS NULL OR ...)

        // Kiểm tra có lọc giá hay không (chỉ cần nhập min HOẶC max là tính có lọc)
        boolean hasPrice = minPrice != null || maxPrice != null;

        // Tự động điền giá trị thiếu:
        // - Nếu thiếu min -> coi như min = 0
        // - Nếu thiếu max -> coi như max = số cực lớn
        Double finalMin = (minPrice != null) ? minPrice : 0.0;
        Double finalMax = (maxPrice != null) ? maxPrice : Double.MAX_VALUE;

        // 2. Kiểm tra Sort
        boolean isSortByPrice = pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equals("price"));

        if (isSortByPrice) {
            // === LOGIC SORT GIÁ (Dùng Native Query Custom) ===
            boolean isAsc = pageable.getSort().getOrderFor("price").isAscending();

            // Tạo Pageable mới KHÔNG CÓ SORT để tránh conflict với ORDER BY trong SQL
            Pageable unsortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

            if (isAsc) {
                productPage = productRepository.findAllWithFiltersSortedByPriceAsc(
                        hasCategory ? categoryId : null,
                        animal, // Thêm tham số animal
                        brand,
                        isDelete,
                        isFeature,// Thêm tham số brand
                        hasSearch ? search.trim() : null,
                        finalMin, finalMax, unsortedPageable);
            } else {
                productPage = productRepository.findAllWithFiltersSortedByPriceDesc(
                        hasCategory ? categoryId : null,
                        animal, // Thêm tham số animal
                        brand,
                        isDelete,
                        isFeature,// Thêm tham số brand
                        hasSearch ? search.trim() : null,
                        finalMin, finalMax, unsortedPageable);
            }
        } else {
            // === LOGIC THƯỜNG (Mặc định, Mới nhất, Bán chạy) ===

            // FIX LỖI: Map tên biến Java sang tên cột Database thủ công
            List<Sort.Order> dbOrders = pageable.getSort().stream()
                    .map(order -> {
                        String property = order.getProperty();
                        // Map createdDate -> created_time (hoặc created_date tùy DB của bạn)
                        if ("createdDate".equals(property)) return new Sort.Order(order.getDirection(), "created_date");
                        // Map soldQuantity -> sold_quantity
                        if ("soldQuantity".equals(property)) return new Sort.Order(order.getDirection(), "sold_quantity");
                        // Các trường khác giữ nguyên
                        return order;
                    })
                    .collect(Collectors.toList());

            // Tạo Pageable mới với tên cột DB chuẩn
            Pageable dbPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(dbOrders));

            productPage = productRepository.findAllWithFilters(
                    hasCategory ? categoryId : null,
                    animal, // Thêm tham số animal
                    brand,
                    isDelete,
                    isFeature,// Thêm tham số brand
                    hasSearch ? search.trim() : null,
                    finalMin, finalMax, dbPageable);
        }

        // 3. Map Response
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }


    @Override
    public ProductResponse getProductByIdForAdmin(int productId) {
        Products product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return mapToProductResponse(product);
    }
    @Override
    @Transactional // Đảm bảo tất cả các thao tác DB trong hàm này là một khối (atomic)
    @PreAuthorize("hasRole('SHOP')")
    public FullProductCreateResponse createFullProduct(FullProductCreateRequest request) {
        // 1. Tạo và lưu đối tượng Product chính
        Categories categories = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Products product = Products.builder()
                .name(request.getName())
                .category(categories)
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .animal(request.getAnimal())
                .brand(request.getBrand())
                .isDeleted("0")
                .isFeatured(request.isFeatured() ? "1" : "0")
                .build();
        Products savedProduct = productRepository.save(product);

        // 2. Lưu danh sách ProductImages và tạo một Map để tra cứu nhanh
        // Key là imageUrl, Value là đối tượng ProductImages đã được lưu
        Map<String, ProductImages> savedImagesMap = request.getImages().stream()
                .map(imageDto -> {
                    ProductImages image = ProductImages.builder()
                            .product(savedProduct)
                            .imageUrl(imageDto.getImageUrl())
                            .publicId(imageDto.getPublicId())
                            .isPrimary(imageDto.isPrimary() ? 1 : 0)
                            .position(imageDto.getPosition())
                            .build();
                    return productImageRepository.save(image);
                })
                .collect(Collectors.toMap(ProductImages::getImageUrl, img -> img));

        // 3. Lưu danh sách ProductVariants và tạo liên kết ảnh
        for (VariantCreateDto variantDto : request.getVariants()) {
            // 3.1. Tạo và lưu biến thể
            ProductVariants variant = ProductVariants.builder()
                    .product(savedProduct)
                    .variantName(variantDto.getVariantName())
                    .weight(variantDto.getWeight())
                    .stockQuantity(variantDto.getStockQuantity())
                    .soldQuantity(0)
                    .price(variantDto.getPrice())
                    .stockQuantity(variantDto.getStockQuantity())
                    .isDeleted("0")
                    .build();
            ProductVariants savedVariant = productVariantRepository.save(variant);

            // 3.2. Liên kết biến thể với ảnh tương ứng
            if (variantDto.getAssociatedImageUrls() != null && !variantDto.getAssociatedImageUrls().isEmpty()) {
                for (String imageUrl : variantDto.getAssociatedImageUrls()) {
                    ProductImages correspondingImage = savedImagesMap.get(imageUrl);

                    if (correspondingImage == null) {
                        // Lỗi nghiêm trọng: Dữ liệu từ frontend không nhất quán.
                        // URL ảnh trong biến thể không tồn tại trong danh sách ảnh chính.
                        throw new AppException(ErrorCode.IMAGE_NOT_FOUND);
                    }

                    // Tạo bản ghi trong bảng trung gian ProductVariantImage
                    ProductVariantImage variantImageLink = ProductVariantImage.builder()
                            .variant(savedVariant)
                            .image(correspondingImage)
                            .build();
                    productVariantImageRepository.save(variantImageLink);
                }
            }
        }

        return FullProductCreateResponse.builder()
                .productId(savedProduct.getId())
                .message("Tạo sản phẩm thành công")
                .build();
    }


    @PreAuthorize("hasRole('SHOP')")
    @Override
    @CacheEvict(value = {"product_list" , "product_detail" }, allEntries = true)
    public ProductResponse updateProduct(int productId, UpdateProductRequest request) {
        Products products = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productMapper.updateProduct(products, request);
        return productMapper.toProductResponse(productRepository.save(products));
    }
    @PreAuthorize("hasRole('SHOP')")
    @Override
    @CacheEvict(value = {"product_list" , "product_detail" }, allEntries = true)
    public String deleteProduct(int productId) {
        Products products = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (products.getIsDeleted().equals("1")) {
            return "Sản phẩm đã bị xóa trước đó";
        }
        products.setIsDeleted("1");
        productRepository.save(products);
        return "Xóa thành công";
    }


    public boolean checkExistProductByName(String productName) {
        return productRepository.existsByName(productName);
    }



    // Hàm map Products entity sang ProductResponse DTO
    private ProductResponse mapToProductResponse(Products product) {
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .animal(product.getAnimal())
                .brand(product.getBrand())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .soldQuantity(String.valueOf(product.getSoldQuantity()))
                .stockQuantity(String.valueOf(product.getStockQuantity()))
                .isDeleted(product.getIsDeleted())
                .isFeatured(product.getIsFeatured())
                .createdDate(product.getCreatedDate())
                .updatedDate(product.getUpdatedDate())
                .build();

        List<ProductImages> images = productImageRepository.findByProductId(product.getId());
        List<ProductImageResponse> imageResponses = images.stream()
                .map(image -> ProductImageResponse.builder()
                        .id(image.getId())
                        .productId(image.getProduct().getId())
                        .imageUrl(image.getImageUrl())
                        .position(image.getPosition())
                        .isPrimary(image.getIsPrimary())
                        .isDeleted(image.getIsDeleted())
                        .createdDate(image.getCreatedDate())
                        .updatedDate(image.getUpdatedDate())
                        .build())
                .collect(Collectors.toList());
        response.setProductImage(imageResponses);




        List<ProductVariants> variants = productVariantRepository.findByProductId(product.getId());
        List<ProductVariantResponse> variantResponses = variants.stream()
                .map(variant -> {
                    List<ProductImages> variantImages = productImageRepository.findImagesByVariantId(variant.getId());

                    List<String> imageUrls = variantImages.stream().map(
                            ProductImages::getImageUrl).toList();

                    return ProductVariantResponse.builder()
                            .id(variant.getId())
                            .productId(variant.getProduct().getId())
                            .variantName(variant.getVariantName())
                            .weight(variant.getWeight())
                            .price(variant.getPrice())
                            .stockQuantity(variant.getStockQuantity())
                            .soldQuantity(variant.getSoldQuantity())
                            .isDeleted(variant.getIsDeleted())
                            .createdDate(variant.getCreatedDate())
                            .updatedDate(variant.getUpdatedDate())
                            .imageUrl(imageUrls)
                            .build();
                })
                .toList();
        response.setProductVariant(variantResponses);

        return response;
    }


}
