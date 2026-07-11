package com.app.finarc.models;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionCategory {
    FOOD_AND_DINING,        // Groceries, Restaurants, Cafés, Delivery
    SHOPPING,               // Apparel, Electronics, Accessories, Cosmetics
    TRAVEL_AND_TRANSPORT,   // Fuel, Public Transit, Cabs, Flights, Parking
    BILLS_AND_UTILITIES,    // Rent, Electricity, Water, Internet, Phone, Subscriptions
    ENTERTAINMENT,          // Movies, Concerts, Gaming, Hobbies, Events
    HEALTH_AND_MEDICAL,     // Pharmacy, Doctor visits, Gym memberships, Insurance
    EDUCATION,              // Tuition, Courses, Books, Certifications
    INVESTMENT_AND_SAVING,  // Stocks, Crypto, Mutual Funds, Savings deposits
    SALARY_AND_INCOME,      // Paychecks, Freelance work, Cash gifts
    OTHER;                  // Generalized fallback for anything else

    @JsonCreator
    public static TransactionCategory fromString(String value) {

        if(value == null) return null;
        try {
            return TransactionCategory.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown transaction category: " + value);
        }
    }
}
