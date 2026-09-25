import dao.OVChipkaartDAO;
import dao.ProductDAO;
import dao.ReizigerDAO;
import daosql.AdresDAOPsql;
import daosql.OVChipkaartDAOPsql;
import daosql.ProductDAOPsql;
import daosql.ReizigerDAOPsql;
import domain.OVChipkaart;
import domain.Product;
import domain.Reiziger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Mainp5 {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("db.properties"));
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
        } catch (IOException exception) {
            throw new RuntimeException("kan db.properties niet laden", exception);
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to the database!");

            AdresDAOPsql adao = new AdresDAOPsql(conn);
            OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(conn);
            ProductDAO pdao = new ProductDAOPsql(conn);
            ReizigerDAO rdao = new ReizigerDAOPsql(conn, adao, odao);

            adao.setReizigerDAO(rdao);
            odao.setReizigerDAO(rdao);

            testProductDAO(rdao, odao, pdao);
        } catch (SQLException e) {
            System.out.println("Er is een fout opgetreden bij de databaseverbinding:");
            e.printStackTrace();
        }
    }

    private static void testProductDAO(ReizigerDAO rdao, OVChipkaartDAO odao, ProductDAO pdao) throws SQLException {
        System.out.println("\n---------- Test ProductDAO -------------");

        Reiziger martijn = new Reiziger(99, "M", "van", "Dijk", java.sql.Date.valueOf("1995-03-12"));
        OVChipkaart kaart1 = new OVChipkaart(991, java.sql.Date.valueOf("2028-01-01"), 2, 20.00, null);
        OVChipkaart kaart2 = new OVChipkaart(992, java.sql.Date.valueOf("2029-05-01"), 1, 40.00, null);

        martijn.voegToeOVChipkaart(kaart1);
        martijn.voegToeOVChipkaart(kaart2);
        rdao.save(martijn);

        Product product1 = new Product(901, "Dal Voordeel", "Korting tijdens daluren", 5.60);
        Product product2 = new Product(902, "Weekend Vrij", "Onbeperkt reizen in het weekend", 34.95);
        Product product3 = new Product(903, "Altijd Voordeel", "Korting tijdens daluren en spits", 26.70);

        kaart1.voegToeProduct(product1);
        kaart1.voegToeProduct(product2);
        kaart2.voegToeProduct(product2);
        kaart2.voegToeProduct(product3);

        pdao.save(product1);
        pdao.save(product2);
        pdao.save(product3);

        System.out.println("[Test] Na save van producten:");
        for (Product product : pdao.findAll()) {
            System.out.println(product);
        }

        System.out.println("[Test] Producten van kaart1:");
        for (Product product : pdao.findByOVChipkaart(kaart1)) {
            System.out.println(product);
        }

        System.out.println("[Test] Producten van kaart2:");
        for (Product product : pdao.findByOVChipkaart(kaart2)) {
            System.out.println(product);
        }

        product1.setPrijs(7.50);
        product1.setBeschrijving("Meer korting tijdens daluren");
        kaart1.verwijderProduct(product2);
        kaart1.voegToeProduct(product3);

        pdao.update(product1);
        pdao.update(product2);
        pdao.update(product3);

        System.out.println("[Test] Producten van kaart1 na update:");
        for (Product product : pdao.findByOVChipkaart(kaart1)) {
            System.out.println(product);
        }

        pdao.delete(product1);
        pdao.delete(product2);
        pdao.delete(product3);
        rdao.delete(martijn);

        System.out.println("[Test] Na delete van producten:");
        for (Product product : pdao.findAll()) {
            System.out.println(product);
        }
    }
}