package informatica.plantmanager.controller;

import informatica.plantmanager.model.CaricaPiante;
import informatica.plantmanager.model.CercaPiantePerNome;
import informatica.plantmanager.model.InserisciPianteUtente;
import informatica.plantmanager.model.Utente;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AddPlantPopupController {

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonCancel;

    @FXML
    private ListView<String> plantList;

    @FXML
    private TextField searchBar;

    @FXML
    private Label labelAvviso;

    @FXML
    private TextField textBarPosition;

    private PauseTransition pause;

    private Utente utente;

    @FXML
    public void initialize() {
        loadAllPlants();

        pause = new PauseTransition(Duration.millis(300));
        pause.setOnFinished(event -> performSearch());

        searchBar.textProperty().addListener((observable, oldText, newText) -> {
            pause.playFromStart();
        });
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        System.out.println("Utente ricevuto nel popup: " + utente.getNickname());
    }

    private void loadAllPlants() {
        CaricaPiante loadService = new CaricaPiante();
        loadService.setOnSucceeded(e -> {
            ObservableList<String> items = FXCollections.observableArrayList(loadService.getValue());
            plantList.setItems(items);
        });
        loadService.setOnFailed(e -> {
            Throwable error = loadService.getException();
            resetLabelColor();
            labelAvviso.setText("Errore durante il caricamento delle piante");
            System.err.println("Errore durante il caricamento dei nomi delle piante: " + error.getMessage());
        });
        loadService.start();
    }

    private void performSearch() {
        String queryText = searchBar.getText();

        if (queryText == null || queryText.trim().isEmpty()) {
            loadAllPlants();
            return;
        }

        CercaPiantePerNome searchService = new CercaPiantePerNome();
        searchService.setSearchQuery(queryText);
        searchService.setOnSucceeded(event -> {
            ObservableList<String> items = FXCollections.observableArrayList(searchService.getValue());
            plantList.setItems(items);
        });
        searchService.setOnFailed(event -> {
            Throwable error = searchService.getException();
            resetLabelColor();
            labelAvviso.setText("Errore durante la ricerca" + error.getMessage());
            System.err.println("Errore durante la ricerca: " + error.getMessage());
        });
        searchService.restart();
    }

    @FXML
    void addPlant(MouseEvent event) {
        String selectedPlant = plantList.getSelectionModel().getSelectedItem();
        if (selectedPlant == null || selectedPlant.trim().isEmpty()) {
            resetLabelColor();
            labelAvviso.setText("Devi selezionare una pianta dalla lista");
            System.err.println("Devi selezionare una pianta dalla lista");
            return;
        }

        String position = textBarPosition.getText();
        if (position == null || position.trim().isEmpty()) {
            resetLabelColor();
            labelAvviso.setText("Devi inserire la posizione");
            System.err.println("Devi inserire la posizione");
            return;
        }

        if (utente == null) {
            resetLabelColor();
            labelAvviso.setText("Errore: utente non definito");
            System.err.println("Errore: utente non definito");
            return;
        }

        InserisciPianteUtente insertService = new InserisciPianteUtente();
        insertService.setParameters(selectedPlant, position, utente);

        insertService.setOnSucceeded(e -> {
            if (insertService.getValue()) {
                labelAvviso.setTextFill(javafx.scene.paint.Color.web("#139E2A"));
                labelAvviso.setText("Pianta aggiunta con successo");
                System.out.println("Pianta aggiunta con successo all'utente.");
                textBarPosition.clear();
                searchBar.clear();
                plantList.getSelectionModel().clearSelection();
            } else {
                resetLabelColor();
                labelAvviso.setText("Errore nell'inserimento della pianta");
                System.err.println("Errore nell'inserimento della pianta per l'utente.");
            }
        });

        insertService.setOnFailed(e -> {
            Throwable error = insertService.getException();
            resetLabelColor();
            labelAvviso.setText("Errore durante l'inserimento" + error.getMessage());
            System.err.println("Errore durante l'inserimento: " + error.getMessage());
        });

        insertService.start();
    }

    private void resetLabelColor() {
        labelAvviso.setTextFill(Color.RED);
    }

    @FXML
    void closePopup(MouseEvent event) {
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        stage.close();
    }
}
