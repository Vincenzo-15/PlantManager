package informatica.plantmanager.controller;

import informatica.plantmanager.model.RimuoviSensore;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class RemoveSensorPopupController {

    @FXML
    private Button buttonOk;

    @FXML
    private Button buttonCancel;

    @FXML
    private Label labelAvviso;

    @FXML
    private AnchorPane rootPane;

    private String piantaUtenteId;
    private String sensoreId;

    private PlantPageDashboardController plantPageDashboardController;

    private String currentTheme;


    public void setDashboardPanelController(PlantPageDashboardController plantPageDashboardController) {
        this.plantPageDashboardController = plantPageDashboardController;
    }
    public void setPiantaUtenteId(String piantaUtenteId) {
        this.piantaUtenteId = piantaUtenteId;
    }
    public void setSensoreId(String sensoreId) {
        this.sensoreId = sensoreId;
    }

    @FXML
    void deleteSensor(MouseEvent e){
        RimuoviSensore rimuoviSensoreService = new RimuoviSensore();
        System.out.println("Rimuovo sensore: " + sensoreId + " dalla pianta: " + piantaUtenteId);
        rimuoviSensoreService.setParameters(piantaUtenteId, sensoreId);
        rimuoviSensoreService.setOnSucceeded(event -> {
            System.out.println("Sensore rimosso con successo");
        });
        rimuoviSensoreService.setOnFailed(event -> {
            labelAvviso.setText("Errore nella rimozione del sensore");
            System.err.println("Errore nella rimozione del sensore");
        });
        rimuoviSensoreService.start();
        buttonOk.getScene().getWindow().hide();
        if (plantPageDashboardController != null) {
            plantPageDashboardController.caricaElementi();
        }
    }

    @FXML
    void closePopup(MouseEvent event) {
        buttonCancel.getScene().getWindow().hide();
        if (plantPageDashboardController != null) {
            plantPageDashboardController.caricaElementi();
        }
    }

    public void setTheme(String theme) {
        this.currentTheme = theme;
        applyTheme(theme);
    }

    private void applyTheme(String theme) {
        Scene scene = rootPane.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            if ("Scuro".equalsIgnoreCase(theme)) {
                scene.getStylesheets().add(getClass().getResource("/informatica/styles/darkTheme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/informatica/styles/lightTheme.css").toExternalForm());
                System.out.println("Tema chiaro applicato.");
            }
        }
    }
}

