package informatica.plantmanager.controller;

import informatica.plantmanager.model.CaricaAcquaSensori;
import informatica.plantmanager.model.CaricaTemperaturaSensori;
import informatica.plantmanager.model.CaricaUmiditaSensori;
import informatica.plantmanager.model.Utente;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DashboardPanelController {

    @FXML
    private GridPane plantGridPanel;

    @FXML
    private ImageView imageStatusPlants;

    @FXML
    private Label labelNickname;

    @FXML
    private Label buttonLeft;

    @FXML
    private Label buttonRight;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label labelOrario;

    @FXML
    private Label labelGradi;

    @FXML
    private Label labelPerUmidita;

    @FXML
    private Label labelVelocitaVento;

    @FXML
    private Label labelStatusPlant;

    @FXML
    private LineChart<String, Number> chartHumidity;

    @FXML
    private BarChart<String, Number> chartTemperature;

    @FXML
    private BarChart<String, Number> chartWater;

    private String userId;

    private Utente utente;

    private static String gradiValue;
    private static String umiditaValue;
    private static String velocitaVentoValue;

    @FXML
    public void initialize() {
        int rows = plantGridPanel.getRowConstraints().size();
        int columns = plantGridPanel.getColumnConstraints().size();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddPlantComponent.fxml"));
                    AnchorPane addPlantComponent = loader.load();
                    plantGridPanel.add(addPlantComponent, col, row);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        buttonLeft.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> scrollLeft());
        buttonRight.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> scrollRight());
        initializeClock();
        setRandomValues();
        Platform.runLater(this::updatePlantStatus);
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        this.userId = utente.getId();
        labelNickname.setText(utente.getNickname());
        loadHumidityData();
        loadTemperatureData();
        loadWaterData();
    }

    private void scrollLeft() {
        double currentValue = scrollPane.getHvalue();
        if (currentValue > 0 && currentValue <= 0.5) {
            scrollPane.setHvalue(0);
        } else if (currentValue > 0.5 && currentValue <= 1) {
            scrollPane.setHvalue(0.5);
        }
    }

    private void scrollRight() {
        double currentValue = scrollPane.getHvalue();
        if (currentValue >= 0 && currentValue < 0.5) {
            scrollPane.setHvalue(0.5);
        } else if (currentValue >= 0.5 && currentValue < 1) {
            scrollPane.setHvalue(1);
        }
    }

    @FXML
    void goLeft(MouseEvent event) {
        scrollLeft();
    }

    @FXML
    void goRight(MouseEvent event) {
        scrollRight();
    }

    private void loadHumidityData() {
        CaricaUmiditaSensori service = new CaricaUmiditaSensori();
        service.setUserId(userId);
        service.setOnSucceeded(event -> {
            List<Double> humidityValues = service.getValue();
            double sum = 0;
            for (double value : humidityValues) {
                sum += value;
            }
            double averageHumidity = humidityValues.isEmpty() ? 0 : sum / humidityValues.size();

            Platform.runLater(() -> updateHumidityChart(averageHumidity));
        });
        service.start();
    }

    private void loadTemperatureData() {
        CaricaTemperaturaSensori service = new CaricaTemperaturaSensori();
        service.setUserId(userId);
        service.setOnSucceeded(event -> {
            List<Double> temperatureValues = service.getValue();
            double sum = 0;
            for (double value : temperatureValues) {
                sum += value;
            }
            double averageTemperature = temperatureValues.isEmpty() ? 0 : sum / temperatureValues.size();

            Platform.runLater(() -> updateTemperatureChart(averageTemperature));
        });
        service.start();
    }

    private void loadWaterData() {
        CaricaAcquaSensori service = new CaricaAcquaSensori();
        service.setUserId(userId);
        service.setOnSucceeded(event -> {
            List<Double> waterValues = service.getValue();
            double sum = 0;
            for (double value : waterValues) {
                sum += value;
            }
            double averageWater = waterValues.isEmpty() ? 0 : sum / waterValues.size();

            Platform.runLater(() -> updateWaterChart(averageWater));
        });
        service.start();
    }

    private void updateHumidityChart(double averageHumidity) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Media Umidità");
        series.getData().add(new XYChart.Data<>("Umidità", averageHumidity));
        chartHumidity.getData().clear();
        chartHumidity.getData().add(series);
    }

    private void updateTemperatureChart(double averageTemperature) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Media Temperatura");
        series.getData().add(new XYChart.Data<>("Temperatura", averageTemperature));
        chartTemperature.getData().clear();
        chartTemperature.getData().add(series);
    }

    private void updateWaterChart(double averageWater) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Media Acqua");
        series.getData().add(new XYChart.Data<>("Acqua", averageWater));
        chartWater.getData().clear();
        chartWater.getData().add(series);
    }

    private void initializeClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            labelOrario.setText(currentTime.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void setRandomValues() {
        Random random = new Random();
        if (gradiValue == null) {
            gradiValue = random.nextInt(41) + "°";
        }
        if (umiditaValue == null) {
            umiditaValue = random.nextInt(101) + "%";
        }
        if (velocitaVentoValue == null) {
            velocitaVentoValue = random.nextInt(101) + "Km/h";
        }
        labelGradi.setText(gradiValue);
        labelPerUmidita.setText(umiditaValue);
        labelVelocitaVento.setText(velocitaVentoValue);
    }

    private void updatePlantStatus() {
        CaricaUmiditaSensori humidityService = new CaricaUmiditaSensori();
        CaricaTemperaturaSensori temperatureService = new CaricaTemperaturaSensori();
        CaricaAcquaSensori waterService = new CaricaAcquaSensori();

        humidityService.setUserId(userId);
        temperatureService.setUserId(userId);
        waterService.setUserId(userId);

        humidityService.setOnSucceeded(event -> {
            List<Double> humidityValues = humidityService.getValue();
            double averageHumidity = humidityValues.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            temperatureService.setOnSucceeded(event2 -> {
                List<Double> temperatureValues = temperatureService.getValue();
                double averageTemperature = temperatureValues.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                waterService.setOnSucceeded(event3 -> {
                    List<Double> waterValues = waterService.getValue();
                    double averageWater = waterValues.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                    Platform.runLater(() -> {
                        double overallAverage = (averageHumidity + averageTemperature + averageWater) / 3;
                        if (overallAverage > 70) {
                            labelStatusPlant.setText("Tutte le piante sono in salute");
                            imageStatusPlants.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/informatica/images/HeartFull.png"))));
                        } else if (overallAverage > 40) {
                            labelStatusPlant.setText("Le piante hanno bisogno di cure");
                            imageStatusPlants.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/informatica/images/HeartHalf.png"))));
                        } else {
                            labelStatusPlant.setText("Le piante non sono in salute");
                            imageStatusPlants.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/informatica/images/HeartEmpty.png"))));
                        }
                    });
                });
                waterService.start();
            });
            temperatureService.start();
        });
        humidityService.start();
    }
}