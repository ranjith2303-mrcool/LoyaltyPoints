# Retail Customer Loyalty and Rewards Management System

A beginner-friendly Spring Boot backend REST API application developed for retail stores to manage customer loyalty points, automatic membership tier upgrades, tier-based purchase discounts, point redemptions, and point transaction history.

---

## Table of Contents

- [Project Title & Description](#project-title--description)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Database Setup & Configuration](#database-setup--configuration)
- [Business Rules](#business-rules)
- [How to Run the Application](#how-to-run-the-application)
- [REST API Endpoints](#rest-api-endpoints)
- [Postman Testing Guide & Examples](#postman-testing-guide--examples)
- [Project Structure](#project-structure)

---

## Project Title & Description

**Project Title:** Retail Customer Loyalty and Rewards Management System  
**Type:** Backend REST API Application

### Real-World Scenario
A retail chain wants to reward repeat customers with loyalty points and membership tiers. Currently, there is no system to calculate points, track customer point history, automatically upgrade customer tiers, apply tier-based discounts, or redeem points for discounts. This Spring Boot system solves all of these problems automatically during purchase processing.

---

## Key Features

1. **Customer Management:** Full CRUD operations for loyalty program customers.
2. **Dynamic Membership Tiers:** Configurable loyalty tiers (e.g., Silver, Gold, Platinum) with customizable minimum point thresholds and discount percentages stored in MySQL database.
3. **Automated Purchase Processing:**
   - Automatic tier discount calculation based on customer's current membership tier.
   - Point redemption for instant price discounts.
   - Points earned calculation for every valid purchase.
   - Validation preventing redemption beyond available points.
   - Validation ensuring final amount cannot be negative.
4. **Automatic Tier Upgrades:** Customer tiers automatically upgrade as lifetime points cross tier thresholds.
5. **Non-Downgrade Guarantee:** Redeeming points reduces available balance but **never** reduces lifetime points or downgrades the tier.
6. **Complete Points Ledger:** Full historical transaction tracking (EARNED and REDEEMED entries) for audit and history view.
7. **Customer Loyalty Summary:** Endpoint to view current points, tier status, discount percentage, and progress towards the next tier.
8. **Global Exception Handling:** Clean JSON error messages with standard HTTP status codes (200, 201, 400, 404, 500).

---

## Technology Stack

- **Java Version:** Java 17
- **Framework:** Spring Boot 3.2.x (Spring MVC / Spring Web)
- **Data Access:** Spring Data JPA / Hibernate
- **Database:** MySQL 8.0
- **Validation:** Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`, `@Min`, `@Max`, `@Email`)
- **Build Tool:** Apache Maven
- **IDE:** IntelliJ IDEA
- **Embedded Web Server:** Tomcat (port 8080)
- **Testing:** JUnit 5, Postman

---

## Architecture

The system follows a clean layered architecture:

```
Postman / REST Client
         │
         ▼
 Controller Layer (com.example.retailloyalty.controller)
         │
         ▼
   Service Layer   (com.example.retailloyalty.service)
         │
         ▼
 Repository Layer  (com.example.retailloyalty.repository)
         │
         ▼
  MySQL Database   (retail_loyalty_db)
```

---

## Database Setup & Configuration

### 1. MySQL Setup
Ensure MySQL Server is running on `localhost:3306`. The JDBC URL includes `createDatabaseIfNotExist=true`, so the database is created automatically when the configured MySQL user has permission to create databases. You can also run the included `database_setup.sql` manually.

### 2. Spring Boot Properties (`src/main/resources/application.properties`)

```properties
server.port=8080

# Database Connection
spring.datasource.url=jdbc:mysql://localhost:3306/retail_loyalty_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Loyalty System Config
loyalty.points-per-rupee=1.0
loyalty.redemption-rate-per-point=1.0
```

*Note: Change `spring.datasource.username` and `spring.datasource.password` to match your local MySQL installation if needed.*

---

## Business Rules

- **Rule 1: Award Points**  
  Earned points = `purchaseAmount * pointsPerRupee` (1 point per ₹1 spent).

- **Rule 2: Lifetime Points**  
  `lifetimePoints = lifetimePoints + earnedPoints`. Lifetime points track total lifetime earnings and are **never** reduced by point redemptions.

- **Rule 3: Available Points**  
  `availablePoints = availablePoints - pointsRedeemed + earnedPoints`. Tracks spendable points balance.

- **Rule 4: Automatic Tier Upgrade**  
  When `lifetimePoints` reaches or exceeds a tier's `minimumPoints` threshold, the customer is upgraded immediately:
  - **Silver:** 0 points (0% discount)
  - **Gold:** 1,000 points (10% discount)
  - **Platinum:** 5,000 points (20% discount)  
  *Redeeming points NEVER downgrades a customer.*

- **Rule 5: Tier Discount**  
  Tier discount is automatically applied to new purchases based on the customer's current tier.
  `tierDiscount = purchaseAmount * (tierDiscountPercentage / 100)`

- **Rule 6: Point Redemption**  
  Customers can redeem available points for price discount. `pointsToRedeem` cannot exceed `availablePoints`. Requests exceeding available points are rejected with a `400 BAD REQUEST` error.

- **Rule 7: Purchase Total**  
  `finalAmount = purchaseAmount - tierDiscount - redemptionDiscount`. Final amount cannot be negative.

---

## How to Run the Application

### Option 1: In IntelliJ IDEA
1. Open IntelliJ IDEA.
2. Select **File > Open** and choose the `d:\loyality_points` project folder.
3. Wait for Maven dependencies to sync.
4. Locate `src/main/java/com/example/retailloyalty/RetailLoyaltyApplication.java`.
5. Right-click `RetailLoyaltyApplication.java` and click **Run 'RetailLoyaltyApplication'**.

### Option 2: Using Maven Command Line
Open PowerShell or Command Prompt in the project folder and run:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

---

## REST API Endpoints

### 1. Customer Endpoints (`/api/customers`)
| HTTP Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/customers` | Register a new customer |
| `GET` | `/api/customers` | Retrieve all customers |
| `GET` | `/api/customers/{id}` | Get customer details by ID |
| `PUT` | `/api/customers/{id}` | Update customer information |
| `DELETE` | `/api/customers/{id}` | Delete customer by ID |
| `GET` | `/api/customers/{customerId}/loyalty` | View customer loyalty summary & tier progress |

### 2. Tier Endpoints (`/api/tiers`)
| HTTP Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/tiers` | Create a new loyalty tier |
| `GET` | `/api/tiers` | List all tiers ordered by minimum points |
| `GET` | `/api/tiers/{id}` | Get tier details by ID |
| `PUT` | `/api/tiers/{id}` | Update tier threshold or discount |
| `DELETE` | `/api/tiers/{id}` | Delete tier by ID |

### 3. Purchase Endpoints (`/api/purchases`)
| HTTP Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/purchases` | Process a new purchase (calculates discounts & awards points) |
| `GET` | `/api/purchases/{id}` | Get purchase details by ID |
| `GET` | `/api/customers/{customerId}/purchases` | Get all purchases made by a customer |

### 4. Points History Endpoint (`/api/customers/{customerId}/points-history`)
| HTTP Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/customers/{customerId}/points-history` | View customer points ledger (EARNED / REDEEMED entries) |

---

## Postman Testing Guide & Examples

### Scenario 1: Create a Customer
**HTTP Method:** `POST`  
**URL:** `http://localhost:8080/api/customers`  
**Headers:** `Content-Type: application/json`  
**Request Body:**
```json
{
  "name": "Raj Kumar",
  "email": "raj.kumar@example.com",
  "phone": "9876543210"
}
```
**Expected Response (`201 CREATED`):**
```json
{
  "id": 1,
  "name": "Raj Kumar",
  "email": "raj.kumar@example.com",
  "phone": "9876543210",
  "lifetimePoints": 0,
  "availablePoints": 0,
  "currentTierId": 1,
  "currentTierName": "Silver",
  "tierDiscountPercentage": 0.0,
  "createdAt": "2026-09-28T12:00:00"
}
```

---

### Scenario 2: First Purchase (Qualifies for Auto-Upgrade to Gold)
**HTTP Method:** `POST`  
**URL:** `http://localhost:8080/api/purchases`  
**Request Body:**
```json
{
  "customerId": 1,
  "purchaseAmount": 1000.0,
  "pointsToRedeem": 0
}
```
**Expected Response (`201 CREATED`):**
```json
{
  "purchaseId": 1,
  "customerId": 1,
  "customerName": "Raj Kumar",
  "purchaseAmount": 1000.0,
  "tierName": "Silver",
  "tierDiscountPercentage": 0.0,
  "tierDiscount": 0.0,
  "pointsRedeemed": 0,
  "redemptionDiscount": 0.0,
  "finalAmount": 1000.0,
  "pointsEarned": 1000,
  "previousLifetimePoints": 0,
  "updatedLifetimePoints": 1000,
  "previousAvailablePoints": 0,
  "updatedAvailablePoints": 1000,
  "tierUpgraded": true,
  "previousTierName": "Silver",
  "newTierName": "Gold",
  "purchaseDate": "2026-09-28T12:05:00"
}
```

---

### Scenario 3: Second Purchase (Uses 10% Gold Tier Discount & Redeems Points)
**HTTP Method:** `POST`  
**URL:** `http://localhost:8080/api/purchases`  
**Request Body:**
```json
{
  "customerId": 1,
  "purchaseAmount": 2000.0,
  "pointsToRedeem": 100
}
```
**Expected Response (`201 CREATED`):**
```json
{
  "purchaseId": 2,
  "customerId": 1,
  "customerName": "Raj Kumar",
  "purchaseAmount": 2000.0,
  "tierName": "Gold",
  "tierDiscountPercentage": 10.0,
  "tierDiscount": 200.0,
  "pointsRedeemed": 100,
  "redemptionDiscount": 100.0,
  "finalAmount": 1700.0,
  "pointsEarned": 2000,
  "previousLifetimePoints": 1000,
  "updatedLifetimePoints": 3000,
  "previousAvailablePoints": 1000,
  "updatedAvailablePoints": 2900,
  "tierUpgraded": false,
  "previousTierName": "Gold",
  "newTierName": "Gold",
  "purchaseDate": "2026-09-28T12:10:00"
}
```

---

### Scenario 4: Test Excessive Point Redemption Rejection
**HTTP Method:** `POST`  
**URL:** `http://localhost:8080/api/purchases`  
**Request Body:**
```json
{
  "customerId": 1,
  "purchaseAmount": 500.0,
  "pointsToRedeem": 99999
}
```
**Expected Response (`400 BAD REQUEST`):**
```json
{
  "status": 400,
  "message": "Insufficient available points for redemption. Available: 2900 points, requested: 99999 points",
  "timestamp": "2026-09-28T12:15:00",
  "details": null
}
```

---

### Scenario 5: View Customer Loyalty Summary
**HTTP Method:** `GET`  
**URL:** `http://localhost:8080/api/customers/1/loyalty`  
**Expected Response (`200 OK`):**
```json
{
  "customerId": 1,
  "customerName": "Raj Kumar",
  "customerEmail": "raj.kumar@example.com",
  "lifetimePoints": 3000,
  "availablePoints": 2900,
  "currentTierName": "Gold",
  "tierDiscountPercentage": 10.0,
  "nextTierName": "Platinum",
  "pointsNeededForNextTier": 2000
}
```

---

### Scenario 6: View Customer Points History
**HTTP Method:** `GET`  
**URL:** `http://localhost:8080/api/customers/1/points-history`  
**Expected Response (`200 OK`):**
```json
[
  {
    "id": 3,
    "customerId": 1,
    "purchaseId": 2,
    "transactionType": "EARNED",
    "points": 2000,
    "description": "Earned 2000 points from purchase #2",
    "createdAt": "2026-09-28T12:10:00"
  },
  {
    "id": 2,
    "customerId": 1,
    "purchaseId": 2,
    "transactionType": "REDEEMED",
    "points": 100,
    "description": "Redeemed 100 points for discount on purchase #2",
    "createdAt": "2026-09-28T12:10:00"
  },
  {
    "id": 1,
    "customerId": 1,
    "purchaseId": 1,
    "transactionType": "EARNED",
    "points": 1000,
    "description": "Earned 1000 points from purchase #1",
    "createdAt": "2026-09-28T12:05:00"
  }
]
```

---

## Project Structure

```
d:\loyality_points
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── retailloyalty
    │   │               ├── RetailLoyaltyApplication.java
    │   │               ├── config
    │   │               │   └── DataInitializer.java
    │   │               ├── controller
    │   │               │   ├── CustomerController.java
    │   │               │   ├── PointsLedgerController.java
    │   │               │   ├── PurchaseController.java
    │   │               │   └── TierController.java
    │   │               ├── dto
    │   │               │   ├── CustomerLoyaltySummaryDto.java
    │   │               │   ├── CustomerRequestDto.java
    │   │               │   ├── CustomerResponseDto.java
    │   │               │   ├── ErrorResponseDto.java
    │   │               │   ├── PointsLedgerResponseDto.java
    │   │               │   ├── PurchaseRequestDto.java
    │   │               │   ├── PurchaseResponseDto.java
    │   │               │   ├── TierRequestDto.java
    │   │               │   └── TierResponseDto.java
    │   │               ├── exception
    │   │               │   ├── GlobalExceptionHandler.java
    │   │               │   ├── InsufficientPointsException.java
    │   │               │   ├── InvalidPurchaseException.java
    │   │               │   ├── InvalidTierException.java
    │   │               │   └── ResourceNotFoundException.java
    │   │               ├── model
    │   │               │   ├── Customer.java
    │   │               │   ├── PointsLedger.java
    │   │               │   ├── Purchase.java
    │   │               │   ├── Tier.java
    │   │               │   └── TransactionType.java
    │   │               ├── repository
    │   │               │   ├── CustomerRepository.java
    │   │               │   ├── PointsLedgerRepository.java
    │   │               │   ├── PurchaseRepository.java
    │   │               │   └── TierRepository.java
    │   │               └── service
    │   │                   ├── CustomerService.java
    │   │                   ├── PointsLedgerService.java
    │   │                   ├── PurchaseService.java
    │   │                   └── TierService.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── example
                    └── retailloyalty
                        ├── LoyaltyServiceTest.java
                        └── RetailLoyaltyApplicationTests.java
```

## Browser UI

A simple frontend is included under `src/main/resources/static`. After starting the application, open:

`http://localhost:8080/`

The page uses HTML, CSS and JavaScript and calls the Spring Boot REST APIs directly.
