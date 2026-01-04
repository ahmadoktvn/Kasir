/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.koneksi.Koneksi;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ahmad
 */

public class RiwayatDAO {
    
    public void loadDataKeTabel(DefaultTableModel modelRiwayat) {
        modelRiwayat.setRowCount(0); 
        try {
            Connection c = Koneksi.configDB();
            
            // 1. Join ke tabel voucher untuk ambil kolom 'potongan'
            String sql = "SELECT dp.*, m.nama_menu, v.potongan " +
                         "FROM data_penjualan dp " +
                         "JOIN menu m ON dp.id_menu = m.id_menu " +
                         "LEFT JOIN voucher v ON dp.id_voucher = v.id_voucher " + 
                         "ORDER BY dp.id DESC";
                         
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);
            
            while(r.next()) {
                double subtotal = r.getDouble("subtotal");
                
                // Ambil diskon (jika null/tidak ada voucher, otomatis jadi 0)
                double diskon = r.getDouble("potongan"); 
                
                // 2. Logika Perhitungan Total
                // Total = Subtotal - Diskon
                double totalBayar = subtotal - diskon;
                
                // Pencegahan agar tidak minus (jika potongan lebih besar dari harga menu)
                if (totalBayar < 0) totalBayar = 0;

                modelRiwayat.addRow(new Object[]{
                    r.getString("no_transaksi"),
                    r.getString("tanggal"),
                    r.getString("nama_pelanggan"),
                    r.getString("metode_pembayaran"),
                    r.getString("nama_menu"),
                    r.getInt("qty"),
                    (int) subtotal,    // Kolom Subtotal (Murni)
                    (int) diskon,      // Kolom Diskon
                    (int) totalBayar   // Kolom Total (Setelah Diskon)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}