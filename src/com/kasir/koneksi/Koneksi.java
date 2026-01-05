/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.koneksi;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Ahmad
 */

public class Koneksi {
    private static Connection mysqlconfig;
    
    public static Connection configDB() throws SQLException {
        if (mysqlconfig == null || mysqlconfig.isClosed()) {
            try {
                String url = "jdbc:mysql://localhost:8889/db_kasir"; 
                String user = "root";
                String pass = "root";
                
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                mysqlconfig = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                System.err.println("Gagal Koneksi: " + e.getMessage());
                throw e;
            }
        }
        return mysqlconfig;
    }
}