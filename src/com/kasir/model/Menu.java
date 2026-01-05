/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.model;

/**
 *
 * @author Ahmad
 */

public class Menu {
    private int id;
    private String nama;
    private double harga;

    public Menu(int id, String nama, double harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public double getHarga() { return harga; }

    @Override
    public String toString() {
        return nama; 
    }
}