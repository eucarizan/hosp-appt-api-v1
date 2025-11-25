package dev.nj.habs.appointment;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/setAppointments")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        logger.info("Received request to create an appointment");
        AppointmentResponse response = appointmentService.createAppointment(request);
        logger.info("Successfully created appointment: {}", response.idApp());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteAppointment")
    public ResponseEntity<AppointmentResponse> deleteAppointment(@RequestParam("id") Long id) {
        logger.info("Received request to delete appointment: {}", id);
        AppointmentResponse response = appointmentService.deleteAppointment(id);
        logger.info("Successfully deleted appointment: {}", id);
        return ResponseEntity.ok(response);
    }

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
}
