package informatica.plantmanager.controller;

import informatica.plantmanager.model.CaricaPianteUtente;
import informatica.plantmanager.model.Pianta;
import informatica.plantmanager.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class MyPlantViewController {

    @FXML
    private AnchorPane anchorPaneScroll;

    @FXML
    private ScrollPane boxScrollPane;

    @FXML
    private AnchorPane buttonAddPlant;

    @FXML
    private AnchorPane buttonSearch;

    @FXML
    private Label labelTitolo;

    @FXML
    private ScrollPane plantScrollPane;

    @FXML
    private TextField searchBar;

    @FXML
    private ToggleButton toggleButtonPosizione;

    private Utente utente;

    private GridPane gridPane = new GridPane();

    private void loadUserPlants() {
        if (gridPane == null) {
            gridPane = new GridPane();
        } else {
            gridPane.getChildren().clear();
        }

        gridPane.setPadding(new Insets(5, 0, 0, 5));
        gridPane.setHgap(20);
        gridPane.setVgap(25);

        anchorPaneScroll.getChildren().clear();
        anchorPaneScroll.getChildren().add(gridPane);

        if (utente == null) {
            System.err.println("Utente non impostato.");
            return;
        }

        javafx.concurrent.Service<List<Pianta>> service = CaricaPianteUtente.getPlantsByUserService(utente.getId());
        service.setOnSucceeded(event -> {
            List<Pianta> plants = service.getValue();
            if (plants == null || plants.isEmpty()) {
                System.out.println("Nessuna pianta trovata per l'utente.");
                return;
            }
            ObservableList<Pianta> observablePlants = FXCollections.observableArrayList(plants);
            int row = 0, col = 0;
            for (Pianta plant : observablePlants) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/MyPlantView.fxml"));
                    AnchorPane plantComponent = loader.load();
                    PlantComponentController componentController = loader.getController();
                    componentController.setPlantData(plant);
                    gridPane.add(plantComponent, col, row);
                    col++;
                    if (col >= 3) {
                        col = 0;
                        row++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        service.setOnFailed(event -> {
            Throwable error = service.getException();
            System.err.println("Errore nel caricamento delle piante: " + error.getMessage());
        });
        service.start();
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        loadUserPlants();
    }

    @FXML
    void addPlant(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddPlantPopup.fxml"));
            AnchorPane popupRoot = fxmlLoader.load();

            AddPlantPopupController popupController = fxmlLoader.getController();
            popupController.setUtente(utente);

            Scene scene = new Scene(popupRoot);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
