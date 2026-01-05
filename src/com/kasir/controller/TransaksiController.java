/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.koneksi.Koneksi;
import com.kasir.view.KasirView;
import com.kasir.model.Menu;
import com.kasir.model.Transaksi;
import com.kasir.model.TransaksiItem;
import com.kasir.model.Voucher;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Ahmad
 */
public class TransaksiController {
    
    // --- HELPER ---
    public String formatRupiah(double number) {
        return "Rp " + new DecimalFormat("###,###.##").format(number);
    }

    public String generateNoTransaksi() {
        return "TR-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }

    // --- UI LOGIC ---
    public void hitungPreview(KasirView view) {
        try {
            String harga = view.getTxtHarga().getText();
            String qty = view.getTxtQty().getText();
            
            if (harga.isEmpty() || qty.isEmpty()) {
                view.getLblSubtotalPreview().setText("Rp 0");
                return;
            }
            double subtotal = Double.parseDouble(harga) * Integer.parseInt(qty);
            view.getLblSubtotalPreview().setText(formatRupiah(subtotal));
        } catch (NumberFormatException e) {
            view.getLblSubtotalPreview().setText("Rp 0");
        }
    }

    public void hitungTotalBelanja(KasirView view, Voucher currentVoucher) {
        DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
        double total = 0;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            total += Double.parseDouble(model.getValueAt(i, 6).toString());
        }
        
        double pot = (currentVoucher != null) ? currentVoucher.getPotongan() : 0;
        view.getLblTotalHarga().setText(formatRupiah(Math.max(0, total - pot)));
    }  

    public void tambahKeKeranjang(KasirView view, Voucher currentVoucher) {
        try {
            // Validasi Input
            if (view.getTxtQty().getText().isEmpty() || view.getTxtQty().getText().equals("0")) {
                JOptionPane.showMessageDialog(view, "Masukkan jumlah beli minimal 1!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTxtNamaPelanggan().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Nama Pelanggan harus diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getCbMenu().getSelectedItem() == null) {
                JOptionPane.showMessageDialog(view, "Pilih menu terlebih dahulu!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // PENTING: Ambil Objek Menu langsung, tidak perlu split String lagi
            Menu selectedMenu = (Menu) view.getCbMenu().getSelectedItem();
            
            int id = selectedMenu.getId();
            String nm = selectedMenu.getNama();
            double hrg = selectedMenu.getHarga();
            int q = Integer.parseInt(view.getTxtQty().getText());
            
            DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
            model.addRow(new Object[]{
                view.getTxtNamaPelanggan().getText(),
                id,
                view.getMetodePembayaran(),
                nm,
                (int)hrg,
                q,
                (int)(hrg * q)
            });

            hitungTotalBelanja(view, currentVoucher);
            view.resetInput(); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error Input: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void hapusItemKeranjang(KasirView view, Voucher currentVoucher) {
        int row = view.getTabelKeranjang().getSelectedRow();
        if (row >= 0) {
            ((DefaultTableModel) view.getTabelKeranjang().getModel()).removeRow(row);
            hitungTotalBelanja(view, currentVoucher);
            view.resetInput(); 
        } else {
            JOptionPane.showMessageDialog(view, "Pilih baris yang mau dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- SIMPAN DATABASE ---
    public void simpanTransaksi(KasirView view, Voucher voucher) {
        if (view.getTabelKeranjang().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Keranjang belanja kosong!", "Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
        
        Transaksi trx = new Transaksi(
            view.getTxtNoTransaksi().getText(),
            view.getTxtNamaPelanggan().getText(),
            view.getMetodePembayaran()
        );
        trx.setVoucher(voucher);

        for (int i = 0; i < model.getRowCount(); i++) {
            trx.tambahItem(new TransaksiItem(
                Integer.parseInt(model.getValueAt(i, 1).toString()),
                Double.parseDouble(model.getValueAt(i, 4).toString()),
                Integer.parseInt(model.getValueAt(i, 5).toString()),
                Double.parseDouble(model.getValueAt(i, 6).toString())
            ));
        }
        
        double grandTotal = trx.hitungTotalBersih();
        if (trx.getMetodePembayaran().equalsIgnoreCase("Cash")) {
            String info = (voucher != null) ? "\n(Potongan: " + formatRupiah(voucher.getPotongan()) + ")" : "";
            String input = JOptionPane.showInputDialog(view, "Total: " + formatRupiah(grandTotal) + info + "\nBayar:", "Pembayaran", JOptionPane.QUESTION_MESSAGE);
            
            if (input == null) return; 
            
            try {
                double bayar = Double.parseDouble(input.replaceAll("[^0-9]", ""));
                if (bayar < grandTotal) {
                    JOptionPane.showMessageDialog(view, "Uang Kurang!", "Gagal", JOptionPane.ERROR_MESSAGE); return;
                }
                JOptionPane.showMessageDialog(view, "Kembalian: " + formatRupiah(bayar - grandTotal), "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) { return; }
        } else {
            trx.setVoucher(null);
        }

        Connection c = null;
        try {
            c = Koneksi.configDB();
            c.setAutoCommit(false);
            
            String sql = "INSERT INTO data_penjualan (no_transaksi, nama_pelanggan, id_menu, harga, qty, subtotal, metode_pembayaran, id_voucher) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);
            
            for (TransaksiItem item : trx.getListItems()) {
                p.setString(1, trx.getNoTransaksi());
                p.setString(2, trx.getNamaPelanggan());
                p.setInt(3, item.getIdMenu());
                p.setDouble(4, item.getHarga());
                p.setInt(5, item.getQty());
                p.setDouble(6, item.getSubtotal());
                p.setString(7, trx.getMetodePembayaran());
                
                if (trx.getVoucher() != null) {
                    p.setInt(8, trx.getVoucher().getId());
                } else {
                    p.setNull(8, java.sql.Types.INTEGER);
                }
                p.addBatch();
            }
            
            p.executeBatch();
            c.commit(); 
            JOptionPane.showMessageDialog(view, "Transaksi Berhasil Disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            view.resetForm(); 
        } catch (Exception e) {
            try { if(c!=null) c.rollback(); } catch(Exception ex){}
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Gagal Simpan ke Database!", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if(c!=null) c.setAutoCommit(true); } catch(Exception ex){}
        }
    }
}