package com.pesanyuk.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class Transaksi merepresentasikan transaksi yang telah berhasil dibayar.
 * Menyimpan rekap pesanan, waktu transaksi, metode pembayaran, dan pencetakan struk.
 */
public class Transaksi {
    private String idTransaksi;
    private Order order;
    private double total;
    private String metode;
    private LocalDateTime waktu;
    private String detailPembayaran;

    public Transaksi(String idTransaksi, Order order, double total, String metode, String detailPembayaran) {
        this.idTransaksi = idTransaksi;
        this.order = order;
        this.total = total;
        this.metode = metode;
        this.waktu = LocalDateTime.now();
        this.detailPembayaran = detailPembayaran;
    }

    public Transaksi(String idTransaksi, Order order, double total, String metode, LocalDateTime waktu, String detailPembayaran) {
        this.idTransaksi = idTransaksi;
        this.order = order;
        this.total = total;
        this.metode = metode;
        this.waktu = waktu;
        this.detailPembayaran = detailPembayaran;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public Order getOrder() {
        return order;
    }

    public double getTotal() {
        return total;
    }

    public String getMetode() {
        return metode;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    public String getDetailPembayaran() {
        return detailPembayaran;
    }

    public String getWaktuFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return waktu.format(formatter);
    }

    /**
     * Menghasilkan teks ringkasan singkat transaksi.
     */
    public String getRingkasan() {
        return String.format("[%s] Transaksi: %s | Meja: %d | Total: Rp %,.0f | Metode: %s",
                getWaktuFormat(), idTransaksi, order.getMeja().getNomor(), total, metode);
    }

    /**
     * Menghasilkan format struk belanja restoran lengkap yang siap dicetak ke layar/file.
     */
    public String generateStruk() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("               PESANYUK POS             \n");
        sb.append("       Restoran Cepat Saji & Cafe       \n");
        sb.append("========================================\n");
        sb.append(String.format("No. Transaksi : %s\n", idTransaksi));
        sb.append(String.format("Waktu         : %s\n", getWaktuFormat()));
        sb.append(String.format("No. Meja      : #%d (%d Tamu)\n", order.getMeja().getNomor(), order.getJumlahTamu()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-22s %3s %13s\n", "Item", "Qty", "Subtotal"));
        sb.append("----------------------------------------\n");

        for (OrderItem oi : order.getDaftarItem()) {
            MenuItem item = oi.getItem();
            String namaItem = item.getNama();
            if (item instanceof Minuman) {
                namaItem += " (" + item.getAtributKhusus() + ")";
            } else if (item instanceof Makanan) {
                namaItem += " [" + item.getAtributKhusus() + "]";
            }
            if (namaItem.length() > 22) {
                namaItem = namaItem.substring(0, 19) + "...";
            }
            sb.append(String.format("%-22s %3d  Rp %,10.0f\n", namaItem, oi.getQty(), oi.getSubtotal()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("%-26s Rp %,10.0f\n", "Subtotal", order.hitungSubtotal()));
        sb.append(String.format("%-26s Rp %,10.0f\n", "Pajak Restoran (10%)", order.hitungPajak()));
        sb.append(String.format("%-26s Rp %,10.0f\n", "TOTAL BAYAR", total));
        sb.append("----------------------------------------\n");
        sb.append(String.format("Pembayaran    : %s\n", detailPembayaran));
        sb.append("========================================\n");
        sb.append("     Terima Kasih Atas Kunjungan Anda!  \n");
        sb.append("========================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return getRingkasan();
    }
}
