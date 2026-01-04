/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.controller;

import com.kasir.controller.TransaksiDAO;
import com.kasir.view.KasirView; 
import com.kasir.model.Voucher;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;

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

    public void hitungTotalBelanja(KasirView view, Voucher currentVoucher) {
        DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
        double totalBelanja = 0;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Object nilai = model.getValueAt(i, 6); 
            double subtotal = Double.parseDouble(nilai.toString());
            totalBelanja += subtotal;
        }
        
        double potongan = 0;
        if (currentVoucher != null) {
            potongan = currentVoucher.getPotongan();
        }
        
        double grandTotal = totalBelanja - potongan;
        if (grandTotal < 0) grandTotal = 0; 
        
        view.getLblTotalHarga().setText(formatRupiah(grandTotal));
    }  

    public void tambahKeKeranjang(KasirView view, Voucher currentVoucher) {
        try {
            if (view.getTxtQty().getText().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Masukkan jumlah beli!");
                return;
            }

            if (view.getCbIdMasakan().getSelectedItem() == null) return;
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
            
            model.addRow(new Object[]{
                namaPelanggan,
                idMenu,      
                metode,    
                namaMenu,   
                (int)harga,   
                qty,
                (int)subtotal 
            });

            hitungTotalBelanja(view, currentVoucher);
            view.getTxtQty().setText("");
            view.getLblSubtotalPreview().setText("Rp 0");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error Tambah: " + e.getMessage());
        }
    }

    public void hapusItemKeranjang(KasirView view, Voucher currentVoucher) {
        DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();
        int row = view.getTabelKeranjang().getSelectedRow();
        if (row >= 0) {
            model.removeRow(row);
            hitungTotalBelanja(view, currentVoucher);
            
            // PENTING: Nyalakan lagi tombol pesan
            view.resetInput(); 
        } else {
            JOptionPane.showMessageDialog(view, "Pilih baris yang mau dihapus!");
        }
    }

  public void simpanTransaksi(KasirView view, Voucher voucher) {
    if (view.getTabelKeranjang().getRowCount() == 0) {
        JOptionPane.showMessageDialog(view, "Keranjang belanja kosong!");
        return;
    }

    String metode = view.getMetodePembayaran().trim();
    double totalBelanjaMurni = 0;
    DefaultTableModel model = (DefaultTableModel) view.getTabelKeranjang().getModel();

    for (int i = 0; i < model.getRowCount(); i++) {
        totalBelanjaMurni += Double.parseDouble(model.getValueAt(i, 6).toString());
    }
    
    double potongan = (voucher != null) ? voucher.getPotongan() : 0;
    double grandTotal = totalBelanjaMurni - potongan;
    if(grandTotal < 0) grandTotal = 0;

    String noTrx = view.getTxtNoTransaksi().getText();
    String namaPel = view.getTxtNamaPelanggan().getText();

    if (metode.equalsIgnoreCase("Cash")) {
        String infoDiskon = (voucher != null) ? "\n(Termasuk Potongan Voucher: " + formatRupiah(potongan) + ")" : "";
        
        String inputUang = JOptionPane.showInputDialog(view, 
                "Total Bayar: " + formatRupiah(grandTotal) + infoDiskon + "\nMasukkan Uang Pembayaran:", 
                "Pembayaran Cash", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (inputUang == null) return; 

        try {
            String uangBersih = inputUang.replaceAll("[^0-9]", "");
            double uangDibayar = Double.parseDouble(uangBersih);
            
            if (uangDibayar < grandTotal) {
                JOptionPane.showMessageDialog(view, "Uang Kurang! Transaksi dibatalkan.");
                return;
            }
            
            double kembalian = uangDibayar - grandTotal;
            JOptionPane.showMessageDialog(view, "Pembayaran Diterima.\nKembalian: " + formatRupiah(kembalian));
            
            int confirm = JOptionPane.showConfirmDialog(view, "Simpan Transaksi?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Input harus berupa angka!");
            return;
        }
    } 
    else {
        voucher = null; // Reset voucher jika kasbon
    }

    Integer idVoucher = (voucher != null) ? voucher.getId() : null;
    
    if (dao.simpanTransaksi(noTrx, namaPel, model, idVoucher)) {
        JOptionPane.showMessageDialog(view, "Transaksi Berhasil Disimpan!");
        view.resetFormKasir(); 
        refreshRiwayat(view.getModelRiwayat());
    } else {
        JOptionPane.showMessageDialog(view, "Gagal menyimpan ke Database!");
    }
  }
    
  public void refreshRiwayat(DefaultTableModel model) {
      riwayatDao.loadDataKeTabel(model);
  }
  
  // --- METHOD BARU: EXPORT PDF ---
  public void cetakLaporanPDF(KasirView view) {
        DefaultTableModel model = view.getModelRiwayat();
        
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Tidak ada data untuk dicetak!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        fileChooser.setSelectedFile(new java.io.File("Laporan_Transaksi.pdf")); 
        
        int userSelection = fileChooser.showSaveDialog(view);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }

            Document doc = new Document();
            try {
                PdfWriter.getInstance(doc, new FileOutputStream(path));
                doc.open();

                // JUDUL
                Paragraph title = new Paragraph("LAPORAN RIWAYAT TRANSAKSI");
                title.setAlignment(Paragraph.ALIGN_CENTER);
                title.setSpacingAfter(20); 
                doc.add(title);
                
                // INFO TANGGAL
                Paragraph subTitle = new Paragraph("Dicetak pada: " + new Date().toString());
                subTitle.setAlignment(Paragraph.ALIGN_CENTER);
                subTitle.setSpacingAfter(10);
                doc.add(subTitle);

                // TABEL PDF (9 KOLOM)
                PdfPTable pdfTable = new PdfPTable(9);
                pdfTable.setWidthPercentage(100);
                
                // Lebar kolom relatif
                float[] columnWidths = {3f, 3f, 3f, 2f, 3f, 1f, 2f, 2f, 2f};
                pdfTable.setWidths(columnWidths);

                // HEADER
                String[] headers = {
                    "No Trx", "Tanggal", "Pelanggan", "Metode", 
                    "Menu", "Qty", "Subtotal", "Diskon", "Total"
                };
                
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    pdfTable.addCell(cell);
                }

                // DATA
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object rawValue = model.getValueAt(i, j);
                        String cellData = (rawValue != null) ? rawValue.toString() : "";
                        
                        PdfPCell cell = new PdfPCell(new Phrase(cellData));
                        
                        // Rata Kanan untuk angka
                        if(j >= 5) cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT); 
                        else cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                        
                        pdfTable.addCell(cell);
                    }
                }

                doc.add(pdfTable);
                doc.close();
                
                JOptionPane.showMessageDialog(view, "Sukses! Laporan tersimpan di:\n" + path);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Gagal mencetak PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}