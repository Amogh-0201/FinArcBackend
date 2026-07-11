package com.app.finarc.controllers;


import com.app.finarc.dtos.common.ExceptionDto;
import com.app.finarc.dtos.transaction.DeleteTransactionResponse;
import com.app.finarc.dtos.transaction.SaveTransactionRequest;
import com.app.finarc.dtos.transaction.TransactionResponse;
import com.app.finarc.dtos.transaction.UpdateTransactionRequest;
import com.app.finarc.models.Transaction;
import com.app.finarc.services.TransactionService;
import com.app.finarc.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
    public ResponseEntity<TransactionResponse> saveTransaction(@RequestBody @Valid SaveTransactionRequest req) {

        Transaction transaction = transactionService.saveTransaction(req);

        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String transactionId) {

        Transaction transaction = transactionService.getTransactionById(transactionId);

        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable String transactionId,
            @RequestBody @Valid UpdateTransactionRequest req
    ) {
        Transaction transaction = transactionService.updateTransaction(req, transactionId);

        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<DeleteTransactionResponse> deleteTransaction(@PathVariable String transactionId) {

        transactionService.deleteTransactionById(transactionId);

        DeleteTransactionResponse response = new DeleteTransactionResponse();
        response.setMessage("Transaction has been deleted successfully");

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

        } else {
            message = e.getMessage();
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(message);

        return ResponseEntity.status(statusCode).body(exceptionDto);
    }

}
