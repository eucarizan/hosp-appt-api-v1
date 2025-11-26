package dev.nj.habs.doctor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl implements DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
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
}
