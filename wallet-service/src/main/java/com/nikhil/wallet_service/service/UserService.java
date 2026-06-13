package com.nikhil.wallet_service.service;

import com.nikhil.wallet_service.dto.CreateUserRequest;
import com.nikhil.wallet_service.entity.User;

public interface UserService {
    User createUser(CreateUserRequest request);

}
