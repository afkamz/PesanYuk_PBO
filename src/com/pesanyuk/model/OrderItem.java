package com.pesanyuk.model;

import com.pesanyuk.exception.InputTidakValidException;

/**
 * Class OrderItem merepresentasikan satu baris menu yang dipesan beserta kuantitasnya.
 * Memanfaatkan Polymorphism saat menghitung subtotal melalui MenuItem.
 */
public class OrderItem {
    private MenuItem item;
    private int qty;

    public OrderItem(MenuItem item, int qty) throws InputTidakValidException {
        if (item == null) {
            throw new InputTidakValidException("Menu item tidak boleh null.");
        }
        if (qty <= 0) {
            throw new InputTidakValidException("Jumlah pesanan (qty) harus lebih besar dari 0.");
        }
        this.item = item;
        this.qty = qty;
    }

    public MenuItem getItem() {
        return item;
    }

    public void setItem(MenuItem item) throws InputTidakValidException {
        if (item == null) {
            throw new InputTidakValidException("Menu item tidak boleh null.");
        }
        this.item = item;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) throws InputTidakValidException {
        if (qty <= 0) {
            throw new InputTidakValidException("Jumlah pesanan (qty) harus lebih besar dari 0.");
        }
        this.qty = qty;
    }

    public void tambahQty(int extraQty) throws InputTidakValidException {
        if (extraQty <= 0) {
            throw new InputTidakValidException("Penambahan kuantitas harus lebih besar dari 0.");
        }
        this.qty += extraQty;
    }

    /**
     * Menghitung subtotal dengan memanggil method polimorfik hitungHarga() dari MenuItem.
     */
    public double getSubtotal() {
        return item.hitungHarga(qty);
    }

    @Override
    public String toString() {
        return String.format("%s x%d = Rp %,.0f", item.getNama(), qty, getSubtotal());
    }
}
