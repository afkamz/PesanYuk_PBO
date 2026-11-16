package com.pesanyuk.model;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.exception.UangKurangException;

/**
 * Implementasi Pembayaran dengan uang Tunai (Cash).
 * Menerapkan prinsip Encapsulation dan Polymorphism.
 */
public class PembayaranTunai implements Pembayaran {
    private double uangDiterima;
    private double kembalian;

    public PembayaranTunai(double uangDiterima) throws InputTidakValidException {
        if (uangDiterima < 0) {
            throw new InputTidakValidException("Uang yang diterima tidak boleh bernilai negatif.");
        }
        this.uangDiterima = uangDiterima;
        this.kembalian = 0.0;
    }

    public double getUangDiterima() {
        return uangDiterima;
    }

    public void setUangDiterima(double uangDiterima) throws InputTidakValidException {
        if (uangDiterima < 0) {
            throw new InputTidakValidException("Uang yang diterima tidak boleh bernilai negatif.");
        }
        this.uangDiterima = uangDiterima;
    }

    public double getKembalian() {
        return kembalian;
    }

    /**
     * Menghitung nilai kembalian uang tunai.
     */
    public double hitungKembalian(double total) {
        return Math.max(0.0, uangDiterima - total);
    }

    @Override
    public boolean bayar(double total) throws UangKurangException {
        if (uangDiterima < total) {
            double kurang = total - uangDiterima;
            throw new UangKurangException(
                    String.format("Uang tunai tidak cukup! Kurang: Rp %,.0f (Diterima: Rp %,.0f, Total: Rp %,.0f)",
                            kurang, uangDiterima, total),
                    kurang
            );
        }
        this.kembalian = uangDiterima - total;
        return true;
    }

    @Override
    public String getNamaMetode() {
        return "Tunai";
    }

    @Override
    public String getDetailPembayaran() {
        return String.format("Metode: Tunai | Diterima: Rp %,.0f | Kembalian: Rp %,.0f",
                uangDiterima, kembalian);
    }

    @Override
    public String toString() {
        return getDetailPembayaran();
    }
}
