package com.kasir.view;

import com.kasir.controller.MenuDAO;
import com.kasir.controller.TransaksiController;
import com.kasir.model.Menu;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainView extends JFrame {

    private JComboBox<String> cboMenu;
    private JTextField txtHarga, txtQty, txtPelanggan;
    private JLabel lblNoTransaksi, lblTotalHarga, lblSubtotalPreview;
    private JTable tabelKeranjang;
    private DefaultTableModel modelKeranjang;
    private JButton btnMasukPesanan, btnUpdate, btnHapus, btnBayar; 
    
    // Untuk Tab Menu
    private JTextField txtNamaMenuBaru, txtHargaMenuBaru;
    private JButton btnSimpanMenu;

    private MenuDAO menuDAO;
    private TransaksiController transController;

    public MainView() {
        menuDAO = new MenuDAO();
        transController = new TransaksiController();
        initUI();
        loadDataMenu();
        lblNoTransaksi.setText(transController.generateNoTransaksi());
    }

    private void initUI() {
        setTitle("Aplikasi Kasir Berelasi");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // PANEL INPUT
        JPanel panelInput = new JPanel(new GridLayout(7, 2, 8, 8));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblNoTransaksi = new JLabel("TR-000");
        txtPelanggan = new JTextField();
        cboMenu = new JComboBox<>();
        txtHarga = new JTextField(); txtHarga.setEditable(false);
        txtQty = new JTextField();
        lblSubtotalPreview = new JLabel("Rp 0"); lblSubtotalPreview.setForeground(Color.BLUE);
        
        btnMasukPesanan = new JButton("Pesan");
        btnUpdate = new JButton("Update Item"); btnUpdate.setEnabled(false);

        panelInput.add(new JLabel("No Transaksi:")); panelInput.add(lblNoTransaksi);
        panelInput.add(new JLabel("Pelanggan:")); panelInput.add(txtPelanggan);
        panelInput.add(new JLabel("---")); panelInput.add(new JLabel("---"));
        panelInput.add(new JLabel("Menu:")); panelInput.add(cboMenu);
        panelInput.add(new JLabel("Harga:")); panelInput.add(txtHarga);
        panelInput.add(new JLabel("Qty:")); panelInput.add(txtQty);
        panelInput.add(new JLabel("Subtotal:")); panelInput.add(lblSubtotalPreview);
        
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.add(btnUpdate); panelBtn.add(btnMasukPesanan);
        
        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(panelInput, BorderLayout.CENTER);
        panelAtas.add(panelBtn, BorderLayout.SOUTH);

        // TABEL (6 KOLOM)
        String[] header = {"Pelanggan", "ID", "Menu", "Harga", "Qty", "Subtotal"};
        modelKeranjang = new DefaultTableModel(header, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelKeranjang = new JTable(modelKeranjang);
        
        // SEMBUNYIKAN KOLOM ID (INDEX 1)
        tabelKeranjang.getColumnModel().getColumn(1).setMinWidth(0);
        tabelKeranjang.getColumnModel().getColumn(1).setMaxWidth(0);
        tabelKeranjang.getColumnModel().getColumn(1).setWidth(0);

        // PANEL BAWAH
        lblTotalHarga = new JLabel("Rp 0");
        lblTotalHarga.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalHarga.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel panelBawah = new JPanel(new BorderLayout());
        JPanel btnBawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnHapus = new JButton("Hapus");
        btnBayar = new JButton("BAYAR");
        btnBawah.add(btnHapus); btnBawah.add(btnBayar);
        
        panelBawah.add(btnBawah, BorderLayout.WEST);
        panelBawah.add(lblTotalHarga, BorderLayout.CENTER);

        // TAB UTAMA
        JPanel panelKasir = new JPanel(new BorderLayout());
        panelKasir.add(panelAtas, BorderLayout.NORTH);
        panelKasir.add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);
        panelKasir.add(panelBawah, BorderLayout.SOUTH);

        // TAB MENU
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
        add(tab);

        // --- EVENTS ---

        // 1. Pilih Menu Combo
        cboMenu.addActionListener(e -> {
            try {
                if(cboMenu.getSelectedItem() != null) {
                    String[] s = cboMenu.getSelectedItem().toString().split(":");
                    txtHarga.setText(s[2]);
                    txtQty.requestFocus();
                }
            } catch(Exception ex){}
        });

        // 2. Ketik Qty (Preview)
        txtQty.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                transController.hitungPreview(MainView.this);
            }
        });

        // 3. Tombol Pesan
        btnMasukPesanan.addActionListener(e -> transController.tambahKeKeranjang(this));

        // 4. Klik Tabel (Ambil Data ke Form)
        tabelKeranjang.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tabelKeranjang.getSelectedRow();
                if (row != -1) {
                    // Index Baru: 0=Plg, 1=ID, 2=Menu, 3=Harga, 4=Qty
                    txtPelanggan.setText(modelKeranjang.getValueAt(row, 0).toString());
                    txtHarga.setText(modelKeranjang.getValueAt(row, 3).toString());
                    txtQty.setText(modelKeranjang.getValueAt(row, 4).toString());
                    
                    btnUpdate.setEnabled(true);
                    btnMasukPesanan.setEnabled(false);
                }
            }
        });

        // 5. Tombol Update (PERBAIKAN UTAMA DISINI)
        btnUpdate.addActionListener(e -> {
            int row = tabelKeranjang.getSelectedRow();
            if (row != -1) {
                try {
                    int qty = Integer.parseInt(txtQty.getText());
                    double harga = Double.parseDouble(txtHarga.getText());
                    double sub = qty * harga;
                    
                    // Update index 4 (Qty) dan 5 (Subtotal)
                    modelKeranjang.setValueAt(txtPelanggan.getText(), row, 0);
                    modelKeranjang.setValueAt(qty, row, 4);
                    modelKeranjang.setValueAt(sub, row, 5);
                    
                    transController.hitungTotalBelanja(this);
                    resetInput();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Qty Salah!");
                }
            }
        });

        // 6. Tombol Lain
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
            // Format String ID:Nama:Harga untuk diparsing Controller
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainView().setVisible(true));
    }
}