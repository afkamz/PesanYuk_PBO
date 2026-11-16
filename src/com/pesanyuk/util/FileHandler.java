package com.pesanyuk.util;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class untuk membaca dan menyimpan data Menu, Meja, dan Transaksi ke file teks (.txt).
 * Menerapkan format sesuai spesifikasi perancangan sistem Bab 3.5.
 */
public class FileHandler {
    private static final String DATA_DIR = "data";
    private static final String MENU_FILE = DATA_DIR + "/menu.txt";
    private static final String MEJA_FILE = DATA_DIR + "/meja.txt";
    private static final String TRANSAKSI_FILE = DATA_DIR + "/transaksi.txt";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Gagal membuat direktori data: " + e.getMessage());
        }
    }

    // ==================== KELOLA MENU ====================

    public static void simpanMenu(List<MenuItem> daftarMenu) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MENU_FILE))) {
            for (MenuItem item : daftarMenu) {
                // Format: id;kategori;nama;harga;atribut(pedas/ukuran)
                String baris = String.format("%s;%s;%s;%.0f;%s",
                        item.getId(),
                        item.getKategori(),
                        item.getNama(),
                        item.getHarga(),
                        item.getAtributKhusus()
                );
                writer.write(baris);
                writer.newLine();
            }
        }
    }

    public static ArrayList<MenuItem> muatMenu() throws IOException {
        ArrayList<MenuItem> list = new ArrayList<>();
        File file = new File(MENU_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String kategori = parts[1];
                    String nama = parts[2];
                    double harga = Double.parseDouble(parts[3]);
                    String atribut = parts[4];

                    try {
                        if ("Makanan".equalsIgnoreCase(kategori)) {
                            list.add(new Makanan(id, nama, harga, atribut));
                        } else if ("Minuman".equalsIgnoreCase(kategori)) {
                            list.add(new Minuman(id, nama, harga, atribut));
                        }
                    } catch (InputTidakValidException e) {
                        System.err.println("Lewati menu tidak valid: " + e.getMessage());
                    }
                }
            }
        }
        return list;
    }

    // ==================== KELOLA MEJA ====================

    public static void simpanMeja(List<Meja> daftarMeja) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEJA_FILE))) {
            for (Meja meja : daftarMeja) {
                // Format: nomor;kapasitas;terisi
                String baris = String.format("%d;%d;%b",
                        meja.getNomor(),
                        meja.getKapasitas(),
                        meja.isTerisi()
                );
                writer.write(baris);
                writer.newLine();
            }
        }
    }

    public static ArrayList<Meja> muatMeja() throws IOException {
        ArrayList<Meja> list = new ArrayList<>();
        File file = new File(MEJA_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    try {
                        int nomor = Integer.parseInt(parts[0]);
                        int kapasitas = Integer.parseInt(parts[1]);
                        boolean terisi = Boolean.parseBoolean(parts[2]);
                        list.add(new Meja(nomor, kapasitas, terisi));
                    } catch (InputTidakValidException | NumberFormatException e) {
                        System.err.println("Lewati meja tidak valid: " + e.getMessage());
                    }
                }
            }
        }
        return list;
    }

    // ==================== KELOLA TRANSAKSI ====================

    public static void simpanTransaksi(List<Transaksi> daftarTransaksi) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSAKSI_FILE))) {
            for (Transaksi t : daftarTransaksi) {
                // Format ringkas baris transaksi:
                // idTransaksi;waktu;nomorMeja;jumlahTamu;total;metode;detailPembayaran
                String baris = String.format("%s;%s;%d;%d;%.0f;%s;%s",
                        t.getIdTransaksi(),
                        t.getWaktuFormat(),
                        t.getOrder().getMeja().getNomor(),
                        t.getOrder().getJumlahTamu(),
                        t.getTotal(),
                        t.getMetode(),
                        t.getDetailPembayaran().replace(";", ",")
                );
                writer.write(baris);
                writer.newLine();
            }
        }
    }
}
