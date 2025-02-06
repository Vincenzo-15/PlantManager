package informatica.plantmanager.controller;

import informatica.plantmanager.model.CaricaPianteUtentePopup;
import informatica.plantmanager.model.PlantComboItem;
import informatica.plantmanager.model.SalvaLayoutPianta;
import informatica.plantmanager.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class AddMyPlantPopupController {

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonCancel;

    @FXML
    private ComboBox<PlantComboItem> comboBoxMyPlant;

    private Utente utente;
    private int row;
    private int col;

    @FXML
    void addMyPlant(MouseEvent event) {
        PlantComboItem selectedItem = comboBoxMyPlant.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String piantaUtenteId = selectedItem.getPlantUtenteId();

            SalvaLayoutPianta service = new SalvaLayoutPianta();
            service.setDati(utente.getId(), piantaUtenteId, row, col);
            service.setOnSucceeded(e -> {
                System.out.println("Layout della pianta salvato con successo!");
                buttonCancel.getScene().getWindow().hide(); // Chiude il popup
            });
            service.setOnFailed(e -> {
                System.err.println("Errore nel salvataggio del layout della pianta.");
            });
            service.start();

        } else {
            System.err.println("Nessuna pianta selezionata!");
        }
    }

    @FXML
    void closePopup(MouseEvent event) {
        buttonCancel.getScene().getWindow().hide();
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
        loadPlants();
    }

    public void setGridPosition(int row, int col) {
        this.row = row;
        this.col = col;
        System.out.println("Posizione griglia: riga " + row + ", colonna " + col);
    }

    private void loadPlants() {
        CaricaPianteUtentePopup service = new CaricaPianteUtentePopup();
        service.setUserId(utente.getId());
        service.setOnSucceeded(event -> {
            List<PlantComboItem> plants = service.getValue();
            ObservableList<PlantComboItem> observablePlants = FXCollections.observableArrayList(plants);
            comboBoxMyPlant.setItems(observablePlants);
        });
        service.setOnFailed(event -> {
            Throwable error = service.getException();
            System.err.println("Errore nel recupero delle piante: " + error.getMessage());
        });
        service.start();
    }
}

