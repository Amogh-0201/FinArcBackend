package com.app.finarc.models;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionSource {
    MANUAL,
    SMS;

    @JsonCreator
    public static TransactionCategory fromString(String value) {

        if(value == null) return null;
        try {
            return TransactionCategory.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown transaction source: " + value);
        }
    }
}
