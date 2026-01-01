/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir;

import com.kasir.view.KasirView;
import javax.swing.SwingUtilities;
/**
 *
 * @author hary
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Panggil KasirView, bukan MainView lagi
            new KasirView().setVisible(true);
        });
    }
}