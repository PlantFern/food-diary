# food-diary

This project is the backend of a food diary platform that allows users to track their daily meals, calorie and nutrient intake, weight, body fat percentage, and sleep duration. 
It supports multiple user roles, enabling trainers, doctors, and dietitians to register and monitor the diaries of users who grant them access.

## Features
- Track daily meals and automatically calculate calories and nutrients
- Log weight, body fat percentage, and sleep duration
- Role-based access (User, Trainer, Doctor, Dietitian)
- Secure sharing of diaries with specialists
- Comments from both users and specialists
- Create custom foods and recipes
- Schedule meal templates
- Mark foods and recipes as favorites

## Configuration & profiles

### Requirements
- JDK 25
- MySQL 8+

### Environment variables

Copy the template and adjust values for your local setup:

```bash
    cp .env.example .env
```

### Running

```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Profiles

The active profile is passed via the  `spring-boot.run.profiles` property.
Available profiles:

- `dev` - additionally loads test data from `db/devdata`

## Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 4.1 + Spring Modulith
- **Security:** Spring Security + OAuth 2.0
- **Persistence:** Spring Data JPA + MySQL + Flyway
- **Mapping:** MapStruct
- **Template engine:** Thymeleaf
- **Build tool:** Maven