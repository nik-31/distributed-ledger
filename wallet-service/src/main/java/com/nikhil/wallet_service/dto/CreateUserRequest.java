package com.nikhil.wallet_service.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String name;

    private String email;
}
