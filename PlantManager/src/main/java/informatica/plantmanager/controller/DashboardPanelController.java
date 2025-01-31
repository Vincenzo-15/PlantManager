package informatica.plantmanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DashboardPanelController {

    @FXML
    private GridPane plantGridPanel;

    @FXML
    public void initialize() {
        int rows = plantGridPanel.getRowConstraints().size();
        int columns = plantGridPanel.getColumnConstraints().size();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddPlantComponent.fxml"));
                    AnchorPane addPlantComponent = loader.load();
                    plantGridPanel.add(addPlantComponent, col, row);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}