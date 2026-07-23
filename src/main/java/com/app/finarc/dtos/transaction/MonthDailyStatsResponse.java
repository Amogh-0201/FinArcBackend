package com.app.finarc.dtos.transaction;


import com.app.finarc.models.TransactionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthDailyStatsResponse {

    private LocalDate date;
    private Double totalSpentThatDay;
    private Map<TransactionCategory, Double> categoryBreakdown;
}
