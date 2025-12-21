package com.webpet_nhom20.backdend.service.Impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.webpet_nhom20.backdend.dto.request.Auth.*;
import com.webpet_nhom20.backdend.dto.response.Auth.AuthenticationResponse;
import com.webpet_nhom20.backdend.dto.response.Auth.IntrospectResponse;
import com.webpet_nhom20.backdend.entity.InvalidatedToken;
import com.webpet_nhom20.backdend.entity.Role;
import com.webpet_nhom20.backdend.entity.User;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.repository.InvalidatedTokenRepository;
import com.webpet_nhom20.backdend.repository.UserRepository;
import com.webpet_nhom20.backdend.service.AuthenticationService;
import com.webpet_nhom20.backdend.service.OtpService;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;
    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @Value("${signerKey}")
    private String signerKey;

    @NonFinal
    @Value("${valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${refreshable-duration}")
    protected long REFRESHABLE_DURATION;


    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        }catch (AppException | JOSEException | ParseException e){
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }
    @Override
    public void SendMailForgotPassword(ForgotPasswordRequest request)  {
        var user = userRepository.findByUsernameOrEmail(request.getIdentifier(), request.getIdentifier()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        if ("1".equals(user.getIsDeleted())) {
            throw new AppException(ErrorCode.USER_DELETED);
        }
        otpService.sendOtpForgotPassword(user.getEmail());
    }
    @Override
    public void ChangePassword(AuthenticationRequest request){
        var user = userRepository.findByUsernameOrEmail(request.getIdentifier(), request.getIdentifier()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        if ("1".equals(user.getIsDeleted())) {
            throw new AppException(ErrorCode.USER_DELETED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsernameOrEmail(request.getIdentifier(), request.getIdentifier()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        if ("1".equals(user.getIsDeleted())) {
            throw new AppException(ErrorCode.USER_DELETED);
        }

        // Kiểm tra mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authenticated = passwordEncoder.matches(request.getPassword(),user.getPassword());

        if(!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException{
        // Kiểm tra chữ ký của token.
        // Kiểm tra token có hết hạn trong khoảng thời gian cho phép refresh không.
        // Kiểm tra token có bị revoke chưa.
        var signJWT = verifyToken(request.getToken(), true);

        // Lấy id và thời gian hết hạn của token cũ
        var jid = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        // vô hiệu hóa token cũ
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jid).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);

        // Lấy user từ token cũ
        var userName = signJWT.getJWTClaimsSet().getSubject();
        var user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        if ("1".equals(user.getIsDeleted())) {
            throw new AppException(ErrorCode.USER_DELETED);
        }

        // Tạo token mới
        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    @Override
    public void logout(LogoutRequest request) {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jwtId = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jwtId).expiryTime(expiryTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException | ParseException | JOSEException e) {
            log.info("Token already expired ");
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        // Tạo đối tượng xác minh
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        // Phân tích token thành 1 object
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Xác minh thời gian hết hạn của token
        // Nếu isRefresh = true lấy IssuaTime( thời gian token được tạo) cộng với thời gian cho phép refresh
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                // ísRefresh = false lấy thời gian hết hạn từ claim exp
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        // Xác minh token
        var verified = signedJWT.verify(verifier);

        // Kiểm tra tính hợp lệ của token
        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Kiểm tra token đã bị vô hiệu hóa chưa
        // Những token bị vô hiệu hóa sẽ được lưu trong bảng invalidatedToken
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Nếu token hợp lệ trả về đối tượng signedJWT
        return signedJWT;
    }

    private String generateToken(User user){
            // Token gồm
            // Header + Payload + Signature

            // Tạo header cho token
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);


            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("Pet_Shop")
                    .issueTime(new Date())
                    .expirationTime(new Date(
                            Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("id", user.getId())
                    .claim("role", user.getRole().getName())
                    .claim("scope" , buildScope(user))
                    .build();
            Payload payload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);

            // Ký token
            try {
                jwsObject.sign(new MACSigner(signerKey.getBytes()));
                return jwsObject.serialize();
            } catch (JOSEException e) {
                log.error("Cannot generate token:", e);
                throw new RuntimeException();
            }


        }
    /**
     * Xây dựng chuỗi scope cho user từ role và permission.
     * Scope này sẽ được nhúng vào JWT để phục vụ phân quyền.
     *
     * Ví dụ:
     *   - User có role "SHOP", permissions ["GET_PRODUCTS", "EDIT_PRODUCTS"]
     *     => "ROLE_SHOP GET_PRODUCTS EDIT_PRODUCTS"
     *   - User có role "CUSTOMER", không có permission
     *     => "ROLE_CUSTOMER"
     */
    private String buildScope(User user) {
        // Dùng StringJoiner để nối các role/permission thành 1 chuỗi, phân tách bởi khoảng trắng
        StringJoiner stringJoiner = new StringJoiner(" ");

        // Lấy role của user (vì thiết kế User chỉ có 1 role)
        Role role = user.getRole();

        if (role != null) {
            // Thêm role vào scope với tiền tố "ROLE_"
            // Ví dụ: role = "SHOP" => "ROLE_SHOP"
            stringJoiner.add("ROLE_" + role.getName());

            // Nếu role có danh sách permission thì thêm từng permission vào scope
            if (!CollectionUtils.isEmpty(role.getPermissions())) {
                role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            }
        }
        return stringJoiner.toString();
    }

}

