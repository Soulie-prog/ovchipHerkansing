import dao.AdresDAO;
import dao.ReizigerDAO;
import daosql.AdresDAOPsql;
import daosql.ReizigerDAOPsql;
import domain.Adres;
import domain.Reiziger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Mainp3 {

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

            ReizigerDAO rdao = new ReizigerDAOPsql(conn);
            AdresDAO adao = new AdresDAOPsql(conn);

            testReizigerAdresDAO(rdao, adao);

        } catch (SQLException e) {
            System.out.println("Er is een fout opgetreden bij de databaseverbinding:");
            e.printStackTrace();
        }
    }

    private static void testReizigerAdresDAO(ReizigerDAO rdao, AdresDAO adao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO en AdresDAO -------------");

        var reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll():");
        printMetAdres(reizigers, adao);

        var sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));
        rdao.save(sietske);

        var adres = new Adres(77, "3511 LX", "37", "Voorstraat", "Utrecht", sietske.getId());
        adao.save(adres);
        sietske.setAdresId(adres.getId());

        System.out.println("[Test] Na save reiziger en adres:");
        printMetAdres(rdao.findById(sietske.getId()), adao);

        sietske.setAchternaam("Jansen");
        rdao.update(sietske);
        System.out.println("[Test] Na update:");
        printMetAdres(rdao.findById(sietske.getId()), adao);

        System.out.println("[Test] AdresDAO.findByReiziger():");
        System.out.println(adao.findByReiziger(sietske));

        System.out.println("[Test] AdresDAO.findAll():");
        for (var a : adao.findAll()) {
            System.out.println(a);
        }

        rdao.delete(sietske);
        System.out.println("[Test] Na delete (reiziger én adres verwijderd):");
        reizigers = rdao.findAll();
        printMetAdres(reizigers, adao);
    }

    private static void printMetAdres(java.util.List<Reiziger> reizigers, AdresDAO adao) throws SQLException {
        for (var r : reizigers) {
            printMetAdres(r, adao);
        }
    }

    private static void printMetAdres(Reiziger r, AdresDAO adao) throws SQLException {
        Adres adres = adao.findByReiziger(r);
        System.out.println("Reiziger {#" + r.getId() + " " + r.getNaam() + ", geb. " + r.getGeboortedatum() + ", " + adres + "}");
    }
}