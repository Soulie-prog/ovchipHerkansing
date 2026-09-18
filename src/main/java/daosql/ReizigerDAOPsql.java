package daosql;

import dao.AdresDAO;
import dao.ReizigerDAO;
import domain.Adres;
import domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {

    private Connection conn;
    private AdresDAO adresDAO;

    public ReizigerDAOPsql(Connection conn, AdresDAO adresDAO) {
        this.conn = conn;
        this.adresDAO = adresDAO;
    }

    @Override
    public Reiziger findById(int id) throws SQLException {
        String sql = "SELECT * FROM reiziger WHERE reiziger_id = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                Reiziger reiziger = maakReiziger(rs);
                koppelAdres(reiziger);
                return reiziger;
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();
        String sql = "SELECT * FROM reiziger";
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                reizigers.add(maakReiziger(rs));
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }
        for (Reiziger reiziger : reizigers) {
            koppelAdres(reiziger);
        }
        return reizigers;
    }

    @Override
    public List<Reiziger> findByGbdatum(Date gbdatum) throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();
        String sql = "SELECT * FROM reiziger WHERE geboortedatum = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setDate(1, gbdatum);
            rs = pst.executeQuery();
            while (rs.next()) {
                reizigers.add(maakReiziger(rs));
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
        for (Reiziger reiziger : reizigers) {
            koppelAdres(reiziger);
        }
        return reizigers;
    }

    @Override
    public boolean save(Reiziger reiziger) throws SQLException {
        String sql = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = null;
        boolean gelukt;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, reiziger.getId());
            pst.setString(2, reiziger.getVoorletters());
            pst.setString(3, reiziger.getTussenvoegsel());
            pst.setString(4, reiziger.getAchternaam());
            pst.setDate(5, reiziger.getGeboortedatum());
            gelukt = pst.executeUpdate() == 1;
        } finally {
            if (pst != null) pst.close();
        }
        if (gelukt && reiziger.getAdres() != null) {
            reiziger.getAdres().setReiziger(reiziger);
            adresDAO.save(reiziger.getAdres());
        }
        return gelukt;
    }

    @Override
    public boolean update(Reiziger reiziger) throws SQLException {
        String sql = "UPDATE reiziger SET voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? WHERE reiziger_id = ?";
        PreparedStatement pst = null;
        boolean gelukt;
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, reiziger.getVoorletters());
            pst.setString(2, reiziger.getTussenvoegsel());
            pst.setString(3, reiziger.getAchternaam());
            pst.setDate(4, reiziger.getGeboortedatum());
            pst.setInt(5, reiziger.getId());
            gelukt = pst.executeUpdate() == 1;
        } finally {
            if (pst != null) pst.close();
        }
        if (!gelukt) {
            return false;
        }
        Adres bestaand = adresDAO.findByReiziger(reiziger);
        Adres nieuw = reiziger.getAdres();
        if (nieuw == null) {
            if (bestaand != null) {
                adresDAO.delete(bestaand);
            }
        } else {
            nieuw.setReiziger(reiziger);
            if (bestaand == null) {
                adresDAO.save(nieuw);
            } else {
                adresDAO.update(nieuw);
            }
        }
        return true;
    }

    @Override
    public boolean delete(Reiziger reiziger) throws SQLException {
        Adres adres = reiziger.getAdres();
        if (adres == null) {
            adres = adresDAO.findByReiziger(reiziger);
        }
        if (adres != null) {
            adresDAO.delete(adres);
            reiziger.setAdres(null);
        }
        String sql = "DELETE FROM reiziger WHERE reiziger_id = ?";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, reiziger.getId());
            return pst.executeUpdate() == 1;
        } finally {
            if (pst != null) pst.close();
        }
    }

    private Reiziger maakReiziger(ResultSet rs) throws SQLException {
        return new Reiziger(
                rs.getInt("reiziger_id"),
                rs.getString("voorletters"),
                rs.getString("tussenvoegsel"),
                rs.getString("achternaam"),
                rs.getDate("geboortedatum")
        );
    }

    private void koppelAdres(Reiziger reiziger) throws SQLException {
        Adres adres = adresDAO.findByReiziger(reiziger);
        if (adres != null) {
            adres.setReiziger(reiziger);
            reiziger.setAdres(adres);
        }
    }

}