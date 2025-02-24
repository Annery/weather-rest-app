package spring.WeatherRestApp.server.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import spring.WeatherRestApp.common.MeasurementDTO;
import spring.WeatherRestApp.server.models.Measurement;
import spring.WeatherRestApp.server.models.Sensor;
import spring.WeatherRestApp.server.services.MeasurementService;
import spring.WeatherRestApp.server.services.SensorsService;
import spring.WeatherRestApp.server.util.MeasurementErrorResponse;
import spring.WeatherRestApp.server.util.MeasurementNotCreatedException;
import spring.WeatherRestApp.server.util.SensorNotCreatedException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/measurements")
public class MeasurementsController {
    private final SensorsService sensorsService;
    private final MeasurementService measurementService;
    private final ModelMapper modelMapper;

    @Autowired
    public MeasurementsController(SensorsService sensorsService, MeasurementService measurementService, ModelMapper modelMapper) {
        this.sensorsService = sensorsService;
        this.measurementService = measurementService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<Measurement> getMeasurements() {
        return measurementService.findAll();
    }

    @GetMapping("/rainyDaysCount")
    public int getRainyDaysCount(){
        return measurementService.getRainyDaysCount();
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid MeasurementDTO measurementDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }

            throw new MeasurementNotCreatedException(errorMsg.toString());
        }

        Measurement measurement = convertToMeasurement(measurementDTO);

        measurementService.save(measurement);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<MeasurementErrorResponse> handleException(MeasurementNotCreatedException e) {
        MeasurementErrorResponse response = new MeasurementErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<MeasurementErrorResponse> handleException(SensorNotCreatedException e) {
        MeasurementErrorResponse response = new MeasurementErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Measurement convertToMeasurement(@Valid MeasurementDTO measurementDTO) {
        Measurement measurement = modelMapper.map(measurementDTO, Measurement.class);

        Sensor sensor = sensorsService.getSensorByName(measurementDTO.getSensor().getName())
                .orElseThrow(() -> new SensorNotCreatedException("A sensor with this name not found"));

        measurement.setSensor(sensor);
        measurement.setTimestamp(LocalDateTime.now());

        return measurement;
    }
}