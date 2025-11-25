package dev.nj.habs.appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
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

        logger.debug("Successfully created an appointment");
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    public AppointmentResponse deleteAppointment(Long id) {
        logger.debug("Attempting to delete an appointment {}", id);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException("The appointment does not exist or was already cancelled"));

        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        appointmentRepository.delete(appointment);
        logger.debug("Successfully deleted the appointment {}", id);
        return response;
    }
}
