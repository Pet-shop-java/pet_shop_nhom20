package com.webpet_nhom20.backdend.service.Impl;

import com.nimbusds.jwt.SignedJWT;
import com.webpet_nhom20.backdend.config.JwtTokenProvider;
import com.webpet_nhom20.backdend.dto.request.Order.OrderRequest;
import com.webpet_nhom20.backdend.dto.request.OrderItem.OrderItemRequest;
import com.webpet_nhom20.backdend.dto.response.Order.OrderResponse;
import com.webpet_nhom20.backdend.dto.response.OrderItem.OrderItemResponse;
import com.webpet_nhom20.backdend.entity.Order;
import com.webpet_nhom20.backdend.entity.OrderItems;
import com.webpet_nhom20.backdend.entity.ProductVariants;
import com.webpet_nhom20.backdend.entity.User;
import com.webpet_nhom20.backdend.enums.OrderStatus;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.repository.OrderItemRepository;
import com.webpet_nhom20.backdend.repository.OrderRepository;
import com.webpet_nhom20.backdend.repository.ProductVariantRepository;
import com.webpet_nhom20.backdend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderResponse createOrder(OrderRequest request) {

        BigDecimal itemsTotal = BigDecimal.ZERO;

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Jwt jwt =(Jwt)  authentication.getPrincipal();


        Number userIdClaim = jwt.getClaim("id");
        Integer userIdFromToken =userIdClaim.intValue();



        // ================== 1. TÍNH TỔNG TIỀN ITEMS ==================
        for (OrderItemRequest itemReq : request.getItems()) {

            ProductVariants variant = productVariantRepository.findById(itemReq.getProductVariantId())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));

            BigDecimal price = BigDecimal.valueOf(variant.getPrice()); // ✅ BigDecimal
            BigDecimal quantity = BigDecimal.valueOf(itemReq.getQuantity());

            BigDecimal totalItemPrice = price.multiply(quantity); // price * quantity
            itemsTotal = itemsTotal.add(totalItemPrice);
        }

        // ================== 2. TÍNH GIẢM GIÁ (GIỮ discountPercent LÀ FLOAT) ==================
        float discountPercent = request.getDiscountPercent() == null
                ? 0f
                : (float) request.getDiscountPercent();

        BigDecimal discountValue = itemsTotal
                .multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // ================== 3. TÍNH TỔNG TIỀN ĐƠN ==================
        BigDecimal shippingAmount = request.getShippingAmount(); // ✅ BigDecimal

        BigDecimal totalAmount = itemsTotal
                .add(shippingAmount)
                .subtract(discountValue);

        // ================== 4. TẠO ORDER ==================
        String orderCode = "ORD-" + System.currentTimeMillis();

        Order order = new Order();

        User user = new User();
        user.setId(userIdFromToken);

        order.setUser(user);
        order.setOrderCode(orderCode);

        order.setTotalAmount(totalAmount);         // ✅ BigDecimal
        order.setShippingAmount(shippingAmount);   // ✅ BigDecimal
        order.setDiscountPercent(discountPercent); // ✅ vẫn FLOAT
        order.setStatus(OrderStatus.WAITING_PAYMENT.name());
        order.setShippingAddress(request.getShippingAddress());
        order.setIsDeleted("0");
        order.setNote(request.getNote());

        Order savedOrder = orderRepository.save(order);

        // ================== 5. LƯU ORDER ITEMS ==================
        List<OrderItems> savedItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {

            ProductVariants variant = productVariantRepository.findById(itemReq.getProductVariantId())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));

            BigDecimal unitPrice = BigDecimal.valueOf(variant.getPrice()); // ✅ BigDecimal
            BigDecimal quantity = BigDecimal.valueOf(itemReq.getQuantity());

            BigDecimal totalPrice = unitPrice.multiply(quantity);

            OrderItems item = new OrderItems();
            item.setOrder(savedOrder);
            item.setProductVariant(variant);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(unitPrice);   // ✅ BigDecimal
            item.setTotalPrice(totalPrice); // ✅ BigDecimal
            item.setIsDeleted("0");

            savedItems.add(orderItemRepository.save(item));
        }

        // ================== 6. MAP RESPONSE ==================
        OrderResponse response = new OrderResponse();

        response.setId(savedOrder.getId());
        response.setOrderCode(savedOrder.getOrderCode());
        response.setUserId(savedOrder.getUser().getId());
        response.setTotalAmount(savedOrder.getTotalAmount());       // ✅ BigDecimal
        response.setShippingAmount(savedOrder.getShippingAmount()); // ✅ BigDecimal
        response.setDiscountPercent(savedOrder.getDiscountPercent());
        response.setStatus(savedOrder.getStatus());
        response.setShippingAddress(savedOrder.getShippingAddress());
        response.setNote(savedOrder.getNote());
        response.setIsDeleted(savedOrder.getIsDeleted());
        response.setCreatedDate(savedOrder.getCreatedDate());
        response.setUpdatedDate(savedOrder.getUpdatedDate());

        // ================== 7. MAP ORDER ITEMS RESPONSE ==================
        List<OrderItemResponse> itemResponses = savedItems.stream().map(item -> {

            OrderItemResponse i = new OrderItemResponse();
            i.setOrderId(item.getOrder().getId());
            i.setProductVariantId(item.getProductVariant().getId());
            i.setQuantity(item.getQuantity());

            i.setUnitPrice(item.getUnitPrice());     // ✅ BigDecimal
            i.setTotalPrice(item.getTotalPrice());   // ✅ BigDecimal

            i.setIsDeleted(item.getIsDeleted());
            i.setCreatedDate(item.getCreatedDate());
            i.setUpdatedDate(item.getUpdatedDate());

            return i;
        }).toList();

        response.setItems(itemResponses);

        return response;
    }

}
