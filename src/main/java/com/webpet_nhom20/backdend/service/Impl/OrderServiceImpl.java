package com.webpet_nhom20.backdend.service.Impl;

import com.nimbusds.jwt.SignedJWT;
import com.webpet_nhom20.backdend.config.JwtTokenProvider;
import com.webpet_nhom20.backdend.dto.request.Order.CheckStockRequest;
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
import java.util.*;

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

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        BigDecimal itemsTotal = BigDecimal.ZERO;
        Map<Integer, ProductVariants> variantMap = new HashMap<>();

        // ================== 1. CHECK STOCK + TRỪ KHO ==================
        for (OrderItemRequest itemReq : request.getItems()) {

            ProductVariants variant = productVariantRepository
                    .findByIdForUpdate((long) itemReq.getProductVariantId())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));


            if (variant.getStockQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException(
                        "Sản phẩm " + variant.getVariantName()
                                + " chỉ còn " + variant.getStockQuantity()
                );
            }


            variant.setStockQuantity(
                    variant.getStockQuantity() - itemReq.getQuantity()
            );
            variant.setSoldQuantity(
                    variant.getSoldQuantity() + itemReq.getQuantity()
            );

            productVariantRepository.save(variant);
            variantMap.put(variant.getId(), variant);

            // 💰 TÍNH TIỀN
            BigDecimal price = BigDecimal.valueOf(variant.getPrice());
            BigDecimal quantity = BigDecimal.valueOf(itemReq.getQuantity());
            itemsTotal = itemsTotal.add(price.multiply(quantity));
        }

        // ================== 2. DISCOUNT ==================
        float discountPercent = request.getDiscountPercent() == null
                ? 0f
                : (float) request.getDiscountPercent();

        BigDecimal discountValue = itemsTotal
                .multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // ================== 3. TOTAL ==================
        BigDecimal shippingAmount = request.getShippingAmount();
        BigDecimal totalAmount = itemsTotal
                .add(shippingAmount)
                .subtract(discountValue);

        // ================== 4. CREATE ORDER ==================
        Order order = new Order();
        User user = new User();
        user.setId(userIdFromToken());

        order.setUser(user);
        order.setOrderCode("ORD-" + System.currentTimeMillis());
        order.setTotalAmount(totalAmount);
        order.setShippingAmount(shippingAmount);
        order.setDiscountPercent(discountPercent);
        order.setShippingAddress(request.getShippingAddress());
        order.setNote(request.getNote());
        order.setIsDeleted("0");

        if ("cod".equals(request.getPaymentMethod())) {
            order.setStatus(OrderStatus.PROCESSING.name());
            order.setPaymentMethod(PaymentMethod.COD.name());
        } else if ("vnpay".equals(request.getPaymentMethod())) {
            order.setStatus(OrderStatus.WAITING_PAYMENT.name());
            order.setPaymentMethod(PaymentMethod.VNPAY.name());
            order.setPaymentExpiredAt(LocalDateTime.now().plusDays(1));
        }

        Order savedOrder = orderRepository.save(order);

        // ================== 5. CREATE ORDER ITEMS ==================
        List<OrderItems> savedItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {

            ProductVariants variant = variantMap.get(itemReq.getProductVariantId());

            BigDecimal unitPrice = BigDecimal.valueOf(variant.getPrice());
            BigDecimal quantity = BigDecimal.valueOf(itemReq.getQuantity());

            OrderItems item = new OrderItems();
            item.setOrder(savedOrder);
            item.setProductVariant(variant);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(unitPrice.multiply(quantity));
            item.setIsDeleted("0");

            savedItems.add(orderItemRepository.save(item));
        }

        // ================== 6. RESPONSE ==================
        OrderResponse response = new OrderResponse();
        response.setId(savedOrder.getId());
        response.setOrderCode(savedOrder.getOrderCode());
        response.setUserId(savedOrder.getUser().getId());
        response.setTotalAmount(savedOrder.getTotalAmount());
        response.setShippingAmount(savedOrder.getShippingAmount());
        response.setDiscountPercent(savedOrder.getDiscountPercent());
        response.setStatus(savedOrder.getStatus());
        response.setShippingAddress(savedOrder.getShippingAddress());
        response.setNote(savedOrder.getNote());
        response.setIsDeleted(savedOrder.getIsDeleted());
        response.setCreatedDate(savedOrder.getCreatedDate());
        response.setUpdatedDate(savedOrder.getUpdatedDate());

        response.setItems(savedItems.stream().map(item -> {
            OrderItemResponse i = new OrderItemResponse();
            i.setOrderId(item.getOrder().getId());
            i.setProductVariantId(item.getProductVariant().getId());
            i.setQuantity(item.getQuantity());
            i.setUnitPrice(item.getUnitPrice());
            i.setTotalPrice(item.getTotalPrice());
            return i;
        }).toList());

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
            response.setId(order.getId());
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

    @Override
    public void checkStock(CheckStockRequest request) {
        for(var item : request.getItems()) {
            ProductVariants variants =
                    productVariantRepository.findById(item.getProductVariantId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if(variants.getStockQuantity() < item.getQuantity()) {
                throw new AppException(ErrorCode.STOCK_NOT_ENOUGHT);
            }
        }

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
