import dao.AdresDAO;
import dao.OVChipkaartDAO;
import dao.ReizigerDAO;
import daosql.AdresDAOPsql;
import daosql.OVChipkaartDAOPsql;
import daosql.ReizigerDAOPsql;
import domain.Adres;
import domain.OVChipkaart;
import domain.Reiziger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Mainp4 {

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
            ReizigerDAO rdao = new ReizigerDAOPsql(conn, adao, odao);
            adao.setReizigerDAO(rdao);
            odao.setReizigerDAO(rdao);

            testOVChipkaartDAO(rdao, odao);

        } catch (SQLException e) {
            System.out.println("Er is een fout opgetreden bij de databaseverbinding:");
            e.printStackTrace();
        }
    }

    private static void testOVChipkaartDAO(ReizigerDAO rdao, OVChipkaartDAO odao) throws SQLException {
        System.out.println("\n---------- Test OVChipkaartDAO -------------");

        // reiziger aanmaken met adres en twee kaarten in een keer
        Reiziger martijn = new Reiziger(88, "M", "van", "Berg", java.sql.Date.valueOf("1990-06-21"));

        Adres adres = new Adres(88, "3512 AB", "10", "Kruisstraat", "Utrecht", martijn);
        martijn.setAdres(adres);

        OVChipkaart kaart1 = new OVChipkaart(881, java.sql.Date.valueOf("2027-01-01"), 2, 25.50, martijn);
        OVChipkaart kaart2 = new OVChipkaart(882, java.sql.Date.valueOf("2028-05-15"), 1, 10.00, martijn);
        List<OVChipkaart> kaarten = new ArrayList<>();
        kaarten.add(kaart1);
        kaarten.add(kaart2);
        martijn.setOvChipkaarten(kaarten);

        rdao.save(martijn);
        System.out.println("[Test] Na save reiziger met adres en 2 ov-chipkaarten:");
        System.out.println(rdao.findById(martijn.getId()));

        System.out.println("[Test] OVChipkaartDAO.findByReiziger():");
        for (OVChipkaart k : odao.findByReiziger(martijn)) {
            System.out.println(k);
        }

        System.out.println("[Test] OVChipkaartDAO.findByKaartNummer(881):");
        System.out.println(odao.findByKaartNummer(881));

        // updatr: saldo van kaart1 aanpassen, kaart2 verwijderen uit de lijst,
        // een nieuwe kaart3 toevoegen
        kaart1.setSaldo(42.75);
        OVChipkaart kaart3 = new OVChipkaart(883, java.sql.Date.valueOf("2029-09-01"), 1, 5.00, martijn);

        List<OVChipkaart> bijgewerkt = new ArrayList<>();
        bijgewerkt.add(kaart1);
        bijgewerkt.add(kaart3);
        martijn.setOvChipkaarten(bijgewerkt);

        rdao.update(martijn);
        System.out.println("[Test] Na update (kaart1 saldo aangepast, kaart2 verwijderd, kaart3 toegevoegd):");
        System.out.println(rdao.findById(martijn.getId()));

        System.out.println("[Test] OVChipkaartDAO.findAll():");
        for (OVChipkaart k : odao.findAll()) {
            System.out.println(k);
        }

        rdao.delete(martijn);
        System.out.println("[Test] Na delete reiziger (adres + alle ov-chipkaarten moeten weg zijn):");
        System.out.println("findById(88) geeft nu: " + rdao.findById(88));
        System.out.println("findByKaartNummer(881) geeft nu: " + odao.findByKaartNummer(881));
        System.out.println("findByKaartNummer(883) geeft nu: " + odao.findByKaartNummer(883));
    }
}