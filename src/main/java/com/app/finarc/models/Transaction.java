package com.app.finarc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    @Indexed
    @NonNull
    private String userId;

    @NonNull
    private Double amount;

    @NonNull
    private String category; // Food, Shopping, Travel, Bills, etc.

    private String description;

    @NonNull
    private Instant timestamp;

    @NonNull
    private String source; // MANUAL | SMS

}
