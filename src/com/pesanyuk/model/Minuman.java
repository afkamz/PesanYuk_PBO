package com.pesanyuk.model;

import com.pesanyuk.exception.InputTidakValidException;

/**
 * Subclass Minuman yang mewarisi MenuItem.
 * Menerapkan konsep Inheritance dan Polymorphism.
 * Ukuran minuman memengaruhi harga akhir (aturan bisnis).
 */
public class Minuman extends MenuItem {
    private String ukuran; // "S", "M", "L"

    public Minuman(String id, String nama, double harga, String ukuran) throws InputTidakValidException {
        super(id, nama, harga);
        setUkuran(ukuran);
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        if (ukuran == null || ukuran.trim().isEmpty()) {
            this.ukuran = "M";
        } else {
            String u = ukuran.trim().toUpperCase();
            if (u.equals("S") || u.equals("M") || u.equals("L")) {
                this.ukuran = u;
            } else {
                this.ukuran = "M"; // default ke Medium jika tidak dikenal
            }
        }
    }

    /**
     * Biaya tambahan berdasarkan ukuran:
     * - S : 0
     * - M : 0 (standar)
     * - L : +3.000
     */
    public double getTambahanUkuran() {
        switch (ukuran) {
            case "L":
                return 3000.0;
            case "S":
                return 0.0;
            case "M":
            default:
                return 0.0;
        }
    }

    /**
     * Override hitungHarga untuk minuman:
     * Harga total = (harga dasar + biaya ukuran) * kuantitas
     */
    @Override
    public double hitungHarga(int qty) {
        return (getHarga() + getTambahanUkuran()) * qty;
    }

    @Override
    public String getKategori() {
        return "Minuman";
    }

    @Override
    public String getAtributKhusus() {
        return ukuran;
    }

    @Override
    public String toString() {
        return String.format("[%s] Minuman: %s (Ukuran: %s) - Rp %,.0f (Tambahan: Rp %,.0f)",
                getId(), getNama(), ukuran, getHarga(), getTambahanUkuran());
    }
}
