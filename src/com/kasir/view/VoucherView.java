/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.view;

import com.kasir.controller.VoucherController;
import com.kasir.model.Voucher;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author hary
 */
public class VoucherView extends JPanel {
    private JTextField txtKode, txtPot;
    private JComboBox<String> cbStat;
    private JLabel lblId;
    private JButton btnSimpan, btnUpdate, btnHapus, btnClear;
    private JTable tabel;
    private DefaultTableModel model;
    private final VoucherController controller = new VoucherController();

    public VoucherView() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        JPanel f = new JPanel(new GridLayout(5,2,5,5));
        txtKode = new JTextField();
        txtPot = new JTextField();
        cbStat = new JComboBox<>(new String[]{"Aktif","Tidak Aktif"});
        lblId = new JLabel("-"); lblId.setVisible(false);
        
        btnSimpan = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        btnUpdate.setEnabled(false); btnHapus.setEnabled(false);
        
        f.add(new JLabel("Kode Voucher:")); f.add(txtKode);
        f.add(new JLabel("Potongan (Rp):")); f.add(txtPot);
        f.add(new JLabel("Status:")); f.add(cbStat);
        f.add(btnSimpan); f.add(btnUpdate);
        f.add(btnHapus); f.add(btnClear);
        
        // Kolom: No, ID, Kode, Potongan, Status
        model = new DefaultTableModel(new String[]{"No", "ID", "Kode", "Potongan", "Status"}, 0) {
            public boolean isCellEditable(int r,int c) { return false; }
        };
        tabel = new JTable(model);
        
        // Sembunyikan ID
        TableColumnModel tcm = tabel.getColumnModel();
        tcm.getColumn(1).setMinWidth(0); tcm.getColumn(1).setMaxWidth(0); tcm.getColumn(1).setWidth(0);
        tcm.getColumn(0).setMaxWidth(50); // Lebar No
        
        JPanel t = new JPanel(new BorderLayout());
        t.add(f, BorderLayout.NORTH);
        t.add(lblId, BorderLayout.SOUTH);
        
        add(t, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        
        // Listeners
        tabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tabel.getSelectedRow();
                if(r != -1) {
                    lblId.setText(model.getValueAt(r,1).toString()); // ID index 1
                    txtKode.setText(model.getValueAt(r,2).toString());
                    txtPot.setText(model.getValueAt(r,3).toString());
                    cbStat.setSelectedItem(model.getValueAt(r,4).toString());
                    btnSimpan.setEnabled(false); btnUpdate.setEnabled(true); btnHapus.setEnabled(true);
                }
            }
        });
        
        btnSimpan.addActionListener(e -> {
            if(!validasi()) return;
            try {
                if(controller.tambahVoucher(txtKode.getText(), Double.parseDouble(txtPot.getText()))) {
                    JOptionPane.showMessageDialog(this, "Berhasil! Voucher baru disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    load(); clear();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal! Kode Voucher mungkin duplikat.", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch(NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Potongan harus angka!"); }
        });
        
        btnUpdate.addActionListener(e -> {
            if(!validasi()) return;
            controller.updateVoucher(Integer.parseInt(lblId.getText()), txtKode.getText(), Double.parseDouble(txtPot.getText()), cbStat.getSelectedItem().toString());
            JOptionPane.showMessageDialog(this, "Berhasil! Voucher diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            load(); clear();
        });
        
        btnHapus.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this,"Yakin ingin menghapus voucher ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                controller.hapusVoucher(Integer.parseInt(lblId.getText()));
                JOptionPane.showMessageDialog(this, "Berhasil! Voucher dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                load(); clear();
            }
        });
        
        btnClear.addActionListener(e -> clear());
        load();
    }
    
    private boolean validasi() {
        if(txtKode.getText().trim().isEmpty() || txtPot.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Potongan harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    void load() {
        model.setRowCount(0);
        int no = 1;
        for(Voucher v : controller.getAllVoucher()) {
            model.addRow(new Object[]{
                no++, 
                v.getId(), 
                v.getKode(), 
                (int)v.getPotongan(), 
                v.getStatus()
            });
        }
    }
    
    void clear() {
        txtKode.setText(""); txtPot.setText(""); lblId.setText("-");
        cbStat.setSelectedIndex(0);
        btnSimpan.setEnabled(true); btnUpdate.setEnabled(false); btnHapus.setEnabled(false);
        tabel.clearSelection();
    }
}