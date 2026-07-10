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
    GET /api/users/{userId}

### 4. Update User Info
    PATCH api/users/{userId}
##### request JSON body
```json
{
  "username (Optional)": "String",
  "monthlyBudgetThreshold (Optional)": "Double",
  "currency (Optional)": "INR | USD | Other etc"
}
```

### 5. Delete User
    DELETE api/users/{userId}

---

### _<ins>Transactions Related</ins>_

### 6. Save A New Transaction
    POST /api/transactions
##### request JSON body
```json
{
  "userId": "String (References Users._id)",
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
    GET /api/transactions/users/{userId}

### 11. Get Current Month Stats Summary
    GET /api/transactions/users/{userId}/stats
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

### 12. Get Only particular Month's Transaction history for each date
    GET /api/transactions/{userId}/{year}/{month_no}
##### JSON response for every date starting from the first date of the ongoing month
```json
[
  { "date": "2026-07-01", "totalSpent": 450.00 },
  { "date": "2026-07-02", "totalSpent": 1200.00 }
]
```

### 13. Get Transaction Info For A Particular Day
    GET /api/transactions/{userId}/{year}/{month-no}/{day}
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

---

### _<ins>AI ML Related</ins>_

### 14. Fetch Predictive Analysis (Not now)
    GET /api/analytics/{userId}/predict
