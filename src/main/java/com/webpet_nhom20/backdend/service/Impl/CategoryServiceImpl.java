package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.Category.CreateCategoryRequest;
import com.webpet_nhom20.backdend.dto.request.Category.UpdateCategoryRequest;
import com.webpet_nhom20.backdend.dto.response.Category.CategoryResponse;
import com.webpet_nhom20.backdend.dto.response.Product.ProductResponse;
import com.webpet_nhom20.backdend.entity.Categories;
import com.webpet_nhom20.backdend.entity.Products;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.mapper.CategoryMapper;
import com.webpet_nhom20.backdend.mapper.ProductMapper;
import com.webpet_nhom20.backdend.repository.CategoryRepository;
import com.webpet_nhom20.backdend.repository.ProductRepository;
import com.webpet_nhom20.backdend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService

{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;

    @PreAuthorize("hasRole('SHOP')")
    @Override
    @CacheEvict(value = { "category_list", "category_detail" }, allEntries = true)
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_IS_EXISTED);
        }
        Categories categories = categoryMapper.toCategory(request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(categories));
    }

    @PreAuthorize("hasRole('SHOP')")
    @Override
    @CacheEvict(value = { "category_list", "category_detail" }, allEntries = true)
    public CategoryResponse updateCategory(int categoryId, UpdateCategoryRequest request) {
        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryMapper.updateCategory(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasRole('SHOP')")
    @Override
    @CacheEvict(value = { "category_list", "category_detail" }, allEntries = true)
    public String deleteCategory(int categoryId) {
        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        category.setIsDeleted("1");
        categoryRepository.save(category);
        return "Xóa thành công";
    }

    @Override
    @Cacheable(value = "category_list", key = "'cat:' + (#search?:'') + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()")
    public Page<CategoryResponse> getAllCategories(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return categoryRepository.findAll(pageable).map(categoryMapper::toCategoryResponse);
        }
        return categoryRepository.findByNameContainingIgnoreCase(search, pageable)
                .map(categoryMapper::toCategoryResponse);
    }

    @Override
    public Page<CategoryResponse> filterByFeature(String isFeature, Pageable pageable) {
        return categoryRepository.filterByFeature(isFeature, pageable).map(categoryMapper::toCategoryResponse);
    }

    @Override
    public Page<CategoryResponse> filterByDelete(String isDelete, Pageable pageable) {
        return categoryRepository.filterByDelete(isDelete, pageable).map(categoryMapper::toCategoryResponse);
    }

    @Override
    @Cacheable(value = "category_detail", key = "#id")
    public CategoryResponse getCategoryById(int id) {
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        CategoryResponse response = categoryMapper.toCategoryResponse(category);
        List<Products> productList = productRepository.findAllByCategoryId(category.getId());

        List<ProductResponse> productResponseList = productList.stream().map(productMapper::toProductResponse).toList();
        response.setProducts(productResponseList);
        return response;
    }
}
