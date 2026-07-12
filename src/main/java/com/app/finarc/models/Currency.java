package com.app.finarc.models;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Currency {
    INR, // Indian Rupee
    USD, // US Dollar
    EUR, // Euro
    GBP, // British Pound Sterling
    AED, // UAE Dirham
    SAR, // Saudi Riyal
    CAD, // Canadian Dollar
    AUD, // Australian Dollar
    SGD, // Singapore Dollar
    JPY;  // Japanese Yen


    @JsonCreator
    public static Currency fromString(String value) {

        if(value == null || value.trim().isEmpty()) {
            return INR;
        }

         try {
             return valueOf(value.toUpperCase().trim());
         } catch (IllegalArgumentException e) {
             throw new IllegalArgumentException("invalid currency value");
         }
    }
}
