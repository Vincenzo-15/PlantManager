package informatica.plantmanager.controller;

import informatica.plantmanager.model.CaricaLayoutPiante;
import informatica.plantmanager.model.PlantComboItem;
import informatica.plantmanager.model.RecuperaSaluteMedia;
import informatica.plantmanager.model.Utente;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import java.util.Random;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardPanelController {

    @FXML
    private GridPane plantGridPanel;

    @FXML
    private ImageView heartIcon;

    @FXML
    private Label labelNickname;

    @FXML
    private Label labelHum;

    @FXML
    private Label labelTemp;

    @FXML
    private Label labelWin;

    @FXML
    private Label labelStatusPlant;

    @FXML
    private Label buttonLeft;

    @FXML
    private Label buttonRight;

    @FXML
    private ScrollPane scrollPane;

    private Utente utente;

    private ScheduledService<Integer> saluteMediaService;

    public void setUtente(Utente utente) {
        this.utente = utente;
        labelNickname.setText(utente.getNickname());

        Random rand = new Random();
        int temp = rand.nextInt(40);
        labelTemp.setText(temp + "°C");
        int hum = rand.nextInt(100);
        labelHum.setText(hum + "%");
        int win = rand.nextInt(40);
        labelWin.setText(win + "km/h");

        caricaGriglia();
        startSaluteMediaService();
    }

    void caricaGriglia() {
        int rows = plantGridPanel.getRowConstraints().size();
        int columns = plantGridPanel.getColumnConstraints().size();
        plantGridPanel.getChildren().clear();

        CaricaLayoutPiante service = new CaricaLayoutPiante();
        service.setUtenteId(utente.getId());
        service.setOnSucceeded(event -> {
            List<PlantComboItem> layoutItems = service.getValue();

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
                            controller.setDashboardPanelController(this);
                            controller.setUtente(utente);
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

    private void startSaluteMediaService() {
        saluteMediaService = new ScheduledService<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                RecuperaSaluteMedia rs = new RecuperaSaluteMedia();
                rs.setUtenteId(utente.getId());
                return rs.createTask();
            }
        };
        saluteMediaService.setPeriod(Duration.seconds(10));
        saluteMediaService.setOnSucceeded(event -> {
            Integer average = saluteMediaService.getValue();
            if (average != null) {
                System.out.println("Salute media: " + average);
                updateHeartIconAndLabel(average);
            }
        });
        saluteMediaService.setOnFailed(event -> {
            Throwable error = saluteMediaService.getException();
            System.err.println("Errore nel recupero della salute media: " + error.getMessage());
        });
        saluteMediaService.start();
    }

    private void updateHeartIconAndLabel(int average) {
        String iconPath;
        String labelText;
        if (average == 100) {
            iconPath = "/informatica/images/Heart100.png";
            labelText = "Tutte le piante sono in perfetta salute";
        } else if (average > 75) {
            iconPath = "/informatica/images/Heart75.png";
            labelText = "Salute piante buona. Continua così";
        } else if (average > 50) {
            iconPath = "/informatica/images/Heart50.png";
            labelText = "Salute piante nella media. Puoi fare di meglio";
        } else if (average > 25) {
            iconPath = "/informatica/images/Heart25.png";
            labelText = "Salute piante bassa. Occorre attenzione";
        } else {
            iconPath = "/informatica/images/Heart0.png";
            labelText = "Piante in condizioni critiche";
        }
        Platform.runLater(() -> {
            Image image = new Image(getClass().getResourceAsStream(iconPath));
            heartIcon.setImage(image);
            labelStatusPlant.setText(labelText);
        });
    }
}
