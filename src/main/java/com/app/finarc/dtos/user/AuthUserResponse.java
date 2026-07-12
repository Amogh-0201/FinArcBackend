package com.app.finarc.dtos.user;


import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthUserResponse {

    private String token;

    private String username;

    private String email;

    private Double monthlyBudgetThreshold;

    private String currency;
}
