package spring.WeatherRestApp.common;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class MeasurementDTO {
    @NotNull(message = "Measurement value should not be empty")
    @DecimalMin(value = "-100.0", message = "Measurement value must be at least -100")
    @DecimalMax(value = "100.0", message = "Measurement value must be at most 100")
    private Double value;

    @NotNull(message = "Raining value should not be empty")
    private Boolean raining;

    @NotNull(message = "Sensor should not be empty")
    private SensorDTO sensor;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Boolean getRaining() {
        return raining;
    }

    public void setRaining(Boolean raining) {
        this.raining = raining;
    }

    public SensorDTO getSensor() {
        return sensor;
    }

    public void setSensor(SensorDTO sensor) {
        this.sensor = sensor;
    }
}
