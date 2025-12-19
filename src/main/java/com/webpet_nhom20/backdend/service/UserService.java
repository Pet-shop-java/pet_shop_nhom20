package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.User.ChangePasswordUserRequest;
import com.webpet_nhom20.backdend.dto.request.User.UserCreationRequest;
import com.webpet_nhom20.backdend.dto.request.User.UserUpdateRequest;
import com.webpet_nhom20.backdend.dto.response.User.UserResponse;

import java.util.List;

public interface UserService  {

    public UserResponse createUser(UserCreationRequest request, String otp);

    public UserResponse updateUser(int userId, UserUpdateRequest request);

    public UserResponse getMyInfo();

    public UserResponse getUser(int id);

    public List<UserResponse> getUsers();

    public String changeUserPassword(int userId, ChangePasswordUserRequest request);


}
