package informatica.plantmanager.model;

public class DatiPiante {
    private int salute;
    private double acqua;
    private double luce;
    private String percorsoImmagine;

    public DatiPiante(int salute, double acqua, double luce, String percorsoImmagine) {
        this.salute = salute;
        this.acqua = acqua;
        this.luce = luce;
        this.percorsoImmagine = percorsoImmagine;
    }

    public int getSalute() {
        return salute;
    }

    public double getAcqua() {
        return acqua;
    }

    public double getLuce() {
        return luce;
    }

    public String getPercorsoImmagine() {
        return percorsoImmagine;
    }
}

