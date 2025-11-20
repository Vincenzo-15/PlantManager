//package informatica.plantmanager.controller;
//
//import informatica.plantmanager.model.PlantAlert;
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.stage.Stage;
//
///**
// * Controller per la finestra popup di notifica delle piante.
// * Gestisce la visualizzazione delle informazioni e la chiusura controllata del popup.
// */
//public class NotificationPopupController {
//
//    @FXML
//    private Label titleLabel;
//    @FXML
//    private Label nameLabel;
//    @FXML
//    private Label positionLabel;
//    @FXML
//    private Label suggestLabel;
//    @FXML
//    private Button closeButton;
//    @FXML
//    private Button planButton;
//
//    private PlantAlert alert;
//
//    /**
//     * Imposta i dati dell'alert da mostrare nel popup.
//     *
//     * @param alert oggetto contenente le informazioni della pianta
//     */
//    public void setAlert(PlantAlert alert) {
//        this.alert = alert;
//
//        titleLabel.setText("⚠️ Avviso per la tua pianta!");
//        nameLabel.setText("Pianta: " + alert.getNomePianta() + " (Salute: " + alert.getSalute() + "%)");
//
//        if (alert.getPosizione() == null || alert.getPosizione().isBlank()) {
//            positionLabel.setText("Posizione: Non specificata");
//        } else {
//            positionLabel.setText("Posizione: " + alert.getPosizione());
//        }
//
//        if (alert.getSuggerimento() == null || alert.getSuggerimento().isBlank()) {
//            suggestLabel.setText("Suggerimento: Controlla le condizioni della pianta.");
//        } else {
//            suggestLabel.setText("Suggerimento: " + alert.getSuggerimento());
//        }
//    }
//
//    @FXML
//    public void initialize() {
//        // Imposta il comportamento del bottone di chiusura
//        closeButton.setOnAction(e -> chiudiPopup());
//    }
//
//    /**
//     * Chiude in modo sicuro il popup e notifica il controller principale.
//     * Usa Platform.runLater per assicurarsi che l’operazione sia eseguita nel thread JavaFX.
//     */
//    private void chiudiPopup() {
//        Platform.runLater(() -> {
//            Stage stage = (Stage) closeButton.getScene().getWindow();
//            if (stage != null) {
//                stage.close();
//            }
//        });
//    }
//}

package informatica.plantmanager.controller;

import informatica.plantmanager.model.PlantAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller per la finestra popup di notifica delle piante.
 * Gestisce la visualizzazione delle informazioni e la chiusura controllata del popup.
 */
public class NotificationPopupController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label positionLabel;
    @FXML
    private Label suggestLabel;
    @FXML
    private Button closeButton;
    @FXML
    private Button planButton;

    private PlantAlert alert;

    // 🔹 riferimento al controller principale (Dashboard)
    private DashboardController dashboardController;


    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setAlert(PlantAlert alert) {
        this.alert = alert;

        titleLabel.setText("⚠️ Avviso per la tua pianta!");
        nameLabel.setText("Pianta: " + alert.getNomePianta() + " (Salute: " + alert.getSalute() + "%)");

        if (alert.getPosizione() == null || alert.getPosizione().isBlank()) {
            positionLabel.setText("Posizione: Non specificata");
        } else {
            positionLabel.setText("Posizione: " + alert.getPosizione());
        }

        if (alert.getSuggerimento() == null || alert.getSuggerimento().isBlank()) {
            suggestLabel.setText("Suggerimento: Controlla le condizioni della pianta.");
        } else {
            suggestLabel.setText("Suggerimento: " + alert.getSuggerimento());
        }
    }

    @FXML
    public void initialize() {
        // 🔸 Chiudi popup
        closeButton.setOnAction(e -> chiudiPopup());

        // 🔸 Apre la pianificazione attività per questa pianta
       planButton.setOnAction(e -> apriPianificazione());
    }

    /**
     * Chiude in modo sicuro il popup e notifica il controller principale.
     * Usa Platform.runLater per assicurarsi che l’operazione sia eseguita nel thread JavaFX.
     */
    private void chiudiPopup() {
        Platform.runLater(() -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });
    }

    /**
     * 🔹 Chiude il popup e apre la pagina della pianificazione attività per la pianta.
     */
    private void apriPianificazione() {
        Platform.runLater(() -> {
            System.out.println("Clic su pianifica attività nel popup");
            Stage stage = (Stage) planButton.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
            // Se il controller principale è disponibile, apri la pianificazione
            if (dashboardController != null && alert != null) {
                System.out.println("Apertura pianificazione per pianta: " + alert.getNomePianta());
                dashboardController.openActivityPlannerForPlant(alert.getPiantaUtenteId());
            } else {
                System.err.println("⚠️ Impossibile aprire la pianificazione: controller principale non impostato.");
            }
        });
    }
}

