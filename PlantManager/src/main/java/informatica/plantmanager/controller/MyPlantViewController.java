package informatica.plantmanager.controller;

import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MyPlantViewController {

    @FXML
    private AnchorPane anchorPaneScroll;

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

    private Utente utente;


    @FXML
    public void initialize() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 0, 0, 5));
        gridPane.setHgap(20);
        gridPane.setVgap(25);
        anchorPaneScroll.getChildren().add(gridPane);

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 3; j++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/MyPlantView.fxml"));
                    AnchorPane myPlantComponent = loader.load();
                    gridPane.add(myPlantComponent, j, i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
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
