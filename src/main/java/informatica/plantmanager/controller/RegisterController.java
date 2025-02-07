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
    private Label labelAvviso;

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
            labelAvviso.setText("Tutti i campi sono obbligatori");
            System.out.println("Tutti i campi sono obbligatori");
            return;
        }

        if (!isValidEmail(email)) {
            labelAvviso.setText("Formato email non valido");
            System.out.println("Formato email non valido");
            return;
        }

        if (!isValidPassword(password)) {
            labelAvviso.setText("La password deve essere lunga almeno 8 caratteri");
            System.out.println("La password deve essere lunga almeno 8 caratteri");
            return;
        }

        if (!password.equals(confermaPassword)) {
            labelAvviso.setText("Le password non coincidono");
            System.out.println("Le password non coincidono");
            return;
        }

        VerificaUtente verificaService = new VerificaUtente();
        verificaService.setEmail(email);

        verificaService.setOnSucceeded(e -> {
            boolean esiste = verificaService.getValue();
            if (esiste) {
                labelAvviso.setText("Errore: l'utente esiste già");
                System.out.println("Errore: l'utente esiste già");
            } else {
                String passwordCriptata = BCrypt.hashpw(password, BCrypt.gensalt());
                Utente nuovoUtente = new Utente(nickname, email, passwordCriptata);

                InserisciUtenteService inserisciService = new InserisciUtenteService(nuovoUtente);
                inserisciService.setOnSucceeded(insertEvent -> {
                    if (inserisciService.getValue()) {
                        labelAvviso.setTextFill(javafx.scene.paint.Color.web("#139E2A"));
                        labelAvviso.setText("Registrazione avvenuta con successo!");
                        System.out.println("Registrazione avvenuta con successo!");
                        campoEmail.clear();
                        campoNickname.clear();
                        campoPassword.clear();
                        campoConfermaPassword.clear();
                    } else {
                        labelAvviso.setText("Errore nell'inserimento dell'utente");
                        System.out.println("Errore nell'inserimento dell'utente");
                    }
                });
                inserisciService.start();
            }
        });

        verificaService.start();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
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

