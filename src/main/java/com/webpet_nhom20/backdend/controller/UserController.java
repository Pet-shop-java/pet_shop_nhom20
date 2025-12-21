package com.webpet_nhom20.backdend.controller;


import com.webpet_nhom20.backdend.dto.request.User.ChangePasswordUserRequest;
import com.webpet_nhom20.backdend.dto.request.User.UserCreationRequest;
import com.webpet_nhom20.backdend.dto.request.User.UserUpdateRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.User.UserResponse;
import com.webpet_nhom20.backdend.service.OtpService;
import com.webpet_nhom20.backdend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private OtpService otpService;

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email) {
        otpService.sendOtp(email);
        return "OTP đã được gửi về email";
    }
    @PostMapping()
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request, @RequestParam String otp){
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Create user successfully")
                .result(userService.createUser(request, otp))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        UserResponse user = userService.getMyInfo();
        return ApiResponse.<UserResponse>builder()
                .result(user)
                .build();
    }
    @PutMapping("/{userId}")
    ApiResponse<UserResponse>updateUser(@PathVariable int userId, @Valid @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Cập nhật user thành công")
                .result( userService.updateUser(userId,request))
                .build();
    }


    @GetMapping()
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(grantedAuthority ->  log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .success(true)
                .build()
                ;
    }
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") int userId){
        UserResponse user = userService.getUser(userId);
        return ApiResponse.<UserResponse>builder()
                .result(user)
                .build();
    }
    @PutMapping("/changePassword/{userId}")
    public ApiResponse<String> changePasswordUser(@PathVariable int userId, @Valid @RequestBody ChangePasswordUserRequest request) {
        String resultMessage = userService.changeUserPassword(userId, request);

        return ApiResponse.<String>builder()
                .success(true)
                .message(resultMessage)
                .build();
    }



}
