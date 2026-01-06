package dev.nj.habs.appointment;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        logger.debug("Creating appointment: doctor={}, patient={}", request.doctor(), request.patient());

        if ("director".equalsIgnoreCase(request.doctor().trim())) {
            throw new IllegalArgumentException("Cannot set appointments for director");
        }

        Appointment appointment = appointmentRepository.save(
                new Appointment(
                        request.doctor().trim().toLowerCase(),
                        request.patient().trim().toLowerCase(),
                        request.date()));

        logger.debug("Appointment persisted: id={}", appointment.getId());
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

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        logger.debug("Retrieving all appointments");

        List<AppointmentResponse> appointments = appointmentRepository.findAll()
                .stream().map(appointmentMapper::toResponse)
                .toList();

        logger.debug("Retrieved {} appointments", appointments.size());
        return appointments;
    }

    @Transactional
    public void transferAppointmentsToDirector(String fromDoctor) {
        List<Appointment> appointments = appointmentRepository.findByDoctor(fromDoctor.toLowerCase());
        for (Appointment appointment : appointments) {
            appointment.setDoctor("director");
        }
        appointmentRepository.saveAll(appointments);
    }

    @Transactional
    public void deleteAppointmentsByDoctor(String doctorName) {
        appointmentRepository.deleteByDoctor(doctorName.toLowerCase());
    }

    @Override
    public List<Map<String, Object>> getStatisticsByDay() {
        List<Object[]> results = appointmentRepository.countAppointmentsByDate();
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put(row[0].toString(), row[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getStatisticsByDoctor() {
        List<Object[]> results = appointmentRepository.countAppointmentsByDoctor();
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put((String) row[0], row[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }
}
