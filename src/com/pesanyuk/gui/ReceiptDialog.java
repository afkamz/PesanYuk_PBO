package com.pesanyuk.gui;

import com.pesanyuk.model.OrderItem;
import com.pesanyuk.model.Transaksi;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modal dialog struk transaksi persis seperti Mockup Figma Gambar/kasir/nota.png:
 * - Header "Struk Pembayaran" + subtitle "Transaksi berhasil diselesaikan" + tombol close X
 * - Kertas struk putih dengan outline halus di tengah:
 *   - Logo PesanYuk (Cloche kuning emas + teks)
 *   - Alamat restoran: "Jl. Melati No. 12, Lamongan / (021) 222-4321"
 *   - Detail TRX, Waktu, Meja, Kasir
 *   - Daftar menu, Sub Total, PPN 10%, Total oranye, Metode (QRIS/Tunai)
 *   - Catatan: "Terima Kasih! Semoga harimu menyenangkan"
 * - Tombol di bawah: [ Cetak ] (outline dengan icon printer) & [ Tutup ] (oranye)
 */
public class ReceiptDialog extends JDialog {

    public ReceiptDialog(Window parent, Transaksi transaksi) {
        super(parent, "Struk Pembayaran", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setSize(460, 640);
        setLocationRelativeTo(parent);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));

        initComponents(transaksi);
    }

    private void initComponents(Transaksi transaksi) {
        if (transaksi == null) return;

        RoundedPanel mainCard = new RoundedPanel(24, Color.WHITE, UITheme.BORDER_COLOR, 1);
        mainCard.setLayout(new BorderLayout(0, 14));
        mainCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        // --- 1. HEADER (Judul + Subtitle + Tombol X) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Struk Pembayaran");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Transaksi berhasil diselesaikan");
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(lblTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 2)));
        titleBox.add(lblSub);

        JLabel btnClose = new JLabel("✕", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF3, 0xF4, 0xF6));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setPreferredSize(new Dimension(30, 30));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setForeground(UITheme.TEXT_MUTED);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(btnClose, BorderLayout.EAST);

        mainCard.add(headerPanel, BorderLayout.NORTH);

        // --- 2. KERTAS STRUK (Card Tengah Berbingkai Halus) ---
        RoundedPanel paperReceipt = new RoundedPanel(12, Color.WHITE, UITheme.BORDER_DARK, 1);
        paperReceipt.setLayout(new BoxLayout(paperReceipt, BoxLayout.Y_AXIS));
        paperReceipt.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Logo Resto di Nota
        JPanel brandBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        brandBox.setOpaque(false);

        JLabel iconLogo = new JLabel(VectorIcons.getClocheIcon(22, UITheme.PRIMARY_ORANGE));
        JLabel nameLogo = new JLabel("PesanYuk");
        nameLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLogo.setForeground(UITheme.TEXT_DARK);

        brandBox.add(iconLogo);
        brandBox.add(nameLogo);

        JLabel lblAlamat = new JLabel("Jl. Melati No. 12, Lamongan", SwingConstants.CENTER);
        lblAlamat.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblAlamat.setForeground(UITheme.TEXT_MUTED);
        lblAlamat.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTelp = new JLabel("(021) 222-4321", SwingConstants.CENTER);
        lblTelp.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTelp.setForeground(UITheme.TEXT_MUTED);
        lblTelp.setAlignmentX(Component.CENTER_ALIGNMENT);

        paperReceipt.add(brandBox);
        paperReceipt.add(Box.createRigidArea(new Dimension(0, 4)));
        paperReceipt.add(lblAlamat);
        paperReceipt.add(lblTelp);
        paperReceipt.add(Box.createRigidArea(new Dimension(0, 12)));

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(UITheme.BORDER_DARK);
        paperReceipt.add(sep1);
        paperReceipt.add(Box.createRigidArea(new Dimension(0, 8)));

        // Info TRX & Meja
        String timeStr = transaksi.getWaktu().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH.mm"));
        String noMejaStr = transaksi.getOrder() != null && transaksi.getOrder().getMeja() != null
                ? "Meja " + transaksi.getOrder().getMeja().getNomor() : "Meja -";

        paperReceipt.add(createReceiptTwoColRow(transaksi.getIdTransaksi(), timeStr));
        paperReceipt.add(Box.createRigidArea(new Dimension(0, 4)));
        paperReceipt.add(createReceiptTwoColRow(noMejaStr, "Kasir Fulan"));
        paperReceipt.add(Box.createRigidArea(new Dimension(0, 8)));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(UITheme.BORDER_DARK);
        paperReceipt.add(sep2);
        paperReceipt.add(Box.createRigidArea(new Dimension(0, 8)));

        // Item List Pesanan
        if (transaksi.getOrder() != null) {
            for (OrderItem oi : transaksi.getOrder().getDaftarItem()) {
                JPanel itemRow = new JPanel(new BorderLayout());
                itemRow.setOpaque(false);

                JPanel leftDesc = new JPanel();
                leftDesc.setLayout(new BoxLayout(leftDesc, BoxLayout.Y_AXIS));
                leftDesc.setOpaque(false);

                JLabel lblItemNama = new JLabel(oi.getItem().getNama());
                lblItemNama.setFont(UITheme.FONT_BOLD);
                lblItemNama.setForeground(UITheme.TEXT_DARK);

                JLabel lblItemQty = new JLabel(oi.getQty() + "x " + UITheme.formatRupiah(oi.getItem().hitungHarga(1)));
                lblItemQty.setFont(UITheme.FONT_SMALL);
                lblItemQty.setForeground(UITheme.TEXT_MUTED);

                leftDesc.add(lblItemNama);
                leftDesc.add(lblItemQty);

                JLabel lblItemSub = new JLabel(UITheme.formatRupiah(oi.getSubtotal()));
                lblItemSub.setFont(UITheme.FONT_BOLD);
                lblItemSub.setForeground(UITheme.TEXT_DARK);

                itemRow.add(leftDesc, BorderLayout.WEST);
                itemRow.add(lblItemSub, BorderLayout.EAST);

                paperReceipt.add(itemRow);
                paperReceipt.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }

        JSeparator sep3 = new JSeparator();
        sep3.setForeground(UITheme.BORDER_DARK);
        paperReceipt.add(sep3);
        paperReceipt.add(Box.createRigidArea(new Dimension(0, 8)));

        // Sub Total, PPN 10%, Total, Metode
        if (transaksi.getOrder() != null) {
            paperReceipt.add(createReceiptTwoColRow("Sub Total", UITheme.formatRupiah(transaksi.getOrder().hitungSubtotal())));
            paperReceipt.add(Box.createRigidArea(new Dimension(0, 4)));
            paperReceipt.add(createReceiptTwoColRow("PPN 10%", UITheme.formatRupiah(transaksi.getOrder().hitungPajak())));
            paperReceipt.add(Box.createRigidArea(new Dimension(0, 6)));

            JPanel totalRow = new JPanel(new BorderLayout());
            totalRow.setOpaque(false);
            JLabel lblTotTitle = new JLabel("Total");
            lblTotTitle.setFont(UITheme.FONT_BOLD);
            lblTotTitle.setForeground(UITheme.PRIMARY_ORANGE);
            JLabel lblTotVal = new JLabel(UITheme.formatRupiah(transaksi.getTotal()));
            lblTotVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblTotVal.setForeground(UITheme.PRIMARY_ORANGE);
            totalRow.add(lblTotTitle, BorderLayout.WEST);
            totalRow.add(lblTotVal, BorderLayout.EAST);
            paperReceipt.add(totalRow);

            paperReceipt.add(Box.createRigidArea(new Dimension(0, 6)));
            paperReceipt.add(createReceiptTwoColRow("Metode", transaksi.getMetode() != null ? transaksi.getMetode() : "Tunai"));
        }

        JSeparator sep4 = new JSeparator();
        sep4.setForeground(UITheme.BORDER_DARK);
        paperReceipt.add(sep4);
        paperReceipt.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel lblThanks = new JLabel("Terima Kasih!", SwingConstants.CENTER);
        lblThanks.setFont(UITheme.FONT_BOLD);
        lblThanks.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblWish = new JLabel("Semoga harimu menyenangkan", SwingConstants.CENTER);
        lblWish.setFont(UITheme.FONT_SMALL);
        lblWish.setForeground(UITheme.TEXT_MUTED);
        lblWish.setAlignmentX(Component.CENTER_ALIGNMENT);

        paperReceipt.add(lblThanks);
        paperReceipt.add(lblWish);

        mainCard.add(paperReceipt, BorderLayout.CENTER);

        // --- 3. FOOTER TOMBOL (Cetak & Tutup) ---
        JPanel footerBtnBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        footerBtnBar.setOpaque(false);

        SecondaryButton btnCetak = new SecondaryButton("Cetak", 12);
        btnCetak.setIcon(VectorIcons.getPrintIcon(14, UITheme.TEXT_DARK));
        btnCetak.setIconTextGap(6);
        btnCetak.setPreferredSize(new Dimension(110, 38));
        btnCetak.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Perintah cetak terkirim ke printer kasir!", "Cetak Struk", JOptionPane.INFORMATION_MESSAGE);
        });

        PrimaryButton btnTutup = new PrimaryButton("Tutup", 12);
        btnTutup.setPreferredSize(new Dimension(110, 38));
        btnTutup.addActionListener(e -> dispose());

        footerBtnBar.add(btnCetak);
        footerBtnBar.add(btnTutup);

        mainCard.add(footerBtnBar, BorderLayout.SOUTH);

        setContentPane(mainCard);
    }

    private JPanel createReceiptTwoColRow(String left, String right) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lLeft = new JLabel(left);
        lLeft.setFont(UITheme.FONT_SMALL);
        lLeft.setForeground(UITheme.TEXT_MUTED);

        JLabel lRight = new JLabel(right);
        lRight.setFont(UITheme.FONT_BOLD);
        lRight.setForeground(UITheme.TEXT_DARK);

        p.add(lLeft, BorderLayout.WEST);
        p.add(lRight, BorderLayout.EAST);
        return p;
    }
}
