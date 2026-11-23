package com.pesanyuk.gui;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.exception.TidakAdaMejaException;
import com.pesanyuk.exception.UangKurangException;
import com.pesanyuk.manager.MejaManager;
import com.pesanyuk.manager.MenuManager;
import com.pesanyuk.manager.OrderManager;
import com.pesanyuk.model.*;
import com.pesanyuk.model.MenuItem;
import com.pesanyuk.util.FileHandler;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tampilan Kasir POS persis seperti Mockup Figma 1:
 * - Header dengan Tanggal dinamis
 * - Bar input jumlah tamu & auto alokasi meja (Best-Fit)
 * - Katalog menu dengan filter [Semua / Makanan / Minuman], search, dan stepper
 * - Panel keranjang di sisi kanan dengan subtotal, pajak 10%, total oranye tebal
 * - Modal Pembayaran Tunai & QRIS lengkap dengan cetak struk
 */
public class KasirViewPanel extends JPanel {

    private MenuManager menuManager;
    private MejaManager mejaManager;
    private OrderManager orderManager;

    // State transaksi saat ini
    private Meja mejaTerpilih = null;
    private final Map<MenuItem, Integer> keranjang = new LinkedHashMap<>();

    // Komponen UI
    private JTextField txtJumlahTamu;
    private JPanel mejaStatusBox;
    private JLabel lblMejaInfo;
    private BadgeLabel lblMejaStatus;
    private SearchField txtSearchMenu;
    private String kategoriAktif = "Semua";

    // Filter Buttons
    private JButton btnFilterSemua;
    private JButton btnFilterMakanan;
    private JButton btnFilterMinuman;

    // Kontainer List Menu & Keranjang
    private JPanel menuListContainer;
    private JPanel cartItemsContainer;
    private JLabel lblCartItemCount;
    private JLabel lblSubtotal;
    private JLabel lblPajak;
    private JLabel lblTotal;
    private PrimaryButton btnBayar;

    public KasirViewPanel(MenuManager menuManager, MejaManager mejaManager, OrderManager orderManager) {
        this.menuManager = menuManager;
        this.mejaManager = mejaManager;
        this.orderManager = orderManager;

        setLayout(new BorderLayout(24, 0));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initComponents();
        refreshMenuDisplay();
        updateCartSummary();
    }

    private void initComponents() {
        // --- 1. SISI KIRI / TENGAH: Header + Bar Meja + Katalog Menu ---
        JPanel leftContainer = new JPanel(new BorderLayout(0, 16));
        leftContainer.setOpaque(false);

        // Header (Kasir Title + Subtitle + Tanggal)
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Kasir");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Buat dan kelola pesanan pelanggan");
        lblSubtitle.setFont(UITheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(lblSubtitle);

        // Date Pill (Kanan Atas)
        String tglSekarang = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID")));
        JLabel dateBadge = new JLabel(tglSekarang);
        dateBadge.setFont(UITheme.FONT_REGULAR);
        dateBadge.setForeground(UITheme.TEXT_MUTED);
        dateBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(6, 14, 6, 14)));

        topHeader.add(titlePanel, BorderLayout.WEST);
        topHeader.add(dateBadge, BorderLayout.EAST);

        // Bar Alokasi Meja
        JPanel tableBar = buildTableAllocationBar();

        JPanel northGroup = new JPanel(new BorderLayout(0, 14));
        northGroup.setOpaque(false);
        northGroup.add(topHeader, BorderLayout.NORTH);
        northGroup.add(tableBar, BorderLayout.SOUTH);

        leftContainer.add(northGroup, BorderLayout.NORTH);

        // Card Katalog Menu (RoundedPanel Putih)
        RoundedPanel menuCard = new RoundedPanel(16, UITheme.CARD_BG, UITheme.BORDER_COLOR, 1);
        menuCard.setLayout(new BorderLayout(0, 12));
        menuCard.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Search bar & Filter Buttons
        JPanel filterBar = buildFilterBar();
        menuCard.add(filterBar, BorderLayout.NORTH);

        // Table Header & Scrollable List
        JPanel menuContentPanel = new JPanel(new BorderLayout());
        menuContentPanel.setOpaque(false);

        JPanel tableHeader = buildMenuTableHeader();

        menuListContainer = new JPanel();
        menuListContainer.setLayout(new BoxLayout(menuListContainer, BoxLayout.Y_AXIS));
        menuListContainer.setBackground(Color.WHITE);

        JScrollPane scrollMenu = new JScrollPane(menuListContainer);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(12);
        scrollMenu.setColumnHeaderView(tableHeader);

        menuContentPanel.add(scrollMenu, BorderLayout.CENTER);
        menuCard.add(menuContentPanel, BorderLayout.CENTER);

        leftContainer.add(menuCard, BorderLayout.CENTER);

        add(leftContainer, BorderLayout.CENTER);

        // --- 2. SISI KANAN: Panel Keranjang Pesanan (Card Putih Lebar 350px) ---
        JPanel cartContainer = buildCartPanel();
        add(cartContainer, BorderLayout.EAST);
    }

    private JPanel buildTableAllocationBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        bar.setOpaque(false);

        // Label & Input Jumlah Tamu
        JLabel lblTamu = new JLabel("Jumlah Tamu");
        lblTamu.setFont(UITheme.FONT_REGULAR);
        lblTamu.setForeground(UITheme.TEXT_MUTED);

        txtJumlahTamu = new JTextField("2", 3);
        txtJumlahTamu.setFont(UITheme.FONT_BOLD);
        txtJumlahTamu.setHorizontalAlignment(JTextField.CENTER);
        txtJumlahTamu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(6, 10, 6, 10)));

        PrimaryButton btnCariMeja = new PrimaryButton("Cari Meja", 10);
        btnCariMeja.setIcon(VectorIcons.getSearchIcon(13, Color.WHITE));
        btnCariMeja.setIconTextGap(6);
        btnCariMeja.setPreferredSize(new Dimension(115, 34));
        btnCariMeja.addActionListener(e -> alokasikanMejaOtomatis());

        // Box Info Meja Terpilih
        mejaStatusBox = new RoundedPanel(12, UITheme.STATUS_BOX_BG, new Color(0xBB, 0xF7, 0xD0), 1);
        mejaStatusBox.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel iconMeja = new JLabel(VectorIcons.getTableIcon(18, UITheme.STATUS_SUCCESS_FG));

        JPanel mejaTextPanel = new JPanel();
        mejaTextPanel.setLayout(new BoxLayout(mejaTextPanel, BoxLayout.Y_AXIS));
        mejaTextPanel.setOpaque(false);

        JLabel lblSub = new JLabel("Meja otomatis");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(UITheme.TEXT_MUTED);

        lblMejaInfo = new JLabel("Belum pilih meja");
        lblMejaInfo.setFont(UITheme.FONT_BOLD);
        lblMejaInfo.setForeground(UITheme.TEXT_DARK);

        mejaTextPanel.add(lblSub);
        mejaTextPanel.add(lblMejaInfo);

        lblMejaStatus = new BadgeLabel("Pilih Meja", UITheme.STATUS_SUCCESS_BG, UITheme.STATUS_SUCCESS_FG);

        mejaStatusBox.add(iconMeja);
        mejaStatusBox.add(mejaTextPanel);
        mejaStatusBox.add(lblMejaStatus);

        bar.add(lblTamu);
        bar.add(txtJumlahTamu);
        bar.add(btnCariMeja);
        bar.add(mejaStatusBox);

        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setOpaque(false);

        // Search Input
        txtSearchMenu = new SearchField("Cari menu...");
        txtSearchMenu.setPreferredSize(new Dimension(240, 36));
        txtSearchMenu.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshMenuDisplay(); }
            public void removeUpdate(DocumentEvent e) { refreshMenuDisplay(); }
            public void changedUpdate(DocumentEvent e) { refreshMenuDisplay(); }
        });

        // Filter Pills: [ Semua ] [ Makanan ] [ Minuman ]
        JPanel filterButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterButtons.setOpaque(false);

        btnFilterSemua = createFilterButton("Semua", true);
        btnFilterMakanan = createFilterButton("Makanan", false);
        btnFilterMinuman = createFilterButton("Minuman", false);

        btnFilterSemua.addActionListener(e -> setKategoriFilter("Semua"));
        btnFilterMakanan.addActionListener(e -> setKategoriFilter("Makanan"));
        btnFilterMinuman.addActionListener(e -> setKategoriFilter("Minuman"));

        filterButtons.add(btnFilterSemua);
        filterButtons.add(btnFilterMakanan);
        filterButtons.add(btnFilterMinuman);

        panel.add(txtSearchMenu, BorderLayout.WEST);
        panel.add(filterButtons, BorderLayout.EAST);

        return panel;
    }

    private JButton createFilterButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_REGULAR);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateFilterButtonStyle(btn, active);
        return btn;
    }

    private void updateFilterButtonStyle(JButton btn, boolean active) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        if (active) {
            btn.setForeground(UITheme.PRIMARY_ORANGE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.PRIMARY_ORANGE, 1, true),
                    new EmptyBorder(5, 14, 5, 14)));
        } else {
            btn.setForeground(UITheme.TEXT_MUTED);
            btn.setBorder(new EmptyBorder(5, 14, 5, 14));
        }
    }

    private void setKategoriFilter(String kategori) {
        this.kategoriAktif = kategori;
        updateFilterButtonStyle(btnFilterSemua, "Semua".equals(kategori));
        updateFilterButtonStyle(btnFilterMakanan, "Makanan".equals(kategori));
        updateFilterButtonStyle(btnFilterMinuman, "Minuman".equals(kategori));
        refreshMenuDisplay();
    }

    private JPanel buildMenuTableHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0xF9, 0xFA, 0xFB));
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(8, 14, 8, 14)));

        String[] cols = {"MENU", "KATEGORI", "HARGA", "JUMLAH"};
        double[] weights = {0.38, 0.18, 0.20, 0.24};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 4, 0, 4);

        for (int i = 0; i < cols.length; i++) {
            gbc.gridx = i;
            gbc.weightx = weights[i];
            JLabel lbl = new JLabel(cols[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(UITheme.TEXT_LIGHT);
            if (i == 3) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
            }
            header.add(lbl, gbc);
        }

        return header;
    }

    private void refreshMenuDisplay() {
        menuListContainer.removeAll();
        String keyword = txtSearchMenu.getText().trim().toLowerCase();

        List<MenuItem> list = menuManager.getDaftarMenu();
        for (MenuItem item : list) {
            // Filter kategori
            if (!"Semua".equals(kategoriAktif) && !item.getKategori().equalsIgnoreCase(kategoriAktif)) {
                continue;
            }
            // Filter pencarian
            if (!keyword.isEmpty()) {
                boolean match = item.getNama().toLowerCase().contains(keyword)
                        || item.getId().toLowerCase().contains(keyword)
                        || item.getKategori().toLowerCase().contains(keyword);
                if (!match) continue;
            }

            JPanel row = buildMenuRow(item);
            menuListContainer.add(row);
        }

        menuListContainer.revalidate();
        menuListContainer.repaint();
    }

    private JPanel buildMenuRow(MenuItem item) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF3, 0xF4, 0xF6)),
                new EmptyBorder(10, 14, 10, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 4, 0, 4);

        double[] weights = {0.38, 0.18, 0.20, 0.24};

        // 1. Kolom MENU (Icon kotak M/D + Nama + Subtitle ID & Atribut)
        gbc.gridx = 0;
        gbc.weightx = weights[0];
        JPanel colMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        colMenu.setOpaque(false);

        boolean isMakanan = item instanceof Makanan;
        JLabel iconBadge = new JLabel(isMakanan ? "M" : "D", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isMakanan ? UITheme.BADGE_MAKANAN_BG : UITheme.BADGE_MINUMAN_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBadge.setPreferredSize(new Dimension(30, 30));
        iconBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        iconBadge.setForeground(isMakanan ? UITheme.BADGE_MAKANAN_FG : UITheme.BADGE_MINUMAN_FG);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel lblNama = new JLabel(item.getNama());
        lblNama.setFont(UITheme.FONT_BOLD);
        lblNama.setForeground(UITheme.TEXT_DARK);

        String atributStr = isMakanan ? ((Makanan) item).getTingkatPedas() : ((Minuman) item).getUkuran();
        JLabel lblSub = new JLabel(item.getId() + " · " + atributStr);
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        namePanel.add(lblNama);
        namePanel.add(lblSub);

        colMenu.add(iconBadge);
        colMenu.add(namePanel);
        row.add(colMenu, gbc);

        // 2. Kolom KATEGORI (Pill Badge)
        gbc.gridx = 1;
        gbc.weightx = weights[1];
        JPanel colKat = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        colKat.setOpaque(false);
        BadgeLabel katBadge = new BadgeLabel(
                item.getKategori(),
                isMakanan ? UITheme.BADGE_MAKANAN_BG : UITheme.BADGE_MINUMAN_BG,
                isMakanan ? UITheme.BADGE_MAKANAN_FG : UITheme.BADGE_MINUMAN_FG
        );
        colKat.add(katBadge);
        row.add(colKat, gbc);

        // 3. Kolom HARGA
        gbc.gridx = 2;
        gbc.weightx = weights[2];
        JPanel colHarga = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        colHarga.setOpaque(false);
        JLabel lblHarga = new JLabel(UITheme.formatRupiah(item.getHarga()));
        lblHarga.setFont(UITheme.FONT_BOLD);
        lblHarga.setForeground(UITheme.TEXT_DARK);
        colHarga.add(lblHarga);
        row.add(colHarga, gbc);

        // 4. Kolom JUMLAH (Stepper atau Tombol + Tambah)
        gbc.gridx = 3;
        gbc.weightx = weights[3];
        JPanel colAction = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        colAction.setOpaque(false);

        int currentQty = keranjang.getOrDefault(item, 0);
        if (currentQty > 0) {
            // Tampilkan Stepper [-] [qty] [+]
            StepperButton btnMin = new StepperButton("-");
            JLabel lblQty = new JLabel(String.valueOf(currentQty), SwingConstants.CENTER);
            lblQty.setPreferredSize(new Dimension(24, 28));
            lblQty.setFont(UITheme.FONT_BOLD);
            StepperButton btnPlus = new StepperButton("+");

            btnMin.addActionListener(e -> updateItemQty(item, currentQty - 1));
            btnPlus.addActionListener(e -> updateItemQty(item, currentQty + 1));

            colAction.add(btnMin);
            colAction.add(lblQty);
            colAction.add(btnPlus);
        } else {
            // Tampilkan Tombol + Tambah (Lebar 90px dengan padding pas)
            SecondaryButton btnTambah = new SecondaryButton("+ Tambah", 8);
            btnTambah.setPreferredSize(new Dimension(90, 28));
            btnTambah.setFont(UITheme.FONT_SMALL.deriveFont(Font.BOLD));
            btnTambah.addActionListener(e -> updateItemQty(item, 1));
            colAction.add(btnTambah);
        }
        row.add(colAction, gbc);

        return row;
    }

    private JPanel buildCartPanel() {
        RoundedPanel cartCard = new RoundedPanel(16, UITheme.CARD_BG, UITheme.BORDER_COLOR, 1);
        cartCard.setLayout(new BorderLayout(0, 16));
        cartCard.setPreferredSize(new Dimension(340, 600));
        cartCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Keranjang
        JPanel cartHeader = new JPanel(new BorderLayout());
        cartHeader.setOpaque(false);

        JPanel headerTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerTitle.setOpaque(false);

        JLabel iconReceipt = new JLabel(VectorIcons.getReceiptIcon(18, UITheme.PRIMARY_ORANGE));
        JLabel titleReceipt = new JLabel("Keranjang Pesanan");
        titleReceipt.setFont(UITheme.FONT_SECTION);
        titleReceipt.setForeground(UITheme.TEXT_DARK);

        headerTitle.add(iconReceipt);
        headerTitle.add(titleReceipt);

        lblCartItemCount = new BadgeLabel("0 item", new Color(0xFE, 0xF3, 0xC7), new Color(0xD9, 0x77, 0x06));

        cartHeader.add(headerTitle, BorderLayout.WEST);
        cartHeader.add(lblCartItemCount, BorderLayout.EAST);

        cartCard.add(cartHeader, BorderLayout.NORTH);

        // List Item Keranjang (Scrollable)
        cartItemsContainer = new JPanel();
        cartItemsContainer.setLayout(new BoxLayout(cartItemsContainer, BoxLayout.Y_AXIS));
        cartItemsContainer.setBackground(Color.WHITE);

        JScrollPane scrollCart = new JScrollPane(cartItemsContainer);
        scrollCart.setBorder(null);
        scrollCart.setOpaque(false);
        scrollCart.getViewport().setOpaque(false);
        scrollCart.getVerticalScrollBar().setUnitIncrement(12);

        cartCard.add(scrollCart, BorderLayout.CENTER);

        // Footer Keranjang (Subtotal, Pajak, Total, Tombol Bayar & Batal)
        JPanel cartFooter = new JPanel();
        cartFooter.setLayout(new BoxLayout(cartFooter, BoxLayout.Y_AXIS));
        cartFooter.setOpaque(false);
        cartFooter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_DARK),
                new EmptyBorder(16, 0, 0, 0)));

        lblSubtotal = new JLabel("Rp 0");
        lblPajak = new JLabel("Rp 0");
        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(UITheme.FONT_PRICE_BIG);
        lblTotal.setForeground(UITheme.PRIMARY_ORANGE);

        cartFooter.add(createSummaryRow("Subtotal", lblSubtotal, false));
        cartFooter.add(Box.createRigidArea(new Dimension(0, 6)));
        cartFooter.add(createSummaryRow("Pajak (10%)", lblPajak, false));
        cartFooter.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel lblTotalText = new JLabel("TOTAL");
        lblTotalText.setFont(UITheme.FONT_BOLD);
        lblTotalText.setForeground(UITheme.TEXT_DARK);
        totalRow.add(lblTotalText, BorderLayout.WEST);
        totalRow.add(lblTotal, BorderLayout.EAST);
        cartFooter.add(totalRow);
        cartFooter.add(Box.createRigidArea(new Dimension(0, 16)));

        // Tombol Bayar & Batal
        btnBayar = new PrimaryButton("Bayar Sekarang  >", 12);
        btnBayar.setPreferredSize(new Dimension(300, 42));
        btnBayar.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        btnBayar.addActionListener(e -> bukaDialogPembayaran());

        SecondaryButton btnBatal = new SecondaryButton("Batal", 12);
        btnBatal.setPreferredSize(new Dimension(300, 36));
        btnBatal.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        btnBatal.addActionListener(e -> batalkanPesanan());

        cartFooter.add(btnBayar);
        cartFooter.add(Box.createRigidArea(new Dimension(0, 8)));
        cartFooter.add(btnBatal);

        cartCard.add(cartFooter, BorderLayout.SOUTH);

        return cartCard;
    }

    private JPanel createSummaryRow(String title, JLabel valueLabel, boolean isBold) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(isBold ? UITheme.FONT_BOLD : UITheme.FONT_REGULAR);
        lblTitle.setForeground(isBold ? UITheme.TEXT_DARK : UITheme.TEXT_MUTED);

        valueLabel.setFont(isBold ? UITheme.FONT_BOLD : UITheme.FONT_REGULAR);
        valueLabel.setForeground(UITheme.TEXT_DARK);

        row.add(lblTitle, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private void updateItemQty(MenuItem item, int newQty) {
        if (newQty <= 0) {
            keranjang.remove(item);
        } else {
            keranjang.put(item, newQty);
        }
        refreshMenuDisplay();
        updateCartSummary();
    }

    private void updateCartSummary() {
        cartItemsContainer.removeAll();

        int totalItems = 0;
        double subtotal = 0;

        for (Map.Entry<MenuItem, Integer> entry : keranjang.entrySet()) {
            MenuItem item = entry.getKey();
            int qty = entry.getValue();
            totalItems += qty;
            double itemTotal = item.hitungHarga(qty);
            subtotal += itemTotal;

            cartItemsContainer.add(buildCartItemRow(item, qty, itemTotal));
            cartItemsContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        double pajak = subtotal * 0.10;
        double total = subtotal + pajak;

        lblCartItemCount.setText(totalItems + " item");
        lblSubtotal.setText(UITheme.formatRupiah(subtotal));
        lblPajak.setText(UITheme.formatRupiah(pajak));
        lblTotal.setText(UITheme.formatRupiah(total));

        btnBayar.setEnabled(totalItems > 0);

        cartItemsContainer.revalidate();
        cartItemsContainer.repaint();
    }

    private JPanel buildCartItemRow(MenuItem item, int qty, double lineTotal) {
        JPanel row = new JPanel(new BorderLayout(0, 6));
        row.setBackground(new Color(0xFA, 0xFA, 0xFA));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));

        // Atas: Nama item & Tombol hapus sampah merah
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblNama = new JLabel(item.getNama());
        lblNama.setFont(UITheme.FONT_BOLD);
        lblNama.setForeground(UITheme.TEXT_DARK);

        JLabel btnHapus = new JLabel(VectorIcons.getTrashIcon(14, UITheme.DANGER_FG));
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateItemQty(item, 0);
            }
        });

        top.add(lblNama, BorderLayout.WEST);
        top.add(btnHapus, BorderLayout.EAST);

        // Bawah: Harga satuan, Stepper, dan Total baris
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel lblHargaSatuan = new JLabel(UITheme.formatRupiah(item.hitungHarga(1)) + " / item");
        lblHargaSatuan.setFont(UITheme.FONT_SMALL);
        lblHargaSatuan.setForeground(UITheme.TEXT_MUTED);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionGroup.setOpaque(false);

        StepperButton btnMin = new StepperButton("-");
        JLabel lblQty = new JLabel(String.valueOf(qty));
        lblQty.setFont(UITheme.FONT_BOLD);
        StepperButton btnPlus = new StepperButton("+");

        btnMin.addActionListener(e -> updateItemQty(item, qty - 1));
        btnPlus.addActionListener(e -> updateItemQty(item, qty + 1));

        JLabel lblLineTotal = new JLabel(UITheme.formatRupiah(lineTotal));
        lblLineTotal.setFont(UITheme.FONT_BOLD);
        lblLineTotal.setForeground(UITheme.TEXT_DARK);

        actionGroup.add(btnMin);
        actionGroup.add(lblQty);
        actionGroup.add(btnPlus);
        actionGroup.add(Box.createRigidArea(new Dimension(8, 0)));
        actionGroup.add(lblLineTotal);

        bottom.add(lblHargaSatuan, BorderLayout.WEST);
        bottom.add(actionGroup, BorderLayout.EAST);

        row.add(top, BorderLayout.NORTH);
        row.add(bottom, BorderLayout.SOUTH);

        return row;
    }

    private void alokasikanMejaOtomatis() {
        try {
            int tamu = Integer.parseInt(txtJumlahTamu.getText().trim());
            if (tamu <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah tamu minimal 1 orang.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Jika sebelumnya sudah ada meja terisi yang dialokasikan, kembalikan dulu
            if (mejaTerpilih != null) {
                mejaManager.kosongkanMeja(mejaTerpilih.getNomor());
            }

            // Alokasikan dengan algoritma Best-Fit
            mejaTerpilih = mejaManager.alokasiMeja(tamu);
            lblMejaInfo.setText("Meja " + mejaTerpilih.getNomor() + " (kapasitas " + mejaTerpilih.getKapasitas() + ")");
            lblMejaStatus.setColors(UITheme.STATUS_SUCCESS_BG, UITheme.STATUS_SUCCESS_FG);
            lblMejaStatus.setText("Tersedia");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka jumlah tamu yang valid!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } catch (TidakAdaMejaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Meja Penuh", JOptionPane.ERROR_MESSAGE);
            mejaTerpilih = null;
            lblMejaInfo.setText("Semua meja penuh");
            lblMejaStatus.setColors(UITheme.DANGER_BG, UITheme.DANGER_FG);
            lblMejaStatus.setText("Penuh");
        }
    }

    private void batalkanPesanan() {
        if (keranjang.isEmpty() && mejaTerpilih == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Batalkan pesanan ini dan bersihkan keranjang?",
                "Konfirmasi Batal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (mejaTerpilih != null) {
                mejaManager.kosongkanMeja(mejaTerpilih.getNomor());
                mejaTerpilih = null;
                lblMejaInfo.setText("Belum pilih meja");
                lblMejaStatus.setColors(UITheme.STATUS_SUCCESS_BG, UITheme.STATUS_SUCCESS_FG);
                lblMejaStatus.setText("Pilih Meja");
            }
            keranjang.clear();
            refreshMenuDisplay();
            updateCartSummary();
        }
    }

    private void bukaDialogPembayaran() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (mejaTerpilih == null) {
            alokasikanMejaOtomatis();
            if (mejaTerpilih == null) return;
        }

        // Hitung total
        double tempSubtotal = 0;
        for (Map.Entry<MenuItem, Integer> e : keranjang.entrySet()) {
            tempSubtotal += e.getKey().hitungHarga(e.getValue());
        }
        final double finalSubtotal = tempSubtotal;
        final double finalTotal = finalSubtotal + (finalSubtotal * 0.10);

        // Buka Dialog Pembayaran
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        PaymentDialog dialog = new PaymentDialog(parentWindow, finalTotal, mejaTerpilih, new PaymentDialog.PaymentCallback() {
            @Override
            public void onPaymentSuccess(Pembayaran pembayaran) {
                prosesTransaksiSelesai(pembayaran, finalSubtotal, finalTotal);
            }
        });
        dialog.setVisible(true);
    }

    private void prosesTransaksiSelesai(Pembayaran pembayaran, double subtotal, double total) {
        try {
            int tamu = Integer.parseInt(txtJumlahTamu.getText().trim());
            Order order = new Order("ORD-" + System.currentTimeMillis() % 10000, tamu, mejaTerpilih);

            for (Map.Entry<MenuItem, Integer> e : keranjang.entrySet()) {
                order.tambahItem(e.getKey(), e.getValue());
            }

            // Polimorfisme bayar() dan generate Transaksi
            Transaksi trx = orderManager.prosesBayar(order, pembayaran);

            // Simpan transaksi ke file
            try {
                FileHandler.simpanTransaksi(orderManager.getRiwayatTransaksi());
                FileHandler.simpanMeja(mejaManager.getDaftarMeja());
            } catch (IOException ex) {
                System.err.println("Gagal simpan ke file: " + ex.getMessage());
            }

            // Tampilkan Struk
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            ReceiptDialog receiptDialog = new ReceiptDialog(parentWindow, trx);
            receiptDialog.setVisible(true);

            // Reset state
            keranjang.clear();
            mejaTerpilih = null;
            lblMejaInfo.setText("Belum pilih meja");
            lblMejaStatus.setText("Pilih Meja");
            refreshMenuDisplay();
            updateCartSummary();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
