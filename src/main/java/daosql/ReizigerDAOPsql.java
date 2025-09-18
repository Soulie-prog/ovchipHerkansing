package daosql;

import dao.ReizigerDAO;
import domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {

    private Connection conn;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
    }
 // feedback verwerkt, project is in sql normaal en hibernate gespl itst dit is normaal sql
    @Override
    public Reiziger findById(int id) throws SQLException {
        String sql = "select reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum from reiziger where reiziger_id = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                return new Reiziger(
                        rs.getInt("reiziger_id"),
                        rs.getString("voorletters"),
                        rs.getString("tussenvoegsel"),
                        rs.getString("achternaam"),
                        rs.getDate("geboortedatum")
                );
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
        }
        return null;
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();
        String sql = "select reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum from reiziger";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                reizigers.add(new Reiziger(
                        rs.getInt("reiziger_id"),
                        rs.getString("voorletters"),
                        rs.getString("tussenvoegsel"),
                        rs.getString("achternaam"),
                        rs.getDate("geboortedatum")
                ));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
        }
        return reizigers;
    }
    public List<Reiziger> findByGbdatum(Date datum) throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();
        String sql = "select reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum from reiziger where geboortedatum = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setDate(1, datum);
            rs = pst.executeQuery();
            while (rs.next()) {
                reizigers.add(new Reiziger(
                        rs.getInt("reiziger_id"),
                        rs.getString("voorletters"),
                        rs.getString("tussenvoegsel"),
                        rs.getString("achternaam"),
                        rs.getDate("geboortedatum")
                ));
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
        return reizigers;
    }
    @Override
    public boolean save(Reiziger r) throws SQLException {
        String sql = "insert into reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) values (?, ?, ?, ?, ?)";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, r.getId());
            pst.setString(2, r.getVoorletters());
            pst.setString(3, r.getTussenvoegsel());
            pst.setString(4, r.getAchternaam());
            pst.setDate(5, r.getGeboortedatum());
            return pst.executeUpdate() == 1;
        } finally {
            if (pst != null) {
                pst.close();
            }
        }
    }

    @Override
    public boolean update(Reiziger r) throws SQLException {
        String sql = "update reiziger set voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? where reiziger_id = ?";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, r.getVoorletters());
            pst.setString(2, r.getTussenvoegsel());
            pst.setString(3, r.getAchternaam());
            pst.setDate(4, r.getGeboortedatum());
            pst.setInt(5, r.getId());
            return pst.executeUpdate() == 1;
        } finally {
            if (pst != null) {
                pst.close();
            }
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "delete from reiziger where reiziger_id = ?";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            return pst.executeUpdate() == 1;
        } finally {
            if (pst != null) {
                pst.close();
            }
        }
    }
}
