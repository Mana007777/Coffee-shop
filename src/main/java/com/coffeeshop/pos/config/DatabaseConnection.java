package com.coffeeshop.pos.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:coffee_pos.db";

    public static Connection connect()
    {
        try{
            return DriverManager.getConnection(URL);
        } catch (SQLException e){
            System.out.println("Database Connection Failed: "+ e.getMessage());
            return null;
        }
    }
}
