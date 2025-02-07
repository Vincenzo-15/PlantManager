package informatica.plantmanager.model;

public class Pianta {
    private String id;
    private String nome;
    private double acqua;
    private double luce;
    private double umidita;
    private double temperatura;
    private double phTerreno;
    private String percorsoImmagine;

    public Pianta(String id, String nome, double acqua, double luce, double umidita,
                 double temperatura, double phTerreno, String percorsoImmagine) {
        this.id = id;
        this.nome = nome;
        this.acqua = acqua;
        this.luce = luce;
        this.umidita = umidita;
        this.temperatura = temperatura;
        this.phTerreno = phTerreno;
        this.percorsoImmagine = percorsoImmagine;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getAcqua() {
        return acqua;
    }

    public double getLuce() {
        return luce;
    }

    public double getUmidita() {
        return umidita;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public double getPhTerreno() {
        return phTerreno;
    }

    public String getPercorsoImmagine() {
        return percorsoImmagine;
    }
}

