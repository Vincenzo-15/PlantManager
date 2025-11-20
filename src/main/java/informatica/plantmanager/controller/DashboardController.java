package informatica.plantmanager.controller;

import informatica.plantmanager.model.*;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.skin.LabeledSkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class DashboardController {

    @FXML
    private ImageView dashboardIcon;

    @FXML
    private ImageView exportIcon;

    @FXML
    private ImageView activityPlannerIcon;

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

    private ImpostazioniUtente impostazioni;

    private final Map<String, Integer> lastNotifiedSalute = new HashMap<>();
    private final int SOGLIA_NOTIFICA = 60;

    private final Queue<PlantAlert> codaNotifiche = new LinkedList<>();

    private boolean notificaInCorso = false;

    @FXML
    public void initialize() {
        icons = new ArrayList<>();
        icons.add(dashboardIcon);
        icons.add(plantIcon);
        icons.add(exportIcon);
        icons.add(activityPlannerIcon);

        resetIcons();
        loadDashboardComponent();
        dashboardIcon.setOpacity(1.0);
        profilePic.setOnMouseClicked(this::profilePicClick);

    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        System.out.println("Utente loggato: " + utente.getNickname());
        System.out.println(utente.getId());

        recuperaTema();
        startAggiornamentoSensori();
    }

    private void recuperaTema() {
        RecuperaImpostazioniUtente impostazioniService = new RecuperaImpostazioniUtente();
        impostazioniService.setUserId(utente.getId());

        impostazioniService.setOnSucceeded(event -> {
            impostazioni = impostazioniService.getValue();
            if (impostazioni != null) {
                System.out.println("Tema selezionato: " + impostazioni.getTema());
                System.out.println("Notifiche abilitate: " + impostazioni.isNotifiche());
                //boolean darkMode = "Scuro".equalsIgnoreCase(impostazioni.getTema());
                applyTheme(impostazioni.getTema());
            }
        });

        impostazioniService.setOnFailed(event -> {
            System.err.println("Errore nel recupero delle impostazioni utente.");
        });

        impostazioniService.start();
    }

    private void applyTheme(String theme) {
        Scene scene = changeComponent.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            String imagePath;
            Image image;
            if ("Scuro".equalsIgnoreCase(theme)) {
                scene.getStylesheets().add(getClass().getResource("/informatica/styles/darkTheme.css").toExternalForm());
                imagePath = "/informatica/images/SettingsIconDark.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                profilePic.setImage(image);
                imagePath = "/informatica/images/DashboardIconDark.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                dashboardIcon.setImage(image);
                imagePath = "/informatica/images/PlantIconDark.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                plantIcon.setImage(image);
                imagePath = "/informatica/images/ExportIconDark.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                exportIcon.setImage(image);
                imagePath = "/informatica/images/ActivityPlannerIconDark.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                activityPlannerIcon.setImage(image);
            } else {
                scene.getStylesheets().add(getClass().getResource("/informatica/styles/lightTheme.css").toExternalForm());
                imagePath = "/informatica/images/SettingsIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                profilePic.setImage(image);
                imagePath = "/informatica/images/DashboardIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                dashboardIcon.setImage(image);
                imagePath = "/informatica/images/PlantIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                plantIcon.setImage(image);
                imagePath = "/informatica/images/ExportIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                exportIcon.setImage(image);
                imagePath = "/informatica/images/ActivityPlannerIcon.png";
                image = new Image(getClass().getResourceAsStream(imagePath));
                activityPlannerIcon.setImage(image);
                System.out.println("Tema chiaro applicato.");
            }
        }
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
                       Platform.runLater(() -> {
                           firstLoginController.setTheme(impostazioni.getTema());
                       });

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
                Platform.runLater(() -> {
                    dashboardPanelController.setTheme(impostazioni.getTema());
                });
            }

            changeComponent.getChildren().setAll(dashboardPanel);
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
            Platform.runLater(() -> {
                myPlantController.setTheme(impostazioni.getTema());
            });

            changeComponent.getChildren().setAll(myPlantComponent);
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

    public void openActivityPlanner() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/ActivityPlanner.fxml"));
            AnchorPane activityPage = loader.load();


            ActivityPlannerController activityPageController = loader.getController();
            activityPageController.setUtente(utente);
            Platform.runLater(() -> {
                activityPageController.setTheme(impostazioni.getTema());
            });

            changeComponent.getChildren().setAll(activityPage);
            System.out.println("Activity Planner aperto.");

        } catch (IOException e) {
            System.err.println("Errore nell'apertura dell'Activity Planner: ");
            e.printStackTrace(); }
    }

    public void openActivityPlannerForPlant(String plantUtenteId) {
        try {
            System.out.println("Apertura Activity Planner per pianta utente ID: " + plantUtenteId);

            System.out.println("Utente corrente in DashboardController: " + (utente != null ? utente.getNickname() : "❌ NULL"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/ActivityPlanner.fxml"));
            AnchorPane activityPage = loader.load();

            ActivityPlannerController controller = loader.getController();
            controller.setUtente(utente);
            Platform.runLater(() -> {
                controller.setTheme(impostazioni.getTema());
            });

            //changeComponent.getChildren().setAll(activityPage);
            if(changeComponent != null) {
                changeComponent.getChildren().setAll(activityPage);
                System.out.println(" ActivityPlanner.fxml caricato nel componente di cambio.");
            } else {
                System.out.println(" changeComponent è null, non posso caricare ActivityPlanner.fxml.");
            }
            // una volta caricato, puoi aprire il form per creare una attività precompilata
            //Platform.runLater(() -> controller.openActivityForm(LocalDate.now(), plantUtenteId));
            controller.openActivityForm(LocalDate.now(), plantUtenteId);
        } catch (IOException e) {
            System.out.println("Errore durante il caricamento di ActivityPlanner.fxml: ");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Errore generico nell'apertura dell'Activity Planner per pianta: ");
            e.printStackTrace();
        }
    }

    private void loadSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/SettingsPopup.fxml"));
            AnchorPane popupRoot = fxmlLoader.load();

            SettingsPopupController popupController = fxmlLoader.getController();
            popupController.setUtente(utente);
            Platform.runLater(() -> {
                popupController.setTheme(impostazioni.getTema());
                popupController.setNotifiche(impostazioni.isNotifiche());
            });

            Scene scene = new Scene(popupRoot);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);

            //Imposta il popup come modale e assegna lo stage principale come owner
            stage.initOwner(changeComponent.getScene().getWindow());

            stage.setOnHidden(event -> {
                if (impostazioni != null) {
                    recuperaTema();
                    loadDashboardComponent();
                    dashboardIcon.setOpacity(1.0);
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
    void activityPlannerIconClick(MouseEvent event) {
        System.out.println("Apertura Activity Planner");
        resetIcons();
        activityPlannerIcon.setOpacity(1.0);
        openActivityPlanner();
    }

    @FXML
    void profilePicClick(MouseEvent event) {
        resetIcons();
        profilePic.setOpacity(1.0);
        loadSettings();
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
                System.out.println("✅ Aggiornamento sensori eseguito con successo.");
                System.out.println("🔎 Controllo notifiche per utente: " + utente.getNickname());

                if (impostazioni != null && impostazioni.isNotifiche()) {
                    RecuperaPianteConSaluteBassa alertService = new RecuperaPianteConSaluteBassa();
                    alertService.setUtenteId(utente.getId());
                    alertService.setSoglia(SOGLIA_NOTIFICA);

                    alertService.setOnSucceeded(ev -> {
                        var alerts = alertService.getValue();
                        System.out.println("📬 Servizio notifiche eseguito. Numero di avvisi: " + (alerts != null ? alerts.size() : 0));

                        if (alerts != null && !alerts.isEmpty()) {
                            boolean nuoveNotifiche = false;

                            for (var alert : alerts) {
                                String puId = alert.getPiantaUtenteId();
                                int salute = alert.getSalute();
                                Integer last = lastNotifiedSalute.get(puId);

                                System.out.println("➡️ Controllo pianta: " + alert.getNomePianta() + " (salute: " + salute + "%)");

                                // 🔸 Caso 1: pianta sotto soglia e peggiora → mostra notifica
                                if (salute < SOGLIA_NOTIFICA && (last == null || salute < last)) {
                                    System.out.println("🆕 Pianta peggiorata. Aggiungo alla coda notifica per: " + alert.getNomePianta());
                                    lastNotifiedSalute.put(puId, salute);
                                    codaNotifiche.add(alert);
                                    nuoveNotifiche = true;
                                }
                                // 🔸 Caso 2: pianta sotto soglia ma stazionaria o in leggero miglioramento → nessuna notifica
                                else if (salute < SOGLIA_NOTIFICA && last != null && salute >= last) {
                                    System.out.println("⏳ Pianta sotto soglia ma non peggiora. Nessuna nuova notifica per: " + alert.getNomePianta());
                                    lastNotifiedSalute.put(puId, salute); // aggiorna comunque il valore
                                }
                                // 🔸 Caso 3: pianta si riprende → reset monitoraggio
                                else if (salute >= SOGLIA_NOTIFICA && last != null) {
                                    System.out.println("🌿 La pianta " + alert.getNomePianta() + " si è ripresa. Resetto il monitoraggio.");
                                    lastNotifiedSalute.remove(puId);
                                }
                            }



                            // ✅ Se ci sono nuove notifiche e nessuna è in corso, mostra la prossima
                            if (nuoveNotifiche && !notificaInCorso) {
                                mostraProssimaNotifica();
                            }

                        } else {
                            System.out.println("ℹ️ Nessun avviso da notificare.");
                        }
                    });

                    alertService.setOnFailed(ev -> {
                        System.err.println("❌ Errore nel servizio di notifiche: " + alertService.getException());
                    });

                    alertService.start();
                }
            } else {
                System.err.println("❌ Errore durante l'aggiornamento dei sensori.");
            }
        });

        aggiornamentoSensori.setOnFailed(e -> {
            Throwable error = aggiornamentoSensori.getException();
            System.err.println("❌ Errore nel servizio di aggiornamento: " + error.getMessage());
        });

        aggiornamentoSensori.start();
    }


    public AnchorPane getChangeComponent() {
        return changeComponent;
    }

    // ✅ Mostra una sola notifica per volta (blocca finché non chiudi)
    private void mostraProssimaNotifica() {
        if (notificaInCorso || codaNotifiche.isEmpty()) return;

        notificaInCorso = true;
        PlantAlert alert = codaNotifiche.poll();

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/NotificationPopup.fxml"));
                AnchorPane popupRoot = loader.load();
                NotificationPopupController controller = loader.getController();
                controller.setAlert(alert);
                controller.setDashboardController(this);

                Scene scene = new Scene(popupRoot);
                if (impostazioni != null) {
                    if ("Scuro".equalsIgnoreCase(impostazioni.getTema())) {
                        scene.getStylesheets().add(getClass().getResource("/informatica/styles/darkTheme.css").toExternalForm());
                    } else {
                        scene.getStylesheets().add(getClass().getResource("/informatica/styles/lightTheme.css").toExternalForm());
                    }
                }

                Stage popup = new Stage();
                popup.initModality(Modality.APPLICATION_MODAL); // ✅ blocca la dashboard
                popup.initOwner(changeComponent.getScene().getWindow()); // legato alla finestra principale
                popup.setResizable(false);
                popup.setScene(scene);

                Stage owner = (Stage) changeComponent.getScene().getWindow();
                popup.setOnShown(ev -> {
                    double centerX = owner.getX() + owner.getWidth() / 2 - popup.getWidth() / 2;
                    double centerY = owner.getY() + owner.getHeight() / 2 - popup.getHeight() / 2;
                    popup.setX(centerX);
                    popup.setY(centerY);
                });

                popup.setOnHidden(e -> {
                    notificaInCorso = false;
                    mostraProssimaNotifica(); // mostra la successiva se presente
                });

                popup.show();

            } catch (Exception e) {
                e.printStackTrace();
                notificaInCorso = false;
            }
        });
    }
}

