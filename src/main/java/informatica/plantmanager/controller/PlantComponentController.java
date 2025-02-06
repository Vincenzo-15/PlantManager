package informatica.plantmanager.controller;

import informatica.plantmanager.model.DatiPiante;
import informatica.plantmanager.model.Pianta;
import informatica.plantmanager.model.RecuperaDatiPiante;
import informatica.plantmanager.model.Utente;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    @FXML
    private ImageView iconview;

    private Utente utente;
    private String plantId;
    private String plantPosition;

    @FXML
    private AnchorPane plantArea;

    public void setPlantData(Pianta plant) {
        labelPlantName.setText(plant.getNome());
    }

    public void setPlantData(String nome) {
        System.out.println("Nome pianta: " + nome);
        //labelPlantName.setText(nome);
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
        RecuperaDatiPiante service = new RecuperaDatiPiante();
        service.setPlantId(plantId);
        service.setOnSucceeded(event -> {
            DatiPiante dati = service.getValue();
            if (dati != null) {

                updateArc(healtArc, dati.getSalute(), 100);
                updateArc(waterArc, dati.getAcqua(), 500);
                updateArc(lightArc, dati.getLuce(), 600);

                if (dati.getAcqua() <= 0) {
                    waterArc.setDisable(true);
                } else {
                    waterArc.setDisable(false);
                }
                if (dati.getLuce() <= 0) {
                    lightArc.setDisable(true);
                } else {
                    lightArc.setDisable(false);
                }
                if (dati.getSalute() < 0) {
                    healtArc.setDisable(true);
                } else {
                    healtArc.setDisable(false);
                }

                if (dati.getPercorsoImmagine() != null && !dati.getPercorsoImmagine().isEmpty()) {
                    String imagePath = dati.getPercorsoImmagine();
                    System.out.println("Immagine caricata: " + imagePath);
                    Image image = new Image(getClass().getResourceAsStream(imagePath));
                    iconview.setImage(image);
                }
            }
        });
        service.setOnFailed(event -> {
            Throwable error = service.getException();
            System.err.println("Errore nel recupero dei dati della pianta: " + error.getMessage());
        });
        service.start();
    }

    private void updateArc(Arc arc, double valore, double maxValore) {
        final double calculatedAngle = (valore / maxValore) * 360;
        final double angle = calculatedAngle > 360 ? 360 : calculatedAngle;
        Platform.runLater(() -> arc.setLength(angle));
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
            // plantPageDashboardController.setPosizionePianta(plantPosition);

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

