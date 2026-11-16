package com.pesanyuk.model;

import com.pesanyuk.exception.InputTidakValidException;

/**
 * Abstract class MenuItem sebagai kelas induk untuk seluruh menu restoran.
 * Menerapkan konsep Abstraction dan Encapsulation.
 */
public abstract class MenuItem {
    private String id;
    private String nama;
    private double harga;

    /**
     * Constructor MenuItem
     * @param id ID unik menu (misal M001, D001)
     * @param nama Nama menu
     * @param harga Harga dasar menu
     * @throws InputTidakValidException jika data masukan tidak valid
     */
    public MenuItem(String id, String nama, double harga) throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID Menu tidak boleh kosong.");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new InputTidakValidException("Nama Menu tidak boleh kosong.");
        }
        if (harga < 0) {
            throw new InputTidakValidException("Harga menu tidak boleh negatif.");
        }
        this.id = id.trim();
        this.nama = nama.trim();
        this.harga = harga;
    }

    // Getter & Setter (Encapsulation)
    public String getId() {
        return id;
    }

    public void setId(String id) throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID Menu tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) throws InputTidakValidException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new InputTidakValidException("Nama Menu tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) throws InputTidakValidException {
        if (harga < 0) {
            throw new InputTidakValidException("Harga menu tidak boleh negatif.");
        }
        this.harga = harga;
    }

    /**
     * Method abstrak untuk menghitung harga berdasarkan kuantitas.
     * Menerapkan Polymorphism melalui method overriding di subclass.
     */
    public abstract double hitungHarga(int qty);

    /**
     * Method abstrak untuk mendapatkan kategori menu (Makanan / Minuman).
     */
    public abstract String getKategori();

    /**
     * Method abstrak untuk mendapatkan deskripsi atribut khusus subclass.
     */
    public abstract String getAtributKhusus();

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (Rp %,.0f)", id, getKategori(), nama, harga);
    }
}
