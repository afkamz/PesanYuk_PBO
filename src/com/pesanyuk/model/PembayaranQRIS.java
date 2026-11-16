package com.pesanyuk.model;

import com.pesanyuk.exception.UangKurangException;
import java.util.UUID;

/**
 * Implementasi Pembayaran dengan QRIS (Quick Response Code Indonesian Standard).
 * Menerapkan prinsip Encapsulation dan Polymorphism.
 */
public class PembayaranQRIS implements Pembayaran {
    private String kodeReferensi;
    private boolean terkonfirmasi;
    private double jumlahDibayar;

    public PembayaranQRIS() {
        this.kodeReferensi = "QRIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.terkonfirmasi = false;
        this.jumlahDibayar = 0.0;
    }

    public PembayaranQRIS(String kodeReferensi) {
        if (kodeReferensi == null || kodeReferensi.trim().isEmpty()) {
            this.kodeReferensi = "QRIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else {
            this.kodeReferensi = kodeReferensi.trim();
        }
        this.terkonfirmasi = false;
        this.jumlahDibayar = 0.0;
    }

    public String getKodeReferensi() {
        return kodeReferensi;
    }

    public boolean isTerkonfirmasi() {
        return terkonfirmasi;
    }

    public double getJumlahDibayar() {
        return jumlahDibayar;
    }

    @Override
    public boolean bayar(double total) throws UangKurangException {
        // Simulasi pembayaran QRIS sukses
        this.jumlahDibayar = total;
        this.terkonfirmasi = true;
        return true;
    }

    @Override
    public String getNamaMetode() {
        return "QRIS";
    }

    @Override
    public String getDetailPembayaran() {
        return String.format("Metode: QRIS | Ref: %s | Status: %s",
                kodeReferensi, terkonfirmasi ? "LUNAS (Verified)" : "PENDING");
    }

    @Override
    public String toString() {
        return getDetailPembayaran();
    }
}
