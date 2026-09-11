package dao;

import domain.Reiziger;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface ReizigerDAO {

    Reiziger findById(int id) throws SQLException;
    List<Reiziger> findAll() throws SQLException;
    List<Reiziger> findByGbdatum(Date gbdatum) throws SQLException;
    boolean save(Reiziger reiziger) throws SQLException;
    boolean delete(Reiziger reiziger) throws SQLException;
    boolean update(Reiziger reiziger) throws SQLException;

}