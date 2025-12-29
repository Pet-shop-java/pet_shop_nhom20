package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.config.JwtTokenProvider;
import com.webpet_nhom20.backdend.dto.request.Cart.CartRequest;
import com.webpet_nhom20.backdend.dto.request.CartItem.CartItemRequest;
import com.webpet_nhom20.backdend.dto.response.Cart.CartResponse;
import com.webpet_nhom20.backdend.dto.response.CartItem.CartItemResponse;
import com.webpet_nhom20.backdend.entity.Cart;
import com.webpet_nhom20.backdend.entity.CartItems;
import com.webpet_nhom20.backdend.entity.ProductVariants;
import com.webpet_nhom20.backdend.entity.User;
import com.webpet_nhom20.backdend.repository.CartItemRepository;
import com.webpet_nhom20.backdend.repository.CartRepository;
import com.webpet_nhom20.backdend.repository.ProductVariantRepository;
import com.webpet_nhom20.backdend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;


    @Override
    public CartResponse addToCart(CartRequest request, String token) {
        Integer userIdFromToken = jwtTokenProvider.getUserId(token);

        // 1. Lấy hoặc tạo cart
        Cart cart = cartRepository.findByUserId(userIdFromToken).orElseGet(() -> {
            Cart c = new Cart();
            User u = new User();
            u.setId(userIdFromToken);
            c.setUser(u);
            return cartRepository.save(c);
        });

        List<CartItems> resultItems = new ArrayList<>();

        for (CartItemRequest itemReq : request.getItems()) {

            ProductVariants variant = productVariantRepository
                    .findByIdAndIsDeletedNot(itemReq.getProductVariantId(), "1")
                    .orElseThrow(() -> new RuntimeException("Product variant not found or deleted"));

            Optional<CartItems> existingItemOpt =
                    cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), variant.getId());

            // CASE 1: chưa có item trong cart
            if (existingItemOpt.isEmpty()) {

                if (itemReq.getQuantity() <= 0) {
                    throw new RuntimeException("Quantity must be greater than 0 for new item");
                }

                CartItems newItem = new CartItems();
                newItem.setCart(cart);
                newItem.setProductVariant(variant);
                newItem.setQuantity(itemReq.getQuantity());
                newItem.setIsDeleted("0");

                resultItems.add(cartItemRepository.save(newItem));
                continue;
            }

            // CASE 2: đã có item
            CartItems existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + itemReq.getQuantity();

            // Không cho âm
            if (newQuantity < 0) {
                throw new RuntimeException("Quantity cannot be negative");
            }

            // Về 0 → xóa hẳn
            if (newQuantity == 0) {
                cartItemRepository.delete(existingItem);
                continue;
            }

            // > 0 → update
            existingItem.setQuantity(newQuantity);
            resultItems.add(cartItemRepository.save(existingItem));
        }

        // Lấy lại danh sách item còn tồn tại trong cart để response đúng
        List<CartItems> finalItems = cartItemRepository.findByCartId(cart.getId());

        return mapCartToResponse(cart, finalItems);
    }

    @Override
    public CartResponse getCart(String token) {
        Integer userId = jwtTokenProvider.getUserId(token);
        // 1. Lấy cart theo user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // 2. Lấy list item trong cart
        List<CartItems> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItems> itemsNotDeleted = new ArrayList<>();
        for (CartItems item : items) {
            boolean isDeleted = productVariantRepository.isDeleted(item.getProductVariant().getId());
            if(!isDeleted) {
                itemsNotDeleted.add(item);
            }
        }

        // 3. Map sang DTO dựa trên hàm mapCartToResponse đã viết
        return mapCartToResponse(cart, itemsNotDeleted);
    }

    @Override
    public void removeItem(Integer itemId, String token) {
        Integer userId = jwtTokenProvider.getUserId(token);

        // 1. Tìm cartItem thuộc đúng user
        CartItems item = cartItemRepository.findByIdAndCart_User_Id(itemId, userId)
                .orElseThrow(() -> new RuntimeException("Cart item not found or you don't have permission"));

        // 2. Delete cứng khỏi DB
        cartItemRepository.delete(item);
    }

    //mapper cart to response
    private CartResponse mapCartToResponse(Cart cart, List<CartItems> items) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setCreatedDate(cart.getCreatedDate());
        response.setUpdatedDate(cart.getUpdatedDate());

        List<CartItemResponse> itemResponses = items.stream().map(item -> {
            CartItemResponse i = new CartItemResponse();
            i.setId(item.getId());
            i.setCartId(item.getCart().getId());
            i.setProductVariantId(item.getProductVariant().getId());
            i.setQuantity(item.getQuantity());
            i.setUnitPrice(BigDecimal.valueOf(item.getProductVariant().getPrice()));
            i.setProductName(item.getProductVariant().getProduct().getName());
            i.setVariantName(item.getProductVariant().getVariantName());

            if (item.getProductVariant().getActualImages() != null &&
                    !item.getProductVariant().getActualImages().isEmpty()) {
                i.setImageUrl(item.getProductVariant().getActualImages().get(0).getImageUrl());
            }

            i.setIsDeleted(item.getIsDeleted());
            i.setCreatedDate(item.getCreatedDate());
            i.setUpdatedDate(item.getUpdatedDate());
            return i;
        }).toList();

        response.setItems(itemResponses);
        response.setTotalItems(itemResponses.size());
        return response;
    }


}
