package informatica.plantmanager.controller;

import informatica.plantmanager.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AddSensorController {

    @FXML
    private AnchorPane addSensorButton;

    private Utente utente;

    @FXML
    void addSensor(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/AddSensorPopup.fxml"));
            AnchorPane popupRoot = fxmlLoader.load();

            AddSensorPopupController popupController = fxmlLoader.getController();
            popupController.setUtente(utente);

            Scene scene = new Scene(popupRoot);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //try {
        //    FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/SensorView.fxml"));
        //    AnchorPane plantDashboardView = loader.load();
//
        //    GridPane parentGrid = (GridPane) addSensorButton.getParent().getParent();
        //    Integer colIndex = GridPane.getColumnIndex(addSensorButton.getParent());
        //    Integer rowIndex = GridPane.getRowIndex(addSensorButton.getParent());
//
        //    parentGrid.add(plantDashboardView, colIndex, rowIndex);
        //    parentGrid.getChildren().remove(addSensorButton.getParent());
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}
