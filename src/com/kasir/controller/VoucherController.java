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
public class VoucherController {

    // Cek Voucher harus Aktif DAN Belum Dihapus
    public Voucher cekVoucherValid(String kode) {
        String sql = "SELECT * FROM voucher WHERE kode = ? AND status = 'Aktif' AND is_deleted = 0";
        try (Connection c = Koneksi.configDB(); 
             PreparedStatement p = c.prepareStatement(sql)) {
            
            p.setString(1, kode);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return new Voucher(
                        r.getInt("id_voucher"), 
                        r.getString("kode"), 
                        r.getDouble("potongan"), 
                        r.getString("status")
                    );
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // Tampilkan Hanya yang Belum Dihapus
    public List<Voucher> getAllVoucher() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM voucher WHERE is_deleted = 0";
        
        try (Connection c = Koneksi.configDB(); 
             Statement s = c.createStatement(); 
             ResultSet r = s.executeQuery(sql)) {
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

    public boolean tambahVoucher(String kode, double potongan) {
        String sql = "INSERT INTO voucher (kode, potongan, status, is_deleted) VALUES (?, ?, 'Aktif', 0)";
        try (Connection c = Koneksi.configDB(); 
             PreparedStatement p = c.prepareStatement(sql)) {
            
            p.setString(1, kode);
            p.setDouble(2, potongan);
            p.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean updateVoucher(int id, String kode, double potongan, String status) {
        String sql = "UPDATE voucher SET kode=?, potongan=?, status=? WHERE id_voucher=?";
        try (Connection c = Koneksi.configDB(); 
             PreparedStatement p = c.prepareStatement(sql)) {
            
            p.setString(1, kode);
            p.setDouble(2, potongan);
            p.setString(3, status);
            p.setInt(4, id);
            p.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }

    // SOFT DELETE
    public boolean hapusVoucher(int id) {
        String sql = "UPDATE voucher SET is_deleted = 1 WHERE id_voucher=?";
        try (Connection c = Koneksi.configDB(); 
             PreparedStatement p = c.prepareStatement(sql)) {
            
            p.setInt(1, id);
            p.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }
}