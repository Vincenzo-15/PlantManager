package informatica.plantmanager.controller;

import informatica.plantmanager.model.RicercaUtente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button buttonAccedi;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label labelRegistrati;

    private final RicercaUtente utente = new RicercaUtente();

    @FXML
    void accedi(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        utente.setOnSucceeded(workerStateEvent -> {
            boolean success = utente.getValue();
            if (success) {
                System.out.println("Login avvenuto con successo!");
                // Puoi navigare a una nuova scena o notificare l'utente
            } else {
                System.out.println("Credenziali non valide.");
            }
        });

        utente.setOnFailed(workerStateEvent -> {
            Throwable error = utente.getException();
            System.err.println("Errore durante il login: " + error.getMessage());
        });

        // Controlla che il servizio non sia già in esecuzione prima di riavviarlo
        if (!utente.isRunning()) {
            utente.setLoginCredentials(email, password);
            utente.restart(); // Riavvia il Service per una nuova operazione
        }
    }

    @FXML
    void goToRegister(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) labelRegistrati.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



