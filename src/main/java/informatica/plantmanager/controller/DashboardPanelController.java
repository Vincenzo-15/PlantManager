package informatica.plantmanager.controller;

import informatica.plantmanager.model.SensorePianta;
import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DashboardPanelController {

    @FXML
    private GridPane plantGridPanel;

    @FXML
    private Label labelNickname;

    @FXML
    private Label buttonLeft;

    @FXML
    private Label buttonRight;

    @FXML
    private ScrollPane scrollPane;

    private Utente utente;

    private void caricaPianteUtente() {
        int rows = plantGridPanel.getRowConstraints().size();
        int columns = plantGridPanel.getColumnConstraints().size();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddPlantComponent.fxml"));
                    AnchorPane addPlantComponent = loader.load();

                    AddPlantDashboardController controller = loader.getController();
                    controller.setUtente(utente);

                    plantGridPanel.add(addPlantComponent, col, row);
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        buttonLeft.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> scrollLeft());
        buttonRight.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> scrollRight());
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        caricaPianteUtente();
        labelNickname.setText(utente.getNickname());
    }

    private void scrollLeft() {
        double currentValue = scrollPane.getHvalue();
        if (currentValue > 0 && currentValue <= 0.5) {
            scrollPane.setHvalue(0);
        }
        else if (currentValue > 0.5 && currentValue <= 1) {
            scrollPane.setHvalue(0.5);
        }
    }

    private void scrollRight() {
        double currentValue = scrollPane.getHvalue();
        if (currentValue >= 0 && currentValue < 0.5) {
            scrollPane.setHvalue(0.5);
        }
        else if (currentValue >= 0.5 && currentValue < 1) {
            scrollPane.setHvalue(1);
        }
    }

    @FXML
    void goLeft(MouseEvent event) {
        scrollLeft();
    }

    @FXML
    void goRight(MouseEvent event) {
        scrollRight();
    }
}