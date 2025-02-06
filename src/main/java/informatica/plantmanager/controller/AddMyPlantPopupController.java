package informatica.plantmanager.controller;

import informatica.plantmanager.model.CaricaPianteUtentePopup;
import informatica.plantmanager.model.PlantComboItem;
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

    @FXML
    void addMyPlant(MouseEvent event) {
        PlantComboItem selectedItem = comboBoxMyPlant.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            System.out.println("Pianta selezionata: " + selectedItem.getNome() +
                    " con ID PianteUtente: " + selectedItem.getPlantUtenteId());
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
