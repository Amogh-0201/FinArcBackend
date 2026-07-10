package com.app.finarc.dtos.user;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateUserRequest {

    private String username;

    private Double monthlyBudgetThreshold;

    private String currency;
}
