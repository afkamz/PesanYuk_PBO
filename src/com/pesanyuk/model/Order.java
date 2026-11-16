package com.pesanyuk.model;

import com.pesanyuk.exception.InputTidakValidException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Order merepresentasikan pesanan pelanggan di satu meja.
 * Mengelola daftar item pesanan dan perhitungan total biaya serta pajak.
 */
public class Order {
    public static final double TARIF_PAJAK = 0.10; // 10% Pajak Restoran (PB1)

    private String idOrder;
    private int jumlahTamu;
    private Meja meja;
    private ArrayList<OrderItem> daftarItem;

    public Order(String idOrder, int jumlahTamu, Meja meja) throws InputTidakValidException {
        if (idOrder == null || idOrder.trim().isEmpty()) {
            throw new InputTidakValidException("ID Order tidak boleh kosong.");
        }
        if (jumlahTamu <= 0) {
            throw new InputTidakValidException("Jumlah tamu harus minimal 1 orang.");
        }
        if (meja == null) {
            throw new InputTidakValidException("Meja tidak boleh null.");
        }
        this.idOrder = idOrder.trim();
        this.jumlahTamu = jumlahTamu;
        this.meja = meja;
        this.daftarItem = new ArrayList<>();
    }

    public String getIdOrder() {
        return idOrder;
    }

    public int getJumlahTamu() {
        return jumlahTamu;
    }

    public Meja getMeja() {
        return meja;
    }

    public void setMeja(Meja meja) throws InputTidakValidException {
        if (meja == null) {
            throw new InputTidakValidException("Meja tidak boleh null.");
        }
        this.meja = meja;
    }

    public List<OrderItem> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }

    /**
     * Menambahkan item menu ke dalam pesanan.
     * Jika item menu sudah ada di keranjang, kuantitas akan diakumulasi.
     */
    public void tambahItem(MenuItem item, int qty) throws InputTidakValidException {
        if (item == null) {
            throw new InputTidakValidException("Item menu tidak boleh null.");
        }
        if (qty <= 0) {
            throw new InputTidakValidException("Jumlah pesanan (qty) harus lebih besar dari 0.");
        }

        // Cek apakah item yang sama (berdasarkan ID dan atribut khusus) sudah ada
        for (OrderItem orderItem : daftarItem) {
            MenuItem existing = orderItem.getItem();
            if (existing.getId().equalsIgnoreCase(item.getId()) &&
                existing.getAtributKhusus().equalsIgnoreCase(item.getAtributKhusus())) {
                orderItem.tambahQty(qty);
                return;
            }
        }
        daftarItem.add(new OrderItem(item, qty));
    }

    /**
     * Menghapus item dari daftar pesanan berdasarkan ID menu.
     */
    public boolean hapusItem(String menuId) {
        if (menuId == null) return false;
        return daftarItem.removeIf(orderItem -> orderItem.getItem().getId().equalsIgnoreCase(menuId.trim()));
    }

    /**
     * Menghitung subtotal sebelum pajak.
     */
    public double hitungSubtotal() {
        double subtotal = 0;
        for (OrderItem oi : daftarItem) {
            subtotal += oi.getSubtotal();
        }
        return subtotal;
    }

    /**
     * Menghitung nilai pajak (10% dari subtotal).
     */
    public double hitungPajak() {
        return hitungSubtotal() * TARIF_PAJAK;
    }

    /**
     * Menghitung total akhir (subtotal + pajak).
     */
    public double hitungTotal() {
        return hitungSubtotal() + hitungPajak();
    }

    @Override
    public String toString() {
        return String.format("Order #%s | Meja #%d | Tamu: %d | Total Item: %d | Total: Rp %,.0f",
                idOrder, meja.getNomor(), jumlahTamu, daftarItem.size(), hitungTotal());
    }
}
