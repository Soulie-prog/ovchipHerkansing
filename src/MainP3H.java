
import Domain.ReizigerHibernate;
import Domain.AdresHibernate;
import DaoSql.ReizigerDAOHibernate;
import DaoSql.AdresDAOHibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Date;
import java.util.List;

public class MainP3H {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(ReizigerHibernate.class)
                .addAnnotatedClass(AdresHibernate.class)
                .buildSessionFactory();

        Session session = factory.openSession();

        try {
            ReizigerDAOHibernate rdao = new ReizigerDAOHibernate(session);
            AdresDAOHibernate adao = new AdresDAOHibernate(session);

            System.out.println("Connected to Hibernate!");

            // save test
            ReizigerHibernate r1 = new ReizigerHibernate(1, "G", "", "van Rijn", Date.valueOf("2002-09-17"), null);
            AdresHibernate a1 = new AdresHibernate(1, "3511 LX", "37", "StraatX", "PlaatsX", r1);
            r1.setAdres(a1);

            rdao.Save(r1);

            ReizigerHibernate r2 = new ReizigerHibernate(2, "B", "", "van Rijn", Date.valueOf("2002-10-22"), null);
            AdresHibernate a2 = new AdresHibernate(2, "3521 AL", "6A", "StraatY", "PlaatsY", r2);
            r2.setAdres(a2);

            rdao.Save(r2);

            ReizigerHibernate r3 = new ReizigerHibernate(3, "H", "", "Lubben", Date.valueOf("1998-08-11"), null);
            AdresHibernate a3 = new AdresHibernate(3, "6707 AA", "375", "StraatZ", "PlaatsZ", r3);
            r3.setAdres(a3);

            rdao.Save(r3);

            ReizigerHibernate r4 = new ReizigerHibernate(4, "F", "", "Memari", Date.valueOf("2002-12-03"), null);
            AdresHibernate a4 = new AdresHibernate(4, "3817 CH", "4", "StraatW", "PlaatsW", r4);
            r4.setAdres(a4);

            rdao.Save(r4);

            ReizigerHibernate r5 = new ReizigerHibernate(5, "G", "", "Piccardo", Date.valueOf("2002-12-03"), null);
            AdresHibernate a5 = new AdresHibernate(5, "3572 WP", "22", "StraatV", "PlaatsV", r5);
            r5.setAdres(a5);

            rdao.Save(r5);

            // findAll test
            List<ReizigerHibernate> reizigers = rdao.findAll();
            for (ReizigerHibernate r : reizigers) {
                System.out.println(r);
            }

            // update test
            r1.setAchternaam("van Rijn Updated");
            rdao.Update(r1);
            a1.setHuisnummer("99");
            adao.update(a1);

            System.out.println("\nNa update:");
            System.out.println(rdao.FindByid(1));


            //delete test
            adao.delete(a2);
            rdao.Delete(r2);

            System.out.println("\nNa delete:");
            reizigers = rdao.findAll();
            for (ReizigerHibernate r : reizigers) {
                System.out.println(r);
            }

        } finally {
            session.close();
            factory.close();
        }
    }
}
