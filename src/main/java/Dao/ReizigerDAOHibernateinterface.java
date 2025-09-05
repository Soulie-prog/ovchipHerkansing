package Dao;

import Domain.ReizigerHibernate;

import java.sql.SQLException;
import java.util.List;

public interface ReizigerDAOHibernateinterface {

    ReizigerHibernate FindByid(int id) throws SQLException;
    List<ReizigerHibernate> findAll() throws SQLException;
    boolean Save(ReizigerHibernate reiziger) throws SQLException;
    boolean Delete(ReizigerHibernate reiziger) throws SQLException;
    boolean Update(ReizigerHibernate reiziger) throws SQLException;

}

