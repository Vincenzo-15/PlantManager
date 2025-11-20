package informatica.plantmanager.model;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GestioneAttivitaService {

    public static List<AttivitaPianta> caricaAttivitaPerMese(String utenteId, int year, int month) {
        List<AttivitaPianta> list = new ArrayList<>();
        String query = "SELECT * FROM AttivitaPianta WHERE UtenteId = ? AND strftime('%Y',Data) = ? AND strftime('%m', Data) = ?";
        System.out.println("DEBUG QUERY: SELECT * FROM AttivitaPianta WHERE UtenteId = '" + utenteId +
                "' AND strftime('%Y', Data) = '" + year + "' AND strftime('%m', Data) = '" + String.format("%02d", month) + "'");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, utenteId);
            ps.setString(2, String.valueOf(year));
            ps.setString(3, String.format("%02d", month));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttivitaPianta a = new AttivitaPianta();
                    a.setId(rs.getString("Id"));
                    a.setUtenteId(rs.getString("UtenteId"));
                    a.setPlantUtenteId(rs.getString("PlantUtenteId"));
                    a.setTitolo(rs.getString("Titolo"));
                    a.setDescrizione(rs.getString("Descrizione"));
                    //a.setData(rs.getDate("Data").toLocalDate());
                    String d = rs.getString("Data");
                    if (d != null && !d.isEmpty()) a.setData(LocalDate.parse(d.substring(0,10)));
                    Time t = rs.getTime("Ora");
                    if (t != null) a.setOra(t.toLocalTime());
                    a.setCompletata(rs.getBoolean("Completata"));
                    a.setRicorrenza(rs.getString("Ricorrenza"));
                    list.add(a);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public static boolean salvaAttivita(AttivitaPianta a) {
        String q = "INSERT INTO AttivitaPianta (Id, UtenteId, PlantUtenteId, Titolo, Descrizione, Data, Ora, Completata, Ricorrenza) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, a.getId());
            ps.setString(2, a.getUtenteId());
            ps.setString(3, a.getPlantUtenteId());
            ps.setString(4, a.getTitolo());
            ps.setString(5, a.getDescrizione());
            ps.setString(6, a.getData().toString());
            if (a.getOra() != null) ps.setTime(7, Time.valueOf(a.getOra())); else ps.setNull(7, Types.TIME);
            ps.setBoolean(8, a.isCompletata());
            ps.setString(9, a.getRicorrenza());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public static boolean eliminaAttivita(String id){

        String q = "DELETE FROM AttivitaPianta WHERE Id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }

    }

    public static boolean aggiornaCompletamento(String id, boolean completata){

        String q = "UPDATE AttivitaPianta SET Completata = ? WHERE Id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setBoolean(1, completata);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public static boolean creaNuovaAttivita(String utenteId, String plantUtenteId, String titolo, String descrizione, LocalDate data, LocalTime ora, String ricorrenza) {

        AttivitaPianta a = new AttivitaPianta();
        a.setId(java.util.UUID.randomUUID().toString());
        a.setUtenteId(utenteId);
        a.setPlantUtenteId(plantUtenteId);
        a.setTitolo(titolo);
        a.setDescrizione(descrizione);
        a.setData(data);
        a.setOra(ora);
        a.setCompletata(false);
        a.setRicorrenza(ricorrenza);
        return salvaAttivita(a);
    }

    public static boolean aggiornaAttivita(AttivitaPianta a) {
        String q = "UPDATE AttivitaPianta SET Titolo=?, Descrizione=?, Data=?, PlantUtenteId=? WHERE Id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(q)) {

            ps.setString(1, a.getTitolo());
            ps.setString(2, a.getDescrizione());
            ps.setString(3, a.getData().toString());
            ps.setString(4, a.getPlantUtenteId());
            ps.setString(5, a.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
