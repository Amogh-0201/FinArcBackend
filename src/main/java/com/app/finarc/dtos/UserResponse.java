package com.app.finarc.dtos;


import lombok.*;

@Getter
@Builder
public class UserResponse {

    private String id;

    private String username;

    private String email;

    private Double monthlyBudgetThreshold;

    private String currency;
}
