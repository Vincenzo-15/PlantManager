package informatica.plantmanager.controller;

import informatica.plantmanager.model.*;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ActivityFormController {

    @FXML private TextField titleField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> plantComboBox;
    @FXML private TextArea notesArea;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private AnchorPane rootPane;

    private final java.util.Map<String, String> plantLabelToIdMap = new java.util.HashMap<>(); // label → id

    private Utente utente;
    private AttivitaPianta attivitaEsistente = null;
    private String plantToSelect = null;
    private String currentTheme;

    public void initialize() {
        saveBtn.setOnAction(e -> onSave());
        cancelBtn.setOnAction(e -> onCancel());
    }

    public void setTheme (String theme) {
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

    // Metodo che carica le piante dell’utente e aggiunge la posizione accanto al nome
    public void setUtente(Utente u) {
        this.utente = u;

        if (utente != null) {
            Service<List<Pianta>> service = CaricaPianteUtente.getPlantsByUserService(utente.getId());

            service.setOnSucceeded(ev -> {
                var lista = service.getValue();
                if (lista != null) {
                    for (Pianta p : lista) {
                        String posizione = getPosizionePianta(p.getId());
                        String label = p.getNome() + " — " + posizione;

                        plantComboBox.getItems().add(label);
                        plantLabelToIdMap.put(label, p.getId());
                    }
                }

                //CASO DI MODIFICA ATTIVITÀ ESISTENTE
                if(attivitaEsistente != null) {
                    prefill(attivitaEsistente);
                }

                //CASO DI NUVOA ATTIVITA' DA NOTIFICA
                if(plantToSelect != null) {
                    for (var entry : plantLabelToIdMap.entrySet()) {
                        if(entry.getValue().equals(plantToSelect)) {
                            plantComboBox.getSelectionModel().select(entry.getKey());
                            break;
                        }
                    }
                }
            });

            service.setOnFailed(ev -> {
                ev.getSource().getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Errore durante il caricamento delle piante.").showAndWait();
            });

            service.start();
        }
    }

    //Recupera la posizione della pianta da PianteUtente
    private String getPosizionePianta(String plantUtenteId) {
        String posizione = "-";
        String query = "SELECT Posizione FROM PianteUtente WHERE Id = ?";
        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(query)) {
            ps.setString(1, plantUtenteId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    posizione = rs.getString("Posizione");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posizione != null ? posizione : "-";
    }

    //Compila la form se si apre da una notifica o attività esistente
    public void prefill(AttivitaPianta attivita) {
        this.attivitaEsistente = attivita;

        if (attivita == null) return;

        // Compila i campi
        titleField.setText(attivita.getTitolo());
        datePicker.setValue(attivita.getData());
        notesArea.setText(attivita.getDescrizione());

        // Preseleziona la pianta nella ComboBox
        String plantId = attivita.getPlantUtenteId();
        if (plantId != null) {
            for (var entry : plantLabelToIdMap.entrySet()) {
                if (entry.getValue().equals(plantId)) {
                    plantComboBox.getSelectionModel().select(entry.getKey());
                    break;
                }
            }
        }
    }

    public void prefillNuovaAttivita(LocalDate date, String plantUtenteId) {
        this.attivitaEsistente = null;

        if (date != null)
            datePicker.setValue(date);

        this.plantToSelect = plantUtenteId;
    }


    private void onSave() {
        String titolo = titleField.getText() != null ? titleField.getText().trim() : "";
        LocalDate data = datePicker.getValue();
        String note = notesArea.getText();
        String selectedLabel = plantComboBox.getSelectionModel().getSelectedItem();

        if (titolo.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Inserisci un titolo per l'attività.").showAndWait();
            return;
        }
        if (data == null) {
            new Alert(Alert.AlertType.WARNING, "Seleziona una data per l'attività.").showAndWait();
            return;
        }

        String plantId = selectedLabel != null ? plantLabelToIdMap.get(selectedLabel) : null;

        boolean ok;

        if (attivitaEsistente == null) {
            // ➕ NUOVA ATTIVITÀ
            ok = GestioneAttivitaService.creaNuovaAttivita(
                    utente.getId(),
                    plantId,
                    titolo,
                    note,
                    data,
                    null,
                    null
            );
        } else {
            // ✏️ MODIFICA ESISTENTE
            attivitaEsistente.setTitolo(titolo);
            attivitaEsistente.setDescrizione(note);
            attivitaEsistente.setData(data);
            attivitaEsistente.setPlantUtenteId(plantId);

            ok = GestioneAttivitaService.aggiornaAttivita(attivitaEsistente);
        }

        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "Errore nel salvataggio.").showAndWait();
        }

        // Chiudi popup
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }


    private void onCancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
