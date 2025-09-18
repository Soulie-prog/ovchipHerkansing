import dao.ReizigerDAO;
import daosql.ReizigerDAOPsql;
import domain.Reiziger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class Mainp2 {

    private static  String URL;
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

            testReizigerDAO(rdao);

        } catch (SQLException e) {
            System.out.println("Er is een fout opgetreden bij de databaseverbinding:");
            e.printStackTrace();
        }
    }

    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // pakt eerst alle reizgers
        var reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll():");
        for (var r : reizigers) {
            System.out.println(r);
        }

        // Voeg een nieuwe reiziger toe
        var sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na save(): ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Update achternaam
        sietske.setAchternaam("Jansen");
        rdao.update(sietske);
        System.out.println("[Test] Na upate:");
        System.out.println(rdao.findById(sietske.getId()));


        //  findByGbdatum (met geboortedatum van sietske)
        System.out.println("\n[Test] ReizigerDAO.findByGbdatum(1981-03-14):");
        var geboortedatumReizigers = rdao.findByGbdatum(java.sql.Date.valueOf("1981-03-14"));
        for (var r : geboortedatumReizigers) {
            System.out.println(r);
        }
        //en Delete hem weer op het eind
        rdao.delete(77);
        System.out.println("[Test Na delete");
        reizigers = rdao.findAll();

        for (var r : reizigers) {
            System.out.println(r);
        }
    }


}
