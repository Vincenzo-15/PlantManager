package informatica.plantmanager.controller;

import informatica.plantmanager.model.Pianta;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Arc;

public class PlantComponentController {

    @FXML
    private Arc healtArc;

    @FXML
    private Label labelPlantName;

    @FXML
    private Arc lightArc;

    @FXML
    private Arc waterArc;

    public void setPlantData(Pianta plant) {
        labelPlantName.setText(plant.getNome());

        double waterNormalized = (plant.getAcqua() - 0.2) / (1.0 - 0.2);
        waterArc.setLength(waterNormalized * 360);

        double lightNormalized = (plant.getLuce() - 200) / 600.0;
        lightArc.setLength(lightNormalized * 360);
    }
}
