package informatica.plantmanager.controller;

import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AddPlantDashboardController {

    @FXML
    private AnchorPane addPlant;

    private Utente utente;

    @FXML
    void addPlant(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddMyPlantPopup.fxml"));
            AnchorPane popupRoot = loader.load();

            AddMyPlantPopupController popupController = loader.getController();
            popupController.setUtente(utente);

            Scene scene = new Scene(popupRoot);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}


