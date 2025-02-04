package informatica.plantmanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class AddPlantDashboardController {

    @FXML
    private AnchorPane addPlant;

    @FXML
    void addPlant(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/PlantDashboardView.fxml"));
            AnchorPane plantDashboardView = loader.load();

            GridPane parentGrid = (GridPane) addPlant.getParent().getParent();
            Integer colIndex = GridPane.getColumnIndex(addPlant.getParent());
            Integer rowIndex = GridPane.getRowIndex(addPlant.getParent());

            parentGrid.add(plantDashboardView, colIndex, rowIndex);
            parentGrid.getChildren().remove(addPlant.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

