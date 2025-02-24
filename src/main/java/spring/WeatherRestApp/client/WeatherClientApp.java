package spring.WeatherRestApp.client;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import spring.WeatherRestApp.common.MeasurementDTO;
import spring.WeatherRestApp.common.SensorDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class WeatherClientApp {
    private static final String sensorName = "Sensor3";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        registerSensor(restTemplate);
        sendMeasurements(restTemplate);
        List<MeasurementDTO> measurements = getMeasurements(restTemplate);
        plotGraph(measurements);
    }

    private static void registerSensor(RestTemplate restTemplate) {
        String url = "http://localhost:8080/sensors/registration";

        SensorDTO sensor = new SensorDTO();
        sensor.setName(sensorName);

        ResponseEntity<String> response = restTemplate.postForEntity(url, sensor, String.class);
        System.out.println("Sensor registration response: " + response.getStatusCode());
    }

    private static void sendMeasurements(RestTemplate restTemplate) {
        String url = "http://localhost:8080/measurements/add";
        Random rnd = new Random();

        for (int i = 0; i < 1000; i++) {
            MeasurementDTO measurement = new MeasurementDTO();
            measurement.setValue(randomDoubleInRange(-100, 100, rnd));
            measurement.setRaining(rnd.nextBoolean());

            SensorDTO sensor = new SensorDTO();
            sensor.setName(sensorName);
            measurement.setSensor(sensor);

            restTemplate.postForEntity(url, measurement, String.class);
        }

        System.out.println("1000 measurements sent");
    }

    private static Double randomDoubleInRange(double min, double max, Random rnd) {
        return min + (max - min) * rnd.nextDouble();
    }

    private static List<MeasurementDTO> getMeasurements(RestTemplate restTemplate) {
        String url = "http://localhost:8080/measurements";
        ResponseEntity<MeasurementDTO[]> response = restTemplate.getForEntity(url, MeasurementDTO[].class);

        List<MeasurementDTO> measurements = Arrays.asList(response.getBody());
        System.out.println("Received " + measurements.size() + " measurements");
        return measurements;
    }

    private static void plotGraph(List<MeasurementDTO> measurements) {
        List<Double> temperatures = measurements.stream()
                .map(MeasurementDTO::getValue)
                .toList();

        List<Integer> indices = IntStream.range(0, temperatures.size())
                .boxed().toList();

        CategoryChart chart = new CategoryChartBuilder()
                .width(800).height(600)
                .title("Temperature Measurements")
                .xAxisTitle("Measurement Number")
                .yAxisTitle("Temperature")
                .build();

        chart.addSeries("Temperature", indices, temperatures);
        new SwingWrapper<>(chart).displayChart();
    }
}