package informatica.plantmanager.controller;

import informatica.plantmanager.model.Pianta;
import informatica.plantmanager.model.RecuperaPosizionePianta;
import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Arc;

import java.io.IOException;

public class PlantComponentController {

    @FXML
    private Arc healtArc;

    @FXML
    private Label labelPlantName;

    @FXML
    private Arc lightArc;

    @FXML
    private Arc waterArc;

    private Utente utente;
    private String plantId;
    private String plantPosition; // Campo per memorizzare la posizione recuperata

    @FXML
    private AnchorPane plantArea;

    public void setPlantData(Pianta plant) {
        labelPlantName.setText(plant.getNome());

        double waterNormalized = (plant.getAcqua() - 0.2) / (1.0 - 0.2);
        waterArc.setLength(waterNormalized * 360);

        double lightNormalized = (plant.getLuce() - 200) / 600.0;
        lightArc.setLength(lightNormalized * 360);
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
        RecuperaPosizionePianta service = new RecuperaPosizionePianta();
        service.setPlantId(plantId);
        service.setOnSucceeded(event -> {
            plantPosition = service.getValue();
            System.out.println("Posizione recuperata per la pianta " + plantId + ": " + plantPosition);
        });
        service.setOnFailed(event -> {
            System.err.println("Errore nel recupero della posizione della pianta.");
        });
        service.start();
    }

    @FXML
    void openPlantPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/PlantPageDashboard.fxml"));
            AnchorPane plantPageDashboard = loader.load();

            PlantPageDashboardController plantPageDashboardController = loader.getController();
            plantPageDashboardController.setUtente(utente);
            plantPageDashboardController.setPlantId(plantId);
            plantPageDashboardController.setNomePianta(labelPlantName.getText());
            if (plantPosition != null) {
                plantPageDashboardController.setPosizionePianta(plantPosition);
            }

            DashboardController dashboardController = (DashboardController) plantArea.getScene().getUserData();
            dashboardController.getChangeComponent().getChildren().setAll(plantPageDashboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}
