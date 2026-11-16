package com.pesanyuk.model;

import com.pesanyuk.exception.InputTidakValidException;

/**
 * Class Meja merepresentasikan meja makan di restoran.
 * Menerapkan prinsip Encapsulation.
 */
public class Meja {
    private int nomor;
    private int kapasitas;
    private boolean terisi;

    public Meja(int nomor, int kapasitas) throws InputTidakValidException {
        if (nomor <= 0) {
            throw new InputTidakValidException("Nomor meja harus bilangan bulat positif (> 0).");
        }
        if (kapasitas <= 0) {
            throw new InputTidakValidException("Kapasitas meja harus minimal 1 orang.");
        }
        this.nomor = nomor;
        this.kapasitas = kapasitas;
        this.terisi = false;
    }

    public Meja(int nomor, int kapasitas, boolean terisi) throws InputTidakValidException {
        this(nomor, kapasitas);
        this.terisi = terisi;
    }

    public int getNomor() {
        return nomor;
    }

    public void setNomor(int nomor) throws InputTidakValidException {
        if (nomor <= 0) {
            throw new InputTidakValidException("Nomor meja harus bilangan bulat positif (> 0).");
        }
        this.nomor = nomor;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(int kapasitas) throws InputTidakValidException {
        if (kapasitas <= 0) {
            throw new InputTidakValidException("Kapasitas meja harus minimal 1 orang.");
        }
        this.kapasitas = kapasitas;
    }

    public boolean isTerisi() {
        return terisi;
    }

    public void setTerisi(boolean terisi) {
        this.terisi = terisi;
    }

    @Override
    public String toString() {
        return String.format("Meja #%d (Kapasitas: %d orang) [%s]",
                nomor, kapasitas, terisi ? "TERISI" : "KOSONG");
    }
}
