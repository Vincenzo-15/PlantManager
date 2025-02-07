package informatica.plantmanager.model;

import java.util.UUID;

public class Utente {
    private String id;
    private String email;
    private String nickname;
    private String password;

    public Utente(String nickname, String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

