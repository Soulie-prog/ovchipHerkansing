package DaoSql;

import Dao.ReizigerDAO;
import Dao.ReizigerDAOHibernateinterface;
import Domain.Reiziger;
import Domain.ReizigerHibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ReizigerDAOHibernate implements ReizigerDAOHibernateinterface {

    private Session session;

    public ReizigerDAOHibernate(Session session) {
        this.session = session;
    }
    @Override
    public boolean Save(ReizigerHibernate reiziger) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(reiziger);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public ReizigerHibernate FindByid(int id) {
        try {
            return session.get(ReizigerHibernate.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ReizigerHibernate> findAll() {
        try {
            return session.createQuery("from ReizigerHibernate ", ReizigerHibernate.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean Update(ReizigerHibernate reiziger) {
        Transaction tx = null;
        try  {
            tx = session.beginTransaction();
            session.merge(reiziger);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // wou op int id verwijderen lukte niet dus toch maar als object zelf
    @Override
    public boolean Delete(ReizigerHibernate reiziger) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.remove(reiziger);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

}
