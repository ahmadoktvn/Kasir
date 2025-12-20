/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.view.MainView; 
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ahmad
 */





public class TransaksiController {
    
    // Format Rupiah
    public static String formatRupiah(double number) {
        DecimalFormat format = new DecimalFormat("###,###.##");
        return "Rp " + format.format(number);
    }

    public static String generateNoTransaksi() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return "TR-" + sdf.format(now);
    }
    
    // --- FITUR BARU: Hitung Preview saat mengetik ---
    public static void hitungPreview(MainView view) {
        try {
            String hargaStr = view.getTxtHarga().getText();
            String qtyStr = view.getTxtQty().getText();
            
            // Cek jika kosong, jangan hitung
            if (hargaStr.isEmpty() || qtyStr.isEmpty()) {
                view.getLblSubtotalPreview().setText("Rp 0");
                return;
            }

            double harga = Double.parseDouble(hargaStr);
            int qty = Integer.parseInt(qtyStr);
            
            double subtotal = harga * qty;
            
            // Tampilkan hasil hitungan langsung di label kecil samping input
            view.getLblSubtotalPreview().setText(formatRupiah(subtotal));
            
        } catch (NumberFormatException e) {
            // Jika user mengetik huruf, anggap 0
            view.getLblSubtotalPreview().setText("Rp 0");
        }
    }

    // Fungsi Hitung Total Akhir (Grand Total) di Tabel
    // Bagian method hitungTotalBelanja saja yang perlu diubah
public static void hitungTotalBelanja(MainView view) {
    DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
    double total = 0;
    
    for (int i = 0; i < model.getRowCount(); i++) {
        // Subtotal sekarang ada di Kolom Index ke-4
        Object nilai = model.getValueAt(i, 4); 
        double subtotal = Double.parseDouble(nilai.toString());
        total += subtotal;
    }
    
    view.getLblTotalHarga().setText(formatRupiah(total));
}
}