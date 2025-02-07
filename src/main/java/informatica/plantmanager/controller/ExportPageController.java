package informatica.plantmanager.controller;

import informatica.plantmanager.model.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportPageController {

    @FXML
    private Button buttonExport;

    @FXML
    private ComboBox<String> comboBoxPlant;

    @FXML
    private ComboBox<String> comboBoxValue;

    @FXML
    private LineChart<String, Number> dataChart;

    @FXML
    private DatePicker datePicker;

    private Utente utente;

    public void setUtente(Utente utente) {
        this.utente = utente;
        initializeComboBoxData();
    }

    public void initializeComboBoxData() {
        if (utente == null) {
            System.err.println("Utente non impostato!");
            return;
        }

        CaricaPianteUtenteExport loadPlantsService = new CaricaPianteUtenteExport(utente.getId());
        loadPlantsService.setOnSucceeded(event -> {
            comboBoxPlant.setItems(loadPlantsService.getValue());
            comboBoxPlant.getItems().add(0, "Tutte");
        });
        loadPlantsService.setOnFailed(event -> loadPlantsService.getException().printStackTrace());
        loadPlantsService.start();

        CaricaNomiMisurazioni loadSensorsService = new CaricaNomiMisurazioni();
        loadSensorsService.setOnSucceeded(event -> {
            comboBoxValue.setItems(loadSensorsService.getValue());
        });
        loadSensorsService.setOnFailed(event -> loadSensorsService.getException().printStackTrace());
        loadSensorsService.start();

        comboBoxValue.setOnAction(event -> loadChartData());
        datePicker.setOnAction(event -> loadChartData());
        comboBoxPlant.setOnAction(event -> loadChartData());
    }

    public void loadChartData() {
        if (comboBoxValue.getValue() == null || comboBoxValue.getValue().isEmpty() || comboBoxValue.getValue().equals("Tutte")) {
            System.out.println("Seleziona un parametro valido dalla comboBoxValue");
            return;
        }

        if (datePicker.getValue() == null) {
            System.out.println("Seleziona una data");
            return;
        }

        String selectedPlant = comboBoxPlant.getValue();
        CaricaMisurazioni loadMeasurementsService = new CaricaMisurazioni(
                utente.getId(),
                datePicker.getValue(),
                comboBoxValue.getValue(),
                selectedPlant
        );

        loadMeasurementsService.setOnSucceeded(event -> {
            dataChart.getData().setAll(loadMeasurementsService.getValue());
        });

        loadMeasurementsService.setOnFailed(event -> loadMeasurementsService.getException().printStackTrace());

        loadMeasurementsService.start();
    }

    @FXML
    void exportData(MouseEvent event) {
        ObservableList<XYChart.Series<String, Number>> chartData = dataChart.getData();

        if (chartData.isEmpty()) {
            System.out.println("Nessun dato da esportare");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file == null) {
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.append("PiantaNome,Data,Valore\n");

            for (XYChart.Series<String, Number> series : chartData) {
                String plantName = series.getName();
                for (XYChart.Data<String, Number> data : series.getData()) {
                    String time = data.getXValue();
                    Number value = data.getYValue();

                    writer.append(plantName).append(",")
                            .append(time).append(",")
                            .append(value.toString()).append("\n");
                }
            }
            System.out.println("Dati esportati con successo nel file CSV");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nell'esportazione del file CSV");
        }
    }
}


