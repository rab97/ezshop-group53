package it.polito.ezshop.persistence;

import java.sql.*;

public class DataSource {
    private String url = "jdbc:sqlite:db.db";

    public DataSource() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Impossibile caricare il driver " + ex.getMessage());
        }
    }

    public Connection getConnection() throws DAOException {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new DAOException("Impossibile stabilire una connessione: " + ex.getMessage());
        }
    }

    public void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("error connection");
        }
    }

}
