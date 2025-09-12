package Dao;

import Domain.AdresHibernate;
import Domain.ReizigerHibernate;
import java.util.List;

public interface AdresDAOHibernateinterface {
    boolean save(AdresHibernate adres);
    boolean update(AdresHibernate adres);
    boolean delete(AdresHibernate adres);
    AdresHibernate findById(int id);
    AdresHibernate findByReiziger(ReizigerHibernate reiziger);
    List<AdresHibernate> findAll();
}
