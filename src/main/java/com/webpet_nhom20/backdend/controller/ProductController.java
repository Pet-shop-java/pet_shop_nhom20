package com.webpet_nhom20.backdend.controller;

import com.webpet_nhom20.backdend.dto.request.Product.FullProductCreateRequest;
import com.webpet_nhom20.backdend.dto.request.Product.CreateProductRequest;
import com.webpet_nhom20.backdend.dto.request.Product.UpdateProductRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.Product.BrandResponse;
import com.webpet_nhom20.backdend.dto.response.Product.FullProductCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Product.ProductResponse;
import com.webpet_nhom20.backdend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public ApiResponse<Page<ProductResponse>> getAllProducts(@RequestParam(required = false) String search, @RequestParam(required = false) Integer categoryId,
                                                             Pageable pageable, @RequestParam (required = false) Double minPrice, @RequestParam(required = false) Double maxPrice,
                                                             @RequestParam (required = false) String animal, @RequestParam (required = false) String brand,
                                                             @RequestParam (required = false) String isFeature, @RequestParam (required = false) String isDelete){
        return ApiResponse.<Page<ProductResponse>>builder().
                success(true)
                .message("Lấy danh sách sản phẩm thành công")
                .result(productService.getAllProduct(pageable,categoryId,search,minPrice, maxPrice, animal , brand, isFeature, isDelete)).build();
    }
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable int productId ){
        ProductResponse response = productService.getProductById(productId );
        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("")
                .result(response)
                .build();
    }
    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Create product successfully")
                .result(productService.createProduct(request))
                .build();
    }
    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable int productId, @RequestBody @Valid UpdateProductRequest request){
        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Cập nhật sản phẩm thành công!")
                .result(productService.updateProduct(productId,request))
                .build();
    }
    @DeleteMapping("/{productId}")
    public ApiResponse<String> deleteProduct(@PathVariable int productId){
        return ApiResponse.<String>builder()
                .success(true)
                .message(productService.deleteProduct(productId))
                .build();
    }

    @PostMapping("/create-all")
    public ApiResponse<FullProductCreateResponse> createFullProduct(
            @Valid @RequestBody FullProductCreateRequest request) {
        FullProductCreateResponse response = productService.createFullProduct(request);
        return ApiResponse.<FullProductCreateResponse>builder()
                .success(true)
                .message(response.getMessage())
                .result(response)
                .build();
    }
    @GetMapping("/brands")
    public ApiResponse<List<BrandResponse>> getBrand() {
        List<BrandResponse> brands = productService.getBrand();
        return ApiResponse.<List<BrandResponse>>builder()
                .success(true)
                .message("Lấy danh sách thương hiệu thành công")
                .result(brands)
                .build();
    }
}
