package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Product.FullProductCreateRequest;
import com.webpet_nhom20.backdend.dto.request.Product.CreateProductRequest;
import com.webpet_nhom20.backdend.dto.request.Product.UpdateProductRequest;
import com.webpet_nhom20.backdend.dto.response.Product.BrandResponse;
import com.webpet_nhom20.backdend.dto.response.Product.FullProductCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {


    public ProductResponse createProduct(CreateProductRequest request) ;

    public ProductResponse updateProduct(int productId , UpdateProductRequest request) ;

    public String deleteProduct(int productId);

    Page<ProductResponse> getAllProduct(Pageable pageable, Integer categoryId, String search, Double minPrice, Double maxPrice, String animal, String brand, String isFeature, String isDelete);

    ProductResponse getProductById(int productId);

    FullProductCreateResponse createFullProduct(FullProductCreateRequest request);
    List<BrandResponse> getBrand();
    public Page<ProductResponse> getAllProductForAdmin(Pageable pageable, Integer categoryId, String search, Double minPrice, Double maxPrice, String animal, String brand, String isFeature, String isDelete);
    public ProductResponse getProductByIdForAdmin(int productId);

}
