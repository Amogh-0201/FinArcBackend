package com.app.finarc.controllers;


import com.app.finarc.dtos.common.ExceptionDto;
import com.app.finarc.dtos.transaction.*;
import com.app.finarc.models.Transaction;
import com.app.finarc.services.TransactionService;
import com.app.finarc.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.access.AccessDeniedException;


import java.net.URI;
import java.time.DateTimeException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final ModelMapper modelMapper;
    public TransactionController(TransactionService transactionService, ModelMapper modelMapper) {
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("")
    public ResponseEntity<TransactionResponse> saveTransaction(
            @RequestBody @Valid SaveTransactionRequest req,
            @AuthenticationPrincipal String userId
    ) {

        Transaction transaction = transactionService.saveTransaction(req, userId);

        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String transactionId,
            @AuthenticationPrincipal String userId
    ) {

        Transaction transaction = transactionService.getTransactionById(transactionId, userId);

        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable String transactionId,
            @RequestBody @Valid UpdateTransactionRequest req,
            @AuthenticationPrincipal String userId
    ) {
        Transaction transaction = transactionService.updateTransaction(req, transactionId, userId);

        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<DeleteTransactionResponse> deleteTransaction(
            @PathVariable String transactionId,
            @AuthenticationPrincipal String userId
    ) {

        transactionService.deleteTransactionById(transactionId, userId);

        DeleteTransactionResponse response = new DeleteTransactionResponse();
        response.setMessage("Transaction has been deleted successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<TransactionResponse>> getUserTransactions (
            @AuthenticationPrincipal  String userId,
            @RequestParam(defaultValue = "0") int pageNumber
    ) {

        Page<Transaction> transactionPage = transactionService.getUserTransactions(userId, pageNumber);

        Page<TransactionResponse> response = transactionPage.map( transaction ->
            modelMapper.map(transaction, TransactionResponse.class)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/stats")
    public ResponseEntity<MonthStatsSummaryResponse> getCurrentMonthStats(@AuthenticationPrincipal String userId) {

        MonthStatsSummaryResponse response = transactionService.getCurrentMonthStats(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{year}/{month_no}")
    public ResponseEntity<List<MonthTransactionHistoryResponse>> getMonthTransactionHistory(
            @AuthenticationPrincipal String userId,
            @PathVariable int year,
            @PathVariable int month_no
    ) {
        List<MonthTransactionHistoryResponse> response = transactionService.getMonthTransactionHistory(userId, month_no, year);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{year}/{month_no}/{day}/stats")
    public ResponseEntity<DayStatsSummaryResponse> getDayStatsSummary(
            @AuthenticationPrincipal String userId,
            @PathVariable int year,
            @PathVariable int month_no,
            @PathVariable int day
    ) {
        DayStatsSummaryResponse response = transactionService.getDayStats(userId, year, month_no, day);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{year}/{month_no}/{day}")
    public ResponseEntity<DayTransactionDetailsResponse> getDayTransactionDetails(
            @AuthenticationPrincipal String userId,
            @PathVariable int year,
            @PathVariable int month_no,
            @PathVariable int day
    ) {
        DayTransactionDetailsResponse response = transactionService.getDayTransactionDetail(userId, year, month_no, day);

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleException(Exception e) {
        String message;
        HttpStatus statusCode;

        if(e instanceof UserService.UserNotFoundException ||
                e instanceof TransactionService.TransactionNotFoundException
        ) {

            message = e.getMessage();
            statusCode = HttpStatus.BAD_REQUEST;

        } else if(e instanceof MethodArgumentNotValidException validEx) {

            message = validEx.getBindingResult().getFieldErrors().stream()
                    .map(org.springframework.validation.FieldError::getDefaultMessage)
                    .collect(java.util.stream.Collectors.joining(", "));
            statusCode = HttpStatus.BAD_REQUEST;

        } else if(e instanceof DateTimeException) {

            message = "invalid date or time format";
            statusCode = HttpStatus.BAD_REQUEST;

        } else if(e instanceof AccessDeniedException) {

            message = e.getMessage();
            statusCode = HttpStatus.FORBIDDEN;

        } else {
            message = e.getMessage();
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(message);

        return ResponseEntity.status(statusCode).body(exceptionDto);
    }

}
