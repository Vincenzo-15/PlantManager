package informatica.plantmanager.controller;

import informatica.plantmanager.model.CaricaLayoutPiante;
import informatica.plantmanager.model.PlantComboItem;
import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

    public void setUtente(Utente utente) {
        this.utente = utente;
        labelNickname.setText(utente.getNickname());
        caricaGriglia();
    }

    void caricaGriglia() {
        // Otteniamo il numero di righe e colonne dalla griglia
        int rows = plantGridPanel.getRowConstraints().size();
        int columns = plantGridPanel.getColumnConstraints().size();
        plantGridPanel.getChildren().clear();

        // Recupera le piante già posizionate nella dashboard
        CaricaLayoutPiante service = new CaricaLayoutPiante();
        service.setUtenteId(utente.getId());
        service.setOnSucceeded(event -> {
            List<PlantComboItem> layoutItems = service.getValue();
            // Crea una mappa che associa "row,col" all'oggetto PlantComboItem
            Map<String, PlantComboItem> layoutMap = new HashMap<>();
            for (PlantComboItem item : layoutItems) {
                layoutMap.put(item.getPosizione(), item);
            }

            // Popola la griglia
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    String key = row + "," + col;
                    try {
                        FXMLLoader loader;
                        if (layoutMap.containsKey(key)) {
                            loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/PlantDashboardView.fxml"));
                            AnchorPane plantDashboardView = loader.load();
                            PlantComponentController controller = loader.getController();
                            PlantComboItem item = layoutMap.get(key);
                            controller.setPlantData(item.getNome());
                            controller.setPlantId(item.getPlantUtenteId());
                            controller.setUtente(utente);
                            plantGridPanel.add(plantDashboardView, col, row);
                        } else {
                            loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddPlantComponent.fxml"));
                            AnchorPane addPlantComponent = loader.load();
                            AddPlantDashboardController controller = loader.getController();
                            controller.setDashboardPanelController(this);                            controller.setUtente(utente);
                            controller.setGridPosition(row, col);
                            plantGridPanel.add(addPlantComponent, col, row);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        service.setOnFailed(event -> {
            Throwable error = service.getException();
            System.err.println("Errore nel recupero del layout della dashboard: " + error.getMessage());
        });
        service.start();

        buttonLeft.setOnMouseClicked(e -> scrollLeft());
        buttonRight.setOnMouseClicked(e -> scrollRight());
    }

    private void scrollLeft() {
        double currentValue = scrollPane.getHvalue();
        if (currentValue > 0 && currentValue <= 0.5) {
            scrollPane.setHvalue(0);
        } else if (currentValue > 0.5 && currentValue <= 1) {
            scrollPane.setHvalue(0.5);
        }
    }

    private void scrollRight() {
        double currentValue = scrollPane.getHvalue();
        if (currentValue >= 0 && currentValue < 0.5) {
            scrollPane.setHvalue(0.5);
        } else if (currentValue >= 0.5 && currentValue < 1) {
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
