package informatica.plantmanager.model;

public class PlantComboItem {
    private String plantUtenteId;
    private String nome;
    private String posizione; // Nuovo campo per la posizione

    public PlantComboItem(String plantUtenteId, String nome, String posizione) {
        this.plantUtenteId = plantUtenteId;
        this.nome = nome;
        this.posizione = posizione;
    }

    public String getPlantUtenteId() {
        return plantUtenteId;
    }

    public String getNome() {
        return nome;
    }

    public String getPosizione() {
        return posizione;
    }

    @Override
    public String toString() {
        return nome + " - " + posizione;
    }
}
