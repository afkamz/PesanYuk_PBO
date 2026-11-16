package com.pesanyuk.manager;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.exception.MenuNotFoundException;
import com.pesanyuk.model.Makanan;
import com.pesanyuk.model.MenuItem;
import com.pesanyuk.model.Minuman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manager untuk mengelola daftar menu (Makanan dan Minuman).
 * Menyediakan operasi CRUD dan pencarian polimorfik.
 */
public class MenuManager {
    private ArrayList<MenuItem> daftarMenu;

    public MenuManager() {
        this.daftarMenu = new ArrayList<>();
    }

    public void tambah(MenuItem item) throws InputTidakValidException {
        if (item == null) {
            throw new InputTidakValidException("Item menu tidak boleh null.");
        }
        for (MenuItem m : daftarMenu) {
            if (m.getId().equalsIgnoreCase(item.getId())) {
                throw new InputTidakValidException("Menu dengan ID " + item.getId() + " sudah ada!");
            }
        }
        daftarMenu.add(item);
    }

    public void ubah(String id, MenuItem baru) throws MenuNotFoundException, InputTidakValidException {
        if (baru == null) {
            throw new InputTidakValidException("Data baru tidak boleh null.");
        }
        for (int i = 0; i < daftarMenu.size(); i++) {
            if (daftarMenu.get(i).getId().equalsIgnoreCase(id)) {
                daftarMenu.set(i, baru);
                return;
            }
        }
        throw new MenuNotFoundException("Menu dengan ID '" + id + "' tidak ditemukan untuk diubah.");
    }

    public void hapus(String id) throws MenuNotFoundException {
        boolean removed = daftarMenu.removeIf(item -> item.getId().equalsIgnoreCase(id));
        if (!removed) {
            throw new MenuNotFoundException("Menu dengan ID '" + id + "' tidak ditemukan untuk dihapus.");
        }
    }

    public MenuItem cariById(String id) throws MenuNotFoundException {
        for (MenuItem item : daftarMenu) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        throw new MenuNotFoundException("Menu dengan ID '" + id + "' tidak ditemukan.");
    }

    /**
     * Pencarian menu berdasarkan kata kunci (nama atau kategori).
     */
    public ArrayList<MenuItem> cari(String kataKunci) {
        ArrayList<MenuItem> hasil = new ArrayList<>();
        if (kataKunci == null || kataKunci.trim().isEmpty()) {
            hasil.addAll(daftarMenu);
            return hasil;
        }

        String keyword = kataKunci.trim().toLowerCase();
        for (MenuItem item : daftarMenu) {
            if (item.getNama().toLowerCase().contains(keyword) ||
                item.getKategori().toLowerCase().contains(keyword) ||
                item.getId().toLowerCase().contains(keyword)) {
                hasil.add(item);
            }
        }
        return hasil;
    }

    public List<MenuItem> getDaftarMenu() {
        return Collections.unmodifiableList(daftarMenu);
    }

    /**
     * Data menu awal restoran PesanYuk.
     */
    public void inisialisasiMenuDefault() {
        daftarMenu.clear();
        try {
            tambah(new Makanan("M001", "Nasi Goreng Spesial", 22000, "Sedang"));
            tambah(new Makanan("M002", "Ayam Bakar Madu", 28000, "Biasa"));
            tambah(new Makanan("M003", "Mie Goreng Seafood", 25000, "Pedas"));
            tambah(new Makanan("M004", "Kwetiau Sapi", 27000, "Sedang"));
            tambah(new Makanan("M005", "Kentang Goreng (Snack)", 15000, "Biasa"));

            tambah(new Minuman("D001", "Es Teh Manis", 5000, "M"));
            tambah(new Minuman("D002", "Es Jeruk Peras", 8000, "M"));
            tambah(new Minuman("D003", "Kopi Susu Gula Aren", 18000, "L"));
            tambah(new Minuman("D004", "Matcha Latte", 20000, "L"));
            tambah(new Minuman("D005", "Air Mineral", 4000, "M"));
        } catch (InputTidakValidException e) {
            // Unreachable untuk data default
        }
    }
}
