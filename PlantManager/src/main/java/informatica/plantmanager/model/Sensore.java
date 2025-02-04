package informatica.plantmanager.model;

public class Sensore {
    private String id;
    private String nome;
    private String funzione;
    private String unitaDiMisura;

    public Sensore(String id, String nome, String funzione, String unitaDiMisura) {
        this.id = id;
        this.nome = nome;
        this.funzione = funzione;
        this.unitaDiMisura = unitaDiMisura;
    }

    // Getters e setters

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getFunzione() {
        return funzione;
    }

    public String getUnitaDiMisura() {
        return unitaDiMisura;
    }

    @Override
    public String toString() {
        // Utilizzato dalla ComboBox per mostrare il nome del sensore
        return nome;
    }
}
