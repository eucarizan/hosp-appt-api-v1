# Hospital Appointment Booking System
A REST API for managing hospital appoitnments

## Features

### Appointment
- Create appointment with doctor's name, patient's name and date
- Delete appointment
- List all appointments

## Tech Stack
- Java 17
- Spring Boot 4.0.0
- Spring Data JPA
- JUnit 6
- Gradle

## Prerequisites
- Java 17+
- Gradle 9.2.0+

## Build and Run

### Local Development

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Running Tests

```bash
# Run all tests
./gradlew test

# Run only unit tests
./gradlew test -Dtest="*Test"
```

## API Endpoints

### Appointments
| Method | Endpoint | Description | Auth |
| ------ | -------- | ----------- | ---- |
| POST | `/setAppointment` | Create an appointment | No |
| DELETE | `/deleteAppointment` | Delete an appointment | No |
| GET | `/appointments` | List all appointments | No |

## Request Examples

### Create an appointment
```bash
curl -X POST http://localhost:8080/setAppointment \
  -H "Content-Type: application/json" \
  -d '{"doctor": "Dr. House", "patient": "John Doe", "date": "2024-01-15"}'
```

### Delete an appointment
```bash
curl -X DELETE http://localhost:8080/deleteAppointment?id=1
```

### List all appointments
``` bash
curl -X GET http://localhost:8080/appointments
```
