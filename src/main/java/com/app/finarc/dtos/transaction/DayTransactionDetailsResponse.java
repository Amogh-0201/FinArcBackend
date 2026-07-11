package com.app.finarc.dtos.transaction;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DayTransactionDetailsResponse {

    private List<TransactionResponse> content;

    private LocalDate date;
}
