package com.app.finarc.services;


import com.app.finarc.dtos.transaction.SaveTransactionRequest;
import com.app.finarc.dtos.transaction.UpdateTransactionRequest;
import com.app.finarc.models.Transaction;
import com.app.finarc.models.User;
import com.app.finarc.repositories.TransactionRepository;
import com.app.finarc.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    public TransactionService (TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Transaction saveTransaction(SaveTransactionRequest req) {

        Optional<User> user = userRepository.findById(req.getUserId());
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


    public Transaction getTransactionById(String transactionId) {

        return transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found, invalid transaction id: " + transactionId)
                );
    }


    public Transaction updateTransaction(UpdateTransactionRequest req, String transactionId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found, invalid transaction id: " + transactionId)
                );

        if(req.getAmount() != null) {
            transaction.setAmount(req.getAmount());
        }
        if(req.getCategory() != null) {
            transaction.setCategory(req.getCategory());
        }
        if(req.getDescription() != null) {
            transaction.setDescription(req.getDescription());
        }

        return transactionRepository.save(transaction);
    }


    public void deleteTransactionById(String transactionId) {

        Optional<Transaction> transaction = transactionRepository.findById(transactionId);

        if(transaction.isEmpty()) {
            throw new TransactionNotFoundException("Transaction not found, invalid transaction id: " + transactionId);
        }

        transactionRepository.delete(transaction.get());
    }


    public static final class TransactionNotFoundException extends RuntimeException {
        public TransactionNotFoundException(String message) {
            super(message);
        }
    }

}
