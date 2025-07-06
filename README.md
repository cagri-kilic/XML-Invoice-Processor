# XML Invoice Processor

## Overview

This project is a Spring Boot application for processing XML invoices. It provides a REST API endpoint to accept a Base64-encoded XML invoice, validates it against a provided XSD schema, unmarshals it into Java objects using JAXB, extracts key fields, and persists them to a PostgreSQL database.

## Features

- REST API for uploading invoices as Base64-encoded XML
- XML validation against XSD schema
- JAXB-based XML to Java object mapping
- Data extraction and persistence with Spring Data JPA
- Exception handling with global error responses
- Unit tests

## Technology Stack

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- JAXB (with jaxb2-maven-plugin)
- Lombok

## Project Structure

```
XML-Invoice-Processor/
├── src/
│   ├── main/
│   │   ├── java/com/cagrikilic/xmlinvoice/
│   │   │   ├── config/                # XML validation config
│   │   │   ├── constant/              # Constants (messages, status)
│   │   │   ├── controller/            # REST controllers
│   │   │   ├── exception/             # Global exception handler
│   │   │   ├── generated/             # JAXB-generated classes (from XSD)
│   │   │   ├── mapper/                # Entity/DTO mappers
│   │   │   ├── model/
│   │   │   │   ├── dto/               # DTOs (request/response)
│   │   │   │   └── entity/            # JPA entities
│   │   │   ├── repository/            # Spring Data JPA repositories
│   │   │   ├── service/               # Service interfaces/implementations
│   │   │   ├── util/                  # General-purpose utility classes
│   │   │   └── XmlInvoiceProcessorApplication.java
│   │   └── resources/
│   │       ├── application.yml        # Main config
│   │       └── xsd/                   # XSD and JAXB binding files
│   └── test/
│       ├── java/com/cagrikilic/xmlinvoice/service/ # Unit/integration tests
│       └── java/com/cagrikilic/xmlinvoice/resources/
│           └── application-test.yml   # Test config 
├── pom.xml
└── README.md
```

## How to Build & Run the Project

### Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL (or use H2 for testing)

### 1. Clone the Repository

```sh
git clone https://github.com/cagri-kilic/XML-Invoice-Processor.git
cd XML-Invoice-Processor
```

### 2. Configure the Database

Edit `src/main/resources/application.yml` and set your PostgreSQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/invoice_db
    username: your_db_user
    password: your_db_password
  jpa:
    hibernate:
      ddl-auto: update
```

Alternatively, for testing, you can use H2 by adjusting the configuration.

### 3. Build the Project

```sh
mvn clean install
```

### 4. Run the Application

```sh
mvn spring-boot:run
```

The application will start on `http://localhost:8080` by default.

## Example Request and Response

### Endpoint

```
POST /api/invoices
Content-Type: application/json
```

### Request Body

```json
{
  "base64xml": "PEludm9pY2U+SU5WLTEwMDE8L0ludm9pY2VOdW1iZXI+PC9JbnZvaWNlPg=="
}
```

### Success Response

```
HTTP/1.1 201 Created
Content-Type: application/json

{
  "status": "SUCCESS",
  "httpStatus": 201,
  "message": "Invoice saved successfully",
  "data": {
    "id": 1,
    "nip": "1234567890",
    "p1": "Test P1",
    "p2": "Test P2",
    "createdAt": "2024-01-01T12:00:00Z"
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Error Response (Invalid XML)

```
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
    "status": "ERROR",
    "httpStatus": 400,
    "message": "XML validation failed",
    "errorDetails": [
        "Element type \"KodKraj\" must be followed by either attribute specifications, \">\" or \"/>\"."
    ],
    "timestamp": "2025-07-06T16:23:56.084684300Z"
}
```

## Notes on XSD and JAXB Usage

- The XSD schema is located in `src/main/resources/xsd/`.
- Java classes under `com.cagrikilic.xmlinvoice.generated` are generated from the XSD using the Maven plugin [`jaxb2-maven-plugin`](https://www.mojohaus.org/jaxb2-maven-plugin/).
- The plugin uses the `src/main/resources/xsd/bindings.xjb` file to customize the generated Java code:
  - It maps each XSD file to a specific Java package (e.g., `schemat.xsd` to `com.cagrikilic.xmlinvoice.generated.local`, `StrukturyDanych_v10-0E.xsd` to `com.cagrikilic.xmlinvoice.generated.imported`).
  - It can also rename generated classes (e.g., the `TAdres` type from `StrukturyDanych_v10-0E.xsd` is mapped to `TAdresImported`).
  - This ensures a clean separation of local and imported XSD types in the generated codebase.
- To regenerate Java classes after any XSD or binding changes, simply run:

  ```shell
  mvn clean compile
  ```

- The plugin is configured in `pom.xml` and will automatically generate or update the classes in `src/main/java/com/cagrikilic/xmlinvoice/generated` based on the schemas and bindings in `src/main/resources/xsd/`.
- JAXB is used for unmarshalling XML to Java objects and for schema validation.

## Additional Configuration & Environment Setup

- All configuration is managed via `application.yml`.
- Lombok is used for boilerplate code reduction. Make sure your IDE supports Lombok.
- For unit tests, see `src/test/java/com/cagrikilic/xmlinvoice/service/InvoiceServiceTest.java`.
- Exception handling is centralized in `GlobalExceptionHandler`.