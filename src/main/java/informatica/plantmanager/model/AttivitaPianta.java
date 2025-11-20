package informatica.plantmanager.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AttivitaPianta {
    private String id;
    private String utenteId;
    private String plantUtenteId;
    private String titolo;
    private String descrizione;
    private LocalDate data;
    private LocalTime ora;
    private boolean completata;
    private String ricorrenza;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public AttivitaPianta() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlantUtenteId() {
        return plantUtenteId;
    }

    public void setPlantUtenteId(String plantUtenteId) {
        this.plantUtenteId = plantUtenteId;
    }

    public String getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(String utenteId) {
        this.utenteId = utenteId;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOra() {
        return ora;
    }

    public void setOra(LocalTime ora) {
        this.ora = ora;
    }

    public boolean isCompletata() {
        return completata;
    }

    public void setCompletata(boolean completata) {
        this.completata = completata;
    }

    public String getRicorrenza() {
        return ricorrenza;
    }

    public void setRicorrenza(String ricorrenza) {
        this.ricorrenza = ricorrenza;
    }
}
