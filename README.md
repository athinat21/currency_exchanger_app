# Foreign Exchange Application

A simple, self-contained foreign exchange application designed to provide real-time currency exchange rates, perform currency conversions, and retrieve conversion history. This application is built following REST principles using Spring Boot and incorporates best practices such as containerization with Docker, unit testing, and API documentation.

---

## **Features**

### **1. Exchange Rate Endpoint**
- **Input**: A pair of currency codes (e.g., `USD` to `EUR`).
- **Output**: The current exchange rate between the two currencies.

### **2. Currency Conversion Endpoint**
- **Input**:
    - An amount in the source currency.
    - Source currency code.
    - Target currency code.
- **Output**:
    - The converted amount in the target currency.
    - A unique transaction identifier.

### **3. Conversion History Endpoint**
- **Input**:
    - A transaction identifier or a transaction date for filtering (at least one must be provided).
- **Output**:
    - A paginated list of currency conversions filtered by the provided criteria.

### **4. External Exchange Rate Integration**
- The application integrates with an external exchange rate service to fetch live rates and optionally perform conversion calculations.

### **5. Error Handling**
- Graceful error handling with meaningful error messages and specific error codes.

---

## **Technical Details**

### **Mandatory Technical Features**
1. **Self-Contained Application**:
    - Requires no additional setup or configuration to run.
    - Fully containerized using Docker for consistency across environments.

2. **RESTful API Design**:
    - Endpoints follow REST principles for a clean and scalable API.

3. **Build and Dependency Management**:
    - Uses Maven for project building and dependency management.

4. **Design Patterns**:
   1. Repository Pattern
         Purpose: Encapsulates data access logic, separating it from the business logic.
         Implementation:
         ExchangeRateRepository and TransactionRepository handle database interactions, ensuring that business logic in ExchangeRateService does not directly deal with the database.
         Benefits:
         Promotes separation of concerns.
         Easier to switch to a different data source or mock data in tests.
   2. Service Layer Pattern
   Purpose: Encapsulates business logic into reusable services.
   Implementation:
   The ExchangeRateService contains all the core business logic, such as fetching exchange rates, converting currencies, and managing transactions.
   Benefits:
   Centralized business logic.
   Reusability across controllers or other services.
   Easy to test.
   3. Controller Pattern (MVC)
   Purpose: Decouples the presentation layer from business logic.
   Implementation:
   The controllers, such as the one with the @GetMapping and @PostMapping, handle HTTP requests and delegate processing to services like ExchangeRateService.
   Benefits:
   Clear separation between HTTP handling and core logic.
   Easier debugging and testing for API endpoints.
   4. DTO (Data Transfer Object) Pattern
   Purpose: Facilitates data transfer between layers while abstracting unnecessary fields.
   Implementation:
   ExchangeRateDTO, CurrencyConversionRequestDTO, and ConversionHistoryResponseDTO are used to transfer data between the controller, service, and repository layers.
   Benefits:
   Ensures a clean contract between layers.
   Reduces coupling between layers.
   Enhances security by only exposing required fields.
   5. Exception Handling Pattern
   Purpose: Centralizes exception handling and provides consistent error responses.
   Implementation:
   The @ExceptionHandler annotation is used to handle exceptions like CurrencyNotFoundException.
   ErrorResponse provides a unified structure for error messages.
   Benefits:
   Better user experience with meaningful error messages.
   Consistent handling of errors across the application.
   6. Singleton Pattern
   Purpose: Ensures a class has only one instance throughout the application and provides global access.
   Implementation:
   Spring’s @Service and @Repository annotations make ExchangeRateService and repositories effectively singletons by default.
   Benefits:
   Efficient resource utilization (e.g., one instance of a service for all requests).
   Simplified dependency management.


5. **Code Structure**:
    - Follows a clean separation of concerns:
      ```
      src
      ├── main
      │   ├── java
      │   │   ├── config         # Configuration files
      │   │   ├── controller     # REST API controllers
      │   │   ├── dto            # Data Transfer Objects (DTOs)
      │   │   ├── entity         # JPA entities
      │   │   ├── exception      # Custom exceptions
      │   │   ├── repository     # Data access layer
      │   │   ├── service        # Business logic layer
      │   │   └── Application.java # Main application entry point
      │   └── resources
      │       ├── application.properties # Configuration properties
      │       └── data.sql               # Sample data for H2 database
      └── test
          ├── java
          │   └── com.example.foreignexchange
          │       ├── ApplicationTests.java   # Integration tests
          │       ├── ServiceTests.java       # Unit tests for services
      ```

6. **Unit Testing**:
    - Includes comprehensive unit tests for services and core functionality.
    - Uses JUnit 5 and Mockito for testing.

7. **API Documentation**:
    - Fully documented endpoints with request/response examples using tools like **Swagger**.

8. **Containerization with Docker**:
    - A `Dockerfile` and `docker-compose.yml` are provided for running the application in a containerized environment.

---