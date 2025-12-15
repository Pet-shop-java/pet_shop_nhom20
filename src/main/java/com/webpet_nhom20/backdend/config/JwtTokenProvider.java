package com.webpet_nhom20.backdend.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    
    @Value("${signerKey}")
    private String signerKey;

    /**
     * Lấy Claims từ JWT token
     * @param token JWT token
     * @return JWTClaimsSet chứa thông tin từ token
     */
    public JWTClaimsSet getClaimsFromToken(String token) {
        try {
            // Tạo đối tượng xác minh
            MACVerifier verifier = new MACVerifier(signerKey.getBytes());
            
            // Phân tích token thành SignedJWT object
            SignedJWT signedJWT = SignedJWT.parse(token.replace("Bearer ", ""));
            
            // Xác minh token
            boolean verified = signedJWT.verify(verifier);
            
            if (!verified) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            
            // Kiểm tra thời gian hết hạn
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiryTime != null && expiryTime.before(new Date())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            
            return signedJWT.getJWTClaimsSet();
            
        } catch (ParseException | JOSEException e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    /**
     * Lấy User ID từ JWT token
     * @param token JWT token
     * @return User ID dưới dạng Integer
     */
    public Integer getUserId(String token) {

        JWTClaimsSet claims = getClaimsFromToken(token);

        Object idClaim = claims.getClaim("id");

        if (idClaim == null) {
            log.error("JWT token does not contain 'id' claim");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        try {
            if (idClaim instanceof Number) {
                return ((Number) idClaim).intValue();
            }

            return Integer.parseInt(idClaim.toString());

        } catch (NumberFormatException e) {
            log.error("Invalid 'id' claim type in JWT: {}", idClaim);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }


    /**
     * Lấy User Role từ JWT token
     * @param token JWT token
     * @return User role dưới dạng String
     */
    public String getUserRole(String token) {
        try {
            JWTClaimsSet claims = getClaimsFromToken(token);
            Object roleClaim = claims.getClaim("role");
            
            if (roleClaim instanceof String) {
                return (String) roleClaim;
            }
            
            // Nếu không có claim "role", thử parse từ scope
            String scope = (String) claims.getClaim("scope");
            if (scope != null && scope.startsWith("ROLE_")) {
                return scope.substring(5); // Loại bỏ "ROLE_" prefix
            }
            
            log.warn("No role claim found in token, scope: {}", scope);
            return null;
            
        } catch (Exception e) {
            log.error("Error extracting user role from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Lấy username từ JWT token
     * @param token JWT token
     * @return Username
     */
    public String getUsername(String token) {
        try {
            JWTClaimsSet claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra token có hợp lệ không
     * @param token JWT token
     * @return true nếu token hợp lệ, false nếu không
     */
    public boolean isValidToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy thời gian hết hạn của token
     * @param token JWT token
     * @return Date object chứa thời gian hết hạn
     */
    public Date getExpirationTime(String token) {
        try {
            JWTClaimsSet claims = getClaimsFromToken(token);
            return claims.getExpirationTime();
        } catch (Exception e) {
            log.error("Error extracting expiration time from token: {}", e.getMessage());
            return null;
        }
    }
}
