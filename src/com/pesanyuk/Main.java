package com.pesanyuk;

import com.pesanyuk.exception.*;
import com.pesanyuk.manager.*;
import com.pesanyuk.model.*;
import com.pesanyuk.util.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Driver Class - PesanYuk POS System
 * Mendukung mode Interaktif (Input Step-by-Step) dan Demo Otomatis (Pengujian Mandiri).
 */
public class Main {
    private static MejaManager mejaManager;
    private static MenuManager menuManager;
    private static OrderManager orderManager;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Jika diberikan argumen --cli atau -c, langsung ke mode terminal
        if (args.length > 0 && (args[0].equalsIgnoreCase("--cli") || args[0].equalsIgnoreCase("-c"))) {
            jalankanModeCLI();
            return;
        }

        // Default: Meluncurkan Antarmuka GUI Modern (Figma Style)
        System.out.println("=====================================================");
        System.out.println("  Meluncurkan GUI Modern PesanYuk POS (Figma UI)... ");
        System.out.println("  Tip: Gunakan opsi '--cli' untuk mode konsol teks.  ");
        System.out.println("=====================================================");

        try {
            com.pesanyuk.gui.MainFrame.main(args);
        } catch (Exception e) {
            System.err.println("Gagal membuka GUI, beralih ke mode konsol: " + e.getMessage());
            jalankanModeCLI();
        }
    }

    public static void jalankanModeCLI() {
        scanner = new Scanner(System.in);
        inisialisasiSistem();

        boolean running = true;
        while (running) {
            tampilkanMenuUtama();
            System.out.print("Pilih menu (0-7): ");
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    alurBuatPesananInteraktif();
                    break;
                case "2":
                    tampilkanStatusMeja();
                    break;
                case "3":
                    menuKelolaMenu();
                    break;
                case "4":
                    fiturCariMenu();
                    break;
                case "5":
                    tampilkanRiwayatTransaksi();
                    break;
                case "6":
                    jalankanDemoOtomatis();
                    break;
                case "7":
                    System.out.println("Membuka GUI PesanYuk...");
                    com.pesanyuk.gui.MainFrame.main(new String[]{});
                    break;
                case "0":
                    System.out.println("\nMenyimpan data sebelum keluar...");
                    simpanSemuaData();
                    System.out.println("Terima kasih telah menggunakan sistem PesanYuk POS!");
                    running = false;
                    break;
                default:
                    System.out.println("[!] Pilihan tidak valid, silakan coba lagi.");
            }
            if (running) {
                System.out.print("\nTekan ENTER untuk kembali ke menu utama...");
                scanner.nextLine();
            }
        }
    }

    private static void inisialisasiSistem() {
        mejaManager = new MejaManager();
        menuManager = new MenuManager();
        orderManager = new OrderManager(mejaManager);

        // Muat dari file jika ada, jika belum ada inisialisasi default
        try {
            ArrayList<MenuItem> menuFile = FileHandler.muatMenu();
            if (menuFile.isEmpty()) {
                menuManager.inisialisasiMenuDefault();
            } else {
                for (MenuItem item : menuFile) {
                    menuManager.tambah(item);
                }
            }

            ArrayList<Meja> mejaFile = FileHandler.muatMeja();
            if (mejaFile.isEmpty()) {
                mejaManager.inisialisasiMejaDefault();
            } else {
                for (Meja m : mejaFile) {
                    mejaManager.tambahMeja(m);
                }
            }
        } catch (Exception e) {
            menuManager.inisialisasiMenuDefault();
            mejaManager.inisialisasiMejaDefault();
        }
    }

    private static void tampilkanMenuUtama() {
        System.out.println("\n========================================================");
        System.out.println("              PESANYUK - RESTAURANT POS SYSTEM          ");
        System.out.println("========================================================");
        System.out.println(" [1] Buat Pesanan Baru (Alokasi Meja & Kasir Interaktif)");
        System.out.println(" [2] Lihat Daftar & Status Meja");
        System.out.println(" [3] Kelola Menu Restoran (Admin)");
        System.out.println(" [4] Cari Menu");
        System.out.println(" [5] Lihat Riwayat Transaksi");
        System.out.println(" [6] Jalankan Demo Pengujian Otomatis (9 Skenario OOP)");
        System.out.println(" [7] Buka Antarmuka GUI Modern (Figma Style)");
        System.out.println(" [0] Keluar Aplikasi");
        System.out.println("--------------------------------------------------------");
    }

    // =========================================================================
    // 1. ALUR PEMESANAN INTERAKTIF 1 PER 1 (STEP-BY-STEP)
    // =========================================================================
    private static void alurBuatPesananInteraktif() {
        System.out.println("\n--------------------------------------------------------");
        System.out.println("               >>> BUAT PESANAN BARU <<<                ");
        System.out.println("--------------------------------------------------------");

        // Langkah 1: Input Jumlah Tamu
        int jumlahTamu = 0;
        Order order = null;
        while (order == null) {
            System.out.print("Masukkan Jumlah Tamu yang datang (atau 0 untuk batal): ");
            String inputTamu = scanner.nextLine().trim();
            try {
                jumlahTamu = Integer.parseInt(inputTamu);
                if (jumlahTamu == 0) {
                    System.out.println("[i] Pemesanan dibatalkan.");
                    return;
                }
                // Alokasi otomatis meja best-fit
                order = orderManager.buatOrder(jumlahTamu);
                System.out.println("\n[SUKSES] Sistem mengalokasikan meja otomatis (Best-Fit):");
                System.out.printf("  -> Meja #%d (Kapasitas: %d orang) [STATUS: TERISI]%n",
                        order.getMeja().getNomor(), order.getMeja().getKapasitas());
            } catch (NumberFormatException e) {
                System.out.println("[!] Masukan harus berupa angka bulat positif.");
            } catch (TidakAdaMejaException e) {
                System.out.println("[GAGAL ALOKASI] " + e.getMessage());
                return;
            } catch (InputTidakValidException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }

        // Langkah 2: Tambah Menu 1 per 1 ke Keranjang
        boolean tambahLagi = true;
        while (tambahLagi) {
            tampilkanTabelMenu();
            System.out.print("\nMasukkan ID Menu yang ingin dipesan (atau ketik 'selesai'): ");
            String menuId = scanner.nextLine().trim();

            if (menuId.equalsIgnoreCase("selesai") || menuId.equals("0")) {
                if (order.getDaftarItem().isEmpty()) {
                    System.out.print("Keranjang masih kosong. Yakin batal memesan? (y/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                        orderManager.batalkanOrder(order);
                        System.out.println("[i] Order dibatalkan. Meja #" + order.getMeja().getNomor() + " kembali dikosongkan.");
                        return;
                    } else {
                        continue;
                    }
                }
                break;
            }

            MenuItem itemTerpilih = null;
            try {
                itemTerpilih = menuManager.cariById(menuId);
            } catch (MenuNotFoundException e) {
                System.out.println("[!] " + e.getMessage() + " Silakan periksa ID menu pada daftar.");
                continue;
            }

            System.out.print("Masukkan Jumlah (Qty) untuk '" + itemTerpilih.getNama() + "': ");
            String inputQty = scanner.nextLine().trim();
            try {
                int qty = Integer.parseInt(inputQty);
                order.tambahItem(itemTerpilih, qty);
                System.out.printf("  -> [V] %dx %s berhasil dimasukkan ke keranjang.%n", qty, itemTerpilih.getNama());
            } catch (NumberFormatException e) {
                System.out.println("[!] Jumlah pesanan harus berupa bilangan bulat positif.");
                continue;
            } catch (InputTidakValidException e) {
                System.out.println("[!] " + e.getMessage());
                continue;
            }

            System.out.print("\nIngin menambah menu lain? (y/n): ");
            String respon = scanner.nextLine().trim();
            if (!respon.equalsIgnoreCase("y") && !respon.equalsIgnoreCase("ya")) {
                tambahLagi = false;
            }
        }

        // Langkah 3: Tampilkan Ringkasan & Hitung Biaya
        System.out.println("\n--------------------------------------------------------");
        System.out.println("                  RINGKASAN PESANAN                     ");
        System.out.println("--------------------------------------------------------");
        System.out.printf("Order ID : %s | Meja #%d (%d Tamu)%n",
                order.getIdOrder(), order.getMeja().getNomor(), order.getJumlahTamu());
        System.out.println("Daftar Item:");
        for (OrderItem oi : order.getDaftarItem()) {
            System.out.printf("  - %-25s x%2d = Rp %,10.0f%n",
                    oi.getItem().getNama(), oi.getQty(), oi.getSubtotal());
        }
        System.out.println("--------------------------------------------------------");
        System.out.printf("Subtotal              : Rp %,10.0f%n", order.hitungSubtotal());
        System.out.printf("Pajak Restoran (10%%)  : Rp %,10.0f%n", order.hitungPajak());
        System.out.printf("TOTAL TAGIHAN         : Rp %,10.0f%n", order.hitungTotal());
        System.out.println("--------------------------------------------------------");

        // Langkah 4: Pilih Metode Pembayaran
        Pembayaran pembayaran = null;
        while (pembayaran == null) {
            System.out.println("\nPilih Metode Pembayaran:");
            System.out.println(" [1] Tunai (Cash)");
            System.out.println(" [2] QRIS (Simulasi)");
            System.out.println(" [0] Batalkan Pembayaran & Order");
            System.out.print("Pilihan (1/2/0): ");
            String metode = scanner.nextLine().trim();

            if (metode.equals("1")) {
                boolean uangCukup = false;
                while (!uangCukup) {
                    System.out.printf("Total Tagihan: Rp %,.0f%n", order.hitungTotal());
                    System.out.print("Masukkan Nominal Uang yang Diterima (Rp): ");
                    String inputUang = scanner.nextLine().trim();
                    try {
                        double uang = Double.parseDouble(inputUang);
                        pembayaran = new PembayaranTunai(uang);
                        // Coba bayar untuk validasi cukup/tidaknya
                        pembayaran.bayar(order.hitungTotal());
                        uangCukup = true;
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Masukan harus berupa angka nominal uang.");
                    } catch (UangKurangException e) {
                        System.out.println("[PERINGATAN UANG KURANG] " + e.getMessage());
                        System.out.print("Coba bayar lagi dengan uang yang cukup? (y/n): ");
                        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                            break;
                        }
                    } catch (InputTidakValidException e) {
                        System.out.println("[!] " + e.getMessage());
                    }
                }
            } else if (metode.equals("2")) {
                PembayaranQRIS qris = new PembayaranQRIS();
                System.out.println("\n[QRIS] Silakan scan QR code pembayaran:");
                System.out.println("  Kode Referensi : " + qris.getKodeReferensi());
                System.out.printf("  Total Tagihan  : Rp %,.0f%n", order.hitungTotal());
                System.out.print("Tekan ENTER jika pelanggan telah selesai melakukan pembayaran...");
                scanner.nextLine();
                pembayaran = qris;
            } else if (metode.equals("0")) {
                orderManager.batalkanOrder(order);
                System.out.println("[i] Pembayaran dibatalkan. Meja #" + order.getMeja().getNomor() + " dikembalikan statusnya menjadi KOSONG.");
                return;
            } else {
                System.out.println("[!] Pilihan metode tidak valid.");
            }
        }

        // Langkah 5: Proses Transaksi, Cetak Struk, dan Pembebasan Meja
        try {
            Transaksi transaksi = orderManager.prosesBayar(order, pembayaran);
            System.out.println("\n[SUKSES] Transaksi Berhasil Diproses!");
            System.out.println("Status Meja #" + order.getMeja().getNomor() + " otomatis kembali: KOSONG.");

            // Cetak Struk
            System.out.println("\n" + transaksi.generateStruk());

            // Simpan ke file teks
            simpanSemuaData();
        } catch (Exception e) {
            System.out.println("[ERROR] Terjadi kesalahan saat memproses pembayaran: " + e.getMessage());
        }
    }

    private static void tampilkanTabelMenu() {
        System.out.println("\n--- DAFTAR MENU RESTORAN ---");
        System.out.printf("%-6s | %-8s | %-25s | %-12s | %s%n", "ID", "Kategori", "Nama Menu", "Harga Dasar", "Atribut");
        System.out.println("----------------------------------------------------------------------------");
        for (MenuItem item : menuManager.getDaftarMenu()) {
            String atribut = "";
            if (item instanceof Makanan) {
                atribut = "Pedas: " + item.getAtributKhusus();
            } else if (item instanceof Minuman) {
                atribut = "Ukuran: " + item.getAtributKhusus();
            }
            System.out.printf("%-6s | %-8s | %-25s | Rp %,10.0f | %s%n",
                    item.getId(), item.getKategori(), item.getNama(), item.getHarga(), atribut);
        }
    }

    private static void tampilkanStatusMeja() {
        System.out.println("\n--- DAFTAR & STATUS MEJA RESTORAN ---");
        System.out.printf("%-10s | %-15s | %s%n", "No. Meja", "Kapasitas", "Status Saat Ini");
        System.out.println("-------------------------------------------------");
        for (Meja m : mejaManager.getDaftarMeja()) {
            System.out.printf("Meja #%-5d | %2d orang         | %s%n",
                    m.getNomor(), m.getKapasitas(), m.isTerisi() ? "[TERISI / DIGUNAKAN]" : "[KOSONG / TERSEDIA]");
        }
    }

    private static void menuKelolaMenu() {
        System.out.println("\n--- KELOLA MENU RESTORAN (ADMIN) ---");
        System.out.println(" [1] Tambah Menu Makanan");
        System.out.println(" [2] Tambah Menu Minuman");
        System.out.println(" [3] Hapus Menu");
        System.out.println(" [0] Kembali");
        System.out.print("Pilih aksi: ");
        String aksi = scanner.nextLine().trim();

        try {
            if (aksi.equals("1")) {
                System.out.print("Masukkan ID Makanan (misal M007): ");
                String id = scanner.nextLine().trim();
                System.out.print("Masukkan Nama Makanan: ");
                String nama = scanner.nextLine().trim();
                System.out.print("Masukkan Harga Dasar (Rp): ");
                double harga = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Tingkat Pedas (Tidak Pedas/Sedang/Pedas/Ekstra Pedas): ");
                String pedas = scanner.nextLine().trim();

                menuManager.tambah(new Makanan(id, nama, harga, pedas));
                System.out.println("[V] Menu makanan berhasil ditambahkan!");
                simpanSemuaData();
            } else if (aksi.equals("2")) {
                System.out.print("Masukkan ID Minuman (misal D007): ");
                String id = scanner.nextLine().trim();
                System.out.print("Masukkan Nama Minuman: ");
                String nama = scanner.nextLine().trim();
                System.out.print("Masukkan Harga Dasar (Rp): ");
                double harga = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Ukuran (S / M / L): ");
                String ukuran = scanner.nextLine().trim();

                menuManager.tambah(new Minuman(id, nama, harga, ukuran));
                System.out.println("[V] Menu minuman berhasil ditambahkan!");
                simpanSemuaData();
            } else if (aksi.equals("3")) {
                tampilkanTabelMenu();
                System.out.print("Masukkan ID Menu yang ingin dihapus: ");
                String id = scanner.nextLine().trim();
                menuManager.hapus(id);
                System.out.println("[V] Menu dengan ID " + id + " berhasil dihapus!");
                simpanSemuaData();
            }
        } catch (Exception e) {
            System.out.println("[!] Gagal: " + e.getMessage());
        }
    }

    private static void fiturCariMenu() {
        System.out.print("\nMasukkan kata kunci pencarian (nama atau kategori): ");
        String keyword = scanner.nextLine().trim();
        List<MenuItem> hasil = menuManager.cari(keyword);
        if (hasil.isEmpty()) {
            System.out.println("[i] Tidak ditemukan menu yang cocok dengan kata kunci '" + keyword + "'.");
        } else {
            System.out.println("\n--- HASIL PENCARIAN MENU ---");
            for (MenuItem item : hasil) {
                System.out.println("  - " + item);
            }
        }
    }

    private static void tampilkanRiwayatTransaksi() {
        List<Transaksi> riwayat = orderManager.getRiwayatTransaksi();
        if (riwayat.isEmpty()) {
            System.out.println("\n[i] Belum ada transaksi yang tercatat pada sesi ini.");
            return;
        }
        System.out.println("\n--- RIWAYAT TRANSAKSI RESTORAN ---");
        for (Transaksi t : riwayat) {
            System.out.println("  - " + t.getRingkasan());
        }
    }

    private static void simpanSemuaData() {
        try {
            FileHandler.simpanMenu(menuManager.getDaftarMenu());
            FileHandler.simpanMeja(mejaManager.getDaftarMeja());
            FileHandler.simpanTransaksi(orderManager.getRiwayatTransaksi());
        } catch (IOException e) {
            System.err.println("[!] Gagal menyimpan data ke file: " + e.getMessage());
        }
    }

    // =========================================================================
    // 6. DEMO PENGUJIAN OTOMATIS (9 SKENARIO OOP MINGGU 2)
    // =========================================================================
    private static void jalankanDemoOtomatis() {
        System.out.println("======================================================================");
        System.out.println("     PESANYUK POS - DEMO IMPLEMENTASI KONSEP OOP (MINGGU 2)");
        System.out.println("======================================================================");

        try {
            System.out.println("\n>>> [1] INISIALISASI MANAGER & DATA AWAL");
            MejaManager testMeja = new MejaManager();
            testMeja.inisialisasiMejaDefault();
            MenuManager testMenu = new MenuManager();
            testMenu.inisialisasiMenuDefault();
            OrderManager testOrderMgr = new OrderManager(testMeja);

            System.out.println("\n>>> [2] PENGUJIAN INHERITANCE & ABSTRACTION");
            MenuItem itemMakanan = new Makanan("M006", "Sate Ayam Madura", 30000, "Sedang");
            MenuItem itemMinuman = new Minuman("D006", "Jus Alpukat Kocok", 15000, "L");
            testMenu.tambah(itemMakanan);
            testMenu.tambah(itemMinuman);
            System.out.println("  - Makanan subclass: " + itemMakanan);
            System.out.println("  - Minuman subclass: " + itemMinuman);

            System.out.println("\n>>> [3] PENGUJIAN POLYMORPHISM (HITUNG HARGA)");
            int qty = 3;
            System.out.printf("  * Makanan (%s x %d): Rp %,.0f%n", itemMakanan.getNama(), qty, itemMakanan.hitungHarga(qty));
            System.out.printf("  * Minuman Ukuran L (%s x %d): Rp %,.0f%n", itemMinuman.getNama(), qty, itemMinuman.hitungHarga(qty));

            System.out.println("\n>>> [4] LOGIKA ALOKASI MEJA OTOMATIS (BEST-FIT)");
            Order ord1 = testOrderMgr.buatOrder(3);
            System.out.printf("  -> Tamu 3 orang: Meja #%d (Kapasitas: %d)%n", ord1.getMeja().getNomor(), ord1.getMeja().getKapasitas());
            Order ord2 = testOrderMgr.buatOrder(5);
            System.out.printf("  -> Tamu 5 orang: Meja #%d (Kapasitas: %d)%n", ord2.getMeja().getNomor(), ord2.getMeja().getKapasitas());

            System.out.println("\n>>> [5] PEMESANAN MENU PADA ORDER 1");
            ord1.tambahItem(testMenu.cariById("M001"), 2);
            ord1.tambahItem(testMenu.cariById("D001"), 2);
            ord1.tambahItem(itemMinuman, 1);
            System.out.printf("Subtotal: Rp %,.0f | Pajak: Rp %,.0f | Total: Rp %,.0f%n",
                    ord1.hitungSubtotal(), ord1.hitungPajak(), ord1.hitungTotal());

            System.out.println("\n>>> [6] PENGUJIAN EXCEPTION HANDLING (UANG KURANG)");
            try {
                testOrderMgr.prosesBayar(ord1, new PembayaranTunai(20000));
            } catch (UangKurangException e) {
                System.out.println("  [SUKSES DITANGKAP] " + e.getMessage());
            }

            System.out.println("\n>>> [7] PEMBAYARAN TUNAI BERHASIL & CETAK STRUK");
            Transaksi t1 = testOrderMgr.prosesBayar(ord1, new PembayaranTunai(100000));
            System.out.println("Transaksi berhasil diproses!");
            System.out.println("Status Meja #" + ord1.getMeja().getNomor() + " kembali: KOSONG.");
            System.out.print(t1.generateStruk());

            System.out.println(">>> [8] PEMESANAN ORDER 2 & PEMBAYARAN QRIS");
            ord2.tambahItem(testMenu.cariById("M002"), 3);
            Transaksi t2 = testOrderMgr.prosesBayar(ord2, new PembayaranQRIS());
            System.out.println("Transaksi QRIS berhasil diproses!");
            System.out.print(t2.generateStruk());

            System.out.println(">>> [9] PENGUJIAN PENYIMPANAN DATA PERSISTEN (FILE HANDLING)");
            FileHandler.simpanMenu(testMenu.getDaftarMenu());
            System.out.println("Data berhasil disimpan ke file data/.");

            System.out.println("\n======================================================================");
            System.out.println("     SELURUH AKTIVITAS MINGGU 2 BERHASIL DIIMPLEMENTASIKAN & DIUJI!   ");
            System.out.println("======================================================================");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
