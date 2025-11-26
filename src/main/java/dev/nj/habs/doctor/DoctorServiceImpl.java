package dev.nj.habs.doctor;

import dev.nj.habs.appointment.AppointmentRepository;
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
    private final DoctorMapper doctorMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public DoctorResponse createDoctor(CreateDoctorRequest request) {
        logger.debug("Attempting to create a doctor");
        String doctorName = request.doctorName().trim().toLowerCase();

        if (doctorRepository.existsByDoctorName(doctorName)) {
            throw new DoctorAlreadyExistsException("Doctor already exists");
        }

        Doctor doctor = new Doctor(doctorName);
        Doctor saved = doctorRepository.save(doctor);

        logger.debug("Successfully created doctor: {}", saved.getId());
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
            return List.of();
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
}
