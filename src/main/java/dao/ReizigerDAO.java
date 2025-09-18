package dao;

import domain.Reiziger;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface ReizigerDAO {

    Reiziger findById(int id) throws SQLException;

    List<Reiziger> findAll() throws SQLException;

    List<Reiziger> findByGbdatum(Date datum) throws SQLException;

    boolean save(Reiziger r) throws SQLException;

    boolean update(Reiziger r) throws SQLException;

    boolean delete(int id) throws SQLException;
}
