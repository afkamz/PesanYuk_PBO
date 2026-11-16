package com.pesanyuk.exception;

/**
 * Exception yang dilempar ketika tidak ada meja kosong
 * yang kapasitasnya mencukupi untuk jumlah tamu yang datang.
 */
public class TidakAdaMejaException extends Exception {
    public TidakAdaMejaException(String message) {
        super(message);
    }
}
