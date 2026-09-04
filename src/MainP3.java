import Dao.AdresDAO;
import Dao.ReizigerDAO;
import DaoSql.AdresDAOPsql;
import DaoSql.ReizigerDAOPsql;
import Domain.Adres;
import Domain.Reiziger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class MainP3 {

    private static final String URL = "jdbc:postgresql://localhost:5432/ovchip";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Soulaim@n25";

    public static void main(String[] args) throws SQLException {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to database!");

            // maak eerst aan met tijdelijk null AdresDAO
            ReizigerDAOPsql rdaoTemp = new ReizigerDAOPsql(conn, null);
            // Maak AdresDAO aan en injecteer rdao
            AdresDAOPsql adao = new AdresDAOPsql(conn, rdaoTemp);
            // Maak ReizigerDAO opnieuw aan met adao
            ReizigerDAOPsql rdao = new ReizigerDAOPsql(conn, adao);

            System.out.println("\n--- Test AdresDAO CRUD ---");

            Reiziger r1 = new Reiziger(77, "T", "", "Tester", Date.valueOf("2000-01-01"));
            Adres a1 = new Adres(77, "1234 AB", "1", "Teststraat", "Teststad");

            //  save Reiziger
            rdao.save(r1);
            // Koppel adres
            a1.setReiziger(r1);
            adao.save(a1);

            // Vind adres via DAO
            Adres gevondenAdres = adao.findById(77);
            System.out.println("Gevonden adres: " + gevondenAdres);

            // Update adres
            a1.setHuisnummer("2");
            adao.update(a1);
            System.out.println("Na update: " + adao.findById(77));

            // Delete adres
            adao.delete(a1);
            System.out.println("Na delete: " + adao.findById(77));

            System.out.println("\n--- Test ReizigerDAO met gekoppelde Adres ---");

            Reiziger r2 = new Reiziger(78, "A", "", "Rijn", Date.valueOf("2001-09-12"));
            Reiziger r3 = new Reiziger(79, "B", "van", "Rijn", Date.valueOf("2002-03-03"));
            rdao.save(r2);
            rdao.save(r3);

            Adres ad2 = new Adres(78, "2345 CD", "10", "StraatA", "PlaatsA");
            Adres ad3 = new Adres(79, "3456 EF", "20", "StraatB", "PlaatsB");
            ad2.setReiziger(r2);
            ad3.setReiziger(r3);
            adao.save(ad2);
            adao.save(ad3);

            List<Reiziger> reizigers = rdao.findAll();
            for (Reiziger r : reizigers) {
                Adres a = adao.findByReiziger(r);
                System.out.println(r.getNaam() + ", Adres: " + (a != null ? a.getStraat() + " " + a.getHuisnummer() : "null"));
            }

//          delete alles
            adao.delete(ad2);
            adao.delete(ad3);
            rdao.delete(77);
            rdao.delete(78);
            rdao.delete(79);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
