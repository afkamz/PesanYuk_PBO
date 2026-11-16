package com.pesanyuk.model;

import com.pesanyuk.exception.InputTidakValidException;

/**
 * Subclass Makanan yang mewarisi MenuItem.
 * Menerapkan konsep Inheritance dan Polymorphism.
 */
public class Makanan extends MenuItem {
    private String tingkatPedas; // Contoh: "Tidak Pedas", "Sedang", "Pedas", "Ekstra Pedas"

    public Makanan(String id, String nama, double harga, String tingkatPedas) throws InputTidakValidException {
        super(id, nama, harga);
        if (tingkatPedas == null || tingkatPedas.trim().isEmpty()) {
            this.tingkatPedas = "Biasa";
        } else {
            this.tingkatPedas = tingkatPedas.trim();
        }
    }

    public String getTingkatPedas() {
        return tingkatPedas;
    }

    public void setTingkatPedas(String tingkatPedas) {
        if (tingkatPedas != null && !tingkatPedas.trim().isEmpty()) {
            this.tingkatPedas = tingkatPedas.trim();
        }
    }

    /**
     * Override hitungHarga untuk makanan:
     * Harga total = harga dasar * kuantitas
     */
    @Override
    public double hitungHarga(int qty) {
        return getHarga() * qty;
    }

    @Override
    public String getKategori() {
        return "Makanan";
    }

    @Override
    public String getAtributKhusus() {
        return tingkatPedas;
    }

    @Override
    public String toString() {
        return String.format("[%s] Makanan: %s (Pedas: %s) - Rp %,.0f",
                getId(), getNama(), tingkatPedas, getHarga());
    }
}
