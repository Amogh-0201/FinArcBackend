package com.app.finarc.dtos.transaction;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SaveTransactionRequest {

    @NotBlank(message = "Please provide user id")
    private String userId;

    @NotNull(message = "Please provide the amount")
    private Double amount;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @Nullable
    private String description;

    @NotNull(message = "Timestamp is needed")
    private Instant timestamp;

    @NotBlank(message = "Source can not be blank")
    private String source;
}
