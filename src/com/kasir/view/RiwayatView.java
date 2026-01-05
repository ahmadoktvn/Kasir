/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.view;

import com.kasir.controller.RiwayatController;
import java.awt.*; 
import javax.swing.*; 
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author hary
 */
public class RiwayatView extends JPanel {
    private JTable tabel; 
    private DefaultTableModel model;
    private JButton btnRefresh, btnPDF;
    private final RiwayatController controller = new RiwayatController();

    public RiwayatView() {
        setLayout(new BorderLayout());
        
        // Kolom ditambah "No" di awal
        String[] headers = {"No", "No Trx", "Tanggal", "Pelanggan", "Metode", "Menu", "Qty", "Subtotal", "Diskon", "Total"};
        model = new DefaultTableModel(headers, 0);
        tabel = new JTable(model);
        
        // Kecilkan kolom No
        tabel.getColumnModel().getColumn(0).setMaxWidth(40);
        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRefresh = new JButton("Refresh Data");
        btnPDF = new JButton("Export to PDF");
        
        p.add(btnRefresh);
        p.add(btnPDF);
        
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
        
        btnRefresh.addActionListener(e -> controller.loadDataKeTabel(model));
        btnPDF.addActionListener(e -> controller.cetakLaporanPDF(this));
        
        controller.loadDataKeTabel(model);
    }
    
    public DefaultTableModel getModelRiwayat() { return model; }
}