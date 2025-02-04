package informatica.plantmanager.controller;

import informatica.plantmanager.model.CaricaListaSensori;
import informatica.plantmanager.model.AggiungiSensori;
import informatica.plantmanager.model.Sensore;
import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AddSensorPopupController {

    @FXML
    private Button addSensorButton;

    @FXML
    private Button buttonAnnulla;

    @FXML
    private ComboBox<Sensore> comboBoxSensori;

    private Utente utente;
    private String pianteUtenteId;
    private String posizioneGriglia;
    private Runnable onCloseCallback; // Add this field

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public void setPianteUtenteId(String pianteUtenteId) {
        this.pianteUtenteId = pianteUtenteId;
        System.out.println("PianteUtenteId impostato: " + pianteUtenteId);
        loadSensoriDisponibili();
    }

    public void setPosizioneGriglia(String posizioneGriglia) {
        this.posizioneGriglia = posizioneGriglia;
        System.out.println("Posizione griglia impostata: " + posizioneGriglia);
    }

    public void setOnCloseCallback(Runnable onCloseCallback) { // Add this setter
        this.onCloseCallback = onCloseCallback;
    }

    public void loadSensoriDisponibili() {
        if (pianteUtenteId == null || pianteUtenteId.trim().isEmpty()) {
            System.err.println("PianteUtenteId non impostato.");
            return;
        }
        CaricaListaSensori service = new CaricaListaSensori();
        service.setPlantId(pianteUtenteId);
        service.setOnSucceeded(event -> {
            comboBoxSensori.getItems().clear();
            comboBoxSensori.getItems().addAll(service.getValue());
        });
        service.setOnFailed(event -> {
            Throwable error = service.getException();
            System.err.println("Errore nel caricamento dei sensori disponibili: " + error.getMessage());
        });
        service.start();
    }

    @FXML
    void addSensor(MouseEvent event) {
        Sensore sensoreSelezionato = comboBoxSensori.getSelectionModel().getSelectedItem();
        if (sensoreSelezionato == null) {
            System.err.println("Seleziona un sensore dalla lista.");
            return;
        }
        if (posizioneGriglia == null || posizioneGriglia.trim().isEmpty()) {
            System.err.println("Posizione griglia non impostata.");
            return;
        }

        AggiungiSensori insertService = new AggiungiSensori();
        insertService.setParameters(pianteUtenteId, sensoreSelezionato.getId(), posizioneGriglia);
        insertService.setOnSucceeded(e -> {
            if (insertService.getValue()) {
                System.out.println("Sensore assegnato alla pianta con successo.");
                closePopup();
            } else {
                System.err.println("Errore nell'inserimento del sensore per la pianta.");
            }
        });
        insertService.setOnFailed(e -> {
            Throwable error = insertService.getException();
            System.err.println("Errore nel service di inserimento sensore: " + error.getMessage());
        });
        insertService.start();
    }

    @FXML
    void closePopup(MouseEvent event) {
        closePopup();
    }

    private void closePopup() {
        Stage stage = (Stage) addSensorButton.getScene().getWindow();
        stage.close();
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }
}
