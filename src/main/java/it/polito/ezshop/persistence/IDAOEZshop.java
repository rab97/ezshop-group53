package it.polito.ezshop.persistence;

import java.sql.SQLException;
import it.polito.ezshop.data.User;

public interface IDAOEZshop {

    public User searchUser(String username, String password) throws DAOException;
}
