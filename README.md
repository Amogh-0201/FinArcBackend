# FinArc - Smart Expense Tracker

An end-to-end ecosystem integrating an Android client with a Spring Boot backend and an auxiliary Python ML service to deliver predictive financial analytics.

---

## 💾 Database Schemas (MongoDB)

### 1. Users Collection
Stores user profile information and monthly budgeting thresholds.
```json
{
  "_id": "ObjectId",
  "username": "String",
  "email": "String",
  "password": "Hashed String",
  "monthlyBudgetThreshold": "Double",
  "currency": "String (e.g., INR, USD)",
  "createdAt": "ISODate"
}
```

### 2. Transactions Collection
Stores every individual expense logged by the user.
```json
{
  "_id": "ObjectId",
  "userId": "String (References Users._id)",
  "amount": "Double",
  "category": "String (Food, Shopping, Travel, Bills, Entertainment, Other)",
  "description": "String",
  "timestamp": "ISODate",
  "source": "String (MANUAL | SMS)"
}
```

---

## 🔌API Endpoints Blueprint

### N.P - Implemented jwt authentication

---
### _<ins>User Related</ins>_

### 1. Create/Register User
    POST /api/users
##### request JSON body
```json
{
  "username": "String",
  "email": "String",
  "password": "String",
  "monthlyBudgetThreshold (Optional)": "Double",
  "currency (Optional)": "INR | USD | Other etc"
}
```

### 2. Login User
    POST /api/users/login
##### request JSON body
```json
{
  "username": "String",
  "password": "String"
}
```

###  3. Get Particular User Info
    GET /api/users

### 4. Update User Info
    PATCH /api/users
##### request JSON body
```json
{
  "username (Optional)": "String",
  "monthlyBudgetThreshold (Optional)": "Double",
  "currency (Optional)": "INR | USD | Other etc"
}
```

### 5. Delete User
    DELETE /api/users

---

### _<ins>Transactions Related</ins>_

### 6. Save A New Transaction
    POST /api/transactions
##### request JSON body
```json
{
  "amount": "Double",
  "category": "String (Food, Shopping, Travel, Bills, Entertainment, Other)",
  "description": "String",
  "timestamp": "2026-07-09T15:30:00Z",
  "source": "String (MANUAL | SMS)"
}
```

### 7. Get A Transaction Details
    GET /api/transactions/{transactionId}

### 8. Update A Transaction Details
    PATCH /api/transactions/{transactionId}
##### request JSON body
```json
{
  "amount": "Double",
  "category": "String (Food, Shopping, Travel, Bills, Entertainment, Other)",
  "description": "String"
}
```

### 9. Delete A Transaction Details
    DELETE /api/transactions/{transactionId}

### 10. Get all Transactions for a particular user (pagination - 20 at a time sorted by recent date)
    GET /api/transactions/users?page=2
##### response json

```json
{
    "content": [
        {
            "id": "6a52011b6db66896b459c181",
            "amount": 550.5,
            "category": "EDUCATION",
            "description": "4th semester exam fees",
            "timestamp": "2026-08-10T16:30:00Z",
            "source": "MANUAL"
        },
        {
            "id": "6a5205a9e4e9297e34856f66",
            "amount": 150.0,
            "category": "FOOD_AND_DINING",
            "description": "Lunch",
            "timestamp": "2026-07-12T16:30:00Z",
            "source": "MANUAL"
        }
    ],
    "page": {
        "size": 20,
        "number": 0,
        "totalElements": 2,
        "totalPages": 1
    }
}
```

### 11. Get Current Month Stats Summary
    GET /api/transactions/users/stats
##### response JSON
```json
{
  "totalSpentThisMonth": 12450.00,
  "budgetThreshold": 50000.00,
  "categoryBreakdown": {
    "Food": 4500.00,
    "Travel": 3200.00,
    "Shopping": 4750.00
  }
}
```

### 12. Get any particular Month's Transaction history for every date
    GET /api/transactions/users/{year}/{month_no}
##### JSON response for every date starting from the first date of the ongoing month
```json
[
  { "date": "2026-07-01", "totalSpent": 450.00 },
  { "date": "2026-07-02", "totalSpent": 1200.00 }
]
```

### 13. Get Transaction Stats Summary For A Particular Day
    GET /api/transactions/users/{year}/{month_no}/{day}/stats
##### response JSON
```json
{
  "totalSpentThatDay": "Double",
  "categoryBreakdown": {
    "Food": 4500.00,
    "Travel": 3200.00,
    "Shopping": 4750.00
  }
}
```

### 14. Get Transaction Details For A Particular Day
    /api/transactions/users/{year}/{month_no}/{day}
##### response JSON
```json
{
  "content": [
    {
      "id": "6a52011b6db66896b459c181",
      "amount": 550.5,
      "category": "EDUCATION",
      "description": "4th semester exam fees",
      "timestamp": "2026-07-10T16:30:00Z",
      "source": "MANUAL"
    },
    {
      "id": "6a5205a9e4e9297e34856f66",
      "amount": 150.0,
      "category": "FOOD_AND_DINING",
      "description": "Lunch",
      "timestamp": "2026-07-10T16:30:00Z",
      "source": "MANUAL"
    }
  ],
  "date": "2026-07-10"
}
```


---

### _<ins>AI ML Related</ins>_

### 15. Fetch Predictive Analysis (Not now)
    GET /api/analytics/predict

---
## Important points:
### Allowed Transaction Categories
```
    FOOD_AND_DINING,        // Groceries, Restaurants, Cafés, Delivery
    SHOPPING,               // Apparel, Electronics, Accessories, Cosmetics
    TRAVEL_AND_TRANSPORT,   // Fuel, Public Transit, Cabs, Flights, Parking
    BILLS_AND_UTILITIES,    // Rent, Electricity, Water, Internet, Phone, Subscriptions
    ENTERTAINMENT,          // Movies, Concerts, Gaming, Hobbies, Events
    HEALTH_AND_MEDICAL,     // Pharmacy, Doctor visits, Gym memberships, Insurance
    EDUCATION,              // Tuition, Courses, Books, Certifications
    INVESTMENT_AND_SAVING,  // Stocks, Crypto, Mutual Funds, Savings deposits
    SALARY_AND_INCOME,      // Paychecks, Freelance work, Cash gifts
    OTHER                   // Generalized fallback for anything else
```

### Allowed Currencies
```
    INR, // Indian Rupee
    USD, // US Dollar
    EUR, // Euro
    GBP, // British Pound Sterling
    AED, // UAE Dirham
    SAR, // Saudi Riyal
    CAD, // Canadian Dollar
    AUD, // Australian Dollar
    SGD, // Singapore Dollar
    JPY  // Japanese Yen
```

### Allowed Transaction sources
```
    MANUAL, 
    SMS
```