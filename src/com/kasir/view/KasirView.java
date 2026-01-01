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

    private JComboBox<String> cboMenu;
    private JTextField txtHarga, txtQty, txtPelanggan;
    private JLabel lblNoTransaksi, lblTotalHarga, lblSubtotalPreview;
    private JTable tabelKeranjang;
    private DefaultTableModel modelKeranjang;
    private JButton btnMasukPesanan, btnUpdate, btnHapus, btnBayar;

    // Radio Button
    private JRadioButton rbCash, rbKasbon;
    private ButtonGroup bgMetode;

    private JTable tabelRiwayat;
    private DefaultTableModel modelRiwayat;
    
    // Tab Menu
    private JTextField txtNamaMenuBaru, txtHargaMenuBaru;
    private JButton btnSimpanMenu;

    private MenuDAO menuDAO;
    private TransaksiController transController;

    public KasirView() {
        menuDAO = new MenuDAO();
        transController = new TransaksiController();
        initUI();
        loadDataMenu();
        lblNoTransaksi.setText(transController.generateNoTransaksi());
    }

    private void initUI() {
        setTitle("Aplikasi Kasir Berelasi");
        setSize(1000, 700); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- PANEL INPUT ---
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

        // --- TABEL (ID Hidden) ---
        String[] header = {"Pelanggan", "ID", "Metode", "Menu", "Harga", "Qty", "Subtotal"};
        modelKeranjang = new DefaultTableModel(header, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelKeranjang = new JTable(modelKeranjang);
        
        // Sembunyikan Kolom ID (Index 1)
        tabelKeranjang.getColumnModel().getColumn(1).setMinWidth(0);
        tabelKeranjang.getColumnModel().getColumn(1).setMaxWidth(0);
        tabelKeranjang.getColumnModel().getColumn(1).setWidth(0);

        // --- BAWAH ---
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

        // --- TAB MENU ---
        JPanel panelMenu = new JPanel(new FlowLayout());
        txtNamaMenuBaru = new JTextField(15);
        txtHargaMenuBaru = new JTextField(10);
        btnSimpanMenu = new JButton("Simpan Menu");
        panelMenu.add(new JLabel("Menu:")); panelMenu.add(txtNamaMenuBaru);
        panelMenu.add(new JLabel("Harga:")); panelMenu.add(txtHargaMenuBaru);
        panelMenu.add(btnSimpanMenu);

        JTabbedPane tab = new JTabbedPane();
        tab.add("Kasir", panelKasir);
        tab.add("Menu", panelMenu);
        
        // --- TAB RIWAYAT ---
        JPanel panelRiwayat = new JPanel(new BorderLayout());
        String[] headerRiwayat = {"No Trx", "Tanggal", "Pelanggan", "Metode", "Menu", "Qty", "Total"};
        modelRiwayat = new DefaultTableModel(headerRiwayat, 0);
        tabelRiwayat = new JTable(modelRiwayat);

        JButton btnRefresh = new JButton("Refresh Riwayat");
        btnRefresh.addActionListener(e -> transController.refreshRiwayat(modelRiwayat));

        panelRiwayat.add(new JScrollPane(tabelRiwayat), BorderLayout.CENTER);
        panelRiwayat.add(btnRefresh, BorderLayout.SOUTH);

        tab.add("Riwayat Transaksi", panelRiwayat);
        add(tab);

        // ================= EVENTS =================

        // 1. Pilih Combo Menu
        cboMenu.addActionListener(e -> {
            try {
                if(cboMenu.getSelectedItem() != null) {
                    String[] s = cboMenu.getSelectedItem().toString().split(":");
                    txtHarga.setText(s[2]); // Set Harga otomatis
                    txtQty.requestFocus();
                }
            } catch(Exception ex){}
        });

        // 2. Hitung Preview saat ketik Qty
        txtQty.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                transController.hitungPreview(KasirView.this);
            }
        });

        // 3. Tombol Pesan
        btnMasukPesanan.addActionListener(e -> transController.tambahKeKeranjang(this));

        // 4. KLIK TABEL (UPDATE LOGIC 1)
        tabelKeranjang.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tabelKeranjang.getSelectedRow();
                if (row != -1) {
                    // Ambil Pelanggan
                    txtPelanggan.setText(modelKeranjang.getValueAt(row, 0).toString());
                    
                    // Ambil Metode Bayar & Set Radio Button
                    String mtd = modelKeranjang.getValueAt(row, 2).toString();
                    if(mtd.equals("Kasbon")) rbKasbon.setSelected(true);
                    else rbCash.setSelected(true);

                    // Ambil Qty
                    txtQty.setText(modelKeranjang.getValueAt(row, 5).toString());
                    
                    // --- LOGIKA PENTING: Set ComboBox sesuai ID Menu di Tabel ---
                    String idDiTabel = modelKeranjang.getValueAt(row, 1).toString();
                    
                    for (int i = 0; i < cboMenu.getItemCount(); i++) {
                        String item = cboMenu.getItemAt(i);
                        // Cek apakah item dimulai dengan ID yang sama?
                        if (item.startsWith(idDiTabel + ":")) {
                            cboMenu.setSelectedIndex(i);
                            break;
                        }
                    }
                    // -------------------------------------------------------------

                    btnUpdate.setEnabled(true);
                    btnMasukPesanan.setEnabled(false);
                }
            }
        });

        // 5. TOMBOL UPDATE (UPDATE LOGIC 2)
        btnUpdate.addActionListener(e -> {
            int row = tabelKeranjang.getSelectedRow();
            if (row != -1) {
                try {
                    // Ambil Data BARU dari Form (Mungkin user ganti Menu/Pelanggan/Metode)
                    String rawCombo = cboMenu.getSelectedItem().toString();
                    String[] split = rawCombo.split(":");
                    
                    int idMenuBaru = Integer.parseInt(split[0]);
                    String namaMenuBaru = split[1];
                    double hargaBaru = Double.parseDouble(split[2]);
                    
                    int qtyBaru = Integer.parseInt(txtQty.getText());
                    double subtotalBaru = hargaBaru * qtyBaru;
                    
                    // UPDATE SEMUA KOLOM DI TABEL
                    modelKeranjang.setValueAt(txtPelanggan.getText(), row, 0); // Pelanggan
                    modelKeranjang.setValueAt(idMenuBaru, row, 1);             // ID Menu (PENTING!)
                    modelKeranjang.setValueAt(getMetodePembayaran(), row, 2);  // Metode
                    modelKeranjang.setValueAt(namaMenuBaru, row, 3);           // Nama Menu
                    modelKeranjang.setValueAt((int)hargaBaru, row, 4);         // Harga (Int)
                    modelKeranjang.setValueAt(qtyBaru, row, 5);                // Qty
                    modelKeranjang.setValueAt((int)subtotalBaru, row, 6);      // Subtotal (Int)
                    
                    // Hitung Ulang Total
                    transController.hitungTotalBelanja(this);
                    resetInput();
                    
                    JOptionPane.showMessageDialog(this, "Data Berhasil Diupdate!");

                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal Update: " + ex.getMessage());
                }
            }
        });

        // 6. Tombol Lainnya
        btnHapus.addActionListener(e -> transController.hapusItemKeranjang(this));
        btnBayar.addActionListener(e -> transController.simpanTransaksi(this));
        
        btnSimpanMenu.addActionListener(e -> {
            menuDAO.tambahMenu(txtNamaMenuBaru.getText(), Double.parseDouble(txtHargaMenuBaru.getText()));
            loadDataMenu();
            txtNamaMenuBaru.setText(""); txtHargaMenuBaru.setText("");
            JOptionPane.showMessageDialog(this, "Menu Disimpan");
        });
    }

    private void resetInput() {
        txtQty.setText("");
        lblSubtotalPreview.setText("Rp 0");
        btnUpdate.setEnabled(false);
        btnMasukPesanan.setEnabled(true);
        tabelKeranjang.clearSelection();
    }

    private void loadDataMenu() {
        cboMenu.removeAllItems();
        List<Menu> list = menuDAO.getAllMenu();
        for(Menu m : list) {
            cboMenu.addItem(m.getId() + ":" + m.getNama() + ":" + (int)m.getHarga());
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