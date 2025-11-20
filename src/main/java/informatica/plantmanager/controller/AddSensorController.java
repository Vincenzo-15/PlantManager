package informatica.plantmanager.controller;

import informatica.plantmanager.model.ImpostazioniUtente;
import informatica.plantmanager.model.Utente;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


public class AddSensorController {

    @FXML
    private AnchorPane addSensorButton;

    @FXML
    private AnchorPane rootPane;

    private Utente utente;
    private String plantId;
    private String posizioneGriglia;
    private Runnable onCloseCallback;
    private String currentTheme;

    @FXML
    void addSensor(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddSensorPopup.fxml"));
            AnchorPane popupRoot = fxmlLoader.load();

            AddSensorPopupController popupController = fxmlLoader.getController();
            popupController.setUtente(utente);
            popupController.setPianteUtenteId(plantId);
            popupController.setPosizioneGriglia(posizioneGriglia);
            popupController.setOnCloseCallback(onCloseCallback);
            popupController.applyTheme(currentTheme);

            Platform.runLater(() -> {
                popupController.setTheme(currentTheme);
            });

            Scene scene = new Scene(popupRoot);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setOnCloseRequest((WindowEvent we) -> {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTheme (String theme) {
        this.currentTheme = theme;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public void setPosizioneGriglia(String posizioneGriglia) {
        this.posizioneGriglia = posizioneGriglia;
    }

    public void setOnCloseCallback(Runnable onCloseCallback) { // Add this setter
        this.onCloseCallback = onCloseCallback;
    }
}
