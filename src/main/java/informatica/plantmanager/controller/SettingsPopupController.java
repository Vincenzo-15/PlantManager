package informatica.plantmanager.controller;

import informatica.plantmanager.model.AggiornaTema;
import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SettingsPopupController {

    @FXML
    private Button buttonSave;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ToggleButton darkButton;

    @FXML
    private ToggleButton lightButton;

    @FXML
    private CheckBox notificheCheckBox;

    @FXML
    private Button logoutButton;

    private String currentTheme;
    private boolean notificheAttive;

    private Utente utente;

    // Service per aggiornare le impostazioni
    private final AggiornaTema aggiornaTemaService = new AggiornaTema();

    @FXML
    void initialize() {
        // 🔸 Gestione toggle tema
        darkButton.setOnAction(event -> {
            if (darkButton.isSelected()) lightButton.setSelected(false);
        });
        lightButton.setOnAction(event -> {
            if (lightButton.isSelected()) darkButton.setSelected(false);
        });

        // 🔸 Logout
        logoutButton.setOnAction(event -> handleLogout());
    }

    /**
     * 🔹 Gestione logout utente — torna alla schermata di login senza aprire nuove finestre
     */
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/Login.fxml"));
            Parent loginRoot = loader.load();

            Stage popupStage = (Stage) rootPane.getScene().getWindow();
            Stage mainStage = (Stage) (popupStage.getOwner() != null ? popupStage.getOwner() : popupStage);

            popupStage.close();

            Scene loginScene = new Scene(loginRoot);
            mainStage.setScene(loginScene);
            mainStage.setTitle("Login");
            mainStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento della scena di login: " + e.getMessage());
        }
    }

    /**
     * 🔹 Imposta il tema corrente all’apertura della finestra
     */
    public void setTheme(String theme) {
        this.currentTheme = theme;
        applyTheme(theme);
        if (Objects.equals(currentTheme, "Chiaro")) {
            lightButton.setSelected(true);
        } else {
            darkButton.setSelected(true);
        }
    }

    /**
     * 🔹 Imposta lo stato delle notifiche
     */
    public void setNotifiche(boolean notifiche) {
        this.notificheAttive = notifiche;
        notificheCheckBox.setSelected(notifiche);
    }

    /**
     * 🔹 Cambia dinamicamente il CSS del tema
     */
    public void applyTheme(String theme) {
        Scene scene = rootPane.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            if ("Scuro".equalsIgnoreCase(theme)) {
                scene.getStylesheets().add(getClass().getResource("/informatica/styles/darkTheme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/informatica/styles/lightTheme.css").toExternalForm());
            }
        }

        Stage popupStage = (Stage) rootPane.getScene().getWindow();
        if (popupStage.getOwner() != null) {
            Scene mainScene = popupStage.getOwner().getScene();
            if (mainScene != null) {
                mainScene.getStylesheets().clear();
                if ("Scuro".equalsIgnoreCase(theme)) {
                    mainScene.getStylesheets().add(getClass().getResource("/informatica/styles/darkTheme.css").toExternalForm());
                } else {
                    mainScene.getStylesheets().add(getClass().getResource("/informatica/styles/lightTheme.css").toExternalForm());
                }
            }
        }
    }

    /**
     * 🔹 Salva tutte le impostazioni dell’utente
     */
    @FXML
    void saveSettings(MouseEvent event) {
        if (utente == null) {
            System.err.println("Errore: Utente non impostato.");
            return;
        }

        String nuovoTema = darkButton.isSelected() ? "Scuro" : "Chiaro";
        boolean notifiche = notificheCheckBox.isSelected();

        // 🔹 Aggiorna il service con i nuovi valori
        aggiornaTemaService.setTema(utente.getId(), nuovoTema, notifiche);

        aggiornaTemaService.setOnSucceeded(workerStateEvent -> {
            if (aggiornaTemaService.getValue()) {
                System.out.println("✅ Impostazioni aggiornate con successo.");
                this.currentTheme = nuovoTema;
                this.notificheAttive = notifiche;

                applyTheme(nuovoTema);;

                Stage stage = (Stage) buttonSave.getScene().getWindow();
                stage.close();
            } else {
                System.err.println("❌ Errore nell'aggiornamento delle impostazioni.");
            }
        });

        aggiornaTemaService.setOnFailed(workerStateEvent -> {
            Throwable error = aggiornaTemaService.getException();
            System.err.println("Errore aggiornamento impostazioni: " + error.getMessage());
        });

        if (!aggiornaTemaService.isRunning()) {
            aggiornaTemaService.restart();
        }
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}

