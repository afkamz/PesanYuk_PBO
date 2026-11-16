package com.pesanyuk.exception;

/**
 * Exception yang dilempar ketika item menu yang dicari, diubah,
 * atau dihapus tidak ditemukan dalam sistem.
 */
public class MenuNotFoundException extends Exception {
    public MenuNotFoundException(String message) {
        super(message);
    }
}
