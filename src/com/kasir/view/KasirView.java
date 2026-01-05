/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.view;

import com.kasir.controller.MenuController;
import com.kasir.controller.TransaksiController;
import com.kasir.controller.VoucherController;
import com.kasir.model.Menu;
import com.kasir.model.Voucher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Ahmad
 */
public class KasirView extends JPanel {
    private JComboBox<Menu> cboMenu;
    private JTextField txtHarga, txtQty, txtPelanggan, txtInputVoucher;
    private JLabel lblNoTransaksi, lblTotalHarga, lblSubtotalPreview, lblInfoDiskon;
    private JTable tabelKeranjang;
    private DefaultTableModel modelKeranjang;
    private JButton btnMasuk, btnUpdate, btnHapus, btnBayar, btnCekVoucher;
    private JRadioButton rbCash, rbKasbon;
    
    private Voucher currentVoucher = null;
    
    private final TransaksiController controller = new TransaksiController();
    private final MenuController menuController = new MenuController();
    private final VoucherController voucherController = new VoucherController();

    public KasirView() {
        setLayout(new BorderLayout());
        initComponents();
        
        // Load data awal
        loadMenu();
        lblNoTransaksi.setText(controller.generateNoTransaksi());
        
        // --- FITUR BARU: AUTO REFRESH ---
        // Listener ini akan berjalan setiap kali Tab Kasir ditampilkan
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                // Reload Menu saat tab dibuka
                loadMenu();
                // Opsional: Generate ulang nomor transaksi jika perlu
                // lblNoTransaksi.setText(controller.generateNoTransaksi());
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                // Tidak perlu aksi saat tab ditutup
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                // Tidak perlu aksi saat window dipindah
            }
        });
    }

    private void initComponents() {
        JPanel f = new JPanel(new GridLayout(8, 2, 8, 8));
        f.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        lblNoTransaksi = new JLabel("TR-000");
        txtPelanggan = new JTextField();
        
        // ComboBox menyimpan Object Menu (bukan String)
        cboMenu = new JComboBox<>();
        
        txtHarga = new JTextField();
        txtHarga.setEditable(false);
        txtQty = new JTextField();
        
        lblSubtotalPreview = new JLabel("Rp 0");
        lblSubtotalPreview.setForeground(Color.BLUE);
        
        rbCash = new JRadioButton("Cash", true);
        rbKasbon = new JRadioButton("Kasbon");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbCash);
        bg.add(rbKasbon);
        
        JPanel pr = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pr.add(rbCash);
        pr.add(rbKasbon);
        
        btnMasuk = new JButton("Tambah");
        btnUpdate = new JButton("Update");
        btnUpdate.setEnabled(false);
        
        JPanel pb = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pb.add(btnUpdate);
        pb.add(btnMasuk);
        
        f.add(new JLabel("No Trx:")); f.add(lblNoTransaksi);
        f.add(new JLabel("Pelanggan:")); f.add(txtPelanggan);
        f.add(new JLabel("Metode:")); f.add(pr);
        f.add(new JLabel("Menu:")); f.add(cboMenu);
        f.add(new JLabel("Harga:")); f.add(txtHarga);
        f.add(new JLabel("Qty:")); f.add(txtQty);
        f.add(new JLabel("Subtotal:")); f.add(lblSubtotalPreview);
        f.add(new JLabel("Aksi:")); f.add(pb);
        
        modelKeranjang = new DefaultTableModel(new String[]{"Pel", "ID", "Mtd", "Menu", "Hrg", "Qty", "Sub"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelKeranjang = new JTable(modelKeranjang);
        
        // Sembunyikan Kolom ID (Index 1) agar tampilan bersih
        TableColumnModel tcm = tabelKeranjang.getColumnModel();
        tcm.getColumn(1).setMinWidth(0);
        tcm.getColumn(1).setMaxWidth(0);
        tcm.getColumn(1).setWidth(0);

        JPanel foot = new JPanel(new BorderLayout());
        JPanel vch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        txtInputVoucher = new JTextField(10);
        btnCekVoucher = new JButton("Cek Voucher");
        lblInfoDiskon = new JLabel("Disc: Rp 0");
        lblInfoDiskon.setForeground(new Color(0,100,0));
        
        vch.add(new JLabel("Kode:"));
        vch.add(txtInputVoucher);
        vch.add(btnCekVoucher);
        vch.add(lblInfoDiskon);
        
        lblTotalHarga = new JLabel("Total: Rp 0");
        lblTotalHarga.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalHarga.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel fl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnHapus = new JButton("Hapus Item");
        btnBayar = new JButton("Bayar");
        btnBayar.setFont(new Font("Arial", Font.BOLD, 14));
        
        fl.add(btnHapus);
        fl.add(btnBayar);
        
        JPanel fr = new JPanel(new GridLayout(2,1));
        fr.add(vch);
        fr.add(lblTotalHarga);
        
        foot.add(fl, BorderLayout.WEST);
        foot.add(fr, BorderLayout.EAST);
        foot.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(f, BorderLayout.NORTH);
        add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);
        add(foot, BorderLayout.SOUTH);
        
        setupEvents();
    }
    
    private void setupEvents() {
        // Event saat Menu dipilih di ComboBox
        cboMenu.addActionListener(e -> {
            Menu m = (Menu) cboMenu.getSelectedItem();
            if(m != null) {
                txtHarga.setText(String.valueOf((int)m.getHarga()));
                txtQty.requestFocus();
            }
        });
        
        // Event hitung otomatis saat ketik Qty
        txtQty.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                controller.hitungPreview(KasirView.this);
            }
        });
        
        // Tombol Tambah
        btnMasuk.addActionListener(e -> controller.tambahKeKeranjang(this, currentVoucher));
        
        // Klik Tabel untuk Edit
        tabelKeranjang.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tabelKeranjang.getSelectedRow();
                if(r != -1) {
                    btnUpdate.setEnabled(true);
                    btnMasuk.setEnabled(false);
                    txtPelanggan.setText(modelKeranjang.getValueAt(r,0).toString());
                    txtQty.setText(modelKeranjang.getValueAt(r,5).toString());
                    
                    // Sinkronisasi ComboBox dengan item yang dipilih di tabel
                    String idDiTabel = modelKeranjang.getValueAt(r, 1).toString();
                    for(int i=0; i<cboMenu.getItemCount(); i++) {
                        Menu m = cboMenu.getItemAt(i);
                        if(String.valueOf(m.getId()).equals(idDiTabel)) {
                            cboMenu.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });
        
        // Tombol Update
        btnUpdate.addActionListener(e -> {
            int r = tabelKeranjang.getSelectedRow();
            if (r != -1) {
                try {
                    Menu m = (Menu) cboMenu.getSelectedItem();
                    int q = Integer.parseInt(txtQty.getText());
                    
                    modelKeranjang.setValueAt(txtPelanggan.getText(), r, 0);
                    modelKeranjang.setValueAt(m.getId(), r, 1);
                    modelKeranjang.setValueAt(getMetodePembayaran(), r, 2);
                    modelKeranjang.setValueAt(m.getNama(), r, 3);
                    modelKeranjang.setValueAt((int)m.getHarga(), r, 4);
                    modelKeranjang.setValueAt(q, r, 5);
                    modelKeranjang.setValueAt((int)(m.getHarga() * q), r, 6);
                    
                    controller.hitungTotalBelanja(this, currentVoucher);
                    resetInput();
                    JOptionPane.showMessageDialog(this, "Item berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal update! Pastikan Qty benar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnHapus.addActionListener(e -> controller.hapusItemKeranjang(this, currentVoucher));
        btnBayar.addActionListener(e -> controller.simpanTransaksi(this, currentVoucher));
        
        // Logika Radio Button (Cash/Kasbon)
        ActionListener mtd = e -> {
            boolean cash = rbCash.isSelected();
            txtInputVoucher.setEnabled(cash);
            btnCekVoucher.setEnabled(cash);
            if(!cash) {
                currentVoucher = null;
                txtInputVoucher.setText("");
                lblInfoDiskon.setText("Tidak berlaku untuk kasbon!");
            } else {
                lblInfoDiskon.setText("Disc: Rp 0");
            }
            controller.hitungTotalBelanja(this, currentVoucher);
        };
        rbCash.addActionListener(mtd);
        rbKasbon.addActionListener(mtd);
        
        // Tombol Cek Voucher
        btnCekVoucher.addActionListener(e -> {
            if(rbKasbon.isSelected()) {
                JOptionPane.showMessageDialog(this, "Voucher hanya untuk metode Cash!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String input = txtInputVoucher.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan kode voucher!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Voucher v = voucherController.cekVoucherValid(input);
            if(v!=null) {
                currentVoucher = v;
                lblInfoDiskon.setText("Disc: "+controller.formatRupiah(v.getPotongan()));
                JOptionPane.showMessageDialog(this, "Voucher Valid!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                currentVoucher = null;
                lblInfoDiskon.setText("Invalid!");
                JOptionPane.showMessageDialog(this, "Kode Voucher Tidak Ditemukan/Tidak Aktif", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
            controller.hitungTotalBelanja(this, currentVoucher);
        });
    }

    // Method ini sekarang dipanggil otomatis oleh AncestorListener
    private void loadMenu() {
        cboMenu.removeAllItems();
        java.util.List<Menu> list = menuController.getAllMenu();
        for(Menu m : list) {
            cboMenu.addItem(m);
        }
    }
    
    public void resetInput() {
        txtQty.setText("");
        lblSubtotalPreview.setText("Rp 0");
        btnUpdate.setEnabled(false);
        btnMasuk.setEnabled(true);
        tabelKeranjang.clearSelection();
    }
    
    public void resetForm() {
        modelKeranjang.setRowCount(0);
        txtPelanggan.setText("");
        lblTotalHarga.setText("Rp 0");
        lblInfoDiskon.setText("Disc: Rp 0");
        txtInputVoucher.setText("");
        currentVoucher = null;
        lblNoTransaksi.setText(controller.generateNoTransaksi());
        resetInput();
    }
    
    public JTextField getTxtHarga() { return txtHarga; }
    public JTextField getTxtQty() { return txtQty; }
    public JTextField getTxtNamaPelanggan() { return txtPelanggan; }
    public JLabel getLblSubtotalPreview() { return lblSubtotalPreview; }
    public JLabel getLblTotalHarga() { return lblTotalHarga; }
    public JTable getTabelKeranjang() { return tabelKeranjang; }
    public JComboBox<Menu> getCbMenu() { return cboMenu; }
    public JLabel getTxtNoTransaksi() { return lblNoTransaksi; }
    public String getMetodePembayaran() { return rbKasbon.isSelected() ? "Kasbon" : "Cash"; }
}