# E-Bank Microservices

An electronic banking project built with Spring Boot and Spring Cloud. It is
being developed as a microservices-based application; the services and APIs
described here reflect the current state of the repository and may change.

## Current features

- **Customer service** for creating and retrieving customers.
- **E-Bank service** for creating and retrieving bank accounts. It looks up
  customer details through the customer service when creating an account and
  when retrieving an account by ID.
- **Discovery service** using Netflix Eureka for service registration and
  discovery.
- **Gateway service** with discovery-based route definitions.
- H2 in-memory databases for customer and account data.
- OpenAPI dependencies in the customer and E-Bank services.

The customer and account services add sample records at startup. Because the
data is stored in memory, it is reset when a service restarts.

## Technology

- Java 21
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3
- Spring Web MVC and Spring Data JPA
- Netflix Eureka
- Spring Cloud Gateway and OpenFeign
- Resilience4j circuit breaker
- H2 and Maven

## Project structure

```text
.
├── custumer-service/    # Customer API (directory name as it exists in the repository)
├── discovery-service/   # Eureka server
├── ebank-service/       # Bank account API
├── gateway-service/     # API gateway
├── src/                 # Root application source
└── pom.xml              # Root application Maven project
```

Each service directory is its own Maven project with its own Maven wrapper.
The root `pom.xml` is a separate Spring Boot project, not a Maven parent or
aggregator for the service projects.

## Prerequisites

- JDK 21
- Git

Maven does not need to be installed separately; use the wrapper included in
each project.

## Run locally

Start each service in a separate terminal. Start Eureka first, then the
customer service, then the E-Bank service. The E-Bank service calls the
customer service while creating its sample accounts.

From the repository root, run the following in separate terminals:

```bash
cd discovery-service
./mvnw spring-boot:run
```

```bash
cd custumer-service
./mvnw spring-boot:run
```

```bash
cd ebank-service
./mvnw spring-boot:run
```

```bash
cd gateway-service
./mvnw spring-boot:run
```

On Windows, replace `./mvnw` with `mvnw.cmd` in each command. The gateway is
optional when calling the customer and account services directly.

| Service | Default URL |
| --- | --- |
| Customer service | `http://localhost:8056` |
| E-Bank service | `http://localhost:8057` |
| Discovery (Eureka) | `http://localhost:8761` |
| Gateway | `http://localhost:9999` |

The customer and E-Bank services currently disable Spring Cloud Config and
use in-memory H2 databases. A separate config server or database is not
required for this local setup.

## API endpoints

### Customers

Base URL: `http://localhost:8056`

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/customers` | List customers |
| `GET` | `/customers/{id}` | Get a customer by ID |
| `POST` | `/customers` | Create a customer |

Example request:

```bash
curl -X POST http://localhost:8056/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Aymen riani","email":"aymenriani001@gmail.com"}'
```

### Bank accounts

Base URL: `http://localhost:8057`

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/accounts` | List bank accounts |
| `GET` | `/accounts/{id}` | Get an account by ID, including its customer details |
| `POST` | `/accounts` | Create an account for an existing customer |

Example request:

```bash
curl -X POST http://localhost:8057/accounts \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"balance":1500.00,"accType":"CURRENT_ACCOUNT"}'
```

The E-Bank service generates an account ID and creation time. Its startup
sample data also creates accounts for customer IDs 1 through 3.

## Build and test

Run the Maven wrapper from an individual service directory:

```bash
./mvnw clean verify
```

On Windows, use `mvnw.cmd clean verify`. For example:

```powershell
cd ebank-service
.\mvnw.cmd clean verify
```

## Development status

This project is a work in progress. The current APIs cover basic customer and
account creation and retrieval; they do not yet implement a complete banking
workflow such as deposits, withdrawals, or transfers. The application also
currently uses in-memory data, and authentication and authorization are not
implemented.
