# Commerce Services - Technical Interview

### Answer
This project contains the solved exercise proposed which is explained further down in this README. It contains the necessary logic to satisfy the functional requirements as well as its corresponding tests and logging.
In some classes some functions have been written in a functional programming way and in an imperative programming way getting to show that both are equally good.

### Context

We are an online book store. We have an API that returns our catalog and stock. 
We want to implement two new features in our system:
- Process new orders
- Retrieve existing orders

### Functional Requirements

- **Create a new Order**:
  - The application receives orders in a JSON format through an HTTP API endpoint (POST).
  - Orders contain a list of books and the quantity.
  - Before registering the order, the system should check if there's enough stock to fulfill the order (`src/main/resources/import.sql` will set the initial stock).
  - If one of the books in the order does not have enough stock we will reject the entire order.
  - After stock validation, the order is marked as a success, and it would return a Unique Order Identifier to the caller of the HTTP API endpoint.
  - If the order was processed we need to update available stock, taking into consideration:
    - Updating stock should not be a blocker for replying to the customer.
    - If the process of updating stock fails, should not cause an error in order processing.

- **Retrieve Orders**:
  - The application has an endpoint to extract a list of existing orders. So that we can run `curl localhost:8080/orders/` and get a list of them

### Required

- Resolution needs to be fully in English
- You need to use Java 17
- This repo contains the existing bookstore code; fork or create a public repository with your solution.
- We expect you to implement tests for the requested functionalities. You decide the scope.
- **Once the code is complete, reply to your hiring person of contact.**

### How to run

Building
```shell
$ ./mvnw compile
```

Test
```shell
$ ./mvnw test
```

Start the application

```shell
$ ./mvnw spring-boot:run
```

Getting current stock for a given book 

```shell
$ curl localhost:8080/books_stock/ae1666d6-6100-4ef0-9037-b45dd0d5bb0e
{"id":"ae1666d6-6100-4ef0-9037-b45dd0d5bb0e","name":"adipisicing culpa Lorem laboris adipisicing","quantity":0}
```
