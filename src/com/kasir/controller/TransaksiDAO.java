/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

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
            c.setAutoCommit(false); // Biar aman, simpan sekaligus

            // Query INSERT ke satu tabel saja
            String sql = "INSERT INTO data_penjualan (no_transaksi, nama_pelanggan, nama_menu, harga, qty, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);

            // Looping isi keranjang belanja
            for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                // Ambil data dari tabel GUI
                // Index 0=Pelanggan(Item), 1=Menu, 2=Harga, 3=Qty, 4=Subtotal
                
                // Catatan: Kita pakai 'namaPelanggan' dari parameter utama (Header) 
                // atau bisa juga ambil dari tabel index 0 jika nama per item beda-beda.
                // Disini saya pakai parameter utama agar konsisten satu struk satu nama.
                
                String namaMenu = modelKeranjang.getValueAt(i, 1).toString();
                double harga = Double.parseDouble(modelKeranjang.getValueAt(i, 2).toString());
                int qty = Integer.parseInt(modelKeranjang.getValueAt(i, 3).toString());
                double subtotal = Double.parseDouble(modelKeranjang.getValueAt(i, 4).toString());

                // Set Parameter Query
                p.setString(1, noTrx);          // Kolom no_transaksi
                p.setString(2, namaPelanggan);  // Kolom nama_pelanggan (Diulang setiap baris)
                p.setString(3, namaMenu);       // Kolom nama_menu
                p.setDouble(4, harga);          // Kolom harga
                p.setInt(5, qty);               // Kolom qty
                p.setDouble(6, subtotal);       // Kolom subtotal
                
                p.addBatch(); // Masukkan ke antrian
            }
            
            p.executeBatch(); // Eksekusi semua antrian
            c.commit();       // Simpan permanen
            c.setAutoCommit(true);
            return true;

        } catch (Exception e) {
            try { if (c != null) c.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        }
    }
}