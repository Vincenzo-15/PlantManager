package informatica.plantmanager.controller;

import informatica.plantmanager.controller.AddSensorController;
import informatica.plantmanager.controller.SensorViewController;
import informatica.plantmanager.model.CaricaSensoriPianta;
import informatica.plantmanager.model.SensorePianta;
import informatica.plantmanager.model.Utente;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantPageDashboardController {

    @FXML
    private Label labelNomePianta;

    @FXML
    private Label labelPosizionePianta;

    @FXML
    private GridPane sensorGridPanel;

    private Utente utente;
    private String plantId;

    private void caricaElementi() {
        int rows = sensorGridPanel.getRowConstraints().size();
        int columns = sensorGridPanel.getColumnConstraints().size();
        sensorGridPanel.getChildren().clear();

        CaricaSensoriPianta sensorService = new CaricaSensoriPianta();
        sensorService.setPlantId(plantId);
        sensorService.setOnSucceeded(event -> {
            List<SensorePianta> sensoriList = sensorService.getValue();
            Map<String, SensorePianta> mappaSensori = new HashMap<>();
            for (SensorePianta sp : sensoriList) {
                mappaSensori.put(sp.getPosizioneGriglia(), sp);
            }

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    String cellKey = row + "," + col;
                    try {
                        FXMLLoader loader;
                        if (mappaSensori.containsKey(cellKey)) {
                            loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/SensorView.fxml"));
                            AnchorPane sensorComponent = loader.load();
                            SensorViewController sensorController = loader.getController();
                            sensorController.setDatiSensore(plantId, mappaSensori.get(cellKey).getSensoreId());
                            sensorGridPanel.add(sensorComponent, col, row);
                        } else {
                            loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddSensorComponent.fxml"));
                            AnchorPane addSensorComponent = loader.load();
                            AddSensorController addSensorController = loader.getController();
                            addSensorController.setUtente(utente);
                            addSensorController.setPlantId(plantId);
                            addSensorController.setPosizioneGriglia(cellKey);
                            addSensorController.setOnCloseCallback(this::caricaElementi); // Set the callback
                            sensorGridPanel.add(addSensorComponent, col, row);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        sensorService.setOnFailed(event -> {
            Throwable error = sensorService.getException();
            System.err.println("Errore nel caricamento dei sensori assegnati: " + error.getMessage());
        });
        sensorService.start();
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
        caricaElementi();
    }

    public void setNomePianta(String nomePianta) {
        labelNomePianta.setText(nomePianta + " -");
    }

    public void setPosizionePianta(String posizionePianta) {
        labelPosizionePianta.setText(posizionePianta);
    }
}
