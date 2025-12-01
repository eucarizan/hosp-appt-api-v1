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

    @PostMapping("/setAppointment")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        logger.info("POST /setAppointment: doctor={}, patient={}", request.doctor(), request.patient());
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteAppointment")
    public ResponseEntity<AppointmentResponse> deleteAppointment(@RequestParam("id") Long id) {
        logger.info("DELETE /deleteAppointment: id={}", id);
        AppointmentResponse response = appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAppointments() {
        logger.info("GET /appointments");
        List<AppointmentResponse> appointments = appointmentService.getAllAppointments();
        if (appointments.isEmpty()) {
            logger.warn("No appointments found");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(appointments);
    }
}
