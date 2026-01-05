/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.model;

/**
 *
 * @author hary
 */
public class Voucher {
    private int id;
    private String kode;
    private double potongan;
    private String status;

    public Voucher(int id, String kode, double potongan, String status) {
        this.id = id;
        this.kode = kode;
        this.potongan = potongan;
        this.status = status;
    }

    public int getId() { return id; }
    public String getKode() { return kode; }
    public double getPotongan() { return potongan; }
    public String getStatus() { return status; }
}