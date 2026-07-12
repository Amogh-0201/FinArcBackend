package com.app.finarc.dtos.transaction;


import com.app.finarc.models.TransactionCategory;
import com.app.finarc.models.TransactionSource;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransactionResponse {

    private String id;

    private Double amount;

    private TransactionCategory category;

    private String description;

    private Instant timestamp;

    private TransactionSource source;
}
