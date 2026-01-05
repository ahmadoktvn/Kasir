/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kasir.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hary
 */
public class Transaksi {
    private String noTransaksi;
    private String namaPelanggan;
    private String metodePembayaran;
    private Voucher voucher;
    private List<TransaksiItem> listItems;

    public Transaksi(String noTransaksi, String namaPelanggan, String metodePembayaran) {
        this.noTransaksi = noTransaksi;
        this.namaPelanggan = namaPelanggan;
        this.metodePembayaran = metodePembayaran;
        this.listItems = new ArrayList<>();
    }

    public void tambahItem(TransaksiItem item) {
        listItems.add(item);
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public String getNoTransaksi() { return noTransaksi; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public Voucher getVoucher() { return voucher; }
    public List<TransaksiItem> getListItems() { return listItems; }

    public double hitungTotalBersih() {
        double total = listItems.stream().mapToDouble(TransaksiItem::getSubtotal).sum();
        double pot = (voucher != null) ? voucher.getPotongan() : 0;
        return Math.max(0, total - pot);
    }
}