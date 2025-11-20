package informatica.plantmanager.controller;

import informatica.plantmanager.model.AttivitaPianta;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ActivityDetailsController {

    @FXML
    private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label plantLabel;
    @FXML private Label timeLabel;
    @FXML private Label recurrencyLabel;
    @FXML private TextArea notesArea;
    @FXML private Button editButton;
    @FXML private Button closeButton;
    @FXML private AnchorPane rootPane;

    private AttivitaPianta attivita;
    private Runnable onEditRequest;
    private String currentTheme;

    public void setData(AttivitaPianta attivita, String nomePianta, String posizione, Runnable onEditRequest) {
        this.attivita = attivita;
        this.onEditRequest = onEditRequest;

        titleLabel.setText(attivita.getTitolo());
        dateLabel.setText("📅 Data: " + attivita.getData());
        plantLabel.setText("🌱 Pianta: " + nomePianta + " — " + posizione);
        timeLabel.setText("⏱ Ora: " + (attivita.getOra() == null ? "-" : attivita.getOra().toString()));
        recurrencyLabel.setText("🔁 Ricorrenza: " + (attivita.getRicorrenza() == null ? "Nessuna" : attivita.getRicorrenza()));
        notesArea.setText(attivita.getDescrizione());
    }

    public void setTheme (String theme) {
        this.currentTheme = theme;
        Platform.runLater(() -> applyTheme(theme));
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

    @FXML
    private void initialize() {
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
        editButton.setOnAction(e -> {
            if (onEditRequest != null) onEditRequest.run();
            ((Stage) editButton.getScene().getWindow()).close();
        });
    }
}

