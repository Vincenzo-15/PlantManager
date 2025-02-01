package informatica.plantmanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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

    @FXML
    public void initialize() {
        GridPane gridPane = new GridPane();
        anchorPaneScroll.getChildren().add(gridPane); // Aggiungi il GridPane all'AnchorPane

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 3; j++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/MyPlantView.fxml"));
                    AnchorPane myPlantComponent = loader.load();
                    gridPane.add(myPlantComponent, j, i); // Aggiungi alla prima colonna, righe diverse
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
