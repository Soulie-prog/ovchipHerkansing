package daosql;

import dao.ProductDAO;
import domain.OVChipkaart;
import domain.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {

    private Connection conn;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Product product) throws SQLException {
        String sql = "INSERT INTO product (product_nummer, naam, beschrijving, prijs) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, product.getProductNummer());
            pst.setString(2, product.getNaam());
            pst.setString(3, product.getBeschrijving());
            pst.setDouble(4, product.getPrijs());

            if (pst.executeUpdate() != 1) {
                return false;
            }
        } finally {
            if (pst != null) pst.close();
        }

        String relatieSql = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";

        for (OVChipkaart kaart : product.getOvChipkaarten()) {
            try (PreparedStatement relatiePst = conn.prepareStatement(relatieSql)) {
                relatiePst.setInt(1, kaart.getKaartNummer());
                relatiePst.setInt(2, product.getProductNummer());
                relatiePst.executeUpdate();
            }
        }

        return true;
    }

    @Override
    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?";
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, product.getNaam());
            pst.setString(2, product.getBeschrijving());
            pst.setDouble(3, product.getPrijs());
            pst.setInt(4, product.getProductNummer());

            if (pst.executeUpdate() != 1) {
                return false;
            }
        } finally {
            if (pst != null) pst.close();
        }

        String deleteRelatiesSql = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";

        try (PreparedStatement deletePst = conn.prepareStatement(deleteRelatiesSql)) {
            deletePst.setInt(1, product.getProductNummer());
            deletePst.executeUpdate();
        }

        String insertRelatieSql = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";

        for (OVChipkaart kaart : product.getOvChipkaarten()) {
            try (PreparedStatement insertPst = conn.prepareStatement(insertRelatieSql)) {
                insertPst.setInt(1, kaart.getKaartNummer());
                insertPst.setInt(2, product.getProductNummer());
                insertPst.executeUpdate();
            }
        }

        return true;
    }

    @Override
    public boolean delete(Product product) throws SQLException {
        String deleteRelatiesSql = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(deleteRelatiesSql)) {
            pst.setInt(1, product.getProductNummer());
            pst.executeUpdate();
        }

        String sql = "DELETE FROM product WHERE product_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, product.getProductNummer());
            return pst.executeUpdate() == 1;
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException {
        List<Product> producten = new ArrayList<>();

        String sql =
                "SELECT p.* " +
                        "FROM product p " +
                        "JOIN ov_chipkaart_product ocp " +
                        "ON p.product_nummer = ocp.product_nummer " +
                        "WHERE ocp.kaart_nummer = ?";

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, ovChipkaart.getKaartNummer());
            rs = pst.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_nummer"),
                        rs.getString("naam"),
                        rs.getString("beschrijving"),
                        rs.getDouble("prijs")
                );

                product.voegToeOVChipkaart(ovChipkaart);
                producten.add(product);
            }

            return producten;
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }

    @Override
    public List<Product> findAll() throws SQLException {
        List<Product> producten = new ArrayList<>();
        String sql = "SELECT * FROM product";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_nummer"),
                        rs.getString("naam"),
                        rs.getString("beschrijving"),
                        rs.getDouble("prijs")
                );

                producten.add(product);
            }

            return producten;
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }
    }
}