# LoanApplication

A Spring Boot REST API for CRUD operations on bank loans.

## Technology
- Java 17
- Spring Boot 3.5.4
- Spring Web
- Spring Data JPA / Hibernate
- H2 file-based database
- Jakarta Bean Validation
- Maven

## Run
Make sure Java 17+ and Maven are installed. From the project root:

```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. The H2 database is persisted in `./data/loandb`.

## Loan model
Each loan contains:
- `id` (generated)
- `borrowerName`
- `loanAccountNumber` (unique)
- `loanType` (for example PERSONAL, HOME, AUTO)
- `principalAmount`
- `annualInterestRate` (percentage, e.g. 8.50)
- `tenureMonths`
- `startDate` (ISO date: `yyyy-MM-dd`)
- `status` (`ACTIVE`, `CLOSED`, `DEFAULTED`, `PENDING`)

## REST endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/loans` | Create a loan |
| GET | `/api/loans` | Get all loans |
| GET | `/api/loans/{id}` | Get loan by ID |
| PUT | `/api/loans/{id}` | Update a loan |
| DELETE | `/api/loans/{id}` | Delete a loan |

## Example requests

### Create a loan
```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "borrowerName": "Aarav Sharma",
    "loanAccountNumber": "LN-10001",
    "loanType": "HOME",
    "principalAmount": 2500000.00,
    "annualInterestRate": 8.50,
    "tenureMonths": 240,
    "startDate": "2026-01-15",
    "status": "ACTIVE"
  }'
```

### Get all loans
```bash
curl http://localhost:8080/api/loans
```

### Get one loan
```bash
curl http://localhost:8080/api/loans/1
```

### Update a loan
Send the same JSON structure as create:
```bash
curl -X PUT http://localhost:8080/api/loans/1 \
  -H "Content-Type: application/json" \
  -d '{
    "borrowerName": "Aarav Sharma",
    "loanAccountNumber": "LN-10001",
    "loanType": "HOME",
    "principalAmount": 2400000.00,
    "annualInterestRate": 8.25,
    "tenureMonths": 228,
    "startDate": "2026-01-15",
    "status": "ACTIVE"
  }'
```

### Delete a loan
```bash
curl -X DELETE http://localhost:8080/api/loans/1
```

## H2 console
Open `http://localhost:8080/h2-console` and use:
- JDBC URL: `jdbc:h2:file:./data/loandb`
- Username: `sa`
- Password: leave blank

## Notes
- Request payloads are validated. Invalid input returns HTTP 400.
- Missing loan IDs return HTTP 404.
- Duplicate loan account numbers are rejected.
- This is a learning/demo project; add authentication, authorization, pagination, audit fields, and production database configuration before production use.
