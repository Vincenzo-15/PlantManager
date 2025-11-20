package informatica.plantmanager.controller;

import informatica.plantmanager.model.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddSensorPopupController {

    @FXML
    private Button addSensorButton;

    @FXML
    private Button buttonAnnulla;

    @FXML
    private Label labelAvviso;

    @FXML
    private ComboBox<Sensore> comboBoxSensori;

    @FXML
    private AnchorPane rootPane;

    private Utente utente;
    private String pianteUtenteId;
    private String posizioneGriglia;
    private Runnable onCloseCallback;
    private String currentTheme;

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public void setTheme (String theme) {
        this.currentTheme = theme;
        applyTheme(theme);
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

    public void applyTheme(String theme) {
        Scene scene = rootPane.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            if ("Scuro".equalsIgnoreCase(theme)) {
                scene.getStylesheets().add(getClass().getResource("/informatica/styles/darkTheme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/informatica/styles/lightTheme.css").toExternalForm());
            }
        }
    }


    public void setOnCloseCallback(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
    }

    public void loadSensoriDisponibili() {
        if (pianteUtenteId == null || pianteUtenteId.trim().isEmpty()) {
            labelAvviso.setText("Errore: Id pianta non impostato.");
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
            labelAvviso.setText("Errore nel caricamento dei sensori disponibili: " + error.getMessage());
            System.err.println("Errore nel caricamento dei sensori disponibili: " + error.getMessage());
        });
        service.start();
    }

    @FXML
    void addSensor(MouseEvent event) {
        Sensore sensoreSelezionato = comboBoxSensori.getSelectionModel().getSelectedItem();
        if (sensoreSelezionato == null) {
            labelAvviso.setText("Seleziona un sensore dalla lista");
            System.err.println("Seleziona un sensore dalla lista");
            return;
        }
        if (posizioneGriglia == null || posizioneGriglia.trim().isEmpty()) {
            labelAvviso.setText("Posizione griglia non impostata");
            System.err.println("Posizione griglia non impostata");
            return;
        }

        AggiungiSensori insertService = new AggiungiSensori();
        insertService.setParameters(pianteUtenteId, sensoreSelezionato.getId(), posizioneGriglia);
        insertService.setOnSucceeded(e -> {
            if (insertService.getValue()) {
                System.out.println("Sensore assegnato alla pianta con successo.");
                closePopup();
            } else {
                labelAvviso.setText("Errore nell'inserimento del sensore per la pianta");
                System.err.println("Errore nell'inserimento del sensore per la pianta");
            }
        });
        insertService.setOnFailed(e -> {
            Throwable error = insertService.getException();
            labelAvviso.setText("Errore nell'inserimento di inserimento sensore: " + error.getMessage());
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
