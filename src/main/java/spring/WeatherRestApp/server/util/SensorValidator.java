package spring.WeatherRestApp.server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import spring.WeatherRestApp.common.SensorDTO;
import spring.WeatherRestApp.server.services.SensorsService;

@Component
public class SensorValidator implements Validator {
    private final SensorsService sensorsService;

    @Autowired
    public SensorValidator(SensorsService sensorsService) {
        this.sensorsService = sensorsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SensorDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SensorDTO sensorDTO = (SensorDTO) target;

        if (sensorDTO.getName() == null || sensorDTO.getName().trim().isEmpty()) {
            errors.rejectValue("name", "", "Name should not be empty");
        } else if (sensorDTO.getName().length() < 3 || sensorDTO.getName().length() > 30) {
            errors.rejectValue("name", "", "Name should be between 3 and 30 characters");
        }

        if (sensorsService.getSensorByName(sensorDTO.getName()).isPresent()) {
            errors.rejectValue("name", "",
                    "A sensor with this name already exists");
        }
    }
}
