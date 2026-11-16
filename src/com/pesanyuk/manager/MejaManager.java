package com.pesanyuk.manager;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.exception.TidakAdaMejaException;
import com.pesanyuk.model.Meja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Manager untuk mengelola daftar meja restoran dan alokasi meja otomatis.
 * Menerapkan algoritma Best-Fit Allocation berdasarkan jumlah tamu.
 */
public class MejaManager {
    private ArrayList<Meja> daftarMeja;

    public MejaManager() {
        this.daftarMeja = new ArrayList<>();
    }

    public void tambahMeja(Meja meja) throws InputTidakValidException {
        if (meja == null) {
            throw new InputTidakValidException("Objek meja tidak boleh null.");
        }
        // Cek duplikasi nomor meja
        for (Meja m : daftarMeja) {
            if (m.getNomor() == meja.getNomor()) {
                throw new InputTidakValidException("Meja dengan nomor #" + meja.getNomor() + " sudah ada.");
            }
        }
        daftarMeja.add(meja);
    }

    public List<Meja> getDaftarMeja() {
        return Collections.unmodifiableList(daftarMeja);
    }

    public Meja cariMeja(int nomor) {
        for (Meja m : daftarMeja) {
            if (m.getNomor() == nomor) {
                return m;
            }
        }
        return null;
    }

    /**
     * Alokasi meja otomatis (Best-Fit):
     * Mencari meja kosong dengan kapasitas >= jumlahTamu,
     * lalu memilih meja dengan kapasitas paling kecil yang mencukupi
     * guna memaksimalkan efisiensi kapasitas ruangan restoran.
     *
     * @param jumlahTamu jumlah orang tamu yang memesan
     * @return Meja yang dialokasikan (status diubah menjadi terisi)
     * @throws TidakAdaMejaException jika tidak tersedia meja kosong yang cukup
     */
    public Meja alokasiMeja(int jumlahTamu) throws TidakAdaMejaException {
        if (jumlahTamu <= 0) {
            throw new IllegalArgumentException("Jumlah tamu minimal 1 orang.");
        }

        Meja mejaTerpilih = null;
        int kapasitasTerkecil = Integer.MAX_VALUE;

        for (Meja m : daftarMeja) {
            if (!m.isTerisi() && m.getKapasitas() >= jumlahTamu) {
                if (m.getKapasitas() < kapasitasTerkecil) {
                    kapasitasTerkecil = m.getKapasitas();
                    mejaTerpilih = m;
                }
            }
        }

        if (mejaTerpilih == null) {
            throw new TidakAdaMejaException(
                    String.format("Maaf, tidak tersedia meja kosong untuk %d orang. Semua meja yang sesuai sedang penuh.",
                            jumlahTamu));
        }

        mejaTerpilih.setTerisi(true);
        return mejaTerpilih;
    }

    /**
     * Mengosongkan status meja kembali setelah pembayaran selesai.
     */
    public void kosongkanMeja(int nomor) {
        Meja m = cariMeja(nomor);
        if (m != null) {
            m.setTerisi(false);
        }
    }

    /**
     * Memuat daftar meja awal standar jika belum ada.
     */
    public void inisialisasiMejaDefault() {
        daftarMeja.clear();
        try {
            tambahMeja(new Meja(1, 2));
            tambahMeja(new Meja(2, 2));
            tambahMeja(new Meja(3, 4));
            tambahMeja(new Meja(4, 4));
            tambahMeja(new Meja(5, 4));
            tambahMeja(new Meja(6, 6));
            tambahMeja(new Meja(7, 8));
        } catch (InputTidakValidException e) {
            // Unreachable untuk nilai default
        }
    }
}
