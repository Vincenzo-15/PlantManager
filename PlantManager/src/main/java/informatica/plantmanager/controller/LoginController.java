package informatica.plantmanager.controller;

import informatica.plantmanager.model.RicercaUtente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private Button buttonAccedi;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

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
}



