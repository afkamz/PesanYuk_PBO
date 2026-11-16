package com.pesanyuk.manager;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.exception.TidakAdaMejaException;
import com.pesanyuk.exception.UangKurangException;
import com.pesanyuk.model.Meja;
import com.pesanyuk.model.Order;
import com.pesanyuk.model.Pembayaran;
import com.pesanyuk.model.Transaksi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manager untuk mengelola alur pemesanan (Order) dan pemrosesan pembayaran (Transaksi).
 * Mengintegrasikan MejaManager, Order, Pembayaran secara polimorfik.
 */
public class OrderManager {
    private ArrayList<Transaksi> riwayatTransaksi;
    private MejaManager mejaManager;
    private int orderCounter;
    private int transaksiCounter;

    public OrderManager(MejaManager mejaManager) {
        this.riwayatTransaksi = new ArrayList<>();
        this.mejaManager = mejaManager;
        this.orderCounter = 1;
        this.transaksiCounter = 1;
    }

    /**
     * Membuat Order baru dengan alokasi meja otomatis via MejaManager.
     * @param jumlahTamu Jumlah tamu yang datang
     * @return Objek Order dengan meja yang telah dialokasikan
     * @throws TidakAdaMejaException Jika tidak ada meja yang cukup
     * @throws InputTidakValidException Jika jumlah tamu tidak valid
     */
    public Order buatOrder(int jumlahTamu) throws TidakAdaMejaException, InputTidakValidException {
        if (jumlahTamu <= 0) {
            throw new InputTidakValidException("Jumlah tamu minimal 1 orang.");
        }
        // Alokasikan meja secara otomatis (best-fit)
        Meja meja = mejaManager.alokasiMeja(jumlahTamu);

        String idOrder = String.format("ORD-%03d", orderCounter++);
        return new Order(idOrder, jumlahTamu, meja);
    }

    /**
     * Membatalkan order sebelum dibayar dan mengembalikan status meja menjadi kosong.
     */
    public void batalkanOrder(Order order) {
        if (order != null && order.getMeja() != null) {
            mejaManager.kosongkanMeja(order.getMeja().getNomor());
        }
    }

    /**
     * Memproses pembayaran secara polimorfik menggunakan interface Pembayaran.
     * Setelah pembayaran berhasil:
     * 1. Menghasilkan objek Transaksi
     * 2. Menandai meja kembali kosong
     * 3. Menyimpan transaksi ke daftar riwayat
     *
     * @param order Pesanan yang akan dibayar
     * @param pembayaran Objek pembayaran (PembayaranTunai / PembayaranQRIS)
     * @return Objek Transaksi selesai
     * @throws UangKurangException Jika uang tunai kurang dari total
     * @throws InputTidakValidException Jika order kosong atau parameter tidak valid
     */
    public Transaksi prosesBayar(Order order, Pembayaran pembayaran)
            throws UangKurangException, InputTidakValidException {

        if (order == null) {
            throw new InputTidakValidException("Order tidak boleh null.");
        }
        if (order.getDaftarItem().isEmpty()) {
            throw new InputTidakValidException("Pesanan belum memiliki item menu. Tambahkan menu terlebih dahulu!");
        }
        if (pembayaran == null) {
            throw new InputTidakValidException("Metode pembayaran harus ditentukan.");
        }

        double total = order.hitungTotal();

        // Pemanggilan polimorfik: bayar() dieksekusi sesuai instance (Tunai atau QRIS)
        boolean sukses = pembayaran.bayar(total);

        if (!sukses) {
            throw new InputTidakValidException("Pembayaran gagal diproses.");
        }

        // Generate ID transaksi unik berbasis tanggal dan nomor urut
        String tanggalKode = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String idTransaksi = String.format("TRX-%s-%03d", tanggalKode, transaksiCounter++);

        Transaksi transaksi = new Transaksi(
                idTransaksi,
                order,
                total,
                pembayaran.getNamaMetode(),
                pembayaran.getDetailPembayaran()
        );

        // Tambah ke riwayat transaksi
        riwayatTransaksi.add(transaksi);

        // Kosongkan meja restoran kembali
        mejaManager.kosongkanMeja(order.getMeja().getNomor());

        return transaksi;
    }

    public List<Transaksi> getRiwayatTransaksi() {
        return Collections.unmodifiableList(riwayatTransaksi);
    }

    public Transaksi cariTransaksi(String idTransaksi) {
        if (idTransaksi == null) return null;
        for (Transaksi t : riwayatTransaksi) {
            if (t.getIdTransaksi().equalsIgnoreCase(idTransaksi.trim())) {
                return t;
            }
        }
        return null;
    }
}
