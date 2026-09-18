package daosql;

import dao.OVChipkaartDAO;
import dao.ReizigerDAO;
import domain.OVChipkaart;
import domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {

    private Connection conn;
    private ReizigerDAO reizigerDAO;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    @Override
    public OVChipkaart findByKaartNummer(int kaartNummer) throws SQLException {
        String sql = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, kaartNummer);
            rs = pst.executeQuery();
            if (rs.next()) {
                Reiziger reiziger = reizigerDAO != null
                        ? reizigerDAO.findById(rs.getInt("reiziger_id"))
                        : null;
                return maakOVChipkaart(rs, reiziger);
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException {
        List<OVChipkaart> kaarten = new ArrayList<>();
        String sql = "SELECT * FROM ov_chipkaart WHERE reiziger_id = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, reiziger.getId());
            rs = pst.executeQuery();
            while (rs.next()) {
                kaarten.add(maakOVChipkaart(rs, reiziger));
            }
            return kaarten;
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }

    @Override
    public List<OVChipkaart> findAll() throws SQLException {
        List<OVChipkaart> alleKaarten = new ArrayList<>();
        for (Reiziger reiziger : reizigerDAO.findAll()) {
            alleKaarten.addAll(reiziger.getOvChipkaarten());
        }
        return alleKaarten;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, ovChipkaart.getKaartNummer());
            pst.setDate(2, ovChipkaart.getGeldigTot());
            pst.setInt(3, ovChipkaart.getKlasse());
            pst.setDouble(4, ovChipkaart.getSaldo());
            pst.setInt(5, ovChipkaart.getReiziger().getId());
            return pst.executeUpdate() == 1;
        } finally {
            if (pst != null) pst.close();
        }
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "UPDATE ov_chipkaart SET geldig_tot = ?, klasse = ?, saldo = ?, reiziger_id = ? WHERE kaart_nummer = ?";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setDate(1, ovChipkaart.getGeldigTot());
            pst.setInt(2, ovChipkaart.getKlasse());
            pst.setDouble(3, ovChipkaart.getSaldo());
            pst.setInt(4, ovChipkaart.getReiziger().getId());
            pst.setInt(5, ovChipkaart.getKaartNummer());
            return pst.executeUpdate() == 1;
        } finally {
            if (pst != null) pst.close();
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "DELETE FROM ov_chipkaart WHERE kaart_nummer = ?";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, ovChipkaart.getKaartNummer());
            return pst.executeUpdate() == 1;
        } finally {
            if (pst != null) pst.close();
        }
    }

    private OVChipkaart maakOVChipkaart(ResultSet rs, Reiziger reiziger) throws SQLException {
        return new OVChipkaart(
                rs.getInt("kaart_nummer"),
                rs.getDate("geldig_tot"),
                rs.getInt("klasse"),
                rs.getDouble("saldo"),
                reiziger
        );
    }

}