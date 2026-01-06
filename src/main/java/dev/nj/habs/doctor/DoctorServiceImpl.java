package dev.nj.habs.doctor;

import dev.nj.habs.appointment.AppointmentRepository;
import dev.nj.habs.appointment.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final DoctorMapper doctorMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, AppointmentService appointmentService, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public DoctorResponse createDoctor(CreateDoctorRequest request) {
        String doctorName = request.doctorName().trim().toLowerCase();
        logger.debug("Creating doctor: {}", doctorName);

        if (doctorRepository.existsByDoctorName(doctorName)) {
            logger.warn("Doctor already exists: {}", doctorName);
            throw new DoctorAlreadyExistsException("Doctor already exists");
        }

        Doctor doctor = new Doctor(doctorName);
        Doctor saved = doctorRepository.save(doctor);

        logger.debug("Doctor persisted: id={}, name={}", saved.getId(), saved.getDoctorName());
        return doctorMapper.toResponse(saved);
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        logger.debug("Attempting to list all doctors");

        List<DoctorResponse> responseList = doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toResponse)
                .toList();

        logger.debug("Successfully listed {} doctors", responseList.size());
        return responseList;
    }

    @Override
    public List<AvailableDateResponse> getAvailableDatesByDoctor(String doctor) {
        String doctorName = doctor.trim().toLowerCase();
        logger.debug("Attempting to get list of available dates for doctor: {}", doctorName);

        if (!doctorRepository.existsByDoctorName(doctorName)) {
            logger.warn("Doctor not found: doc='{}'", doctorName);
            throw new DoctorNotFoundException("Doctor not found");
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AvailableDateResponse> responseList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            boolean booked = appointmentRepository.existsByDoctorAndDate(doctorName, tomorrow.plusDays(i));
            AvailableDateResponse response = new AvailableDateResponse(tomorrow.plusDays(i), booked);
            responseList.add(response);
        }

        logger.debug("Successfully listed {} dates", responseList.size());
        return responseList;
    }

    @Override
    public DoctorResponse deleteDoctor(String doctorName) {
        String normalizedName = doctorName.trim().toLowerCase();
        logger.info("Attempting to delete doctor: {}", normalizedName);

        Doctor doctor = doctorRepository.findByDoctorName(normalizedName)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found"));

        DoctorResponse response = doctorMapper.toResponse(doctor);

        if ("director".equals(normalizedName)) {
            appointmentService.deleteAppointmentsByDoctor("director");
        } else {
            if (!doctorRepository.existsByDoctorName("director")) {
                Doctor director = new Doctor("director");
                doctorRepository.save(director);
            }
            appointmentService.transferAppointmentsToDirector(normalizedName);
        }

        doctorRepository.delete(doctor);
        logger.info("Successfully deleted doctor: {}", normalizedName);
        return response;
    }
}
