# Hospital Appointment Booking System
A REST API for managing hospital appoitnments

## Features

### Appointment
- Create appointment with doctor's name, patient's name and date
- Delete appointment
- List all appointments

### Doctor
- Create doctor
- List all doctors
- Get available dates by doctor (next 4 days)
- Delete doctor (transfers appointments to director)

### Statistics
- Get appointment statistics by day
- Get appointment statistics by doctor

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
| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| POST | `/setAppointment` | Create an appointment |
| DELETE | `/deleteAppointment?id={id}` | Delete an appointment |
| GET | `/appointments` | List all appointments |

### Doctors
| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| POST | `/newDoctor` | Create a doctor |
| GET | `/allDoctorslist` | List all doctors |
| GET | `/availableDatesByDoctor?doc={name}` | Get available dates for a doctor |
| DELETE | `/deleteDoctor?doc={name}` | Delete a doctor |

### Statistics
| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| GET | `/statisticsDay` | Get appointment count by day |
| GET | `/statisticsDoc` | Get appointment count by doctor |

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
```bash
curl -X GET http://localhost:8080/appointments
```

### Create a doctor
```bash
curl -X POST http://localhost:8080/newDoctor \
  -H "Content-Type: application/json" \
  -d '{"doctorName": "Dr. House"}'
```

### List all doctors
```bash
curl -X GET http://localhost:8080/allDoctorslist
```

### Get available dates for a doctor
```bash
curl -X GET "http://localhost:8080/availableDatesByDoctor?doc=dr. house"
```

### Delete a doctor
```bash
curl -X DELETE "http://localhost:8080/deleteDoctor?doc=dr. house"
```

### Get statistics by day
```bash
curl -X GET http://localhost:8080/statisticsDay
```

### Get statistics by doctor
```bash
curl -X GET http://localhost:8080/statisticsDoc
```
