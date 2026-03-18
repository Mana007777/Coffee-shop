package com.coffeeshop.pos.config;
import com.coffeeshop.pos.config.DatabaseConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connection = DatabaseConnection.connect();

        if (connection != null){
            System.out.println("Connected to SQLite successfully");
        }else{
            System.out.println("Connection Failed");
        }
    }
}