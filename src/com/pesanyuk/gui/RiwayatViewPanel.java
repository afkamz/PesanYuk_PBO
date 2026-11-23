package com.pesanyuk.gui;

import com.pesanyuk.manager.OrderManager;
import com.pesanyuk.model.Transaksi;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tampilan Riwayat Transaksi persis seperti Mockup Figma Gambar/kasir/riwayat.png & Gambar/admin/riwayat (2).png:
 * - Banner gelap atas bertuliskan "RIWAYAT TRANSAKSI"
 * - 3 Card Ringkasan Statistik: "Transaksi hari ini", "Pendapatan hari ini", "Rata - rata transaksi" + Date Pill
 * - Card Tabel Riwayat: Search Bar + Kolom: ID Transaksi, Waktu, Meja, Total, Metode, Aksi ("Lihat Struk >")
 */
public class RiwayatViewPanel extends JPanel {

    private OrderManager orderManager;
    private SearchField txtSearch;
    private JPanel tableBody;

    // Stat Labels
    private JLabel lblTotalTrxCount;
    private JLabel lblTotalRevenue;
    private JLabel lblAvgRevenue;

    public RiwayatViewPanel(OrderManager orderManager) {
        this.orderManager = orderManager;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // --- 1. BANNER GELAP ATAS "RIWAYAT TRANSAKSI" ---
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BANNER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(0x28, 0x2E, 0x37));
                for (int i = 0; i < getWidth(); i += 90) {
                    g2.fillOval(i - 20, -30, 80, 80);
                }
                g2.dispose();
            }
        };
        bannerPanel.setLayout(new GridBagLayout());
        bannerPanel.setPreferredSize(new Dimension(0, 72));

        JLabel lblBannerTitle = new JLabel("RIWAYAT TRANSAKSI");
        lblBannerTitle.setFont(UITheme.FONT_BANNER);
        lblBannerTitle.setForeground(Color.WHITE);
        bannerPanel.add(lblBannerTitle);

        add(bannerPanel, BorderLayout.NORTH);

        // --- 2. KONTEN UTAMA ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(16, 24, 20, 24));

        // Group Atas: Ringkasan Transaksi (Title + 3 Stat Cards + Date Pill)
        JPanel topGroup = new JPanel(new BorderLayout(0, 12));
        topGroup.setOpaque(false);

        // Header Bar: "Ringkasan transaksi" & Date Pill
        JPanel titleAndDate = new JPanel(new BorderLayout());
        titleAndDate.setOpaque(false);

        JLabel lblSectionTitle = new JLabel("Ringkasan transaksi");
        lblSectionTitle.setFont(UITheme.FONT_TITLE);
        lblSectionTitle.setForeground(UITheme.TEXT_DARK);

        String tglSekarang = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID")));
        RoundedPanel datePill = new RoundedPanel(12, Color.WHITE, UITheme.BORDER_DARK, 1);
        datePill.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));

        JLabel calIcon = new JLabel(VectorIcons.getCalendarIcon(14, UITheme.PRIMARY_ORANGE));
        JLabel lblDate = new JLabel(tglSekarang);
        lblDate.setFont(UITheme.FONT_SMALL);
        lblDate.setForeground(UITheme.PRIMARY_ORANGE);

        datePill.add(calIcon);
        datePill.add(lblDate);

        titleAndDate.add(lblSectionTitle, BorderLayout.WEST);
        titleAndDate.add(datePill, BorderLayout.EAST);

        topGroup.add(titleAndDate, BorderLayout.NORTH);

        // 3 Kartu Statistik
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        statsPanel.setOpaque(false);

        lblTotalTrxCount = new JLabel("0");
        lblTotalRevenue = new JLabel("Rp. 0");
        lblAvgRevenue = new JLabel("Rp. 0");

        statsPanel.add(buildStatCard("Transaksi hari ini", lblTotalTrxCount));
        statsPanel.add(buildStatCard("Pendapatan hari ini", lblTotalRevenue));
        statsPanel.add(buildStatCard("Rata - rata transaksi", lblAvgRevenue));

        topGroup.add(statsPanel, BorderLayout.SOUTH);
        mainContent.add(topGroup, BorderLayout.NORTH);

        // --- 3. MAIN CARD TABEL RIWAYAT ---
        RoundedPanel cardTable = new RoundedPanel(20, Color.WHITE, UITheme.BORDER_COLOR, 1);
        cardTable.setLayout(new BorderLayout(0, 14));
        cardTable.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Search Bar
        txtSearch = new SearchField("Cari id transaksi atau meja...");
        txtSearch.setPreferredSize(new Dimension(320, 38));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });
        cardTable.add(txtSearch, BorderLayout.NORTH);

        // Table Header & Body
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
        cardTable.add(tableContainer, BorderLayout.CENTER);

        mainContent.add(cardTable, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel buildStatCard(String title, JLabel valueLabel) {
        RoundedPanel card = new RoundedPanel(18, Color.WHITE, UITheme.BORDER_COLOR, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        card.setPreferredSize(new Dimension(0, 90));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_REGULAR);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(UITheme.TEXT_DARK);

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(valueLabel);

        return card;
    }

    private JPanel buildTableHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0xF9, 0xFA, 0xFB));
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(8, 14, 8, 14)));

        String[] cols = {"Id Transaksi", "Waktu", "Meja", "Total", "Metode", "Aksi"};
        double[] weights = {0.20, 0.24, 0.14, 0.18, 0.12, 0.12};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 4, 0, 4);

        for (int i = 0; i < cols.length; i++) {
            gbc.gridx = i;
            gbc.weightx = weights[i];
            JLabel lbl = new JLabel(cols[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(UITheme.TEXT_MUTED);
            if (i == cols.length - 1) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
            }
            header.add(lbl, gbc);
        }

        return header;
    }

    public void refreshTable() {
        tableBody.removeAll();
        String keyword = txtSearch.getText().trim().toLowerCase();

        List<Transaksi> list = orderManager.getRiwayatTransaksi();
        double totalRev = 0;
        int count = 0;

        double[] weights = {0.20, 0.24, 0.14, 0.18, 0.12, 0.12};

        for (Transaksi trx : list) {
            totalRev += trx.getTotal();
            count++;

            String id = trx.getIdTransaksi().toLowerCase();
            String noMeja = trx.getOrder() != null && trx.getOrder().getMeja() != null
                    ? String.valueOf(trx.getOrder().getMeja().getNomor()) : "";

            if (!keyword.isEmpty() && !id.contains(keyword) && !noMeja.contains(keyword)) {
                continue;
            }

            tableBody.add(buildTableRow(trx, weights));
        }

        // Update Ringkasan Stat
        lblTotalTrxCount.setText(String.format("%02d", count));
        lblTotalRevenue.setText(UITheme.formatRupiah(totalRev));
        double avg = count > 0 ? (totalRev / count) : 0;
        lblAvgRevenue.setText(UITheme.formatRupiah(avg));

        tableBody.revalidate();
        tableBody.repaint();
    }

    private JPanel buildTableRow(Transaksi trx, double[] weights) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF3, 0xF4, 0xF6)),
                new EmptyBorder(12, 14, 12, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 4, 0, 4);

        // 1. Id Transaksi (Pill Badge Cream Lembut)
        gbc.gridx = 0;
        gbc.weightx = weights[0];
        BadgeLabel idBadge = new BadgeLabel(trx.getIdTransaksi(), new Color(0xFE, 0xEB, 0xDF), UITheme.TEXT_DARK);
        JPanel pId = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pId.setOpaque(false);
        pId.add(idBadge);
        row.add(pId, gbc);

        // 2. Waktu
        gbc.gridx = 1;
        gbc.weightx = weights[1];
        String timeStr = trx.getWaktu().format(DateTimeFormatter.ofPattern("d MMM yyyy, HH.mm"));
        JLabel lblTime = new JLabel(timeStr);
        lblTime.setFont(UITheme.FONT_REGULAR);
        lblTime.setForeground(UITheme.TEXT_DARK);
        row.add(lblTime, gbc);

        // 3. Meja
        gbc.gridx = 2;
        gbc.weightx = weights[2];
        String noMejaStr = trx.getOrder() != null && trx.getOrder().getMeja() != null
                ? "Meja " + trx.getOrder().getMeja().getNomor() : "-";
        JLabel lblMeja = new JLabel(noMejaStr);
        lblMeja.setFont(UITheme.FONT_REGULAR);
        lblMeja.setForeground(UITheme.TEXT_DARK);
        row.add(lblMeja, gbc);

        // 4. Total
        gbc.gridx = 3;
        gbc.weightx = weights[3];
        JLabel lblTotal = new JLabel(UITheme.formatRupiah(trx.getTotal()));
        lblTotal.setFont(UITheme.FONT_BOLD);
        lblTotal.setForeground(UITheme.TEXT_DARK);
        row.add(lblTotal, gbc);

        // 5. Metode (QRIS biru / Tunai hijau)
        gbc.gridx = 4;
        gbc.weightx = weights[4];
        String metode = trx.getMetode() != null ? trx.getMetode() : "Tunai";
        boolean isQris = metode.toUpperCase().contains("QRIS");
        BadgeLabel badgeMetode = new BadgeLabel(
                metode,
                isQris ? new Color(0xDF, 0xF0, 0xFD) : new Color(0xD8, 0xF6, 0xE4),
                isQris ? new Color(0x02, 0x84, 0xC7) : new Color(0x15, 0x98, 0x48)
        );
        JPanel pBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pBadge.setOpaque(false);
        pBadge.add(badgeMetode);
        row.add(pBadge, gbc);

        // 6. Aksi ("Lihat Struk >" Teks Oranye)
        gbc.gridx = 5;
        gbc.weightx = weights[5];
        JLabel btnStruk = new JLabel("Lihat Struk  >", SwingConstants.CENTER);
        btnStruk.setFont(UITheme.FONT_BOLD);
        btnStruk.setForeground(UITheme.PRIMARY_ORANGE);
        btnStruk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStruk.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Window parent = SwingUtilities.getWindowAncestor(RiwayatViewPanel.this);
                ReceiptDialog dialog = new ReceiptDialog(parent, trx);
                dialog.setVisible(true);
            }
        });
        row.add(btnStruk, gbc);

        return row;
    }
}
