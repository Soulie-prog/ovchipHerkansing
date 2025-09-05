package Dao;

import Domain.Reiziger;

import java.sql.SQLException;
import java.util.List;

public interface ReizigerDAO {

    Reiziger FindByid(int id) throws SQLException;
    List<Reiziger> findAll() throws SQLException;
    boolean Save (Reiziger reiziger) throws SQLException;
    boolean Delete (int id) throws SQLException;
    boolean Update (Reiziger reiziger) throws SQLException;

}
