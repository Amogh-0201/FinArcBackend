package com.app.finarc.dtos.transaction;


import com.app.finarc.models.TransactionCategory;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DayStatsSummaryResponse {

    private Double totalSpentThatDay;

    Map<TransactionCategory, Double> categoryBreakdown;
}
