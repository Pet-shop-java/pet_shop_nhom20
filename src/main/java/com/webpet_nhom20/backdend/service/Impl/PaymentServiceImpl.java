package com.webpet_nhom20.backdend.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webpet_nhom20.backdend.config.VnPayConfig;
import com.webpet_nhom20.backdend.dto.response.Payment.PaymentResponseDTO;
import com.webpet_nhom20.backdend.entity.Order;
import com.webpet_nhom20.backdend.entity.Payment;
import com.webpet_nhom20.backdend.enums.OrderStatus;
import com.webpet_nhom20.backdend.enums.PaymentMethod;
import com.webpet_nhom20.backdend.enums.PaymentStatus;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.repository.OrderRepository;
import com.webpet_nhom20.backdend.repository.PaymentRepository;
import com.webpet_nhom20.backdend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    PaymentRepository paymentRepository;


    @Override
    public PaymentResponseDTO createPayment(Integer orderId,HttpServletRequest httpReq) throws UnsupportedEncodingException {


        Order order = orderRepository.findById(orderId).orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_FOUND));

        String orderType = "other";
        String vnp_TxnRef = order.getOrderCode();
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VnPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        BigDecimal amount = order.getFinalAmount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP);
        vnp_Params.put("vnp_Amount", amount.toPlainString());
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", VnPayConfig.getIpAddress(httpReq));




        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);


        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setUrl(paymentUrl);
        return dto;
    }


    public void handleVnPaySuccess(HttpServletRequest request) throws Exception {

        long start = System.nanoTime();
        log.info("[VNPAY] Start handleVnPaySuccess");

        String txnRef = request.getParameter("vnp_TxnRef");
        String amountStr = request.getParameter("vnp_Amount");

        long t1 = System.nanoTime();
        log.info("[VNPAY] Read request params took {} ms",
                (t1 - start) / 1_000_000);

        Order order = orderRepository
                .findByOrderCode(txnRef)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        long t2 = System.nanoTime();
        log.info("[VNPAY] Find order took {} ms",
                (t2 - t1) / 1_000_000);

        if (order.getStatus().equals(OrderStatus.PROCESSING.name())) {
            log.warn("[VNPAY] Order {} already PROCESSING → skip", txnRef);
            return;
        }

        BigDecimal amount = new BigDecimal(amountStr)
                .divide(BigDecimal.valueOf(100));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(PaymentMethod.VNPAY.name());
        payment.setAmount(amount);
        payment.setProviderRef(txnRef);
        payment.setCurrency("VND");
        payment.setStatus(PaymentStatus.SUCCESSFUL.name());
        payment.setResponseCode(request.getParameter("vnp_ResponseCode"));
        payment.setTransactionNo(request.getParameter("vnp_TransactionNo"));
        payment.setBankCode(request.getParameter("vnp_BankCode"));
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        long t3 = System.nanoTime();
        log.info("[VNPAY] Save payment took {} ms",
                (t3 - t2) / 1_000_000);

        order.setStatus(OrderStatus.PROCESSING.name());
        orderRepository.save(order);

        long t4 = System.nanoTime();
        log.info("[VNPAY] Update order took {} ms",
                (t4 - t3) / 1_000_000);

        log.info("[VNPAY] Total handleVnPaySuccess took {} ms",
                (t4 - start) / 1_000_000);
    }



    public void handleVnPayFailed(HttpServletRequest request) throws Exception{

        String txnRef = request.getParameter("vnp_TxnRef");
        String amountStr = request.getParameter("vnp_Amount");
        Order order = orderRepository
                .findByOrderCode(txnRef)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus().equals(OrderStatus.PROCESSING.name())) {
            return ;
        }

        BigDecimal amount = new BigDecimal(amountStr)
                .divide(BigDecimal.valueOf(100));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(PaymentMethod.VNPAY.name());
        payment.setAmount(amount);
        payment.setProviderRef(txnRef);
        payment.setCurrency("VND");
        payment.setStatus(PaymentStatus.FAILED.name());
        payment.setResponseCode(request.getParameter("vnp_ResponseCode"));
        payment.setTransactionNo(request.getParameter("vnp_TransactionNo"));
        payment.setBankCode(request.getParameter("vnp_BankCode"));
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}

