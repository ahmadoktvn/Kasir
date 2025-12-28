/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.controller.TransaksiDAO; // Import DAO
import com.kasir.view.MainView; 
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
    
    // Panggil DAO agar bisa simpan ke database
    private TransaksiDAO dao = new TransaksiDAO();
    private RiwayatDAO riwayatDao = new RiwayatDAO();
    // --- 1. UTILITIES ---
    
    public String formatRupiah(double number) {
        DecimalFormat format = new DecimalFormat("###,###.##");
        return "Rp " + format.format(number);
    }

    public String generateNoTransaksi() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return "TR-" + sdf.format(now);
    }
    
    // --- 2. LOGIKA PRATINJAU (PREVIEW) ---
    
    public void hitungPreview(MainView view) {
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
            
            // Tampilkan hasil hitungan langsung
            view.getLblSubtotalPreview().setText(formatRupiah(subtotal));
            
        } catch (NumberFormatException e) {
            view.getLblSubtotalPreview().setText("Rp 0");
        }
    }

    // --- 3. LOGIKA TAMBAH KE KERANJANG (Tabel GUI) ---
    
    public void tambahKeKeranjang(MainView view) {
        try {
            // Validasi Input
            if (view.getTxtQty().getText().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Masukkan jumlah beli!");
                return;
            }

            // 1. Ambil Data dari ComboBox (Format harus: "101:Nasi Goreng:15000")
            // Pastikan item di ComboBox View kamu formatnya ada titik duanya
            String rawCombo = view.getCbIdMasakan().getSelectedItem().toString(); 
            String[] split = rawCombo.split(":");
            
            // Mencegah error jika format combo salah
            if (split.length < 3) {
                 JOptionPane.showMessageDialog(view, "Format Menu Salah! Harus 'ID:Nama:Harga'");
                 return;
            }

            int idMenu = Integer.parseInt(split[0]);     // Index 0 = ID
            String namaMenu = split[1];                 // Index 1 = Nama
            double harga = Double.parseDouble(split[2]); // Index 2 = Harga
            
            String namaPelanggan = view.getTxtNamaPelanggan().getText();
            int qty = Integer.parseInt(view.getTxtQty().getText());
            double subtotal = harga * qty;

            // 2. Masukkan ke JTable
            DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
            
            // STRUKTUR KOLOM HARUS URUT (Total 6 Kolom):
            // [0] Pelanggan, [1] ID Menu, [2] Nama Menu, [3] Harga, [4] Qty, [5] Subtotal
            model.addRow(new Object[]{
                namaPelanggan,
                idMenu,      // <--- PENTING: ID disimpan disini agar DAO bisa baca
                namaMenu,   
                harga,
                qty,
                subtotal
            });

            // 3. Update Total & Reset Input
            hitungTotalBelanja(view);
            view.getTxtQty().setText("");
            view.getLblSubtotalPreview().setText("Rp 0");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error Tambah: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void hapusItemKeranjang(MainView view) {
        DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
        int row = view.getTabelKeranjang().getSelectedRow();
        
        if (row >= 0) {
            model.removeRow(row);
            hitungTotalBelanja(view);
        } else {
            JOptionPane.showMessageDialog(view, "Pilih baris yang mau dihapus!");
        }
    }

    // --- 4. LOGIKA SIMPAN KE DATABASE (Trigger DAO) ---
    
    public void simpanTransaksi(MainView view) {
        // Cek keranjang kosong
        if (view.getTabelKeranjang().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Keranjang belanja kosong!");
            return;
        }

        // Konfirmasi
        int confirm = JOptionPane.showConfirmDialog(view, "Simpan & Bayar Transaksi?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            
            String noTrx = view.getTxtNoTransaksi().getText();
            String namaPel = view.getTxtNamaPelanggan().getText();
            DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();

            // PANGGIL FUNGSI SIMPAN DI DAO
            boolean sukses = dao.simpanTransaksi(noTrx, namaPel, model);

            if (sukses) {
                JOptionPane.showMessageDialog(view, "Transaksi Berhasil Disimpan!");
                
                // Reset View setelah sukses
                model.setRowCount(0); 
                view.getTxtNamaPelanggan().setText("");
                view.getLblTotalHarga().setText("Rp 0");
                view.getTxtNoTransaksi().setText(generateNoTransaksi()); // Generate ID Baru
            } else {
                JOptionPane.showMessageDialog(view, "Gagal Menyimpan Transaksi!");
            }
        }
    }

    // --- 5. HITUNG TOTAL BELANJA ---
    
    public void hitungTotalBelanja(MainView view) {
        DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
        double total = 0;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            // Subtotal sekarang ada di Index ke-5 (karena ada sisipan ID Menu)
            // [0]Pel [1]ID [2]Nama [3]Hrg [4]Qty [5]Subtotal
            Object nilai = model.getValueAt(i, 5); 
            double subtotal = Double.parseDouble(nilai.toString());
            total += subtotal;
        }
        
        view.getLblTotalHarga().setText(formatRupiah(total));
    }
    public void refreshRiwayat(DefaultTableModel model) {
    riwayatDao.loadDataKeTabel(model);
}
}
