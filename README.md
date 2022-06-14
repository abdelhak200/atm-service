# ATM Simulator API

This is a Java API using Spring Boot which simulates some actions of an ATM.

## Features
* Spring Boot 2.7
* Java 16
* Maven 3.8.1
* MySQL
* Spring Data JPA
* Lombok

## Pre-requisites
This project has been tested using:
* Windows 11
* jdk 16
* Docker Desktop 4.9.0 (80466)

## Installation

To run this project you can:      

1- Clone from GitHub:

```bash
git clone https://github.com/abdelhak200/atm-service.git
```
2- Execute the docker-compose.yml file to run the image: abdelhak2019/hug:MySQLServer creating a containter and creating the network: atm-service_mysql-network

```bash
docker-compose up -d
```
3- This command permet to create atm-0.0.1-SNAPSHOT.jar skipping the tests
```bash
mvn clean install -Dmaven.test.skip=true
```
4- build a Docker image and run
```bash
docker build -t atm-api .
docker run -p 8080:8080 --name atm-service --network atm-service_mysql-network -d atm-api
```

## Usage

The ATM application initialises with two accounts, as per the requirements:

Account #1  

   * Account Number: 123456789
   * PIN: 1234
   * Opening Balance: 800
   * Overdraft: 200

Account #2

   * Account Number: 987654321
   * PIN: 4321
   * Opening Balance: 1230
   * Overdraft: 150

### Calling the API

To request the balance for a specific account, send a POST request to the "localhost:8080/balance" endpoint with the following request body:

#### Example
```bash
{
    "pin": 4321,
    "accountNumber": 987654321
}
```

![balance](https://user-images.githubusercontent.com/21033378/173260292-eebfed38-f56a-47cb-aefc-e9c17e4a40cb.png?raw=true "Sample POST request")

To request a withdrawal, send a POST request to the "localhost:8080/withdraw" endpoint with the following request body:

#### Example
```bash
{
    "amount": 85,
    "pin": 4321,
    "accountNumber": 987654321
}
```
![withdraw](https://user-images.githubusercontent.com/21033378/173260375-2bd5b0e4-d98c-46a2-9d3b-97bd16ddddd3.png?raw=true "Sample POST request")

## Future Enhancements
* With Spring Boot, this project can easily be extended.
* Custom exceptions should be added to give the user more information about errors.
* Extra functionality could be provided to allow for:
   * User account creation
   * Deposit funds
   * PIN change service
* A frontend UI application could be developed which would consume this API and display data to a user.


