/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.koneksi.Koneksi;
import com.kasir.view.RiwayatView;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Ahmad
 */

public class RiwayatController {
    
    public void loadDataKeTabel(DefaultTableModel model) {
        model.setRowCount(0); 
        // Mengambil semua data termasuk yang menu/vouchernya sudah di soft-delete
        String sql = "SELECT dp.*, m.nama_menu, v.potongan FROM data_penjualan dp " +
                     "JOIN menu m ON dp.id_menu = m.id_menu " +
                     "LEFT JOIN voucher v ON dp.id_voucher = v.id_voucher " +
                     "ORDER BY dp.id DESC";

        try (Connection c = Koneksi.configDB(); 
             Statement s = c.createStatement(); 
             ResultSet r = s.executeQuery(sql)) {
            
            int no = 1;
            while(r.next()) {
                double sub = r.getDouble("subtotal");
                double disc = r.getDouble("potongan"); 
                
                model.addRow(new Object[]{
                    no++, // Penomoran
                    r.getString("no_transaksi"), 
                    r.getString("tanggal"), 
                    r.getString("nama_pelanggan"), 
                    r.getString("metode_pembayaran"),
                    r.getString("nama_menu"), 
                    r.getInt("qty"), 
                    (int)sub, 
                    (int)disc, 
                    (int)Math.max(0, sub - disc)
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ... (Bagian PDF Logic tetap sama, hanya perlu menyesuaikan jumlah kolom jika mau menampilkan No di PDF juga) ...
    public void cetakLaporanPDF(RiwayatView view) {
        DefaultTableModel model = view.getModelRiwayat();
        if (model.getRowCount() == 0) { JOptionPane.showMessageDialog(view, "Data Kosong!"); return; }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Laporan_Transaksi.pdf")); 
        
        if (fc.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) path += ".pdf";
            
            Document doc = null;
            try {
                doc = new Document();
                PdfWriter.getInstance(doc, new FileOutputStream(path));
                doc.open();

                Paragraph t = new Paragraph("LAPORAN TRANSAKSI");
                t.setAlignment(Paragraph.ALIGN_CENTER);
                doc.add(t);
                doc.add(new Paragraph("Dicetak: " + new Date().toString()));
                doc.add(new Paragraph(" ")); 
                
                // Tambah 1 kolom untuk "No" (Total 10 kolom sekarang)
                PdfPTable table = new PdfPTable(10);
                table.setWidthPercentage(100);
                // Sesuaikan lebar kolom
                table.setWidths(new float[]{1f, 3f, 3f, 3f, 2f, 3f, 1f, 2f, 2f, 2f});
                
                String[] h = {"No", "No Trx", "Tanggal", "Pel", "Metode", "Menu", "Qty", "Sub", "Disc", "Total"};
                for (String s : h) {
                    PdfPCell c = new PdfPCell(new Phrase(s));
                    c.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(c);
                }
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        String d = (model.getValueAt(i, j) != null) ? model.getValueAt(i, j).toString() : "";
                        PdfPCell c = new PdfPCell(new Phrase(d));
                        // Angka mulai dari kolom index 6 (Qty) sampai akhir
                        if (j >= 6) c.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        else c.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(c);
                    }
                }
                doc.add(table);
                JOptionPane.showMessageDialog(view, "PDF Berhasil Disimpan!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Gagal PDF: " + e.getMessage());
            } finally {
                if (doc != null) doc.close();
            }
        }
    }
}