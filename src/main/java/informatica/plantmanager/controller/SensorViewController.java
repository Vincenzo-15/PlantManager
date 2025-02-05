package informatica.plantmanager.controller;

import informatica.plantmanager.model.AggiornaValoreSensore;
import informatica.plantmanager.model.DatiMisurazioni;
import informatica.plantmanager.model.RecuperaValoriConsigliati;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

public class SensorViewController {

    @FXML
    private Arc arcValore;

    @FXML
    private Label labelNomeSensore;

    @FXML
    private ImageView imageViewIcona;

    @FXML
    private Label labelValore;

    @FXML
    private Label labelAlert;

    private String piantaUtenteId;
    private String sensoreId;
    private PlantPageDashboardController plantPageDashboardController;

    private ScheduledService<DatiMisurazioni> scheduledService;

    public void setDatiSensore(String piantaUtenteId, String sensoreId) {
        this.piantaUtenteId = piantaUtenteId;
        this.sensoreId = sensoreId;
        startScheduledService();
    }

    public void setPlantPageDashboardController(PlantPageDashboardController controller) {
        this.plantPageDashboardController = controller;
    }

    private void startScheduledService() {
        scheduledService = new ScheduledService<DatiMisurazioni>() {
            @Override
            protected Task<DatiMisurazioni> createTask() {
                AggiornaValoreSensore service = new AggiornaValoreSensore();
                service.setParameters(piantaUtenteId, sensoreId);
                return service.createTask();
            }
        };
        scheduledService.setPeriod(Duration.seconds(10));
        scheduledService.setOnSucceeded(event -> {
            DatiMisurazioni data = scheduledService.getValue();
            if (data != null) {
                updateUI(data);
            }
        });
        scheduledService.setOnFailed(event -> {
            Throwable error = scheduledService.getException();
            System.err.println("Errore nel recupero della misurazione: " + error.getMessage());
        });
        scheduledService.start();
    }

    private void updateUI(DatiMisurazioni data) {
        Platform.runLater(() -> {
            labelNomeSensore.setText(data.getNomeSensore());
            labelValore.setText(data.getValore() + " " + data.getUnitaMisura());
            updateArc(data.getValore(), data.getNomeSensore());
            updateAlert(data.getValore(), data.getNomeSensore());
        });
    }

    private void updateArc(double valore, String nomeSensore) {
        double angle = calculateAngleAndColor(valore, nomeSensore);
        arcValore.setLength(angle);
        //updateArcColor(nomeSensore);
    }

    private double calculateAngleAndColor(double valore, String nomeSensore) {
        double angle = 0;
        Color color;
        String imagePath;
        Image image;
        switch (nomeSensore.toLowerCase()) {
            case "acqua":
                angle = (valore / 500.0) * 360;
                color = Color.web("#29B6F6");
                imagePath = "/informatica/images/WaterIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                imageViewIcona.setFitWidth(22.97);
                imageViewIcona.setFitHeight(30.41);
                imageViewIcona.setX(9);
                imageViewIcona.setY(5);
                imageViewIcona.setImage(image);
                break;
            case "luce":
                angle = (valore / 600.0) * 360;
                color = Color.web("#FADE3F");
                imagePath = "/informatica/images/SunIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                imageViewIcona.setFitWidth(38.59);
                imageViewIcona.setFitHeight(38.58);
                imageViewIcona.setX(1);
                imageViewIcona.setY(2);
                imageViewIcona.setImage(image);
                break;
            case "umidita":
                angle = (valore / 100.0) * 360;
                color = Color.web("#29B6F6");
                imagePath = "/informatica/images/HumidityIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                imageViewIcona.setFitWidth(36.98);
                imageViewIcona.setFitHeight(31.36);
                imageViewIcona.setX(2);
                imageViewIcona.setY(6);
                imageViewIcona.setImage(image);
                break;
            case "temperatura":
                angle = (valore / 40.0) * 360;
                color = Color.web("#EC7171");
                imagePath = "/informatica/images/TermometerIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                imageViewIcona.setFitWidth(13);
                imageViewIcona.setFitHeight(39.36);
                imageViewIcona.setX(13);
                imageViewIcona.setY(1);
                imageViewIcona.setImage(image);
                break;
            case "ph":
                angle = ((valore - 5.5) / (7.5 - 5.5)) * 360;
                color = Color.web("#ED71BD");
                imagePath = "/informatica/images/PhIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                imageViewIcona.setFitWidth(22.97);
                imageViewIcona.setFitHeight(30.41);
                imageViewIcona.setX(9);
                imageViewIcona.setY(4);
                imageViewIcona.setImage(image);
                break;
            case "vento":
                angle = (valore / 100.0) * 360;
                color = Color.web("#5DD7E5");
                imagePath = "/informatica/images/WindIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                imageViewIcona.setFitWidth(30.86);
                imageViewIcona.setFitHeight(25.35);
                imageViewIcona.setX(5);
                imageViewIcona.setY(8);
                imageViewIcona.setImage(image);
                break;
            default:
                angle = valore * 10;
                color = Color.GRAY;
                break;
        }
        arcValore.setFill(color);
        arcValore.setStroke(color);

        if (plantPageDashboardController != null) {
            plantPageDashboardController.updateAngle(angle);
        }

        return Math.min(angle, 360);
    }

    private void updateAlert(double valoreMisurato, String nomeSensore) {
        RecuperaValoriConsigliati recService = new RecuperaValoriConsigliati();
        recService.setParameters(piantaUtenteId, nomeSensore);
        recService.setOnSucceeded(e -> {
            Double valoreConsigliato = recService.getValue();
            String alertMessage;
            if (valoreConsigliato == null) {
                alertMessage = "Nessun valore consigliato disponibile.";
            } else {
                if (valoreMisurato < 0.9 * valoreConsigliato) {
                    alertMessage = "Valore basso rispetto a quello consigliato";
                } else if (valoreMisurato > 1.1 * valoreConsigliato) {
                    alertMessage = "Valore alto rispetto a quello consigliato";
                } else {
                    alertMessage = "Valore nella norma";
                }
            }
            Platform.runLater(() -> {
                labelAlert.setText(alertMessage);
            });
        });
        recService.setOnFailed(e -> {
            Throwable error = recService.getException();
            System.err.println("Errore nel recupero del valore consigliato: " + error.getMessage());
        });
        recService.start();
    }
}

