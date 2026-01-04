/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.koneksi.Koneksi;
import com.kasir.model.Voucher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hary
 */
public class VoucherDAO {

    // 1. FITUR UTAMA: Cek apakah voucher VALID untuk transaksi?
    public Voucher cekVoucherValid(String kodeInput) {
        Voucher v = null;
        try {
            Connection c = Koneksi.configDB();
            // Cari voucher yang kodenya cocok DAN statusnya 'Aktif'
            String sql = "SELECT * FROM voucher WHERE kode = ? AND status = 'Aktif'";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, kodeInput);
            
            ResultSet r = p.executeQuery();
            if (r.next()) {
                v = new Voucher(
                    r.getInt("id_voucher"),
                    r.getString("kode"),
                    r.getDouble("potongan"),
                    r.getString("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v; // Mengembalikan null jika tidak ditemukan/tidak aktif
    }

    // 2. AMBIL SEMUA (Untuk Tabel Manajemen)
    public List<Voucher> getAllVoucher() {
        List<Voucher> list = new ArrayList<>();
        try {
            Connection c = Koneksi.configDB();
            String sql = "SELECT * FROM voucher";
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);
            while(r.next()) {
                list.add(new Voucher(
                    r.getInt("id_voucher"),
                    r.getString("kode"),
                    r.getDouble("potongan"),
                    r.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. TAMBAH VOUCHER
    public boolean tambahVoucher(String kode, double potongan) {
        try {
            Connection c = Koneksi.configDB();
            String sql = "INSERT INTO voucher (kode, potongan, status) VALUES (?, ?, 'Aktif')";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, kode);
            p.setDouble(2, potongan);
            p.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. UPDATE VOUCHER (Edit Nominal & Status)
    public boolean updateVoucher(int id, String kode, double potongan, String status) {
        try {
            Connection c = Koneksi.configDB();
            String sql = "UPDATE voucher SET kode=?, potongan=?, status=? WHERE id_voucher=?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, kode);
            p.setDouble(2, potongan);
            p.setString(3, status);
            p.setInt(4, id);
            p.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. HAPUS VOUCHER
    public boolean hapusVoucher(int id) {
        try {
            Connection c = Koneksi.configDB();
            String sql = "DELETE FROM voucher WHERE id_voucher=?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, id);
            p.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
