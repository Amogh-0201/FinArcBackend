package com.app.finarc.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
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
    private String category; // TODO: make different pre made categories Food, Shopping, Travel, Bills, etc.

    private String description;

    @NonNull
    private Instant timestamp;

    @NonNull
    private String source; // TODO: set only these two options MANUAL | SMS

}
