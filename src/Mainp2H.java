import Dao.ReizigerDAO;
import Dao.ReizigerDAOHibernateinterface;
import DaoSql.ReizigerDAOHibernate;
import Domain.Reiziger;
import Domain.ReizigerHibernate;
import org.hibernate.Session;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import static Hibernate.HibernateSessionFactory.getSessionFactory;

public class Mainp2H {

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



    public static void main(String[] args) throws SQLException {
        // startHibernate sesion
        Session session = getSessionFactory().openSession();
        ReizigerDAOHibernateinterface rdao = new ReizigerDAOHibernate(session);


        testReizigerDAO(rdao);

        session.close();
    }

    private static void testReizigerDAO(ReizigerDAOHibernateinterface rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // pakt eerst alle reizgers
        var reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll():");
        for (var r : reizigers) {
            System.out.println(r);
        }

        // Voeg een nieuwe reiziger toe
        ReizigerHibernate sietske = new ReizigerHibernate(77, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na save(): ");
        rdao.Save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Update achternaam
        sietske.setAchternaam("Jansen");
        rdao.Update(sietske);
        System.out.println("[Test] Na upate:");
        System.out.println(rdao.FindByid(sietske.getId()));
        //en Delete hem weer op het eind
        rdao.Delete(sietske);
        System.out.println("[Test Na delete");
        reizigers = rdao.findAll();
        for (var r : reizigers) {
            System.out.println(r);
        }
    }
}
