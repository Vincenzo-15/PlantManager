package informatica.plantmanager.controller;

import informatica.plantmanager.model.*;
import informatica.plantmanager.model.RecuperaSalute;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlantPageDashboardController {

    @FXML
    private Label labelNomePianta;

    @FXML
    private Label labelAvviso;

    @FXML
    private Label labelPosizionePianta;

    @FXML
    private Label labelPercentuale;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private GridPane sensorGridPanel;

    private Utente utente;
    private String plantId;
    private ScheduledExecutorService scheduler;
    private List<Integer> angleValues = new ArrayList<>();
    private List<Integer> sensorValues = new ArrayList<>();
    private int expectedSensorCount = 0;
    private int receivedSensorCount = 0;

    public void setExpectedSensorCount(int count) {
        this.expectedSensorCount = count;
    }

    public PlantPageDashboardController() {
        scheduler = Executors.newScheduledThreadPool(1);
    }

/*    public void updateAngle(double angle) {
        int roundedAngle = (int) Math.round(angle);
        angleValues.add(roundedAngle);
        receivedSensorCount++;

        if (receivedSensorCount == expectedSensorCount) {
            int averageAngle = (int) Math.round(calculateAverageAngle());

            if (averageAngle < 0) {
                averageAngle = 0;
            }

            labelPercentuale.setText(averageAngle + "%");

            updateSaluteInDatabase(averageAngle);

            receivedSensorCount = 0;
            angleValues.clear();
        }
    }

    private double calculateAverageAngle() {
        double sum = 0;
        for (int angle : angleValues) {
            sum += angle;
        }
        return (sum / expectedSensorCount)*100/360;
    }
*/
    private void updateSaluteInDatabase(int averageAngle) {
        AggiornaSalute saluteService = new AggiornaSalute();
        saluteService.setParameters(plantId, averageAngle);
        saluteService.setOnSucceeded(e -> {
            if (saluteService.getValue()) {
                System.out.println("Salute aggiornata con successo per la pianta " + plantId);
                progressBar.setProgress(averageAngle / 100.0);
            } else {
                labelAvviso.setText("Errore nell'aggiornamento della salute");
                System.err.println("Errore nell'aggiornamento della salute per la pianta " + plantId);
            }
        });
        saluteService.setOnFailed(e -> {
            Throwable error = saluteService.getException();
            labelAvviso.setText("Errore nell'aggiornamento della salute");
            System.err.println("Errore nel service AggiornaSaluteDirectService: " + error.getMessage());
        });
        saluteService.start();
    }

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
            this.setExpectedSensorCount(sensoriList.size());
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
                            sensorController.setPlantPageDashboardController(this);
                            sensorGridPanel.add(sensorComponent, col, row);
                        } else {
                            loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddSensorComponent.fxml"));
                            AnchorPane addSensorComponent = loader.load();
                            AddSensorController addSensorController = loader.getController();
                            addSensorController.setUtente(utente);
                            addSensorController.setPlantId(plantId);
                            addSensorController.setPosizioneGriglia(cellKey);
                            addSensorController.setOnCloseCallback(this::caricaElementi);
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
            labelAvviso.setText("Errore nel caricamento dei sensori assegnati");
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
        RecuperaPosizionePianta posizioneService = new RecuperaPosizionePianta();
        posizioneService.setPlantId(plantId);
        posizioneService.setOnSucceeded(event -> {
            String posizione = posizioneService.getValue();
            setPosizionePianta(posizione);
        });
        posizioneService.setOnFailed(event -> {
            Throwable error = posizioneService.getException();
            labelAvviso.setText("Errore nel recupero della posizione della pianta");
            System.err.println("Errore nel recupero della posizione della pianta: " + error.getMessage());
        });
        posizioneService.start();
    }

    public void setNomePianta(String nomePianta) {
        labelNomePianta.setText(nomePianta + " -");
    }

    public void setPosizionePianta(String posizionePianta) {
        labelPosizionePianta.setText(posizionePianta);
    }

    public void updateValue(double valoreMisurato, Double valoreConsigliato) {
        System.out.println("Valore misurato: " + valoreMisurato + " - Valore consigliato: " + valoreConsigliato);
        float i = 0;
        while(i<=40){
            if(valoreMisurato >= valoreConsigliato * 1-i && valoreMisurato <= valoreConsigliato * 1+i) {
                sensorValues.add((int) ((40-i)*2.5));
                break;
            }
            i+=0.1f;
        }
        receivedSensorCount++;
        if (receivedSensorCount == expectedSensorCount) {
            int averageValue = calculateAverageValue();
            labelPercentuale.setText(averageValue + "%");
            updateSaluteInDatabase(averageValue);
            receivedSensorCount = 0;
            sensorValues.clear();
        }
    }

    private int calculateAverageValue() {
        double sum = 0;
        for (int value : sensorValues) {
            sum += value;
        }
        return (int)(sum / expectedSensorCount);
    }
}
