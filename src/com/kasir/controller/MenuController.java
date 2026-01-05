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

public class MenuController {
    
    // TAMPILKAN HANYA YANG BELUM DIHAPUS (is_deleted = 0)
    public List<Menu> getAllMenu() {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE is_deleted = 0";
        
        try (Connection c = Koneksi.configDB(); 
             Statement s = c.createStatement(); 
             ResultSet r = s.executeQuery(sql)) {
            
            while(r.next()) {
                list.add(new Menu(
                    r.getInt("id_menu"), 
                    r.getString("nama_menu"), 
                    r.getDouble("harga")
                ));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
    
    public boolean tambahMenu(String nama, double harga) {
        String sql = "INSERT INTO menu (nama_menu, harga, is_deleted) VALUES (?, ?, 0)";
        try (Connection c = Koneksi.configDB(); 
             PreparedStatement p = c.prepareStatement(sql)) {
            
            p.setString(1, nama);
            p.setDouble(2, harga);
            p.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }
    
    public boolean updateMenu(int id, String nama, double harga) {
        String sql = "UPDATE menu SET nama_menu=?, harga=? WHERE id_menu=?";
        try (Connection c = Koneksi.configDB(); 
             PreparedStatement p = c.prepareStatement(sql)) {
            
            p.setString(1, nama);
            p.setDouble(2, harga);
            p.setInt(3, id);
            p.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }

    // SOFT DELETE: Ubah is_deleted jadi 1 (Data tidak hilang di DB)
    public boolean deleteMenu(int id) {
        String sql = "UPDATE menu SET is_deleted = 1 WHERE id_menu=?";
        try (Connection c = Koneksi.configDB(); 
             PreparedStatement p = c.prepareStatement(sql)) {
            
            p.setInt(1, id);
            p.executeUpdate();
            return true;
        } catch (Exception e) { 
            e.printStackTrace();
            return false; 
        }
    }
}