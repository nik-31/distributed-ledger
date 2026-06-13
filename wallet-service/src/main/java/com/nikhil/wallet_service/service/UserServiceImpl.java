package com.nikhil.wallet_service.service;

import com.nikhil.wallet_service.dto.CreateUserRequest;
import com.nikhil.wallet_service.entity.User;
import com.nikhil.wallet_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(CreateUserRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        return userRepository.save(user);
    }
}