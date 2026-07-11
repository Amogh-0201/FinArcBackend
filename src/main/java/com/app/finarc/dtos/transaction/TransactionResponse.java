package com.app.finarc.dtos.transaction;


import lombok.*;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransactionResponse {

    private String id;

    private String userId;

    private Double amount;

    private String category;

    private String description;

    private Instant timestamp;

    private String source;
}
