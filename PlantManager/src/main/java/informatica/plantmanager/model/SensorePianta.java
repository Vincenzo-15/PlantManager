package informatica.plantmanager.model;

public class SensorePianta {

    private String id;
    private String sensoreId;
    private String piantaUtenteId;
    private String posizioneGriglia;

    // Costruttore completo
    public SensorePianta(String id, String sensoreId, String piantaUtenteId, String posizioneGriglia) {
        this.id = id;
        this.sensoreId = sensoreId;
        this.piantaUtenteId = piantaUtenteId;
        this.posizioneGriglia = posizioneGriglia;
    }

    // Costruttore vuoto
    public SensorePianta() {}

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSensoreId() {
        return sensoreId;
    }

    public void setSensoreId(String sensoreId) {
        this.sensoreId = sensoreId;
    }

    public String getPiantaUtenteId() {
        return piantaUtenteId;
    }

    public void setPiantaUtenteId(String piantaUtenteId) {
        this.piantaUtenteId = piantaUtenteId;
    }

    public String getPosizioneGriglia() {
        return posizioneGriglia;
    }

    public void setPosizioneGriglia(String posizioneGriglia) {
        this.posizioneGriglia = posizioneGriglia;
    }

    @Override
    public String toString() {
        return "SensorePianta{" +
                "id='" + id + '\'' +
                ", sensoreId='" + sensoreId + '\'' +
                ", piantaUtenteId='" + piantaUtenteId + '\'' +
                ", posizioneGriglia='" + posizioneGriglia + '\'' +
                '}';
    }
}

