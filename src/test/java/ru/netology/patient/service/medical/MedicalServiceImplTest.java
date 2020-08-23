package ru.netology.patient.service.medical;

import org.junit.jupiter.api.AfterEach;
import org.junit.Test;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MedicalServiceImplTest {

    private MedicalServiceImpl medServ;

    @AfterEach
    void unsetTest() {
        medServ = null;
    }

    @Test
    public void checkBloodPressureTestNormal() {
        checkBloodPressure(true);
    }

    @Test
    public void checkBloodPressureTestNotNormal() {
        checkBloodPressure(false);
    }

    @Test
    public void checkTemperatureTestNormal() {
        checkTemperature(true);
    }

    @Test
    public void checkTemperatureTestNotNormal() {
        checkTemperature(false);
    }

    void checkBloodPressure(boolean isNormal) {
        checkTest(isNormal, false);
    }

    void checkTemperature(boolean isNormal) {
        checkTest(isNormal, true);
    }

    void checkTest(boolean isNormal, boolean onlyTemperature) {
        PatientInfo testPatient = getTestPatientInfo(isNormal);

        String alertTest = String.format("Warning, patient with id: %s, need help", testPatient.getId());
        BloodPressure bpNormal = new BloodPressure(130, 80);
        BigDecimal tONormal = new BigDecimal("36.6");

        PatientInfoRepository pir = Mockito.mock(PatientInfoFileRepository.class);
        SendAlertService sas = Mockito.spy(SendAlertServiceImpl.class);

        Mockito.when(pir.getById(testPatient.getId())).thenReturn(testPatient);
        Mockito.doNothing().when(sas).send(alertTest);

        medServ = new MedicalServiceImpl(pir, sas);
        if (onlyTemperature) {
            medServ.checkTemperature(testPatient.getId(), tONormal);
        } else {
            medServ.checkBloodPressure(testPatient.getId(), bpNormal);
        }

        Mockito.verify(sas, isNormal ? Mockito.never() : Mockito.atLeast(1)).send(alertTest);
    }

    PatientInfo getTestPatientInfo(boolean normal) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        BloodPressure bp;
        BigDecimal tO;
        if (normal) {
            bp = new BloodPressure(130, 80);
            tO = new BigDecimal("36.6");
        } else {
            bp = new BloodPressure(180, 130);
            tO = new BigDecimal("38.5");
        }
        return new PatientInfo(
                "test",
                "test",
                "test",
                LocalDate.parse("1970-01-01", format),
                new HealthInfo(tO, bp)
        );
    }

}
