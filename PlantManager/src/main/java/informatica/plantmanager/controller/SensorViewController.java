package informatica.plantmanager.controller;

import informatica.plantmanager.model.AggiornaValoreSensore;
import informatica.plantmanager.model.DatiMisurazioni;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class SensorViewController {

    @FXML
    private Arc arcValore;

    @FXML
    private Label labelNomeSensore;

    @FXML
    private Label labelValore;

    @FXML
    private TextFlow textFlowAlert;

    private String piantaUtenteId;
    private String sensoreId;

    private ScheduledService<DatiMisurazioni> scheduledService;

    /**
     * Imposta i parametri per il sensore e avvia il ScheduledService.
     * @param piantaUtenteId l'ID della piantaUtente.
     * @param sensoreId l'ID del sensore.
     */
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

    /**
     * Aggiorna la UI in base ai dati recuperati.
     * @param data I dati della misurazione.
     */
    private void updateUI(DatiMisurazioni data) {
        Platform.runLater(() -> {
            labelNomeSensore.setText(data.getNomeSensore());
            labelValore.setText(data.getValore() + " " + data.getUnitaMisura());
            updateArc(data.getValore(), data.getNomeSensore());
        });
    }

    /**
     * Aggiorna l'arc in base al valore e cambia anche il colore in base al nome del sensore.
     * @param valore Il valore della misurazione.
     * @param nomeSensore Il nome del sensore.
     */
    private void updateArc(double valore, String nomeSensore) {
        double angle = calculateAngle(valore, nomeSensore);
        arcValore.setLength(angle);
        updateArcColor(nomeSensore);
    }

    /**
     * Calcola l'angolo normalizzato (massimo 360 gradi) in base al valore e al tipo di sensore.
     * @param valore Il valore della misurazione.
     * @param nomeSensore Il nome del sensore.
     * @return L'angolo in gradi.
     */
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

    /**
     * Aggiorna il colore dell'arc in base al nome del sensore.
     * @param nomeSensore Il nome del sensore.
     */
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
}
