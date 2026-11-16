package com.pesanyuk.model;

import com.pesanyuk.exception.UangKurangException;

/**
 * Interface Pembayaran sebagai kontrak untuk seluruh metode pembayaran di PesanYuk.
 * Menerapkan prinsip Abstraction dan Polymorphism.
 */
public interface Pembayaran {
    /**
     * Memproses pembayaran sebesar total tagihan.
     * @param total jumlah total yang harus dibayar
     * @return true jika pembayaran berhasil
     * @throws UangKurangException jika uang yang diserahkan kurang dari total
     */
    boolean bayar(double total) throws UangKurangException;

    /**
     * Mengembalikan nama metode pembayaran (misal "Tunai", "QRIS").
     */
    String getNamaMetode();

    /**
     * Informasi rincian pembayaran untuk dicantumkan pada struk / riwayat.
     */
    String getDetailPembayaran();
}
