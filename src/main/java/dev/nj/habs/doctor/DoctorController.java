package dev.nj.habs.doctor;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/newDoctor")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        logger.info("POST /newDoctor: doctor={}", request.doctorName());
        DoctorResponse response = doctorService.createDoctor(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allDoctorslist")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        logger.info("GET /allDoctorslist");

        List<DoctorResponse> doctorsList = doctorService.getAllDoctors();
        if (doctorsList.isEmpty()) {
            logger.warn("No doctors found");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(doctorsList);
    }

    @GetMapping("/availableDatesByDoctor")
    public ResponseEntity<List<AvailableDateResponse>> getAvailableDates(@RequestParam("doc") String doctorName) {
        logger.info("GET /availableDatesByDoctor: doc={}", doctorName);

        List<AvailableDateResponse> dateResponseList = doctorService.getAvailableDatesByDoctor(doctorName);
        if (dateResponseList.isEmpty()) {
            logger.warn("No available dates");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(dateResponseList);
    }
}
