package com.pesanyuk.exception;

/**
 * Exception yang dilempar ketika uang tunai yang dibayarkan
 * oleh pelanggan kurang dari total tagihan transaksi.
 */
public class UangKurangException extends Exception {
    private final double kekurangan;

    public UangKurangException(String message, double kekurangan) {
        super(message);
        this.kekurangan = kekurangan;
    }

    public UangKurangException(String message) {
        this(message, 0.0);
    }

    public double getKekurangan() {
        return kekurangan;
    }
}
