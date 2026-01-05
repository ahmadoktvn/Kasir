/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.view;

import com.kasir.controller.MenuController;
import com.kasir.model.Menu;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
/**
 *
 * @author hary
 */
public class MenuView extends JPanel {
    private JTextField txtNama, txtHarga;
    private JLabel lblId;
    private JButton btnSimpan, btnUpdate, btnHapus, btnClear;
    private JTable tabel;
    private DefaultTableModel model;
    private final MenuController controller = new MenuController();

    public MenuView() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        // --- Form Input ---
        JPanel f = new JPanel(new GridLayout(4,2,5,5));
        txtNama = new JTextField();
        txtHarga = new JTextField();
        lblId = new JLabel("-"); // ID Tersembunyi untuk Logic
        lblId.setVisible(false);
        
        btnSimpan = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        
        // Kondisi Awal Tombol
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        
        f.add(new JLabel("Nama Menu:")); f.add(txtNama);
        f.add(new JLabel("Harga (Rp):")); f.add(txtHarga);
        f.add(btnSimpan); f.add(btnUpdate);
        f.add(btnHapus); f.add(btnClear);
        
        // --- Tabel dengan Penomoran ---
        // Kolom: No (Tampil), ID (Sembunyi), Nama, Harga
        model = new DefaultTableModel(new String[]{"No", "ID", "Nama Menu", "Harga"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        
        // Sembunyikan Kolom ID (Index 1)
        TableColumnModel tcm = tabel.getColumnModel();
        tcm.getColumn(1).setMinWidth(0);
        tcm.getColumn(1).setMaxWidth(0);
        tcm.getColumn(1).setWidth(0);
        
        // Kecilkan Kolom No
        tcm.getColumn(0).setMaxWidth(50);
        
        JPanel t = new JPanel(new BorderLayout());
        t.add(f, BorderLayout.NORTH);
        t.add(lblId, BorderLayout.SOUTH);
        
        add(t, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        
        // --- Event Listeners ---
        
        // Klik Tabel -> Isi Form
        tabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tabel.getSelectedRow();
                if(r != -1) {
                    // Ambil data dari tabel (ID ada di index 1)
                    lblId.setText(model.getValueAt(r, 1).toString());
                    txtNama.setText(model.getValueAt(r, 2).toString());
                    // Harga mungkin ada format Rp, kita ambil angkanya saja atau dari model raw jika perlu
                    // Di sini kita ambil string langsung
                    String hargaStr = model.getValueAt(r, 3).toString().replace("Rp ", "").replace(",", "");
                    txtHarga.setText(hargaStr);
                    
                    btnSimpan.setEnabled(false);
                    btnUpdate.setEnabled(true);
                    btnHapus.setEnabled(true);
                }
            }
        });
        
        // Simpan
        btnSimpan.addActionListener(e -> {
            if(!validasiInput()) return;
            try {
                if(controller.tambahMenu(txtNama.getText(), Double.parseDouble(txtHarga.getText()))) {
                    JOptionPane.showMessageDialog(this, "Berhasil! Menu baru telah disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    load(); clear();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan! Kemungkinan nama menu sudah ada.", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Update
        btnUpdate.addActionListener(e -> {
            if(!validasiInput()) return;
            try {
                controller.updateMenu(Integer.parseInt(lblId.getText()), txtNama.getText(), Double.parseDouble(txtHarga.getText()));
                JOptionPane.showMessageDialog(this, "Berhasil! Data menu telah diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                load(); clear();
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat update!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Hapus
        btnHapus.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus menu ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                controller.deleteMenu(Integer.parseInt(lblId.getText()));
                JOptionPane.showMessageDialog(this, "Berhasil! Menu telah dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                load(); clear();
            }
        });
        
        btnClear.addActionListener(e -> clear());
        
        load();
    }
    
    private boolean validasiInput() {
        if(txtNama.getText().trim().isEmpty() || txtHarga.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Menu dan Harga tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    void load() {
        model.setRowCount(0);
        int no = 1; // Counter untuk nomor urut
        java.util.List<Menu> list = controller.getAllMenu();
        for(Menu m : list) {
            model.addRow(new Object[]{
                no++,           // No Urut
                m.getId(),      // ID (Hidden)
                m.getNama(), 
                (int)m.getHarga()
            });
        }
    }
    
    void clear() {
        txtNama.setText("");
        txtHarga.setText("");
        lblId.setText("-");
        btnSimpan.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        tabel.clearSelection();
    }
}