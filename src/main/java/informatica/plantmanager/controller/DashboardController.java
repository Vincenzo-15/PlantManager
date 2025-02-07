package informatica.plantmanager.controller;

import informatica.plantmanager.model.AggiornaSensori;
import informatica.plantmanager.model.Utente;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class DashboardController {

    @FXML
    private ImageView dashboardIcon;

    @FXML
    private ImageView exportIcon;

    @FXML
    private ImageView profilePic;

    @FXML
    private ImageView plantIcon;

    @FXML
    private AnchorPane changeComponent;

    private List<ImageView> icons;

    private Utente utente;

    private boolean _firstLogin = true;

    private ScheduledService<Boolean> aggiornamentoSensori;

    @FXML
    public void initialize() {
        icons = new ArrayList<>();
        icons.add(dashboardIcon);
        icons.add(plantIcon);
        icons.add(exportIcon);

        resetIcons();
        loadDashboardComponent();
        dashboardIcon.setOpacity(1.0);

        profilePic.setOnMouseClicked(this::profilePicClick);

    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        System.out.println("Utente loggato: " + utente.getNickname());
        System.out.println(utente.getId());
        startAggiornamentoSensori();
    }

    private void resetIcons() {
        for (ImageView icon : icons) {
            icon.setOpacity(0.5);
        }
    }

    private void loadDashboardComponent() {
        try {
            if (_firstLogin) {
               Platform.runLater(() -> {
                   try {
                       FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/DashboardPanel.fxml"));
                       AnchorPane firstLoginComponent = loader.load();

                       DashboardPanelController firstLoginController = loader.getController();
                       firstLoginController.setUtente(utente);

                       changeComponent.getChildren().setAll(firstLoginComponent);
                       _firstLogin = false;
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               });
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/DashboardPanel.fxml"));
            AnchorPane dashboardPanel = loader.load();

            DashboardPanelController dashboardPanelController = loader.getController();
            if (utente != null) {
                dashboardPanelController.setUtente(utente);
            }

            changeComponent.getChildren().setAll(dashboardPanel);
            //applySavedFontStyle(changeComponent.getScene());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMyPlantComponent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/MyPlantComponent.fxml"));
            AnchorPane myPlantComponent = loader.load();

            MyPlantViewController myPlantController = loader.getController();
            myPlantController.setUtente(utente);

            changeComponent.getChildren().setAll(myPlantComponent);
            //applySavedFontStyle(changeComponent.getScene());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExportPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/ExportPage.fxml"));
            AnchorPane exportPage = loader.load();

            ExportPageController exportPageController = loader.getController();
            exportPageController.setUtente(utente);

            changeComponent.getChildren().setAll(exportPage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //private void loadProfileMenuComponent() {
    //    try {
    //        FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/MenuProfile.fxml"));
    //        AnchorPane profileMenu = loader.load();
//
    //        ProfileMenuController profileMenuController = loader.getController();
//
//
    //        changeComponent.getChildren().setAll(profileMenu);
    //        //applySavedFontStyle(changeComponent.getScene());
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //}

    @FXML
    void dashboardIconClick(MouseEvent event) {
        resetIcons();
        dashboardIcon.setOpacity(1.0);
        loadDashboardComponent();
    }

    @FXML
    void exportIconClick(MouseEvent event) {
        resetIcons();
        exportIcon.setOpacity(1.0);
        loadExportPage();
    }

    @FXML
    void plantIconClick(MouseEvent event) {
        resetIcons();
        plantIcon.setOpacity(1.0);
        loadMyPlantComponent();
    }

    @FXML
    void profilePicClick(MouseEvent event) {
        resetIcons();
        profilePic.setOpacity(1.0);
        //loadProfileMenuComponent();
    }

    private void startAggiornamentoSensori() {
        aggiornamentoSensori = new ScheduledService<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                AggiornaSensori service = new AggiornaSensori();
                return service.createTask();
            }
        };
        aggiornamentoSensori.setPeriod(Duration.seconds(10));
        aggiornamentoSensori.setOnSucceeded(e -> {
            Boolean success = aggiornamentoSensori.getValue();
            if (success) {
                System.out.println("Aggiornamento sensori eseguito con successo.");
            } else {
                System.err.println("Errore durante l'aggiornamento dei sensori.");
            }
        });
        aggiornamentoSensori.setOnFailed(e -> {
            Throwable error = aggiornamentoSensori.getException();
            System.err.println("Errore nel servizio di aggiornamento: " + error.getMessage());
        });
        aggiornamentoSensori.start();
    }

    public AnchorPane getChangeComponent() {
        return changeComponent;
    }

    private void applySavedFontStyle(Scene scene) {
        String savedFont = loadFontStyle();
        if (savedFont != null && scene != null) {
            scene.getRoot().lookupAll(".label").forEach(node -> {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-font-family: '" + savedFont + "';");
                }
            });
        }
    }

    private String loadFontStyle() {
        try (FileReader reader = new FileReader("config.properties")) {
            Properties properties = new Properties();
            properties.load(reader);
            return properties.getProperty("fontStyle");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
