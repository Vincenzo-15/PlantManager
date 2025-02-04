package informatica.plantmanager.controller;

import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class PlantPageDashboardController {

    @FXML
    private Label labelNomePianta;

    @FXML
    private Label labelPosizionePianta;

    @FXML
    private GridPane sensorGridPanel;

    private Utente utente;

    private void caricaElementi() {
        int rows = sensorGridPanel.getRowConstraints().size();
        int columns = sensorGridPanel.getColumnConstraints().size();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddSensorComponent.fxml"));
                    AnchorPane addPlantComponent = loader.load();

                    AddSensorController addSensorController = loader.getController();
                    addSensorController.setUtente(utente);

                    sensorGridPanel.add(addPlantComponent, col, row);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        caricaElementi();
    }

}

