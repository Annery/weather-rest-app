package spring.WeatherRestApp.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.WeatherRestApp.server.models.Measurement;
import spring.WeatherRestApp.server.repositories.MeasurementRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeasurementService {
    private final MeasurementRepository measurementRepository;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public List<Measurement> findAll() {
        return measurementRepository.findAll();
    }

    public int getRainyDaysCount(){
        return measurementRepository.countByRainingTrue();
    }

    @Transactional
    public void save(Measurement measurement) {
        measurementRepository.save(measurement);
    }
}
