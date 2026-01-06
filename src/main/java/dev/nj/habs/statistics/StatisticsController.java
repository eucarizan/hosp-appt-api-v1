package dev.nj.habs.statistics;

import dev.nj.habs.appointment.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);
    private final AppointmentService appointmentService;

    public StatisticsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/statisticsDay")
    public ResponseEntity<?> getStatisticsDay() {
        logger.info("GET /statisticsDay");
        List<Map<String, Object>> statistics = appointmentService.getStatisticsByDay();

        if (statistics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(statistics);
    }
}
