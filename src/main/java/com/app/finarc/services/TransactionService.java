package com.app.finarc.services;


import com.app.finarc.dtos.transaction.*;
import com.app.finarc.models.Transaction;
import com.app.finarc.models.TransactionCategory;
import com.app.finarc.models.User;
import com.app.finarc.repositories.TransactionRepository;
import com.app.finarc.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    public TransactionService (TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Transaction saveTransaction(SaveTransactionRequest req, String userId) {

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new UserService.UserNotFoundException("User not found, invalid user id");
        }

        Transaction transaction = Transaction.builder()
                .userId(user.get().getId())
                .amount(req.getAmount())
                .category(req.getCategory())
                .description(req.getDescription())
                .timestamp(req.getTimestamp())
                .source(req.getSource())
                .build();

        return transactionRepository.save(transaction);
    }


    public Transaction getTransactionById(String transactionId, String userId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found, invalid transaction id: " + transactionId)
                );

        if (!transaction.getUserId().equals(userId)) {
            throw new AccessDeniedException("Cannot access data! invalid ownership");
        }

        return transaction;
    }


    public Transaction updateTransaction(UpdateTransactionRequest req, String transactionId, String userId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found, invalid transaction id: " + transactionId)
                );

        if (!transaction.getUserId().equals(userId)) {
            throw new AccessDeniedException("Cannot access data! invalid ownership");
        }

        if(req.getAmount() != null) {
            transaction.setAmount(req.getAmount());
        }
        if(req.getCategory() != null) {
            transaction.setCategory(req.getCategory());
        }
        if(req.getDescription() != null) {
            transaction.setDescription(req.getDescription());
        }
        if(req.getTimestamp() != null) {
            transaction.setTimestamp(req.getTimestamp());
        }

        return transactionRepository.save(transaction);
    }


    public void deleteTransactionById(String transactionId, String userId) {

        Optional<Transaction> transaction = transactionRepository.findById(transactionId);

        if(transaction.isEmpty()) {
            throw new TransactionNotFoundException("Transaction not found, invalid transaction id: " + transactionId);
        }

        if(!transaction.get().getUserId().equals(userId)) {
            throw new AccessDeniedException("Cannot access data! invalid ownership");
        }

        transactionRepository.delete(transaction.get());
    }


    public Page<Transaction> getUserTransactions(String userId, int pageNumber) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserService.UserNotFoundException("User not found, invalid user id: " + userId)
                );

        Pageable pageable = PageRequest.of(
                pageNumber,
                20,
                Sort.by(Sort.Direction.DESC, "timestamp")
        );

        return transactionRepository.findByUserId(user.getId(), pageable);
    }


    public MonthStatsSummaryResponse getMonthStats(String userId, int year, int month) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserService.UserNotFoundException("User not found, invalid user id: " + userId)
                );

        YearMonth yearMonth = YearMonth.of(year, month);

        Instant startOfMonth = yearMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999).toInstant(ZoneOffset.UTC);

        List<Transaction> transactions = transactionRepository.findByUserIdAndTimestampBetween(user.getId(), startOfMonth, endOfMonth);

        Double totalSpent = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        Map<TransactionCategory, Double> breakdown = transactions.stream()
                .collect(Collectors.groupingBy(
                   Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        String monthPeriod = String.format("%d/%02d", year, month);

        return MonthStatsSummaryResponse.builder()
                .month(monthPeriod)
                .totalSpentThisMonth(totalSpent)
                .budgetThreshold(user.getMonthlyBudgetThreshold())
                .categoryBreakdown(breakdown)
                .build();
    }


    public List<MonthTransactionHistoryResponse> getMonthTransactionHistory(String userId, int month, int year) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserService.UserNotFoundException("User not found, invalid user id: " + userId)
                );

        LocalDate startLocalDate = LocalDate.of(year, month, 1);
        int lengthOfMonth = startLocalDate.lengthOfMonth();
        LocalDate endLocalDate = LocalDate.of(year, month, lengthOfMonth);

        Instant startInstant = startLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endInstant = endLocalDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();

        List<Transaction> transactions = transactionRepository.findByUserIdAndTimestampBetween(user.getId(), startInstant, endInstant);

        Map<LocalDate, Double> spendingPerDay = transactions.stream()
                .collect(Collectors.groupingBy(
                   t -> t.getTimestamp().atZone(ZoneOffset.UTC).toLocalDate(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        List<MonthTransactionHistoryResponse> history = new ArrayList<>();

        for(int day = 1; day<=lengthOfMonth; day++) {
            LocalDate currentDate = LocalDate.of(year, month, day);
            Double totalSpent = spendingPerDay.getOrDefault(currentDate, 0.0);

            history.add(MonthTransactionHistoryResponse.builder()
                    .date(currentDate)
                    .totalSpent(totalSpent)
                    .build());
        }

        return history;
    }


    public DayStatsSummaryResponse getDayStats(String userId, int year, int month, int day) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserService.UserNotFoundException("User not found, invalid user id: " + userId)
                );

        LocalDate targetDate = LocalDate.of(year, month, day);
        Instant startOfDay = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = targetDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();

        List<Transaction> transactions = transactionRepository.findByUserIdAndTimestampBetween(user.getId(), startOfDay, endOfDay);

        Double totalSpent = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        Map<TransactionCategory, Double> breakDown = transactions.stream()
                .collect(Collectors.groupingBy(
                   Transaction::getCategory,
                   Collectors.summingDouble(Transaction::getAmount)
                ));

        return DayStatsSummaryResponse.builder()
                .totalSpentThatDay(totalSpent)
                .categoryBreakdown(breakDown)
                .build();
    }


    public DayTransactionDetailsResponse getDayTransactionDetail(String userId, int year, int month, int day) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserService.UserNotFoundException("User not found, invalid user id: " + userId)
                );

        LocalDate targetDate = LocalDate.of(year, month, day);
        Instant startOfDay = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = targetDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();

        List<Transaction> transactions = transactionRepository.findByUserIdAndTimestampBetween(user.getId(), startOfDay, endOfDay);

        List<TransactionResponse> mappedContent = transactions.stream()
                .map(t -> TransactionResponse.builder()
                        .id(t.getId())
                        .amount(t.getAmount())
                        .category(t.getCategory())
                        .description(t.getDescription())
                        .timestamp(t.getTimestamp())
                        .source(t.getSource())
                        .build())
                .toList();

        return DayTransactionDetailsResponse.builder()
                .content(mappedContent)
                .date(targetDate)
                .build();
    }


    public static final class TransactionNotFoundException extends RuntimeException {
        public TransactionNotFoundException(String message) {
            super(message);
        }
    }

}
