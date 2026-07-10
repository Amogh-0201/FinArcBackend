package com.app.finarc.dtos.user;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResponse {

    private String id;

    private String username;

    private String email;

    private Double monthlyBudgetThreshold;

    private String currency;
}
