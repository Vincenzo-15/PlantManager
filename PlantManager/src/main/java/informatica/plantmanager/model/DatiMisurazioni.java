package informatica.plantmanager.model;

public class DatiMisurazioni {
    private double valore;
    private String nomeSensore;
    private String unitaMisura;

    public DatiMisurazioni(double valore, String nomeSensore, String unitaMisura) {
        this.valore = valore;
        this.nomeSensore = nomeSensore;
        this.unitaMisura = unitaMisura;
    }

    public double getValore() {
        return valore;
    }

    public String getNomeSensore() {
        return nomeSensore;
    }

    public String getUnitaMisura() {
        return unitaMisura;
    }

    @Override
    public String toString() {
        return "MeasurementData{" +
                "valore=" + valore +
                ", nomeSensore='" + nomeSensore + '\'' +
                ", unitaMisura='" + unitaMisura + '\'' +
                '}';
    }
}
