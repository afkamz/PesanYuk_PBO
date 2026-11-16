package com.pesanyuk.exception;

/**
 * Exception yang dilempar ketika data masukan pengguna tidak valid
 * (misalnya nilai negatif, teks kosong, atau format di luar aturan bisnis).
 */
public class InputTidakValidException extends Exception {
    public InputTidakValidException(String message) {
        super(message);
    }
}
