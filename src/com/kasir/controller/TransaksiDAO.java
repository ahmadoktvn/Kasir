/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller; // Pastikan package ini sesuai struktur folder Anda (biasanya com.kasir.dao)

import com.kasir.koneksi.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ahmad
 */
public class TransaksiDAO {

    public boolean simpanTransaksi(String noTrx, String namaPelanggan, DefaultTableModel modelKeranjang) {
        Connection c = null;
        try {
            c = Koneksi.configDB();
            c.setAutoCommit(false); // Mode Transaksi: Simpan sekaligus

            // --- PERUBAHAN 1: SQL menggunakan kolom 'id_menu' ---
            String sql = "INSERT INTO data_penjualan (no_transaksi, nama_pelanggan, id_menu, harga, qty, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);

            // Looping isi keranjang belanja
            for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                
                // --- PERUBAHAN 2: Mapping Kolom Tabel GUI ---
                // Asumsi urutan kolom dari Controller nanti adalah:
                // [0] Pelanggan 
                // [1] ID MENU (Integer) <--- Kita ambil ini
                // [2] Nama Menu (String)
                // [3] Harga
                // [4] Qty
                // [5] Subtotal
                
                int idMenu = Integer.parseInt(modelKeranjang.getValueAt(i, 1).toString()); // Ambil ID
                double harga = Double.parseDouble(modelKeranjang.getValueAt(i, 3).toString());
                int qty = Integer.parseInt(modelKeranjang.getValueAt(i, 4).toString());
                double subtotal = Double.parseDouble(modelKeranjang.getValueAt(i, 5).toString());

                // Set Parameter Query
                p.setString(1, noTrx);          
                p.setString(2, namaPelanggan);  
                p.setInt(3, idMenu);           // Masukkan ID Menu (Int)
                p.setDouble(4, harga);          
                p.setInt(5, qty);               
                p.setDouble(6, subtotal);       
                
                p.addBatch(); // Masukkan ke antrian
            }
            
            p.executeBatch(); // Eksekusi semua antrian
            c.commit();       // Simpan permanen ke database
            c.setAutoCommit(true);
            return true;

        } catch (Exception e) {
            try { if (c != null) c.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        }
    }
}