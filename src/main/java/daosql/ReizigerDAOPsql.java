package daosql;

import dao.AdresDAO;
import dao.OVChipkaartDAO;
import dao.ReizigerDAO;
import domain.Adres;
import domain.OVChipkaart;
import domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {

    private Connection conn;
    private AdresDAO adresDAO;
    private OVChipkaartDAO ovChipkaartDAO;

    public ReizigerDAOPsql(Connection conn, AdresDAO adresDAO, OVChipkaartDAO ovChipkaartDAO) {
        this.conn = conn;
        this.adresDAO = adresDAO;
        this.ovChipkaartDAO = ovChipkaartDAO;
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
                koppelOVChipkaarten(reiziger);
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
            koppelOVChipkaarten(reiziger);
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
            koppelOVChipkaarten(reiziger);
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
        if (!gelukt) {
            return false;
        }
        if (reiziger.getAdres() != null) {
            reiziger.getAdres().setReiziger(reiziger);
            adresDAO.save(reiziger.getAdres());
        }
        for (OVChipkaart kaart : reiziger.getOvChipkaarten()) {
            kaart.setReiziger(reiziger);
            ovChipkaartDAO.save(kaart);
        }
        return true;
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

        // adres synchroniseren
        Adres bestaandAdres = adresDAO.findByReiziger(reiziger);
        Adres nieuwAdres = reiziger.getAdres();
        if (nieuwAdres == null) {
            if (bestaandAdres != null) {
                adresDAO.delete(bestaandAdres);
            }
        } else {
            nieuwAdres.setReiziger(reiziger);
            if (bestaandAdres == null) {
                adresDAO.save(nieuwAdres);
            } else {
                adresDAO.update(nieuwAdres);
            }
        }

        // ov-chipkaarten synchroniseren
        List<OVChipkaart> bestaandeKaarten = ovChipkaartDAO.findByReiziger(reiziger);
        List<OVChipkaart> nieuweKaarten = reiziger.getOvChipkaarten();

        for (OVChipkaart bestaand : bestaandeKaarten) {
            boolean nogAanwezig = false;
            for (OVChipkaart nieuw : nieuweKaarten) {
                if (nieuw.getKaartNummer() == bestaand.getKaartNummer()) {
                    nogAanwezig = true;
                    break;
                }
            }
            if (!nogAanwezig) {
                ovChipkaartDAO.delete(bestaand);
            }
        }

        for (OVChipkaart nieuw : nieuweKaarten) {
            nieuw.setReiziger(reiziger);
            boolean bestondAl = false;
            for (OVChipkaart bestaand : bestaandeKaarten) {
                if (bestaand.getKaartNummer() == nieuw.getKaartNummer()) {
                    bestondAl = true;
                    break;
                }
            }
            if (bestondAl) {
                ovChipkaartDAO.update(nieuw);
            } else {
                ovChipkaartDAO.save(nieuw);
            }
        }

        return true;
    }

    @Override
    public boolean delete(Reiziger reiziger) throws SQLException {
        for (OVChipkaart kaart : ovChipkaartDAO.findByReiziger(reiziger)) {
            ovChipkaartDAO.delete(kaart);
        }
        reiziger.setOvChipkaarten(new ArrayList<>());

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

    private void koppelOVChipkaarten(Reiziger reiziger) throws SQLException {
        List<OVChipkaart> kaarten = ovChipkaartDAO.findByReiziger(reiziger);
        reiziger.setOvChipkaarten(kaarten);
    }

}