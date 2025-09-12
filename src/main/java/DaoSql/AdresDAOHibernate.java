package DaoSql;

import Dao.AdresDAOHibernateinterface;
import Domain.AdresHibernate;
import Domain.ReizigerHibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class AdresDAOHibernate implements AdresDAOHibernateinterface {

    private Session session;

    public AdresDAOHibernate(Session session) {
        this.session = session;
    }

    @Override
    public boolean save(AdresHibernate adres) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(adres);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(AdresHibernate adres) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(adres);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(AdresHibernate adres) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.remove(adres);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public AdresHibernate findById(int id) {
        return session.get(AdresHibernate.class, id);
    }

    @Override
    public AdresHibernate findByReiziger(ReizigerHibernate reiziger) {
        return session.createQuery("from AdresHibernate a where a.reiziger.id = :id", AdresHibernate.class)
                .setParameter("id", reiziger.getId())
                .uniqueResult();
    }

    @Override
    public List<AdresHibernate> findAll() {
        return session.createQuery("from AdresHibernate", AdresHibernate.class).list();
    }
}
