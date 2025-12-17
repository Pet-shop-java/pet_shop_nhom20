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
import com.webpet_nhom20.backdend.enums.PaymentMethod;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        for (OrderItemRequest itemReq : request.getItems()) {

            ProductVariants variant = productVariantRepository.findById(itemReq.getProductVariantId())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));

            BigDecimal price = BigDecimal.valueOf(variant.getPrice()); // ✅ BigDecimal
            BigDecimal quantity = BigDecimal.valueOf(itemReq.getQuantity());

            BigDecimal totalItemPrice = price.multiply(quantity); // price * quantity
            itemsTotal = itemsTotal.add(totalItemPrice);
        }


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
        user.setId(userIdFromToken());

        order.setUser(user);
        order.setOrderCode(orderCode);

        order.setTotalAmount(totalAmount);         // ✅ BigDecimal
        order.setShippingAmount(shippingAmount);   // ✅ BigDecimal
        order.setDiscountPercent(discountPercent); // ✅ vẫn FLOAT
        if(request.getPaymentMethod().equals("cod")){
            order.setStatus(OrderStatus.PROCESSING.name());
            order.setPaymentMethod(PaymentMethod.COD.name());
        }
        if(request.getPaymentMethod().equals("vnpay")){
            order.setStatus(OrderStatus.WAITING_PAYMENT.name());
            order.setPaymentMethod(PaymentMethod.VNPAY.name());
            LocalDateTime now = LocalDateTime.now();
            order.setPaymentExpiredAt(now.plusDays(1));
        }
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

    @PreAuthorize("hasRole('CUSTOMER')")
    @Override
    public Page<OrderResponse> getAllOrder(String status, Pageable pageable) {
        Integer userId = userIdFromToken();
        Page<Order> orderPage;

        // Logic: Nếu có status thì lọc theo status, không thì lấy tất cả
        if (status != null) {
            orderPage = orderRepository.findAllByUserIdAndStatus(userId, status, pageable);
        } else {
            orderPage = orderRepository.findAllByUserId(userId, pageable);
        }

        return orderPage.map(order -> {
            OrderResponse response = new OrderResponse();
            expireIfNeeded(order); // Logic kiểm tra hết hạn của bạn

            response.setOrderCode(order.getOrderCode());
            response.setUserId(order.getUser().getId());
            response.setTotalAmount(order.getTotalAmount());
            response.setShippingAmount(order.getShippingAmount());
            response.setShippingAddress(order.getShippingAddress());
            response.setNote(order.getNote());
            response.setStatus(order.getStatus());
            response.setCreatedDate(order.getCreatedDate());

            return response;
        });
    }
    public String cancelOrder(String orderCode) throws AppException {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        boolean isCancelable = order.getStatus().equals(OrderStatus.WAITING_PAYMENT.name())
                || order.getStatus().equals(OrderStatus.PROCESSING.name());

        if (!isCancelable) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_ORDER);
        }

        order.setStatus(OrderStatus.CANCELLED.name());
        orderRepository.save(order);
        return "Hủy đơn hàng thành công";
    }
    public OrderResponse findOrderItemsByOrderId(Integer orderId) {
        Integer Id = userIdFromToken();
        List<OrderItemResponse> order = orderItemRepository.findByOrderId(orderId);

        OrderResponse response = new OrderResponse();
        response.setId(orderId);
        response.setItems(order);

        return response;
    }



//    @Transactional
//    public void markPaid(String orderCode) {
//        Order order = orderRepository.findByOrderCode(orderCode)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
//
//        if (order.getStatus().equals(OrderStatus.PROCESSING.name())) return;
//
//        order.setStatus(OrderStatus.PROCESSING.name());
//        orderRepository.save(order);
//    }
//    @Transactional
//    public void markFailed(String orderCode) {
//        Order order = orderRepository.findByOrderCode(orderCode)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
//
//        order.setStatus(OrderStatus.WAITING_PAYMENT.name());
//        orderRepository.save(order);
//    }

    private Integer userIdFromToken(){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Jwt jwt =(Jwt)  authentication.getPrincipal();


        Number userIdClaim = jwt.getClaim("id");
        Integer userIdFromToken =userIdClaim.intValue();
        return userIdFromToken;
    }

    private void expireIfNeeded(Order order) {
        if (!OrderStatus.WAITING_PAYMENT.name().equals(order.getStatus())) {
            return;
        }

        if (order.getPaymentExpiredAt() == null) {
            return;
        }

        if (order.getPaymentExpiredAt().isBefore(LocalDateTime.now())) {
            order.setStatus(OrderStatus.CANCELLED.name());
            orderRepository.save(order);
        }
    }


}
