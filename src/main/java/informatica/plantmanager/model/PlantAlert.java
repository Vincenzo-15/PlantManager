package informatica.plantmanager.model;

public class PlantAlert {
    private final String piantaUtenteId;
    private final String nomePianta;
    private final String posizione;
    private final int salute;
    private final String suggerimento;

    public PlantAlert(String piantaUtenteId, String nomePianta, String posizione, int salute, String suggerimento) {
        this.piantaUtenteId = piantaUtenteId;
        this.nomePianta = nomePianta;
        this.posizione = posizione;
        this.salute = salute;
        this.suggerimento = suggerimento;
    }

    public String getPiantaUtenteId() {
        return piantaUtenteId;
    }

    public String getNomePianta() {
        return nomePianta;
    }

    public String getPosizione() {
        return posizione;
    }

    public int getSalute() {
        return salute;
    }

    public String getSuggerimento() {
        return suggerimento;
    }
}

