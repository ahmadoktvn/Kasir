/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.view;

import com.kasir.controller.MenuDAO;
import com.kasir.controller.TransaksiController;
import com.kasir.controller.VoucherDAO;  // Import DAO
import com.kasir.model.Voucher;
import com.kasir.model.Menu;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ahmad
 */
public class KasirView extends JFrame {

    // --- KOMPONEN TAB KASIR ---
    private JComboBox<String> cboMenu;
    private JTextField txtHarga, txtQty, txtPelanggan;
    private JLabel lblNoTransaksi, lblTotalHarga, lblSubtotalPreview;
    
    // Komponen Voucher di Kasir
    private JTextField txtInputVoucher;
    private JButton btnCekVoucher;
    private JLabel lblInfoDiskon;
    private Voucher currentVoucher = null; 
    
    private JTable tabelKeranjang;
    private DefaultTableModel modelKeranjang;
    private JButton btnMasukPesanan, btnUpdate, btnHapus, btnBayar;

    private JRadioButton rbCash, rbKasbon;
    private ButtonGroup bgMetode;

    // --- KOMPONEN TAB MENU (CRUD) ---
    private JTextField txtNamaMenuBaru, txtHargaMenuBaru;
    private JLabel lblIdMenuHidden;
    private JButton btnSimpanMenu, btnUpdateMenu, btnHapusMenu, btnClearMenu;
    private JTable tabelMenu;
    private DefaultTableModel modelMenu;
    
    // --- KOMPONEN TAB VOUCHER (CRUD) ---
    private JTextField txtKodeVoucherCRUD, txtPotonganCRUD;
    private JComboBox<String> cbStatusVoucher;
    private JTable tabelVoucher;
    private DefaultTableModel modelVoucher;
    private JLabel lblIdVoucherHidden;
    private JButton btnSimpanVoucher, btnUpdateVoucher, btnHapusVoucher, btnClearVoucher;

    // --- KOMPONEN TAB RIWAYAT ---
    private JTable tabelRiwayat;
    private DefaultTableModel modelRiwayat;
    private JButton btnCetakPDF; // Tombol Baru untuk PDF

    // --- CONTROLLERS & DAO ---
    private MenuDAO menuDAO;
    private TransaksiController transController;
    private VoucherDAO voucherDAO;

    public KasirView() {
        menuDAO = new MenuDAO();
        transController = new TransaksiController();
        voucherDAO = new VoucherDAO();
        
        initUI();
        
        loadDataMenu();      
        refreshTabelMenu();  
        refreshTabelVoucher();
        lblNoTransaksi.setText(transController.generateNoTransaksi());
    }

    private void initUI() {
        setTitle("Aplikasi Kasir Warung Nasi - Full Features");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ==========================================
        // TAB 1: KASIR TRANSAKSI
        // ==========================================
        JPanel panelInput = new JPanel(new GridLayout(8, 2, 8, 8));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblNoTransaksi = new JLabel("TR-000");
        txtPelanggan = new JTextField();
        cboMenu = new JComboBox<>();
        txtHarga = new JTextField(); txtHarga.setEditable(false);
        txtQty = new JTextField();
        lblSubtotalPreview = new JLabel("Rp 0"); lblSubtotalPreview.setForeground(Color.BLUE);
        
        rbCash = new JRadioButton("Cash", true);
        rbKasbon = new JRadioButton("Kasbon");
        bgMetode = new ButtonGroup();
        bgMetode.add(rbCash); bgMetode.add(rbKasbon);
        JPanel panelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelRadio.add(rbCash); panelRadio.add(rbKasbon);

        btnMasukPesanan = new JButton("Pesan");
        btnUpdate = new JButton("Update Item"); btnUpdate.setEnabled(false);

        panelInput.add(new JLabel("No Transaksi:")); panelInput.add(lblNoTransaksi);
        panelInput.add(new JLabel("Pelanggan:")); panelInput.add(txtPelanggan);
        panelInput.add(new JLabel("Metode Bayar:")); panelInput.add(panelRadio); 
        panelInput.add(new JLabel("Menu:")); panelInput.add(cboMenu);
        panelInput.add(new JLabel("Harga:")); panelInput.add(txtHarga);
        panelInput.add(new JLabel("Qty:")); panelInput.add(txtQty);
        panelInput.add(new JLabel("Subtotal:")); panelInput.add(lblSubtotalPreview);
        panelInput.add(new JLabel("Aksi:")); 
        
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.add(btnUpdate); panelBtn.add(btnMasukPesanan);
        panelInput.add(panelBtn);
        
        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(panelInput, BorderLayout.CENTER);

        // Tabel Keranjang
        String[] header = {"Pelanggan", "ID", "Metode", "Menu", "Harga", "Qty", "Subtotal"};
        modelKeranjang = new DefaultTableModel(header, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelKeranjang = new JTable(modelKeranjang);
        
        // Hide ID Column
        tabelKeranjang.getColumnModel().getColumn(1).setMinWidth(0);
        tabelKeranjang.getColumnModel().getColumn(1).setMaxWidth(0);
        tabelKeranjang.getColumnModel().getColumn(1).setWidth(0);

        // --- BAGIAN BAWAH (TOTAL & VOUCHER) ---
        JPanel panelBawah = new JPanel(new BorderLayout());
        
        // Area Voucher Input
        JPanel panelVoucherArea = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtInputVoucher = new JTextField(10);
        btnCekVoucher = new JButton("Gunakan Kode");
        lblInfoDiskon = new JLabel("Diskon: Rp 0");
        lblInfoDiskon.setForeground(new Color(0, 150, 0)); 
        
        panelVoucherArea.add(new JLabel("Kode Voucher:"));
        panelVoucherArea.add(txtInputVoucher);
        panelVoucherArea.add(btnCekVoucher);
        panelVoucherArea.add(Box.createHorizontalStrut(10));
        panelVoucherArea.add(lblInfoDiskon);

        lblTotalHarga = new JLabel("Total: Rp 0");
        lblTotalHarga.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalHarga.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel panelKiriBawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnHapus = new JButton("Hapus Item");
        btnBayar = new JButton("BAYAR SEKARANG");
        btnBayar.setFont(new Font("Arial", Font.BOLD, 14));
        panelKiriBawah.add(btnHapus); panelKiriBawah.add(btnBayar);
        
        JPanel panelFooterKanan = new JPanel(new GridLayout(2, 1));
        panelFooterKanan.add(panelVoucherArea);
        panelFooterKanan.add(lblTotalHarga);
        
        panelBawah.add(panelKiriBawah, BorderLayout.WEST);
        panelBawah.add(panelFooterKanan, BorderLayout.EAST);
        panelBawah.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelKasir = new JPanel(new BorderLayout());
        panelKasir.add(panelAtas, BorderLayout.NORTH);
        panelKasir.add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);
        panelKasir.add(panelBawah, BorderLayout.SOUTH);

        // ==========================================
        // TAB 2: KELOLA MENU
        // ==========================================
        JPanel panelMenu = new JPanel(new BorderLayout(10, 10));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formMenu = new JPanel(new GridLayout(4, 2, 5, 5));
        txtNamaMenuBaru = new JTextField();
        txtHargaMenuBaru = new JTextField();
        lblIdMenuHidden = new JLabel("-"); lblIdMenuHidden.setVisible(false);

        btnSimpanMenu = new JButton("Simpan Baru");
        btnUpdateMenu = new JButton("Update Menu"); btnUpdateMenu.setEnabled(false);
        btnHapusMenu = new JButton("Hapus Menu"); btnHapusMenu.setEnabled(false);
        btnClearMenu = new JButton("Clear");

        formMenu.add(new JLabel("Nama Menu:")); formMenu.add(txtNamaMenuBaru);
        formMenu.add(new JLabel("Harga:")); formMenu.add(txtHargaMenuBaru);
        formMenu.add(btnSimpanMenu); formMenu.add(btnUpdateMenu);
        formMenu.add(btnHapusMenu); formMenu.add(btnClearMenu);
        
        String[] headerMenu = {"ID", "Nama Menu", "Harga"};
        modelMenu = new DefaultTableModel(headerMenu, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelMenu = new JTable(modelMenu);

        JPanel panelAtasMenu = new JPanel(new BorderLayout());
        panelAtasMenu.add(formMenu, BorderLayout.NORTH);
        panelMenu.add(panelAtasMenu, BorderLayout.NORTH);
        panelMenu.add(new JScrollPane(tabelMenu), BorderLayout.CENTER);

        // ==========================================
        // TAB 3: RIWAYAT (UPDATE DENGAN TOMBOL PDF)
        // ==========================================
        JPanel panelRiwayat = new JPanel(new BorderLayout());
        
        // Header Tabel (9 Kolom)
        String[] headerRiwayat = {
            "No Trx", "Tanggal", "Pelanggan", "Metode", 
            "Menu", "Qty", "Subtotal", "Diskon", "Total"
        };
        
        modelRiwayat = new DefaultTableModel(headerRiwayat, 0);
        tabelRiwayat = new JTable(modelRiwayat);

        // --- PANEL TOMBOL DI BAWAH ---
        JPanel panelBawahRiwayat = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnRefresh = new JButton("Refresh Riwayat");
        btnCetakPDF = new JButton("Cetak Laporan PDF"); // Tombol Baru
        
        panelBawahRiwayat.add(btnRefresh);
        panelBawahRiwayat.add(btnCetakPDF); // Masukkan tombol ke panel

        panelRiwayat.add(new JScrollPane(tabelRiwayat), BorderLayout.CENTER);
        panelRiwayat.add(panelBawahRiwayat, BorderLayout.SOUTH);

        // ==========================================
        // TAB 4: KELOLA VOUCHER (UPDATE CRUD)
        // ==========================================
        JPanel panelVoucher = new JPanel(new BorderLayout(10, 10));
        panelVoucher.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formVoucher = new JPanel(new GridLayout(5, 2, 5, 5));
        
        txtKodeVoucherCRUD = new JTextField();
        txtPotonganCRUD = new JTextField();
        String[] statusOpsi = {"Aktif", "Tidak Aktif"};
        cbStatusVoucher = new JComboBox<>(statusOpsi);
        lblIdVoucherHidden = new JLabel("-"); lblIdVoucherHidden.setVisible(false);
        
        btnSimpanVoucher = new JButton("Simpan Voucher");
        btnUpdateVoucher = new JButton("Update Voucher"); btnUpdateVoucher.setEnabled(false);
        btnHapusVoucher = new JButton("Hapus Voucher"); btnHapusVoucher.setEnabled(false);
        btnClearVoucher = new JButton("Clear / Batal");

        formVoucher.add(new JLabel("Kode Voucher:")); formVoucher.add(txtKodeVoucherCRUD);
        formVoucher.add(new JLabel("Nominal (Rp):")); formVoucher.add(txtPotonganCRUD);
        formVoucher.add(new JLabel("Status:")); formVoucher.add(cbStatusVoucher);
        formVoucher.add(btnSimpanVoucher); formVoucher.add(btnUpdateVoucher);
        formVoucher.add(btnHapusVoucher); formVoucher.add(btnClearVoucher);
        panelVoucher.add(lblIdVoucherHidden, BorderLayout.SOUTH);

        String[] headerVoucher = {"ID", "Kode", "Potongan", "Status"};
        modelVoucher = new DefaultTableModel(headerVoucher, 0) {
             @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelVoucher = new JTable(modelVoucher);

        JPanel panelAtasVoucher = new JPanel(new BorderLayout());
        panelAtasVoucher.add(formVoucher, BorderLayout.NORTH);
        panelVoucher.add(panelAtasVoucher, BorderLayout.NORTH);
        panelVoucher.add(new JScrollPane(tabelVoucher), BorderLayout.CENTER);

        // ADD TABS
        JTabbedPane tab = new JTabbedPane();
        tab.add("Kasir", panelKasir);
        tab.add("Kelola Menu", panelMenu);
        tab.add("Riwayat Transaksi", panelRiwayat);
        tab.add("Kelola Voucher", panelVoucher);
        add(tab);

        // ==========================================
        // EVENTS - KASIR
        // ==========================================
        cboMenu.addActionListener(e -> {
            try {
                if(cboMenu.getSelectedItem() != null) {
                    String[] s = cboMenu.getSelectedItem().toString().split(":");
                    txtHarga.setText(s[2]); 
                    txtQty.requestFocus();
                }
            } catch(Exception ex){}
        });

        txtQty.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                transController.hitungPreview(KasirView.this);
            }
        });

        btnMasukPesanan.addActionListener(e -> transController.tambahKeKeranjang(this, currentVoucher));

        tabelKeranjang.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tabelKeranjang.getSelectedRow();
                if (row != -1) {
                    txtPelanggan.setText(modelKeranjang.getValueAt(row, 0).toString());
                    String mtd = modelKeranjang.getValueAt(row, 2).toString();
                    if(mtd.equals("Kasbon")) rbKasbon.setSelected(true); else rbCash.setSelected(true);
                    txtQty.setText(modelKeranjang.getValueAt(row, 5).toString());
                    
                    String idDiTabel = modelKeranjang.getValueAt(row, 1).toString();
                    for (int i = 0; i < cboMenu.getItemCount(); i++) {
                        if (cboMenu.getItemAt(i).startsWith(idDiTabel + ":")) {
                            cboMenu.setSelectedIndex(i); break;
                        }
                    }
                    btnUpdate.setEnabled(true); btnMasukPesanan.setEnabled(false);
                }
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = tabelKeranjang.getSelectedRow();
            if (row != -1) {
                try {
                    String rawCombo = cboMenu.getSelectedItem().toString();
                    String[] split = rawCombo.split(":");
                    
                    int idMenuBaru = Integer.parseInt(split[0]);
                    String namaMenuBaru = split[1];
                    double hargaBaru = Double.parseDouble(split[2]);
                    int qtyBaru = Integer.parseInt(txtQty.getText());
                    double subtotalBaru = hargaBaru * qtyBaru;
                    
                    modelKeranjang.setValueAt(txtPelanggan.getText(), row, 0);
                    modelKeranjang.setValueAt(idMenuBaru, row, 1);
                    modelKeranjang.setValueAt(getMetodePembayaran(), row, 2);
                    modelKeranjang.setValueAt(namaMenuBaru, row, 3);
                    modelKeranjang.setValueAt((int)hargaBaru, row, 4);
                    modelKeranjang.setValueAt(qtyBaru, row, 5);
                    modelKeranjang.setValueAt((int)subtotalBaru, row, 6);
                    
                    transController.hitungTotalBelanja(this, currentVoucher);
                    resetInput();
                    JOptionPane.showMessageDialog(this, "Data Berhasil Diupdate!");
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal Update: " + ex.getMessage());
                }
            }
        });

        btnHapus.addActionListener(e -> {
            transController.hapusItemKeranjang(this, currentVoucher);
        });
        
        ActionListener metodeListener = e -> {
            if (rbKasbon.isSelected()) {
                currentVoucher = null;
                txtInputVoucher.setText("");
                txtInputVoucher.setEnabled(false);
                btnCekVoucher.setEnabled(false);
                lblInfoDiskon.setText("Diskon: Tidak tersedia untuk Kasbon");
            } else {
                txtInputVoucher.setEnabled(true);
                btnCekVoucher.setEnabled(true);
                lblInfoDiskon.setText("Diskon: Rp 0");
            }
            transController.hitungTotalBelanja(this, currentVoucher);
        };
        rbCash.addActionListener(metodeListener);
        rbKasbon.addActionListener(metodeListener);

        btnCekVoucher.addActionListener(e -> {
            if (rbKasbon.isSelected()) {
                JOptionPane.showMessageDialog(this, "Voucher tidak bisa dipakai untuk Kasbon!");
                return;
            }
            String inputKode = txtInputVoucher.getText().trim();
            if(inputKode.isEmpty()) {
                currentVoucher = null; 
                lblInfoDiskon.setText("Diskon: Rp 0");
            } else {
                Voucher v = voucherDAO.cekVoucherValid(inputKode);
                if (v != null) {
                    currentVoucher = v; 
                    lblInfoDiskon.setText("Diskon: " + transController.formatRupiah(v.getPotongan()) + " (" + v.getKode() + ")");
                    JOptionPane.showMessageDialog(this, "Voucher Diterapkan!");
                } else {
                    currentVoucher = null;
                    lblInfoDiskon.setText("Voucher Tidak Valid!");
                    JOptionPane.showMessageDialog(this, "Kode Voucher Salah / Tidak Aktif!");
                }
            }
            transController.hitungTotalBelanja(this, currentVoucher);
        });

        btnBayar.addActionListener(e -> transController.simpanTransaksi(this, currentVoucher));

        // ==========================================
        // EVENTS - KELOLA MENU
        // ==========================================
        tabelMenu.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tabelMenu.getSelectedRow();
                if (row != -1) {
                    lblIdMenuHidden.setText(modelMenu.getValueAt(row, 0).toString());
                    txtNamaMenuBaru.setText(modelMenu.getValueAt(row, 1).toString());
                    txtHargaMenuBaru.setText(modelMenu.getValueAt(row, 2).toString());
                    btnSimpanMenu.setEnabled(false);
                    btnUpdateMenu.setEnabled(true); btnHapusMenu.setEnabled(true);
                }
            }
        });

        btnSimpanMenu.addActionListener(e -> {
            try {
                String nama = txtNamaMenuBaru.getText();
                double harga = Double.parseDouble(txtHargaMenuBaru.getText());
                if (!nama.isEmpty()) {
                    menuDAO.tambahMenu(nama, harga);
                    loadAllData(); clearFormMenu();
                    JOptionPane.showMessageDialog(this, "Menu Ditambah!");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Harga harus angka!"); }
        });

        btnUpdateMenu.addActionListener(e -> {
            try {
                int id = Integer.parseInt(lblIdMenuHidden.getText());
                String nama = txtNamaMenuBaru.getText();
                double harga = Double.parseDouble(txtHargaMenuBaru.getText());
                menuDAO.updateMenu(id, nama, harga);
                loadAllData(); clearFormMenu();
                JOptionPane.showMessageDialog(this, "Menu Diupdate!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gagal Update!"); }
        });

        btnHapusMenu.addActionListener(e -> {
            try {
                if (lblIdMenuHidden.getText().equals("-")) return;
                int id = Integer.parseInt(lblIdMenuHidden.getText());
                if (JOptionPane.showConfirmDialog(this, "Hapus menu ini?") == JOptionPane.YES_OPTION) {
                    if (menuDAO.deleteMenu(id)) {
                        loadAllData(); clearFormMenu();
                        JOptionPane.showMessageDialog(this, "Menu Dihapus!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal! Menu sudah ada di riwayat transaksi.");
                    }
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        btnClearMenu.addActionListener(e -> clearFormMenu());
        
        // ==========================================
        // EVENTS - KELOLA VOUCHER (FULL CRUD)
        // ==========================================
        tabelVoucher.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tabelVoucher.getSelectedRow();
                if(row != -1) {
                    lblIdVoucherHidden.setText(modelVoucher.getValueAt(row, 0).toString());
                    txtKodeVoucherCRUD.setText(modelVoucher.getValueAt(row, 1).toString());
                    txtPotonganCRUD.setText(modelVoucher.getValueAt(row, 2).toString());
                    cbStatusVoucher.setSelectedItem(modelVoucher.getValueAt(row, 3).toString());
                    
                    btnSimpanVoucher.setEnabled(false);
                    btnUpdateVoucher.setEnabled(true);
                    btnHapusVoucher.setEnabled(true);
                }
            }
        });

        btnSimpanVoucher.addActionListener(e -> {
            try {
                String kode = txtKodeVoucherCRUD.getText().trim().toUpperCase();
                String potStr = txtPotonganCRUD.getText().trim();
                if(kode.isEmpty() || potStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Isi data dengan lengkap!"); return;
                }
                double pot = Double.parseDouble(potStr);
                
                if(voucherDAO.tambahVoucher(kode, pot)) {
                    JOptionPane.showMessageDialog(this, "Voucher Disimpan!");
                    refreshTabelVoucher(); clearFormVoucher();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal! Kode Voucher mungkin sudah ada.");
                }
            } catch(NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Potongan harus angka!"); }
        });

        btnUpdateVoucher.addActionListener(e -> {
            try {
                int id = Integer.parseInt(lblIdVoucherHidden.getText());
                String kode = txtKodeVoucherCRUD.getText().trim().toUpperCase();
                double pot = Double.parseDouble(txtPotonganCRUD.getText().trim());
                String status = cbStatusVoucher.getSelectedItem().toString();
                
                if(voucherDAO.updateVoucher(id, kode, pot, status)) {
                    JOptionPane.showMessageDialog(this, "Voucher Berhasil Diupdate!");
                    refreshTabelVoucher(); clearFormVoucher();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal Update!");
                }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });
        
        btnHapusVoucher.addActionListener(e -> {
            if(lblIdVoucherHidden.getText().equals("-")) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus voucher ini?");
            if(confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(lblIdVoucherHidden.getText());
                if(voucherDAO.hapusVoucher(id)) {
                    JOptionPane.showMessageDialog(this, "Voucher Dihapus!");
                    refreshTabelVoucher(); clearFormVoucher();
                }
            }
        });
        
        btnClearVoucher.addActionListener(e -> clearFormVoucher());
        
        // ==========================================
        // EVENTS - RIWAYAT & PDF
        // ==========================================
        btnRefresh.addActionListener(e -> transController.refreshRiwayat(modelRiwayat));
        
        // Event Listener untuk Tombol PDF
        btnCetakPDF.addActionListener(e -> transController.cetakLaporanPDF(this));
    }

    // --- HELPER METHODS ---
    
    public void resetInput() { // PUBLIC agar bisa dipanggil controller
        txtQty.setText("");
        lblSubtotalPreview.setText("Rp 0");
        btnUpdate.setEnabled(false);
        btnMasukPesanan.setEnabled(true);
        tabelKeranjang.clearSelection();
    }

    public void clearFormMenu() { 
        txtNamaMenuBaru.setText("");
        txtHargaMenuBaru.setText("");
        lblIdMenuHidden.setText("-");
        btnSimpanMenu.setEnabled(true);
        btnUpdateMenu.setEnabled(false);
        btnHapusMenu.setEnabled(false);
        tabelMenu.clearSelection();
    }
    
    private void clearFormVoucher() {
        txtKodeVoucherCRUD.setText("");
        txtPotonganCRUD.setText("");
        cbStatusVoucher.setSelectedIndex(0);
        lblIdVoucherHidden.setText("-");
        btnSimpanVoucher.setEnabled(true);
        btnUpdateVoucher.setEnabled(false);
        btnHapusVoucher.setEnabled(false);
        tabelVoucher.clearSelection();
    }
    
    public void resetFormKasir() {
        modelKeranjang.setRowCount(0);
        txtPelanggan.setText("");
        lblTotalHarga.setText("Rp 0");
        lblInfoDiskon.setText("Diskon: Rp 0");
        txtInputVoucher.setText("");
        currentVoucher = null; 
        lblNoTransaksi.setText(transController.generateNoTransaksi());
        
        // PENTING: Reset tombol Pesan agar nyala lagi
        resetInput();
    }

    private void loadAllData() {
        loadDataMenu();      
        refreshTabelMenu();  
    }

    private void loadDataMenu() {
        cboMenu.removeAllItems();
        List<Menu> list = menuDAO.getAllMenu();
        for(Menu m : list) {
            cboMenu.addItem(m.getId() + ":" + m.getNama() + ":" + (int)m.getHarga());
        }
    }
    
    private void refreshTabelMenu() {
        modelMenu.setRowCount(0);
        List<Menu> list = menuDAO.getAllMenu();
        for(Menu m : list) {
            modelMenu.addRow(new Object[]{m.getId(), m.getNama(), (int)m.getHarga()});
        }
    }
    
    private void refreshTabelVoucher() {
        modelVoucher.setRowCount(0);
        List<Voucher> list = voucherDAO.getAllVoucher();
        for(Voucher v : list) {
            modelVoucher.addRow(new Object[]{v.getId(), v.getKode(), (int)v.getPotongan(), v.getStatus()});
        }
    }

    // Getters
    public JTable getTabelKeranjang() { return tabelKeranjang; }
    public JLabel getLblTotalHarga() { return lblTotalHarga; }
    public JTextField getTxtHarga() { return txtHarga; }
    public JTextField getTxtQty() { return txtQty; }
    public JTextField getTxtNamaPelanggan() { return txtPelanggan; }
    public JLabel getLblSubtotalPreview() { return lblSubtotalPreview; }
    public JComboBox<String> getCbIdMasakan() { return cboMenu; }
    public JLabel getTxtNoTransaksi() { return lblNoTransaksi; }
    public DefaultTableModel getModelRiwayat() { return modelRiwayat; }

    public String getMetodePembayaran() {
        if (rbKasbon.isSelected()) return "Kasbon";
        return "Cash";
    }
}