/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.controller.TransaksiDAO;
import com.kasir.view.KasirView; 
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Ahmad
 */
public class TransaksiController {
    
    private TransaksiDAO dao = new TransaksiDAO();
    private RiwayatDAO riwayatDao = new RiwayatDAO();
    
    public String formatRupiah(double number) {
        DecimalFormat format = new DecimalFormat("###,###.##");
        return "Rp " + format.format(number);
    }

    public String generateNoTransaksi() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return "TR-" + sdf.format(now);
    }
    
    public void hitungPreview(KasirView view) {
        try {
            String hargaStr = view.getTxtHarga().getText();
            String qtyStr = view.getTxtQty().getText();
            
            if (hargaStr.isEmpty() || qtyStr.isEmpty()) {
                view.getLblSubtotalPreview().setText("Rp 0");
                return;
            }

            double harga = Double.parseDouble(hargaStr);
            int qty = Integer.parseInt(qtyStr);
            double subtotal = harga * qty;
            view.getLblSubtotalPreview().setText(formatRupiah(subtotal));
            
        } catch (NumberFormatException e) {
            view.getLblSubtotalPreview().setText("Rp 0");
        }
    }

    public void tambahKeKeranjang(KasirView view) {
        try {
            if (view.getTxtQty().getText().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Masukkan jumlah beli!");
                return;
            }

            String rawCombo = view.getCbIdMasakan().getSelectedItem().toString(); 
            String[] split = rawCombo.split(":");
            
            if (split.length < 3) {
                 JOptionPane.showMessageDialog(view, "Format Menu Salah!");
                 return;
            }

            int idMenu = Integer.parseInt(split[0]);     
            String namaMenu = split[1];                 
            double harga = Double.parseDouble(split[2]); 
            
            String namaPelanggan = view.getTxtNamaPelanggan().getText();
            String metode = view.getMetodePembayaran(); 
            
            int qty = Integer.parseInt(view.getTxtQty().getText());
            double subtotal = harga * qty;

            DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
            
            // Masukkan ke JTable (Pakai INT agar bersih)
            model.addRow(new Object[]{
                namaPelanggan,
                idMenu,      
                metode,    
                namaMenu,   
                (int)harga,    // Casting ke int
                qty,
                (int)subtotal  // Casting ke int
            });

            hitungTotalBelanja(view);
            view.getTxtQty().setText("");
            view.getLblSubtotalPreview().setText("Rp 0");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error Tambah: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void hapusItemKeranjang(KasirView view) {
        DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
        int row = view.getTabelKeranjang().getSelectedRow();
        
        if (row >= 0) {
            model.removeRow(row);
            hitungTotalBelanja(view);
        } else {
            JOptionPane.showMessageDialog(view, "Pilih baris yang mau dihapus!");
        }
    }

    public void simpanTransaksi(KasirView view) {
        if (view.getTabelKeranjang().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Keranjang belanja kosong!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Simpan & Bayar Transaksi?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            
            String noTrx = view.getTxtNoTransaksi().getText();
            String namaPel = view.getTxtNamaPelanggan().getText();
            DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();

            boolean sukses = dao.simpanTransaksi(noTrx, namaPel, model);

            if (sukses) {
                JOptionPane.showMessageDialog(view, "Transaksi Berhasil Disimpan!");
                model.setRowCount(0); 
                view.getTxtNamaPelanggan().setText("");
                view.getLblTotalHarga().setText("Rp 0");
                view.getTxtNoTransaksi().setText(generateNoTransaksi()); 
                refreshRiwayat(view.getModelRiwayat());
            } else {
                JOptionPane.showMessageDialog(view, "Gagal Menyimpan Transaksi!");
            }
        }
    }
    
    public void hitungTotalBelanja(KasirView view) {
        DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
        double total = 0;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            // Ambil Subtotal dari Index 6
            Object nilai = model.getValueAt(i, 6); 
            double subtotal = Double.parseDouble(nilai.toString());
            total += subtotal;
        }
        
        view.getLblTotalHarga().setText(formatRupiah(total));
    }   
    public void refreshRiwayat(DefaultTableModel model) {
        riwayatDao.loadDataKeTabel(model);
    }
}