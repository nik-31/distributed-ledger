package com.nikhil.wallet_service.controller;

import com.nikhil.wallet_service.dto.CreateUserRequest;
import com.nikhil.wallet_service.entity.User;
import com.nikhil.wallet_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(
            @RequestBody CreateUserRequest request) {

        return userService.createUser(request);
    }
}
