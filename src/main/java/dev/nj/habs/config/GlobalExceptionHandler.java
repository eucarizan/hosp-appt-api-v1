package dev.nj.habs.config;

import dev.nj.habs.appointment.AppointmentNotFoundException;
import dev.nj.habs.doctor.DoctorAlreadyExistsException;
import dev.nj.habs.doctor.DoctorNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        logger.warn("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body(Map.of("messages", errors));
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAppointmentNotFoundException(AppointmentNotFoundException ex) {
        logger.warn("Appointment not found: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DoctorAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleDoctorAlreadyExistsException(DoctorAlreadyExistsException ex) {
        logger.warn("Failed to create doctor: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDoctorNotFoundException(DoctorNotFoundException ex) {
        logger.warn("Failed to retrieve list of available dates: {}", ex.getMessage());
//        return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
