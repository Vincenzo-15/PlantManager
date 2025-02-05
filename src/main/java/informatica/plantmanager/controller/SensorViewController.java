package informatica.plantmanager.controller;

import informatica.plantmanager.model.AggiornaValoreSensore;
import informatica.plantmanager.model.DatiMisurazioni;
import informatica.plantmanager.model.RecuperaValoriConsigliati;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

public class SensorViewController {

    @FXML
    private Arc arcValore;

    @FXML
    private Label labelNomeSensore;

    @FXML
    private Label labelValore;

    @FXML
    private Label labelAlert;

    private String piantaUtenteId;
    private String sensoreId;

    private ScheduledService<DatiMisurazioni> scheduledService;

    public void setDatiSensore(String piantaUtenteId, String sensoreId) {
        this.piantaUtenteId = piantaUtenteId;
        this.sensoreId = sensoreId;
        startScheduledService();
    }

    private void startScheduledService() {
        scheduledService = new ScheduledService<DatiMisurazioni>() {
            @Override
            protected Task<DatiMisurazioni> createTask() {
                AggiornaValoreSensore service = new AggiornaValoreSensore();
                service.setParameters(piantaUtenteId, sensoreId);
                return service.createTask();
            }
        };
        scheduledService.setPeriod(Duration.seconds(10));
        scheduledService.setOnSucceeded(event -> {
            DatiMisurazioni data = scheduledService.getValue();
            if (data != null) {
                updateUI(data);
            }
        });
        scheduledService.setOnFailed(event -> {
            Throwable error = scheduledService.getException();
            System.err.println("Errore nel recupero della misurazione: " + error.getMessage());
        });
        scheduledService.start();
    }

    private void updateUI(DatiMisurazioni data) {
        Platform.runLater(() -> {
            labelNomeSensore.setText(data.getNomeSensore());
            labelValore.setText(data.getValore() + " " + data.getUnitaMisura());
            updateArc(data.getValore(), data.getNomeSensore());
            updateAlert(data.getValore(), data.getNomeSensore());
        });
    }

    private void updateArc(double valore, String nomeSensore) {
        double angle = calculateAngle(valore, nomeSensore);
        arcValore.setLength(angle);
        updateArcColor(nomeSensore);
    }

    private double calculateAngle(double valore, String nomeSensore) {
        double angle = 0;
        switch (nomeSensore.toLowerCase()) {
            case "acqua":
                angle = (valore / 500.0) * 360;
                break;
            case "luce":
                angle = (valore / 600.0) * 360;
                break;
            case "umidita":
                angle = (valore / 100.0) * 360;
                break;
            case "temperatura":
                angle = (valore / 40.0) * 360;
                break;
            case "ph":
                angle = ((valore - 5.5) / (7.5 - 5.5)) * 360;
                break;
            case "vento":
                angle = (valore / 100.0) * 360;
                break;
            default:
                angle = valore * 10;
                break;
        }
        return Math.min(angle, 360);
    }

    private void updateArcColor(String nomeSensore) {
        Color color;
        switch (nomeSensore.toLowerCase()) {
            case "acqua":
                color = Color.web("#29B6F6");
                break;
            case "luce":
                color = Color.web("#FADE3F");
                break;
            case "umidita":
                color = Color.web("#29B6F6");
                break;
            case "temperatura":
                color = Color.web("#EC7171");
                break;
            case "ph":
                color = Color.web("#ED71BD");
                break;
            case "vento":
                color = Color.web("#5DD7E5");
                break;
            default:
                color = Color.GRAY;
                break;
        }
        arcValore.setFill(color);
        arcValore.setStroke(color);
    }

    private void updateAlert(double valoreMisurato, String nomeSensore) {
        RecuperaValoriConsigliati recService = new RecuperaValoriConsigliati();
        recService.setParameters(piantaUtenteId, nomeSensore);
        recService.setOnSucceeded(e -> {
            Double valoreConsigliato = recService.getValue();
            String alertMessage;
            if (valoreConsigliato == null) {
                alertMessage = "Nessun valore consigliato disponibile.";
            } else {
                if (valoreMisurato < 0.9 * valoreConsigliato) {
                    alertMessage = "Valore troppo basso!";
                } else if (valoreMisurato > 1.1 * valoreConsigliato) {
                    alertMessage = "Valore troppo alto!";
                } else {
                    alertMessage = "Valore nella norma.";
                }
            }
            Platform.runLater(() -> {
                labelAlert.setText(alertMessage);
            });
        });
        recService.setOnFailed(e -> {
            Throwable error = recService.getException();
            System.err.println("Errore nel recupero del valore consigliato: " + error.getMessage());
        });
        recService.start();
    }
}

