package com.pesanyuk.gui;

import com.pesanyuk.manager.OrderManager;
import com.pesanyuk.model.Transaksi;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tampilan Riwayat Transaksi: Menampilkan seluruh riwayat transaksi pesanan yang telah dibayar.
 */
public class RiwayatViewPanel extends JPanel {

    private OrderManager orderManager;
    private SearchField txtSearch;
    private JLabel lblTotalTransaksi;
    private JPanel tableBody;

    public RiwayatViewPanel(OrderManager orderManager) {
        this.orderManager = orderManager;

        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Riwayat Transaksi");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Daftar seluruh transaksi yang telah berhasil dibayar");
        lblSubtitle.setFont(UITheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(lblSubtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Card
        RoundedPanel cardPanel = new RoundedPanel(16, UITheme.CARD_BG, UITheme.BORDER_COLOR, 1);
        cardPanel.setLayout(new BorderLayout(0, 14));
        cardPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Search Bar & Counter
        JPanel cardTop = new JPanel(new BorderLayout());
        cardTop.setOpaque(false);

        txtSearch = new SearchField("Cari ID transaksi atau no meja...");
        txtSearch.setPreferredSize(new Dimension(300, 38));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });

        lblTotalTransaksi = new JLabel("0 transaksi ditemukan");
        lblTotalTransaksi.setFont(UITheme.FONT_SMALL);
        lblTotalTransaksi.setForeground(UITheme.TEXT_MUTED);

        cardTop.add(txtSearch, BorderLayout.WEST);
        cardTop.add(lblTotalTransaksi, BorderLayout.EAST);

        cardPanel.add(cardTop, BorderLayout.NORTH);

        // Header Tabel Kolom
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        JPanel tableHeader = buildTableHeader();

        tableBody = new JPanel();
        tableBody.setLayout(new BoxLayout(tableBody, BoxLayout.Y_AXIS));
        tableBody.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tableBody);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setColumnHeaderView(tableHeader);

        tableContainer.add(scroll, BorderLayout.CENTER);
        cardPanel.add(tableContainer, BorderLayout.CENTER);

        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel buildTableHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0xF9, 0xFA, 0xFB));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(10, 12, 10, 12)));

        String[] cols = {"ID TRANSAKSI", "NO MEJA", "WAKTU", "METODE", "TOTAL BAYAR", "AKSI"};
        double[] weights = {0.22, 0.12, 0.22, 0.16, 0.16, 0.12};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 4, 0, 4);

        for (int i = 0; i < cols.length; i++) {
            gbc.gridx = i;
            gbc.weightx = weights[i];
            JLabel lbl = new JLabel(cols[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(UITheme.TEXT_LIGHT);
            if (i == cols.length - 1) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
            }
            header.add(lbl, gbc);
        }

        return header;
    }

    public void refreshTable() {
        tableBody.removeAll();
        String keyword = txtSearch.getText().trim().toLowerCase();

        List<Transaksi> list = orderManager.getRiwayatTransaksi();
        int count = 0;

        double[] weights = {0.22, 0.12, 0.22, 0.16, 0.16, 0.12};

        for (Transaksi trx : list) {
            String id = trx.getIdTransaksi().toLowerCase();
            String noMeja = trx.getOrder() != null && trx.getOrder().getMeja() != null
                    ? String.valueOf(trx.getOrder().getMeja().getNomor()) : "";

            if (!keyword.isEmpty() && !id.contains(keyword) && !noMeja.contains(keyword)) {
                continue;
            }

            count++;
            tableBody.add(buildTableRow(trx, weights));
        }

        lblTotalTransaksi.setText(count + " transaksi ditemukan");
        tableBody.revalidate();
        tableBody.repaint();
    }

    private JPanel buildTableRow(Transaksi trx, double[] weights) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF3, 0xF4, 0xF6)),
                new EmptyBorder(12, 12, 12, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 4, 0, 4);

        // 1. ID Transaksi
        gbc.gridx = 0;
        gbc.weightx = weights[0];
        JLabel lblId = new JLabel(trx.getIdTransaksi());
        lblId.setFont(UITheme.FONT_BOLD);
        lblId.setForeground(UITheme.TEXT_DARK);
        row.add(lblId, gbc);

        // 2. No Meja
        gbc.gridx = 1;
        gbc.weightx = weights[1];
        String noMejaStr = trx.getOrder() != null && trx.getOrder().getMeja() != null
                ? "Meja #" + trx.getOrder().getMeja().getNomor() : "-";
        JLabel lblMeja = new JLabel(noMejaStr);
        lblMeja.setFont(UITheme.FONT_REGULAR);
        lblMeja.setForeground(UITheme.TEXT_MUTED);
        row.add(lblMeja, gbc);

        // 3. Waktu
        gbc.gridx = 2;
        gbc.weightx = weights[2];
        String timeStr = trx.getWaktu().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        JLabel lblTime = new JLabel(timeStr);
        lblTime.setFont(UITheme.FONT_REGULAR);
        lblTime.setForeground(UITheme.TEXT_MUTED);
        row.add(lblTime, gbc);

        // 4. Metode
        gbc.gridx = 3;
        gbc.weightx = weights[3];
        String metode = trx.getMetode() != null ? trx.getMetode() : "Tunai";
        boolean isQris = metode.toUpperCase().contains("QRIS");
        BadgeLabel badge = new BadgeLabel(
                metode,
                isQris ? UITheme.BADGE_MINUMAN_BG : UITheme.BADGE_MAKANAN_BG,
                isQris ? UITheme.BADGE_MINUMAN_FG : UITheme.BADGE_MAKANAN_FG
        );
        JPanel pBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pBadge.setOpaque(false);
        pBadge.add(badge);
        row.add(pBadge, gbc);

        // 5. Total Bayar
        gbc.gridx = 4;
        gbc.weightx = weights[4];
        JLabel lblTotal = new JLabel(UITheme.formatRupiah(trx.getTotal()));
        lblTotal.setFont(UITheme.FONT_BOLD);
        lblTotal.setForeground(UITheme.PRIMARY_ORANGE);
        row.add(lblTotal, gbc);

        // 6. Aksi (Lihat Struk)
        gbc.gridx = 5;
        gbc.weightx = weights[5];
        SecondaryButton btnStruk = new SecondaryButton("Struk", 8);
        btnStruk.setPreferredSize(new Dimension(65, 28));
        btnStruk.setFont(UITheme.FONT_SMALL);
        btnStruk.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            ReceiptDialog dialog = new ReceiptDialog(parent, trx);
            dialog.setVisible(true);
        });
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pBtn.setOpaque(false);
        pBtn.add(btnStruk);
        row.add(pBtn, gbc);

        return row;
    }
}
