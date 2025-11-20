package informatica.plantmanager.controller;

import informatica.plantmanager.model.AttivitaPianta;
import informatica.plantmanager.model.DatabaseConnection;
import informatica.plantmanager.model.GestioneAttivitaService;
import informatica.plantmanager.model.Utente;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ActivityPlannerController {

    @FXML private GridPane calendarGrid;
    @FXML private ListView<AttivitaPianta> listActivities;
    @FXML private Button btnAddActivity;
    @FXML private Button btnPrevMonth;
    @FXML private Button btnNextMonth;
    @FXML private Label lblCurrentMonth;

    private Utente utente;
    private List<AttivitaPianta> attivitaCaricate = new ArrayList<>();
    private YearMonth currentYearMonth = YearMonth.now();
    private Map<String, String> plantNamesMap = new HashMap<>(); // plantUtenteId -> nomePianta
    private LocalDate selectedDate = null;
    private String currentTheme;

    public void initialize() {
        System.out.println("✅ ActivityPlannerController inizializzato");

        btnAddActivity.setOnAction(e -> openActivityForm(LocalDate.now(), null));
        btnPrevMonth.setOnAction(e -> changeMonth(-1));
        btnNextMonth.setOnAction(e -> changeMonth(1));

        // Configura ListView per contenere direttamente AttivitaPianta
        listActivities.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(AttivitaPianta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitolo() + (item.isCompletata() ? " [✓]" : ""));
                }
            }
        });

        listActivities.setOnMouseClicked(evt -> {
            if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
                AttivitaPianta selected = listActivities.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openActivityDetails(selected);
                }
            }
        });

        buildCalendar(currentYearMonth);
    }

    public void setUtente(Utente u) {
        this.utente = u;
        System.out.println("👤 Utente impostato: " + u.getNickname());

        // Carica mappa nomi pianta per ottimizzare chiamate DB
        plantNamesMap = loadPlantNames();

        loadActivitiesForMonth(currentYearMonth);
        System.out.println("🟦 plantNamesMap caricata:");
        plantNamesMap.forEach((k, v) -> System.out.println("   " + k + " -> " + v));

    }

    public void setTheme(String theme) {
        this.currentTheme = theme;
    }

    private Map<String, String> loadPlantNames() {
        Map<String, String> map = new HashMap<>();
        String query = """
                SELECT PU.Id, P.Nome
                FROM PianteUtente PU
                JOIN Piante P ON PU.PiantaId = P.Id
                WHERE PU.UtenteId = ?
                """;
        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(query)) {
            ps.setString(1, utente.getId());
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("Id"), rs.getString("Nome"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void loadActivitiesForMonth(YearMonth ym) {
        if (utente == null) return;

        attivitaCaricate = GestioneAttivitaService.caricaAttivitaPerMese(utente.getId(), ym.getYear(), ym.getMonthValue());
        if (attivitaCaricate == null) attivitaCaricate = new ArrayList<>();

        // Aggiorna ListView solo se esiste una data selezionata
        if (!attivitaCaricate.isEmpty()) {
            listActivities.getItems().clear();
            attivitaCaricate.stream()
                    .filter(a -> a.getData() != null)
                    .forEach(a -> listActivities.getItems().add(a));
        }

        buildCalendar(ym);
    }

    private void buildCalendar(YearMonth ym) {
        calendarGrid.getChildren().clear();

        LocalDate firstOfMonth = ym.atDay(1);
        int daysInMonth = ym.lengthOfMonth();
        int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1=Lunedì

        lblCurrentMonth.setText(
                ym.getMonth().toString().substring(0, 1).toUpperCase() +
                        ym.getMonth().toString().substring(1).toLowerCase() + " " +
                        ym.getYear()
        );

        int dayCounter = 1;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                VBox cell = new VBox(3);
                cell.setPadding(new Insets(5));
                cell.setPrefSize(120, 100);

                cell.getStyleClass().add("calendar-cell");

                if ((row * 7 + col + 1) >= startDayOfWeek && dayCounter <= daysInMonth) {
                    LocalDate date = firstOfMonth.withDayOfMonth(dayCounter);
                    Label dayLabel = new Label(String.valueOf(dayCounter));
                    dayLabel.getStyleClass().add("calendar-day-number");
                    cell.getChildren().add(dayLabel);

                    List<AttivitaPianta> attivitaDelGiorno = attivitaCaricate.stream()
                            .filter(a -> a.getData() != null && date.equals(a.getData()))
                            .collect(Collectors.toList());

                    if (date.equals(LocalDate.now())) {
                        cell.getStyleClass().add("calendar-cell-today");
                    }

                    if (!attivitaDelGiorno.isEmpty()) {
                        cell.getStyleClass().add("calendar-cell-has-activities");
                        // Click sulla cella → aggiorna ListView
                        cell.setOnMouseClicked(evt -> {
                            if (evt.getButton() == MouseButton.PRIMARY) {
                                selectedDate = date;
                                showActivitiesForDay(date);
                            }
                        });
                    }

                    for (AttivitaPianta a : attivitaDelGiorno) {
                        Label actLabel = new Label("• " + a.getTitolo() + (a.isCompletata() ? " [✓]" : ""));
                        actLabel.getStyleClass().add("activity-label");

                        if (a.isCompletata()) {
                            actLabel.getStyleClass().add("activity-completed");
                        }

                        // 🔹 ContextMenu come prima
                        ContextMenu menu = new ContextMenu();

                        MenuItem modifica = new MenuItem("✏️ Modifica");
                        modifica.setOnAction(e -> openActivityForm(a));

                        MenuItem completa = new MenuItem(a.isCompletata() ? "↩️ Segna come da completare" : "✔️ Segna come completata");
                        completa.setOnAction(e -> {
                            boolean nuovoStato = !a.isCompletata();
                            if (GestioneAttivitaService.aggiornaCompletamento(a.getId(), nuovoStato)) {
                                loadActivitiesForMonth(currentYearMonth);
                            }
                        });

                        MenuItem elimina = new MenuItem("🗑️ Elimina");
                        elimina.setOnAction(e -> {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                            confirm.setTitle("Conferma eliminazione");
                            confirm.setHeaderText("Vuoi davvero eliminare questa attività?");
                            confirm.setContentText(a.getTitolo());

                            confirm.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    if (GestioneAttivitaService.eliminaAttivita(a.getId())) {
                                        loadActivitiesForMonth(currentYearMonth);
                                    }
                                }
                            });
                        });

                        menu.getItems().addAll(modifica, completa, new SeparatorMenuItem(), elimina);

                        // 🔸 Click sulla label → mostra ContextMenu
                        actLabel.setOnMouseClicked(evt -> {
                            if (evt.getButton() == MouseButton.PRIMARY) {
                                menu.show(actLabel, evt.getScreenX(), evt.getScreenY());
                                evt.consume(); // evita che il click propaghi al VBox
                            }
                        });

                        cell.getChildren().add(actLabel);
                    }

                    dayCounter++;
                } else {
                    cell.getStyleClass().add("calendar-cell-empty");
                }

                calendarGrid.add(cell, col, row);
            }
        }
    }


    private void changeMonth(int offset) {
        currentYearMonth = currentYearMonth.plusMonths(offset);
        loadActivitiesForMonth(currentYearMonth);
    }

    private void showActivitiesForDay(LocalDate date) {
        listActivities.getItems().clear();
        List<AttivitaPianta> delGiorno = attivitaCaricate.stream()
                .filter(a -> a.getData() != null && a.getData().equals(date))
                .collect(Collectors.toList());
        listActivities.getItems().addAll(delGiorno);
    }

    public void openActivityForm(LocalDate data, String plantUtenteId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/ActivityForm.fxml"));
            Pane pane = loader.load();
            ActivityFormController controller = loader.getController();
            controller.setUtente(utente);
            controller.prefillNuovaAttivita(data, plantUtenteId);
            Platform.runLater(() -> controller.setTheme(currentTheme));

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nuova attività");
            dialog.setScene(new Scene(pane));
            dialog.showAndWait();

            loadActivitiesForMonth(currentYearMonth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openActivityForm(AttivitaPianta attivita) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/ActivityForm.fxml"));
            Pane pane = loader.load();
            ActivityFormController controller = loader.getController();
            controller.setUtente(utente);
            controller.prefill(attivita);
            Platform.runLater(() -> controller.setTheme(currentTheme));

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Modifica attività");
            dialog.setScene(new Scene(pane));
            dialog.showAndWait();

            loadActivitiesForMonth(currentYearMonth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openActivityDetails(AttivitaPianta attivita) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/ActivityDetails.fxml"));
            Pane pane = loader.load();

            System.out.println("PlantUtenteID dell'attività selezionata: " + attivita.getPlantUtenteId());
            String nomePianta = getNomePianta(attivita.getPlantUtenteId());
            String posizione = getPosizionePianta(attivita.getPlantUtenteId());

            ActivityDetailsController controller = loader.getController();
            controller.setData(attivita, nomePianta, posizione, () -> openActivityForm(attivita));
            Platform.runLater(() -> controller.setTheme(currentTheme));

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Dettagli attività");
            stage.setScene(new Scene(pane));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private String getNomePianta(String plantUtenteId) {

        if (plantUtenteId == null || plantUtenteId.isBlank()) {
            return "-";
        }

        // 1️⃣ Prima prova a prendere il nome dalla mappa (più veloce)
        if (plantNamesMap.containsKey(plantUtenteId)) {
            return plantNamesMap.get(plantUtenteId);
        }

        // 2️⃣ Fallback: ricava il nome tramite query
        String query = """
        SELECT P.Nome
        FROM PianteUtente PU
        JOIN Piante P ON PU.PiantaId = P.Id
        WHERE PU.Id = ?
    """;

        try (var conn = DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(query)) {

            ps.setString(1, plantUtenteId);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("Nome");

                    // 🔥 Aggiorna la mappa per i futuri utilizzi
                    plantNamesMap.put(plantUtenteId, nome);

                    return nome;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-";
    }

}
