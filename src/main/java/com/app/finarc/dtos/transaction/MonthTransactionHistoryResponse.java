package com.app.finarc.dtos.transaction;


import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MonthTransactionHistoryResponse {

    private LocalDate date;

    private Double totalSpent;
}
