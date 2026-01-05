/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.view;

import javax.swing.*;

/**
 *
 * @author hary
 */
public class MainView extends JFrame {
    public MainView() {
        setTitle("Aplikasi Kasir Warung Nasihuy");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JTabbedPane tab = new JTabbedPane();
        tab.add("Kasir", new KasirView());
        tab.add("Kelola Menu", new MenuView());
        tab.add("Kelola Voucher", new VoucherView());
        tab.add("Riwayat Transaksi", new RiwayatView());
        
        add(tab);
    }
}
