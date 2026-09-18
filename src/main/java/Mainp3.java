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
import java.util.List;
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

            AdresDAOPsql adao = new AdresDAOPsql(conn);
            ReizigerDAO rdao = new ReizigerDAOPsql(conn, adao);
            adao.setReizigerDAO(rdao);

            testReizigerAdresDAO(rdao, adao);

        } catch (SQLException e) {
            System.out.println("Er is een fout opgetreden bij de databaseverbinding:");
            e.printStackTrace();
        }
    }

    private static void testReizigerAdresDAO(ReizigerDAO rdao, AdresDAO adao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO en AdresDAO -------------");

        System.out.println("[Test] ReizigerDAO.findAll():");
        printReizigers(rdao.findAll());

        Reiziger sietske = new Reiziger(78, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));
        Adres adres = new Adres(78, "3511 LX", "37", "Voorstraat", "Utrecht", sietske);
        sietske.setAdres(adres);
        rdao.save(sietske);

        System.out.println("[Test] Na save van reiziger met adres:");
        System.out.println(rdao.findById(sietske.getId()));

        sietske.setAchternaam("Jansen");
        sietske.getAdres().setStraat("Nieuwestraat");
        rdao.update(sietske);
        System.out.println("[Test] Na update van reiziger en adres:");
        System.out.println(rdao.findById(sietske.getId()));

        System.out.println("[Test] AdresDAO.findByReiziger():");
        System.out.println(adao.findByReiziger(sietske));

        System.out.println("[Test] AdresDAO.findAll():");
        for (Adres a : adao.findAll()) {
            System.out.println(a);
        }

        System.out.println("[Test] ReizigerDAO.findByGbdatum():");
        printReizigers(rdao.findByGbdatum(java.sql.Date.valueOf("1981-03-14")));

        rdao.delete(sietske);
        System.out.println("[Test] Na delete (reiziger en adres verwijderd):");
        printReizigers(rdao.findAll());
        System.out.println("findById(78) geeft nu: " + rdao.findById(77));
    }

    private static void printReizigers(List<Reiziger> reizigers) {
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
    }
}