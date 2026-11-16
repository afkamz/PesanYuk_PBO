package com.pesanyuk;

import com.pesanyuk.exception.*;
import com.pesanyuk.manager.*;
import com.pesanyuk.model.*;
import com.pesanyuk.util.FileHandler;

import java.io.IOException;

/**
 * Main Class - Demonstrasi dan Pengujian Mandiri
 * MINGGU 2: IMPLEMENTASI KONSEP PEMROGRAMAN BERORIENTASI OBJEK (OOP)
 * Project: PesanYuk (Restaurant Point of Sale)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("     PESANYUK POS - DEMO IMPLEMENTASI KONSEP OOP (MINGGU 2)");
        System.out.println("======================================================================");

        try {
            // ------------------------------------------------------------------
            // 1. Inisialisasi Manager & Data Awal (Class & Object, Encapsulation)
            // ------------------------------------------------------------------
            System.out.println("\n>>> [1] INISIALISASI MANAGER & DATA AWAL");
            MejaManager mejaManager = new MejaManager();
            mejaManager.inisialisasiMejaDefault();

            MenuManager menuManager = new MenuManager();
            menuManager.inisialisasiMenuDefault();

            OrderManager orderManager = new OrderManager(mejaManager);

            System.out.println("Daftar Meja Terdaftar:");
            for (Meja m : mejaManager.getDaftarMeja()) {
                System.out.println("  - " + m);
            }

            System.out.println("\nDaftar Menu Awal:");
            for (MenuItem item : menuManager.getDaftarMenu()) {
                System.out.println("  - " + item);
            }

            // ------------------------------------------------------------------
            // 2. Demonstrasi Abstraction & Inheritance
            // ------------------------------------------------------------------
            System.out.println("\n>>> [2] PENGUJIAN INHERITANCE & ABSTRACTION");
            MenuItem itemMakanan = new Makanan("M006", "Sate Ayam Madura", 30000, "Sedang");
            MenuItem itemMinuman = new Minuman("D006", "Jus Alpukat Kocok", 15000, "L");
            menuManager.tambah(itemMakanan);
            menuManager.tambah(itemMinuman);
            System.out.println("Menu berhasil ditambahkan melalui relasi induk MenuItem:");
            System.out.println("  - Makanan subclass: " + itemMakanan);
            System.out.println("  - Minuman subclass: " + itemMinuman);

            // ------------------------------------------------------------------
            // 3. Demonstrasi Polymorphism pada MenuItem.hitungHarga()
            // ------------------------------------------------------------------
            System.out.println("\n>>> [3] PENGUJIAN POLYMORPHISM (HITUNG HARGA)");
            int qtyBeli = 3;
            System.out.printf("  * Makanan (%s x %d): Rp %,.0f%n",
                    itemMakanan.getNama(), qtyBeli, itemMakanan.hitungHarga(qtyBeli));
            System.out.printf("  * Minuman Ukuran L (%s x %d): Rp %,.0f (termasuk biaya ukuran)%n",
                    itemMinuman.getNama(), qtyBeli, itemMinuman.hitungHarga(qtyBeli));

            // ------------------------------------------------------------------
            // 4. Demonstrasi Logika Alokasi Meja Otomatis (Best-Fit)
            // ------------------------------------------------------------------
            System.out.println("\n>>> [4] LOGIKA ALOKASI MEJA OTOMATIS (BEST-FIT)");
            System.out.println("Skenario 1: Tamu 3 orang datang (Harus memilih meja kapasitas 4 terdekat)");
            Order order1 = orderManager.buatOrder(3);
            System.out.printf("  -> Meja dialokasikan untuk 3 tamu: Meja #%d (Kapasitas: %d)%n",
                    order1.getMeja().getNomor(), order1.getMeja().getKapasitas());

            System.out.println("\nSkenario 2: Tamu 5 orang datang (Harus memilih meja kapasitas 6)");
            Order order2 = orderManager.buatOrder(5);
            System.out.printf("  -> Meja dialokasikan untuk 5 tamu: Meja #%d (Kapasitas: %d)%n",
                    order2.getMeja().getNomor(), order2.getMeja().getKapasitas());

            // ------------------------------------------------------------------
            // 5. Pemesanan Menu & Perhitungan Subtotal, Pajak, dan Total
            // ------------------------------------------------------------------
            System.out.println("\n>>> [5] PEMESANAN MENU PADA ORDER 1");
            order1.tambahItem(menuManager.cariById("M001"), 2); // 2x Nasi Goreng
            order1.tambahItem(menuManager.cariById("D001"), 2); // 2x Es Teh
            order1.tambahItem(itemMinuman, 1);                  // 1x Jus Alpukat L

            System.out.println("Daftar Item pada " + order1.getIdOrder() + ":");
            for (OrderItem oi : order1.getDaftarItem()) {
                System.out.println("  - " + oi);
            }
            System.out.printf("Subtotal : Rp %,.0f%n", order1.hitungSubtotal());
            System.out.printf("Pajak 10%%: Rp %,.0f%n", order1.hitungPajak());
            System.out.printf("Total    : Rp %,.0f%n", order1.hitungTotal());

            // ------------------------------------------------------------------
            // 6. Demonstrasi Exception Handling (Uang Kurang)
            // ------------------------------------------------------------------
            System.out.println("\n>>> [6] PENGUJIAN EXCEPTION HANDLING (UANG KURANG)");
            double totalTagihan = order1.hitungTotal();
            PembayaranTunai bayarKurang = new PembayaranTunai(20000); // Sengaja uang kurang
            try {
                System.out.println("Mencoba membayar Rp 20,000 untuk tagihan Rp " + String.format("%,.0f", totalTagihan) + "...");
                orderManager.prosesBayar(order1, bayarKurang);
            } catch (UangKurangException e) {
                System.out.println("  [SUKSES DITANGKAP] " + e.getMessage());
            }

            // ------------------------------------------------------------------
            // 7. Demonstrasi Polymorphism Interface Pembayaran: Tunai Sukses
            // ------------------------------------------------------------------
            System.out.println("\n>>> [7] PEMBAYARAN TUNAI BERHASIL & CETAK STRUK");
            PembayaranTunai bayarCukup = new PembayaranTunai(100000); // Bayar Rp 100.000
            Transaksi trx1 = orderManager.prosesBayar(order1, bayarCukup);
            System.out.println("Transaksi berhasil diproses!");
            System.out.println("Status Meja #" + order1.getMeja().getNomor() + " setelah transaksi: "
                    + (order1.getMeja().isTerisi() ? "TERISI" : "KOSONG (Otomatis Dibebaskan)"));

            System.out.println("\n--- STRUK TRANSAKSI TUNAI ---");
            System.out.print(trx1.generateStruk());

            // ------------------------------------------------------------------
            // 8. Demonstrasi Polymorphism Interface Pembayaran: QRIS Sukses
            // ------------------------------------------------------------------
            System.out.println("\n>>> [8] PEMESANAN ORDER 2 & PEMBAYARAN QRIS");
            order2.tambahItem(menuManager.cariById("M002"), 3); // 3x Ayam Bakar Madu
            order2.tambahItem(menuManager.cariById("D003"), 3); // 3x Kopi Susu Aren
            System.out.printf("Total Order 2: Rp %,.0f%n", order2.hitungTotal());

            PembayaranQRIS bayarQRIS = new PembayaranQRIS();
            Transaksi trx2 = orderManager.prosesBayar(order2, bayarQRIS);
            System.out.println("Transaksi QRIS berhasil diproses!");
            System.out.println("\n--- STRUK TRANSAKSI QRIS ---");
            System.out.print(trx2.generateStruk());

            // ------------------------------------------------------------------
            // 9. Demonstrasi FileHandler (Menyimpan Data ke File Teks)
            // ------------------------------------------------------------------
            System.out.println(">>> [9] PENGUJIAN PENYIMPANAN DATA PERSISTEN (FILE HANDLING)");
            FileHandler.simpanMenu(menuManager.getDaftarMenu());
            FileHandler.simpanMeja(mejaManager.getDaftarMeja());
            FileHandler.simpanTransaksi(orderManager.getRiwayatTransaksi());
            System.out.println("Berhasil menyimpan data menu.txt, meja.txt, dan transaksi.txt ke folder data/.");

            System.out.println("\n======================================================================");
            System.out.println("     SELURUH AKTIVITAS MINGGU 2 BERHASIL DIIMPLEMENTASIKAN & DIUJI!   ");
            System.out.println("======================================================================");

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan pada alur demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
