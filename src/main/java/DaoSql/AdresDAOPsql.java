package DaoSql;

import Dao.AdresDAO;
import Dao.ReizigerDAO;
import Domain.Adres;
import Domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {

    private Connection conn;
    private ReizigerDAO rdao; // dependency injectie via constructor

    // Constructor met ReizigerDAO
    public AdresDAOPsql(Connection conn, ReizigerDAO rdao) {
        this.conn = conn;
        this.rdao = rdao;
    }


    @Override
    public boolean save(Adres adres) {
        try {
            String sql = "INSERT INTO adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, adres.getAdresId());
            pst.setString(2, adres.getPostcode());
            pst.setString(3, adres.getHuisnummer());
            pst.setString(4, adres.getStraat());
            pst.setString(5, adres.getWoonplaats());
            pst.setInt(6, adres.getReiziger().getId());
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Adres adres) {
        try {
            String sql = "UPDATE adres SET postcode=?, huisnummer=?, straat=?, woonplaats=?, reiziger_id=? WHERE adres_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, adres.getPostcode());
            pst.setString(2, adres.getHuisnummer());
            pst.setString(3, adres.getStraat());
            pst.setString(4, adres.getWoonplaats());
            pst.setInt(5, adres.getReiziger().getId());
            pst.setInt(6, adres.getAdresId());
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Adres adres) {
        try {
            String sql = "DELETE FROM adres WHERE adres_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, adres.getAdresId());
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Adres findById(int id) {
        try {
            String sql = "SELECT * FROM adres WHERE adres_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Adres(
                        rs.getInt("adres_id"),
                        rs.getString("postcode"),
                        rs.getString("huisnummer"),
                        rs.getString("straat"),
                        rs.getString("woonplaats"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        try {
            String sql = "SELECT * FROM adres WHERE reiziger_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, reiziger.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Adres(
                        rs.getInt("adres_id"),
                        rs.getString("postcode"),
                        rs.getString("huisnummer"),
                        rs.getString("straat"),
                        rs.getString("woonplaats")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Adres> findAll() {
        List<Adres> adressen = new ArrayList<>();
        try {
            String sql = "SELECT * FROM adres";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Adres adres = new Adres(
                        rs.getInt("adres_id"),
                        rs.getString("postcode"),
                        rs.getString("huisnummer"),
                        rs.getString("straat"),
                        rs.getString("woonplaats")
                );
                adressen.add(adres);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adressen;
    }
}
