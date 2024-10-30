# Java Spring Data Access
The target of this exercise is to practice Spring JMS and Spring Testing with Java 17.

## Features
- JMS and Spring Testing
- Usage of JPA to interact with Persistence API in a booking backend
- Usage of Hibernate
- Based on https://github.com/santiagocb/java-task05-spring-mvc
- Usage of awaitility to test Async process.

## Requirements
- Install Docker
- Download Postgres Docker image
- Install psql client

## Run the project
1. Run postgres through Docker with following command: `docker run --rm --name lil-postgres -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres`
2. Run psql command to create DB: `psql -h localhost -U postgres -f database.sql` and enter the password: `password`
3. Run activemq through Docker with following command: `docker run --rm -d --name activemq -p 61616:61616 -p 8161:8161 rmohr/activemq`
4. Run maven spring boot run.
5. Run command to stop Docker execution: `docker stop lil-postgres`

## Output tests
![Screenshot 2024-10-30 at 12 00 59 AM](https://github.com/user-attachments/assets/943b2c6c-508b-4827-a3b6-019ff9763b5a)
