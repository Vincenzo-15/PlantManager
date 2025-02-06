package informatica.plantmanager.model;

import java.time.LocalDateTime;

public class Misurazione {
    private final String nomePianta;
    private final String tipoSensore;
    private final double valore;
    private final LocalDateTime dataOra;

    public Misurazione(String nomePianta, String tipoSensore, double valore, LocalDateTime dataOra) {
        this.nomePianta = nomePianta;
        this.tipoSensore = tipoSensore;
        this.valore = valore;
        this.dataOra = dataOra;
    }

    public String getNomePianta() {
        return nomePianta;
    }

    public String getTipoSensore() {
        return tipoSensore;
    }

    public double getValore() {
        return valore;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }
}

