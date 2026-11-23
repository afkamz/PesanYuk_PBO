package com.pesanyuk.gui;

import com.pesanyuk.model.OrderItem;
import com.pesanyuk.model.Transaksi;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modal dialog struk transaksi yang bersih dan rapi menyerupai kertas thermal printer restoran.
 */
public class ReceiptDialog extends JDialog {

    public ReceiptDialog(Window parent, Transaksi transaksi) {
        super(parent, "Struk Pembayaran - " + (transaksi != null ? transaksi.getIdTransaksi() : ""), ModalityType.APPLICATION_MODAL);
        setSize(400, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 14));

        initComponents(transaksi);
    }

    private void initComponents(Transaksi transaksi) {
        if (transaksi == null) return;

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Header Struk
        JLabel brand = new JLabel("PESANYUK RESTO & CAFE", SwingConstants.CENTER);
        brand.setFont(new Font("Monospaced", Font.BOLD, 16));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Jl. Raya Kampus PENS Lamongan", SwingConstants.CENTER);
        sub.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel garis1 = new JLabel("========================================", SwingConstants.CENTER);
        garis1.setFont(new Font("Monospaced", Font.PLAIN, 11));
        garis1.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(brand);
        content.add(sub);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(garis1);

        // Info Transaksi
        content.add(createReceiptInfoRow("ID Transaksi", transaksi.getIdTransaksi()));
        content.add(createReceiptInfoRow("Nomor Meja", "#" + (transaksi.getOrder() != null && transaksi.getOrder().getMeja() != null ? transaksi.getOrder().getMeja().getNomor() : "-")));
        content.add(createReceiptInfoRow("Waktu", transaksi.getWaktu().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        content.add(createReceiptInfoRow("Metode Bayar", transaksi.getMetode() != null ? transaksi.getMetode() : "-"));

        JLabel garis2 = new JLabel("----------------------------------------", SwingConstants.CENTER);
        garis2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        garis2.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(garis2);

        // Daftar Item
        if (transaksi.getOrder() != null) {
            for (OrderItem oi : transaksi.getOrder().getDaftarItem()) {
                JPanel itemRow = new JPanel(new BorderLayout());
                itemRow.setOpaque(false);

                JLabel lblItem = new JLabel(oi.getItem().getNama() + " x" + oi.getQty());
                lblItem.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JLabel lblTotal = new JLabel(UITheme.formatRupiah(oi.getSubtotal()));
                lblTotal.setFont(new Font("Monospaced", Font.PLAIN, 12));

                itemRow.add(lblItem, BorderLayout.WEST);
                itemRow.add(lblTotal, BorderLayout.EAST);
                content.add(itemRow);
                content.add(Box.createRigidArea(new Dimension(0, 4)));
            }
        }

        JLabel garis3 = new JLabel("----------------------------------------", SwingConstants.CENTER);
        garis3.setFont(new Font("Monospaced", Font.PLAIN, 11));
        garis3.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(garis3);

        // Subtotal, Pajak, Total
        if (transaksi.getOrder() != null) {
            content.add(createReceiptInfoRow("Subtotal", UITheme.formatRupiah(transaksi.getOrder().hitungSubtotal())));
            content.add(createReceiptInfoRow("Pajak (10%)", UITheme.formatRupiah(transaksi.getOrder().hitungPajak())));
            content.add(createReceiptInfoRow("TOTAL", UITheme.formatRupiah(transaksi.getTotal())));
        }

        JLabel garis4 = new JLabel("========================================", SwingConstants.CENTER);
        garis4.setFont(new Font("Monospaced", Font.PLAIN, 11));
        garis4.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(garis4);

        JLabel thankYou = new JLabel("TERIMA KASIH ATAS KUNJUNGAN ANDA", SwingConstants.CENTER);
        thankYou.setFont(new Font("Monospaced", Font.BOLD, 11));
        thankYou.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(thankYou);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Tombol Tutup di Bawah
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 14, 0));

        PrimaryButton btnTutup = new PrimaryButton("Tutup Struk", 12);
        btnTutup.setPreferredSize(new Dimension(200, 36));
        btnTutup.addActionListener(e -> dispose());
        footer.add(btnTutup);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createReceiptInfoRow(String key, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lKey = new JLabel(key);
        lKey.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JLabel lVal = new JLabel(value);
        lVal.setFont(new Font("Monospaced", Font.BOLD, 11));
        p.add(lKey, BorderLayout.WEST);
        p.add(lVal, BorderLayout.EAST);
        return p;
    }
}
