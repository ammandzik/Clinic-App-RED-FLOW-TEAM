package pl.infoshare.clinicweb.patient;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.infoshare.clinicweb.file.FileService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService implements PatientRepository {

    private static final String PATIENT_PATH = "src/main/resources/patients.json";
    private final FileService fileService;
    private List<Patient> patientList;

    public PatientService(FileService fileService, List<Patient> patientList) {

        this.fileService = fileService;
    }

    public void savePatient(Patient patient) {
        patient.setDateOfBirth(patient);
        fileService.writeToFile(patient, PATIENT_PATH);

    }

    @Override
    public List<Patient> getAll() {
        List<Patient> patients = fileService.readFromFile(PATIENT_PATH, new TypeReference<List<Patient>>() {
        });
        return patients != null ? patients : new ArrayList<>();
    }


    public List<PatientDto> findAll() {

        return getAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }

    public PatientDto convertToDto(Patient patient) {

        PatientDto patientDto = new PatientDto();

        patientDto.setName(patient.getPersonDetails().getName());
        patientDto.setSurname(patient.getPersonDetails().getSurname());
        patientDto.setPhoneNumber(patient.getPersonDetails().getPhoneNumber());

        return patientDto;
    }

    public Patient findByPesel(String pesel) {
        List<Patient> patients = getAll();
        Optional<Patient> patOpt = Optional.of(new Patient());
        if (patients == null) {
            return patOpt.orElseThrow();
        }
        return patients.stream()
                .filter(patient -> patient.getPersonDetails().getPesel().equals(pesel))
                .findFirst()
                .orElse(null);
    }

    public void saveOrUpdatePatient(Patient patient) {
        Patient patientByPesel = findByPesel(patient.getPersonDetails().getPesel());
        if (patientByPesel != null) {
            patientByPesel.getPersonDetails().setName(patient.getPersonDetails().getName());
            patientByPesel.getPersonDetails().setSurname(patient.getPersonDetails().getSurname());
            savePatient(patientByPesel);
        } else {
            savePatient(patient);
        }
    }
}