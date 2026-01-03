/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.view;

import com.kasir.controller.MenuDAO;
import com.kasir.controller.TransaksiController;
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
    private JTable tabelKeranjang;
    private DefaultTableModel modelKeranjang;
    private JButton btnMasukPesanan, btnUpdate, btnHapus, btnBayar;

    // Radio Button
    private JRadioButton rbCash, rbKasbon;
    private ButtonGroup bgMetode;

    // --- KOMPONEN TAB MENU (UPDATE CRUD) ---
    private JTextField txtNamaMenuBaru, txtHargaMenuBaru;
    private JLabel lblIdMenuHidden; // Untuk menyimpan ID saat edit
    private JButton btnSimpanMenu, btnUpdateMenu, btnHapusMenu, btnClearMenu;
    private JTable tabelMenu;
    private DefaultTableModel modelMenu;

    // --- KOMPONEN TAB RIWAYAT ---
    private JTable tabelRiwayat;
    private DefaultTableModel modelRiwayat;

    private MenuDAO menuDAO;
    private TransaksiController transController;

    public KasirView() {
        menuDAO = new MenuDAO();
        transController = new TransaksiController();
        initUI();
        loadDataMenu();      // Load ComboBox Kasir
        refreshTabelMenu();  // Load Tabel Menu
        lblNoTransaksi.setText(transController.generateNoTransaksi());
    }

    private void initUI() {
        setTitle("Aplikasi Kasir Warung Nasi - Full CRUD");
        setSize(1000, 700); 
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

        lblTotalHarga = new JLabel("Rp 0");
        lblTotalHarga.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalHarga.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel panelBawah = new JPanel(new BorderLayout());
        JPanel btnBawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnHapus = new JButton("Hapus");
        btnBayar = new JButton("Bayar");
        btnBawah.add(btnHapus); btnBawah.add(btnBayar);
        
        panelBawah.add(btnBawah, BorderLayout.WEST);
        panelBawah.add(lblTotalHarga, BorderLayout.CENTER);

        JPanel panelKasir = new JPanel(new BorderLayout());
        panelKasir.add(panelAtas, BorderLayout.NORTH);
        panelKasir.add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);
        panelKasir.add(panelBawah, BorderLayout.SOUTH);

        // ==========================================
        // TAB 2: KELOLA MENU (FULL CRUD)
        // ==========================================
        JPanel panelMenu = new JPanel(new BorderLayout(10, 10));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Menu
        JPanel formMenu = new JPanel(new GridLayout(4, 2, 5, 5));
        txtNamaMenuBaru = new JTextField();
        txtHargaMenuBaru = new JTextField();
        lblIdMenuHidden = new JLabel("-"); lblIdMenuHidden.setVisible(false); // ID Hidden

        btnSimpanMenu = new JButton("Simpan Baru");
        btnUpdateMenu = new JButton("Update Menu"); btnUpdateMenu.setEnabled(false);
        btnHapusMenu = new JButton("Hapus Menu"); btnHapusMenu.setEnabled(false);
        btnClearMenu = new JButton("Clear");

        formMenu.add(new JLabel("Nama Menu:")); formMenu.add(txtNamaMenuBaru);
        formMenu.add(new JLabel("Harga:")); formMenu.add(txtHargaMenuBaru);
        formMenu.add(btnSimpanMenu); formMenu.add(btnUpdateMenu);
        formMenu.add(btnHapusMenu); formMenu.add(btnClearMenu);
        
        // Tabel Menu
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
        // TAB 3: RIWAYAT
        // ==========================================
        JPanel panelRiwayat = new JPanel(new BorderLayout());
        String[] headerRiwayat = {"No Trx", "Tanggal", "Pelanggan", "Metode", "Menu", "Qty", "Total"};
        modelRiwayat = new DefaultTableModel(headerRiwayat, 0);
        tabelRiwayat = new JTable(modelRiwayat);

        JButton btnRefresh = new JButton("Refresh Riwayat");
        btnRefresh.addActionListener(e -> transController.refreshRiwayat(modelRiwayat));

        panelRiwayat.add(new JScrollPane(tabelRiwayat), BorderLayout.CENTER);
        panelRiwayat.add(btnRefresh, BorderLayout.SOUTH);

        // ADD TABS
        JTabbedPane tab = new JTabbedPane();
        tab.add("Kasir", panelKasir);
        tab.add("Kelola Menu", panelMenu);
        tab.add("Riwayat Transaksi", panelRiwayat);
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

        btnMasukPesanan.addActionListener(e -> transController.tambahKeKeranjang(this));

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
                    
                    transController.hitungTotalBelanja(this);
                    resetInput();
                    JOptionPane.showMessageDialog(this, "Data Berhasil Diupdate!");
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal Update: " + ex.getMessage());
                }
            }
        });

        btnHapus.addActionListener(e -> transController.hapusItemKeranjang(this));
        btnBayar.addActionListener(e -> transController.simpanTransaksi(this));

        // ==========================================
        // EVENTS - KELOLA MENU (CRUD)
        // ==========================================
        
        // 1. KLIK TABEL MENU -> ISI FORM
        tabelMenu.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tabelMenu.getSelectedRow();
                if (row != -1) {
                    lblIdMenuHidden.setText(modelMenu.getValueAt(row, 0).toString());
                    txtNamaMenuBaru.setText(modelMenu.getValueAt(row, 1).toString());
                    txtHargaMenuBaru.setText(modelMenu.getValueAt(row, 2).toString());
                    
                    btnSimpanMenu.setEnabled(false);
                    btnUpdateMenu.setEnabled(true);
                    btnHapusMenu.setEnabled(true);
                }
            }
        });

        // 2. SIMPAN MENU BARU
        btnSimpanMenu.addActionListener(e -> {
            try {
                String nama = txtNamaMenuBaru.getText();
                double harga = Double.parseDouble(txtHargaMenuBaru.getText());
                if (!nama.isEmpty()) {
                    menuDAO.tambahMenu(nama, harga);
                    loadAllData();
                    clearFormMenu();
                    JOptionPane.showMessageDialog(this, "Menu Ditambah!");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Harga harus angka!"); }
        });

        // 3. UPDATE MENU
        btnUpdateMenu.addActionListener(e -> {
            try {
                int id = Integer.parseInt(lblIdMenuHidden.getText());
                String nama = txtNamaMenuBaru.getText();
                double harga = Double.parseDouble(txtHargaMenuBaru.getText());
                
                menuDAO.updateMenu(id, nama, harga);
                loadAllData();
                clearFormMenu();
                JOptionPane.showMessageDialog(this, "Menu Diupdate!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gagal Update!"); }
        });

        // 4. HAPUS MENU
       // Di dalam initUI() KasirView.java

        // 4. HAPUS MENU (REVISI)
        btnHapusMenu.addActionListener(e -> {
            try {
                // Cek apakah ada ID yang dipilih (bukan tanda "-")
                if (lblIdMenuHidden.getText().equals("-")) {
                    JOptionPane.showMessageDialog(this, "Pilih menu dari tabel dulu!");
                    return;
                }

                int id = Integer.parseInt(lblIdMenuHidden.getText());
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus menu ini?");
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Panggil DAO dan tampung hasilnya (Sukses/Gagal)
                    boolean sukses = menuDAO.deleteMenu(id);
                    
                    if (sukses) {
                        loadAllData();
                        clearFormMenu();
                        JOptionPane.showMessageDialog(this, "Menu Berhasil Dihapus!");
                    } else {
                        // Muncul jika kena Foreign Key Constraint
                        JOptionPane.showMessageDialog(this, 
                            "GAGAL MENGHAPUS!\n\n" +
                            "Menu ini sudah pernah terjual dan tercatat di riwayat transaksi." +
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        // 5. CLEAR FORM
        btnClearMenu.addActionListener(e -> clearFormMenu());
    }

    // --- HELPER METHODS ---
    
    private void resetInput() {
        txtQty.setText("");
        lblSubtotalPreview.setText("Rp 0");
        btnUpdate.setEnabled(false);
        btnMasukPesanan.setEnabled(true);
        tabelKeranjang.clearSelection();
    }

    private void clearFormMenu() {
        txtNamaMenuBaru.setText("");
        txtHargaMenuBaru.setText("");
        lblIdMenuHidden.setText("-");
        btnSimpanMenu.setEnabled(true);
        btnUpdateMenu.setEnabled(false);
        btnHapusMenu.setEnabled(false);
        tabelMenu.clearSelection();
    }

    private void loadAllData() {
        loadDataMenu();      // Refresh ComboBox Kasir
        refreshTabelMenu();  // Refresh Tabel Menu
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