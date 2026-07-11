package com.app.finarc.dtos.transaction;


import com.app.finarc.models.TransactionCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateTransactionRequest {

    private Double amount;

    private TransactionCategory category;

    private String description;
}
