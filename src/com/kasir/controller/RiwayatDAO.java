/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.koneksi.Koneksi;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class RiwayatDAO {
    public void loadDataKeTabel(DefaultTableModel modelRiwayat) {
        modelRiwayat.setRowCount(0); 
        try {
            Connection c = Koneksi.configDB();
            String sql = "SELECT dp.*, m.nama_menu FROM data_penjualan dp " +
                         "JOIN menu m ON dp.id_menu = m.id_menu ORDER BY dp.id DESC";
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);
            
            while(r.next()) {
                modelRiwayat.addRow(new Object[]{
                    r.getString("no_transaksi"),
                    r.getString("tanggal"),
                    r.getString("nama_pelanggan"),
                    r.getString("nama_menu"),
                    r.getInt("qty"),
                    r.getDouble("subtotal")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}