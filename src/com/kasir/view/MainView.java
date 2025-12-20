/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.view;

import com.kasir.controller.MenuDAO;
import com.kasir.controller.TransaksiController;
import com.kasir.controller.TransaksiDAO;
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




public class MainView extends JFrame {

    private JTabbedPane tabPane;
    private JLabel lblNoTransaksi, lblTotalHarga, lblSubtotalPreview;
    private JComboBox<Menu> cboMenu;
    private JTextField txtHarga, txtQty, txtPelanggan;
    private JTable tabelKeranjang;
    private DefaultTableModel modelKeranjang;
    private JButton btnMasukPesanan, btnUpdate, btnHapus, btnBayar; 
    
    private JTextField txtNamaMenuBaru, txtHargaMenuBaru;
    private JButton btnSimpanMenu;

    private MenuDAO menuDAO;
    private TransaksiDAO transaksiDAO;

    public MainView() {
        menuDAO = new MenuDAO();
        transaksiDAO = new TransaksiDAO();
        initUI();
        loadDataMenu();
        resetTransaksi();
    }

    private void initUI() {
        setTitle("Aplikasi Kasir (Warung Nasihuy)");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- PANEL KASIR ---
        JPanel panelKasir = new JPanel(new BorderLayout(10, 10));
        
        JPanel panelInput = new JPanel(new GridLayout(7, 2, 8, 8));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblNoTransaksi = new JLabel("TR-000");
        lblNoTransaksi.setFont(new Font("Arial", Font.BOLD, 14));
        txtPelanggan = new JTextField(); 
        cboMenu = new JComboBox<>();
        txtHarga = new JTextField(); txtHarga.setEditable(false);
        txtQty = new JTextField();
        lblSubtotalPreview = new JLabel("Rp 0"); 
        lblSubtotalPreview.setForeground(Color.BLUE);

        btnMasukPesanan = new JButton("Pesan");
        btnMasukPesanan.setBackground(new Color(200, 255, 200));
        
        btnUpdate = new JButton("Update Item");
        btnUpdate.setBackground(new Color(255, 255, 200));
        btnUpdate.setEnabled(false);

        panelInput.add(new JLabel("No Transaksi:")); panelInput.add(lblNoTransaksi);
        panelInput.add(new JLabel("Nama Pelanggan:")); panelInput.add(txtPelanggan);
        panelInput.add(new JLabel("----------------")); panelInput.add(new JLabel("----------------"));
        panelInput.add(new JLabel("Pilih Menu:")); panelInput.add(cboMenu);
        panelInput.add(new JLabel("Harga Satuan:")); panelInput.add(txtHarga);
        panelInput.add(new JLabel("Jumlah (Qty):")); panelInput.add(txtQty);
        panelInput.add(new JLabel("Subtotal Item:")); panelInput.add(lblSubtotalPreview);
        
        JPanel panelTombolInput = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTombolInput.add(btnUpdate);
        panelTombolInput.add(btnMasukPesanan);

        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(panelInput, BorderLayout.CENTER);
        panelAtas.add(panelTombolInput, BorderLayout.SOUTH);

        // --- TABEL (DENGAN KOLOM PELANGGAN) ---
        // Perubahan: Menambahkan "Pelanggan" di indeks 0
        String[] header = {"Pelanggan", "Nama Item", "Harga", "Qty", "Subtotal"};
        modelKeranjang = new DefaultTableModel(header, 0);
        tabelKeranjang = new JTable(modelKeranjang);
        
        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblTotalHarga = new JLabel("Total: Rp 0");
        lblTotalHarga.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTotalHarga.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel tombolBawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnHapus = new JButton("Hapus Item");
        btnBayar = new JButton("SIMPAN & BAYAR");
        btnBayar.setBackground(new Color(200, 200, 255));
        
        tombolBawah.add(btnHapus);
        tombolBawah.add(btnBayar);
        
        panelBawah.add(tombolBawah, BorderLayout.WEST);
        panelBawah.add(lblTotalHarga, BorderLayout.CENTER);

        panelKasir.add(panelAtas, BorderLayout.NORTH);
        panelKasir.add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);
        panelKasir.add(panelBawah, BorderLayout.SOUTH);

        // --- PANEL MENU ---
        JPanel panelMenu = new JPanel(new GridBagLayout());
        JPanel formMenu = new JPanel(new GridLayout(3, 2, 10, 10));
        txtNamaMenuBaru = new JTextField(20);
        txtHargaMenuBaru = new JTextField(20);
        btnSimpanMenu = new JButton("Simpan Menu Database");
        formMenu.add(new JLabel("Nama:")); formMenu.add(txtNamaMenuBaru);
        formMenu.add(new JLabel("Harga:")); formMenu.add(txtHargaMenuBaru);
        formMenu.add(new JLabel("")); formMenu.add(btnSimpanMenu);
        panelMenu.add(formMenu);

        tabPane = new JTabbedPane();
        tabPane.addTab("Transaksi Kasir", panelKasir);
        tabPane.addTab("Kelola Menu", panelMenu);
        add(tabPane);

        // ================= EVENTS =================

        cboMenu.addActionListener(e -> {
            Menu m = (Menu) cboMenu.getSelectedItem();
            if(m != null) {
                txtHarga.setText(String.valueOf(m.getHarga()));
                txtQty.requestFocus();
            }
        });

        txtQty.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                TransaksiController.hitungPreview(MainView.this);
            }
        });

        btnMasukPesanan.addActionListener(e -> aksiMasukPesanan());

        // Update Mouse Listener untuk menyesuaikan Index Kolom Baru
        tabelKeranjang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabelKeranjang.getSelectedRow();
                if (row != -1) {
                    // Index 0: Pelanggan, Index 1: Nama Menu, Index 2: Harga, Index 3: Qty
                    String namaPlg = modelKeranjang.getValueAt(row, 0).toString();
                    String harga = modelKeranjang.getValueAt(row, 2).toString();
                    String qty = modelKeranjang.getValueAt(row, 3).toString();
                    
                    txtPelanggan.setText(namaPlg); // Set balik nama pelanggan
                    txtHarga.setText(harga);
                    txtQty.setText(qty);
                    
                    btnUpdate.setEnabled(true);
                    btnMasukPesanan.setEnabled(false);
                }
            }
        });

        btnUpdate.addActionListener(e -> aksiUpdatePesanan());

        btnHapus.addActionListener(e -> {
            if(tabelKeranjang.getSelectedRow() != -1) {
                modelKeranjang.removeRow(tabelKeranjang.getSelectedRow());
                TransaksiController.hitungTotalBelanja(this);
                resetFormInput();
            }
        });

        btnBayar.addActionListener(e -> aksiBayarDatabase());

        btnSimpanMenu.addActionListener(e -> aksiSimpanMenu());
    }

    // --- METHOD LOGIKA ---

    private void aksiMasukPesanan() {
        try {
            Menu m = (Menu) cboMenu.getSelectedItem();
            if (txtQty.getText().isEmpty()) return;
            
            int qty = Integer.parseInt(txtQty.getText());
            double subtotal = m.getHarga() * qty;
            
            // Ambil Nama Pelanggan (Jika kosong, isi "Umum")
            String namaPlg = txtPelanggan.getText().trim();
            if (namaPlg.isEmpty()) namaPlg = "Pelanggan";

            // Tambahkan kolom pelanggan di awal
            modelKeranjang.addRow(new Object[]{
                namaPlg, 
                m.getNama(), 
                m.getHarga(), 
                qty, 
                subtotal
            });
            
            TransaksiController.hitungTotalBelanja(this);
            resetFormInput();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Qty harus angka!");
        }
    }

    private void aksiUpdatePesanan() {
        int row = tabelKeranjang.getSelectedRow();
        if (row != -1) {
            try {
                int qtyBaru = Integer.parseInt(txtQty.getText());
                double harga = Double.parseDouble(txtHarga.getText());
                double subtotalBaru = harga * qtyBaru;
                String namaPlg = txtPelanggan.getText();

                // Update Tabel sesuai index baru
                modelKeranjang.setValueAt(namaPlg, row, 0);      // Update Nama Pelanggan
                modelKeranjang.setValueAt(qtyBaru, row, 3);      // Update Qty (Index 3)
                modelKeranjang.setValueAt(subtotalBaru, row, 4); // Update Subtotal (Index 4)
                
                TransaksiController.hitungTotalBelanja(this);
                resetFormInput();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal Update, cek inputan!");
            }
        }
    }

   private void aksiBayarDatabase() {
        if (modelKeranjang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!");
            return;
        }

        String noTrx = lblNoTransaksi.getText();
        // Ambil nama pelanggan yang diketik di text field atas
        String pelangganUtama = txtPelanggan.getText().isEmpty() ? "Pelanggan" : txtPelanggan.getText();
        
        // Panggil DAO (Parameter Total dihapus karena tabelnya sudah disatukan)
        boolean sukses = transaksiDAO.simpanTransaksi(noTrx, pelangganUtama, modelKeranjang);
        
        if (sukses) {
            JOptionPane.showMessageDialog(this, "Transaksi BERHASIL Disimpan!");
            resetTransaksi();
        } else {
            JOptionPane.showMessageDialog(this, "GAGAL menyimpan transaksi!");
        }
    }
    
    private void aksiSimpanMenu() {
        String nama = txtNamaMenuBaru.getText();
        String hargaS = txtHargaMenuBaru.getText();
        if(!nama.isEmpty() && !hargaS.isEmpty()){
            menuDAO.tambahMenu(nama, Double.parseDouble(hargaS));
            JOptionPane.showMessageDialog(this, "Menu Tersimpan!");
            txtNamaMenuBaru.setText(""); txtHargaMenuBaru.setText("");
            loadDataMenu(); 
        }
    }

    private void resetFormInput() {
        txtQty.setText("");
        lblSubtotalPreview.setText("Rp 0");
        btnUpdate.setEnabled(false);
        btnMasukPesanan.setEnabled(true);
        tabelKeranjang.clearSelection();
    }

    private void loadDataMenu() {
        cboMenu.removeAllItems();
        List<Menu> data = menuDAO.getAllMenu();
        for(Menu m : data) cboMenu.addItem(m);
    }

    private void resetTransaksi() {
        modelKeranjang.setRowCount(0);
        lblTotalHarga.setText("Rp 0");
        lblSubtotalPreview.setText("Rp 0");
        lblNoTransaksi.setText(TransaksiController.generateNoTransaksi());
        txtPelanggan.setText("");
        resetFormInput();
    }

    // Getter
    public JTable getTabelKeranjang() { return tabelKeranjang; }
    public JLabel getLblTotalHarga() { return lblTotalHarga; }
    public JTextField getTxtHarga() { return txtHarga; }
    public JTextField getTxtQty() { return txtQty; }
    public JLabel getLblSubtotalPreview() { return lblSubtotalPreview; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainView().setVisible(true));
    }
}