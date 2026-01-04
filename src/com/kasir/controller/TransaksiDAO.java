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

    // UPDATE: Menambahkan parameter idVoucher (Bisa NULL)
    public boolean simpanTransaksi(String noTrx, String namaPelanggan, DefaultTableModel modelKeranjang, Integer idVoucher) {
        Connection c = null;
        try {
            c = Koneksi.configDB();
            c.setAutoCommit(false); 

            // SQL Update: Menambahkan kolom id_voucher
            String sql = "INSERT INTO data_penjualan (no_transaksi, nama_pelanggan, id_menu, harga, qty, subtotal, metode_pembayaran, id_voucher) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);

            for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                
                int idMenu = Integer.parseInt(modelKeranjang.getValueAt(i, 1).toString());
                String metode = modelKeranjang.getValueAt(i, 2).toString(); 
                double harga = Double.parseDouble(modelKeranjang.getValueAt(i, 4).toString());
                int qty = Integer.parseInt(modelKeranjang.getValueAt(i, 5).toString());
                double subtotal = Double.parseDouble(modelKeranjang.getValueAt(i, 6).toString());

                p.setString(1, noTrx);          
                p.setString(2, namaPelanggan);
                p.setInt(3, idMenu);            
                p.setDouble(4, harga);          
                p.setInt(5, qty);               
                p.setDouble(6, subtotal);       
                p.setString(7, metode);
                
                // Set id_voucher (Jika null, setNull di SQL)
                if (idVoucher == null) {
                    p.setNull(8, java.sql.Types.INTEGER);
                } else {
                    p.setInt(8, idVoucher);
                }
                
                p.addBatch();
            }
            
            p.executeBatch();
            c.commit();       
            c.setAutoCommit(true);
            return true;

        } catch (Exception e) {
            try { if (c != null) c.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        }
    }
}