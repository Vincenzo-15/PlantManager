package informatica.plantmanager.model;

public class ImpostazioniUtente {
    private String tema;
    private boolean notifiche;

    public ImpostazioniUtente(String tema, boolean notifiche) {
        this.tema = tema;
        this.notifiche = notifiche;
    }

    public String getTema() {
        return tema;
    }


    public boolean isNotifiche(){ return notifiche;}

}
