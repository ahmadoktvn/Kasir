/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.model;

/**
 *
 * @author hary
 */
public class TransaksiItem {
    private int idMenu;
    private double harga;
    private int qty;
    private double subtotal;

    public TransaksiItem(int idMenu, double harga, int qty, double subtotal) {
        this.idMenu = idMenu;
        this.harga = harga;
        this.qty = qty;
        this.subtotal = subtotal;
    }

    public int getIdMenu() { return idMenu; }
    public double getHarga() { return harga; }
    public int getQty() { return qty; }
    public double getSubtotal() { return subtotal; }
}