
package Dao;

import Domain.Reiziger;

import java.sql.SQLException;
import java.util.List;

public interface ReizigerDAO {

    Reiziger findById(int id) throws SQLException;
    List<Reiziger> findAll() throws SQLException;
    boolean save (Reiziger reiziger) throws SQLException;
    boolean delete (int id) throws SQLException;
    boolean update (Reiziger reiziger) throws SQLException;
    void setAdresDAO(AdresDAO adao) throws SQLException;
}
