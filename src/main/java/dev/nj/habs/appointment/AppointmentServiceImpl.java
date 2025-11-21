package dev.nj.habs.appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        logger.debug("Attempting to create an appointment");

        if (request.doctor() == null || request.doctor().toLowerCase(Locale.ROOT).trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor field is required");
        }

        if (request.patient() == null || request.patient().toLowerCase(Locale.ROOT).trim().isEmpty()) {
            throw new IllegalArgumentException("Patient field is required");
        }

        if (request.date() == null) {
            throw new IllegalArgumentException("Date field is required");
        }

        Appointment appointment = appointmentRepository.save(new Appointment(request.doctor(), request.patient(), request.date()));

        AppointmentResponse response = new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor(),
                appointment.getPatient(),
                appointment.getDate()
        );

        logger.debug("Successfully created an appointment");
        return response;
    }
}
