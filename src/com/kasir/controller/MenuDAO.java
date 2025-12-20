/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;


import com.kasir.koneksi.Koneksi;
import com.kasir.model.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ahmad
 */

public class MenuDAO {
    
    // Ambil semua menu untuk ditampilkan di ComboBox
    public List<Menu> getAllMenu() {
        List<Menu> listMenu = new ArrayList<>();
        try {
            Connection c = Koneksi.configDB();
            String sql = "SELECT * FROM menu";
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);
            
            while(r.next()){
                Menu m = new Menu(
                    r.getInt("id_menu"),
                    r.getString("nama_menu"),
                    r.getDouble("harga")
                );
                listMenu.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMenu;
    }
    
    // Simpan menu baru
    public boolean tambahMenu(String nama, double harga) {
        try {
            Connection c = Koneksi.configDB();
            String sql = "INSERT INTO menu (nama_menu, harga) VALUES (?, ?)";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, nama);
            p.setDouble(2, harga);
            p.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}