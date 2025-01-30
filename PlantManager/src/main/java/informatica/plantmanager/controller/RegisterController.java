package informatica.plantmanager.controller;

import informatica.plantmanager.model.InserisciUtente;
import informatica.plantmanager.model.Utente;
import informatica.plantmanager.model.VerificaUtente;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class RegisterController {

    @FXML
    private Button buttonRegister;

    @FXML
    private PasswordField campoConfermaPassword;

    @FXML
    private TextField campoEmail;

    @FXML
    private TextField campoNickname;

    @FXML
    private PasswordField campoPassword;

    @FXML
    private Label goToLogin;

    @FXML
    void register(ActionEvent event) {
        String nickname = campoNickname.getText();
        String email = campoEmail.getText();
        String password = campoPassword.getText();
        String confermaPassword = campoConfermaPassword.getText();

        if (nickname.isEmpty() || email.isEmpty() || password.isEmpty() || confermaPassword.isEmpty()) {
            System.out.println("Tutti i campi sono obbligatori.");
            return;
        }

        if (!password.equals(confermaPassword)) {
            System.out.println("Le password non coincidono.");
            return;
        }

        // Crea il servizio di verifica dell'utente
        VerificaUtente verificaService = new VerificaUtente();
        verificaService.setEmail(email);

        verificaService.setOnSucceeded(e -> {
            boolean esiste = verificaService.getValue();
            if (esiste) {
                System.out.println("Errore: l'utente esiste già.");
            } else {
                // L'utente non esiste, procedi con l'inserimento
                String passwordCriptata = BCrypt.hashpw(password, BCrypt.gensalt());
                Utente nuovoUtente = new Utente(nickname, email, passwordCriptata);

                InserisciUtenteService inserisciService = new InserisciUtenteService(nuovoUtente);
                inserisciService.setOnSucceeded(insertEvent -> {
                    if (inserisciService.getValue()) {
                        System.out.println("Registrazione avvenuta con successo!");
                    } else {
                        System.out.println("Errore nell'inserimento dell'utente.");
                    }
                });
                inserisciService.start();
            }
        });

        verificaService.start(); // Avvia il controllo dell'utente
    }


    private static class InserisciUtenteService extends Service<Boolean> {
        private final Utente utente;

        public InserisciUtenteService(Utente utente) {
            this.utente = utente;
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<>() {
                @Override
                protected Boolean call() {
                    return InserisciUtente.inserisciUtente(utente);
                }
            };
        }
    }

    @FXML
    void goToLogin(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/informatica/plantmanager/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) goToLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

