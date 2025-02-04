package informatica.plantmanager.controller;

import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;

public class AddSensorPopupController {

    @FXML
    private Button addSensorButton;

    @FXML
    private Button buttonAnnulla;

    @FXML
    private ComboBox<?> comboBoxSensori;

    private Utente utente;

    @FXML
    void addSensor(MouseEvent event) {

    }

    @FXML
    void closePopup(MouseEvent event) {

    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}
