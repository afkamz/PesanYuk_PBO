package com.pesanyuk.gui;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.exception.TidakAdaMejaException;
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
 * Tampilan Kasir POS persis seperti Mockup Figma Gambar/kasir/kasir 1.png:
 * - Banner atas gelap bertuliskan "KASIR"
 * - Subheader baris: Stepper Jumlah tamu + Tombol Cari Meja + Box Meja Otomatis + Pill Tanggal
 * - Katalog menu berbasis GRID (3 kolom kartu menu modern dengan thumbnail & tombol Tambah)
 * - Panel Keranjang Pesanan di kanan dengan item list, stepper, trash, Sub Total, PPN 10%, Total, Bayar Sekarang & Batal
 */
public class KasirViewPanel extends JPanel {

    private MenuManager menuManager;
    private MejaManager mejaManager;
    private OrderManager orderManager;

    // State transaksi
    private Meja mejaTerpilih = null;
    private final Map<MenuItem, Integer> keranjang = new LinkedHashMap<>();
    private int jumlahTamu = 1;

    // Komponen Subheader
    private JLabel lblJumlahTamuVal;
    private JPanel mejaStatusBox;
    private JLabel lblMejaInfo;
    private BadgeLabel lblMejaStatus;

    // Filter & Search
    private SearchField txtSearchMenu;
    private String kategoriAktif = "Semua";
    private JButton btnFilterSemua;
    private JButton btnFilterMakanan;
    private JButton btnFilterMinuman;

    // Kontainer Menu Grid & Keranjang
    private JPanel menuGridContainer;
    private JPanel cartItemsContainer;
    private JLabel lblSubtotal;
    private JLabel lblPajak;
    private JLabel lblTotal;
    private PrimaryButton btnBayar;

    public KasirViewPanel(MenuManager menuManager, MejaManager mejaManager, OrderManager orderManager) {
        this.menuManager = menuManager;
        this.mejaManager = mejaManager;
        this.orderManager = orderManager;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        initComponents();
        refreshMenuDisplay();
        updateCartSummary();
    }

    private void initComponents() {
        // --- 1. BANNER GELAP ATAS DENGAN TEKS BESAR "KASIR" ---
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background dark slate banner
                g2.setColor(UITheme.BANNER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Aksen corak gelap lembut
                g2.setColor(new Color(0x28, 0x2E, 0x37));
                for (int i = 0; i < getWidth(); i += 90) {
                    g2.fillOval(i - 20, -30, 80, 80);
                }

                g2.dispose();
            }
        };
        bannerPanel.setLayout(new GridBagLayout());
        bannerPanel.setPreferredSize(new Dimension(0, 72));

        JLabel lblBannerTitle = new JLabel("KASIR");
        lblBannerTitle.setFont(UITheme.FONT_BANNER);
        lblBannerTitle.setForeground(Color.WHITE);
        bannerPanel.add(lblBannerTitle);

        add(bannerPanel, BorderLayout.NORTH);

        // --- 2. KONTEN UTAMA: Subheader + (Katalog Grid di Kiri, Keranjang di Kanan) ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(16, 24, 20, 24));

        // Subheader Bar (Jumlah Tamu + Meja Otomatis + Tanggal)
        JPanel subHeaderBar = buildSubHeaderBar();
        mainContent.add(subHeaderBar, BorderLayout.NORTH);

        // Area Tengah (Split Katalog Grid vs Keranjang Pesanan)
        JPanel centerSplit = new JPanel(new BorderLayout(20, 0));
        centerSplit.setOpaque(false);

        // KIRI: Card Katalog Menu
        RoundedPanel catalogCard = new RoundedPanel(20, Color.WHITE, UITheme.BORDER_COLOR, 1);
        catalogCard.setLayout(new BorderLayout(0, 14));
        catalogCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Search & Filter Bar
        JPanel filterBar = buildFilterBar();
        catalogCard.add(filterBar, BorderLayout.NORTH);

        // Grid Menu Container
        menuGridContainer = new JPanel(new GridLayout(0, 3, 14, 14));
        menuGridContainer.setBackground(Color.WHITE);

        JScrollPane scrollMenu = new JScrollPane(menuGridContainer);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(14);
        scrollMenu.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        catalogCard.add(scrollMenu, BorderLayout.CENTER);
        centerSplit.add(catalogCard, BorderLayout.CENTER);

        // KANAN: Card Keranjang Pesanan
        JPanel cartPanel = buildCartPanel();
        centerSplit.add(cartPanel, BorderLayout.EAST);

        mainContent.add(centerSplit, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel buildSubHeaderBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        // Kiri: Stepper Jumlah Tamu & Tombol Cari Meja
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftGroup.setOpaque(false);

        JLabel lblTamu = new JLabel("Jumlah tamu");
        lblTamu.setFont(UITheme.FONT_BOLD);
        lblTamu.setForeground(UITheme.TEXT_DARK);

        // Stepper Jumlah Tamu Box Rounded
        RoundedPanel guestStepperBox = new RoundedPanel(10, Color.WHITE, UITheme.BORDER_DARK, 1);
        guestStepperBox.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));

        JButton btnMinGuest = createMiniStepperBtn("-");
        lblJumlahTamuVal = new JLabel(String.valueOf(jumlahTamu), SwingConstants.CENTER);
        lblJumlahTamuVal.setFont(UITheme.FONT_BOLD);
        lblJumlahTamuVal.setPreferredSize(new Dimension(28, 24));
        JButton btnPlusGuest = createMiniStepperBtn("+");

        btnMinGuest.addActionListener(e -> {
            if (jumlahTamu > 1) {
                jumlahTamu--;
                lblJumlahTamuVal.setText(String.valueOf(jumlahTamu));
            }
        });
        btnPlusGuest.addActionListener(e -> {
            jumlahTamu++;
            lblJumlahTamuVal.setText(String.valueOf(jumlahTamu));
        });

        guestStepperBox.add(btnMinGuest);
        guestStepperBox.add(lblJumlahTamuVal);
        guestStepperBox.add(btnPlusGuest);

        // Tombol Cari Meja Oranye
        PrimaryButton btnCariMeja = new PrimaryButton("Cari meja", 10);
        btnCariMeja.setIcon(VectorIcons.getSearchIcon(13, Color.WHITE));
        btnCariMeja.setIconTextGap(6);
        btnCariMeja.setPreferredSize(new Dimension(110, 34));
        btnCariMeja.addActionListener(e -> alokasikanMejaOtomatis());

        // Box Info Meja Otomatis Hijau
        mejaStatusBox = new RoundedPanel(12, UITheme.STATUS_SUCCESS_BG, new Color(0xBB, 0xF7, 0xD0), 1);
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

        lblMejaStatus = new BadgeLabel("Pilih Meja", Color.WHITE, UITheme.STATUS_SUCCESS_FG);

        mejaStatusBox.add(iconMeja);
        mejaStatusBox.add(mejaTextPanel);
        mejaStatusBox.add(lblMejaStatus);

        leftGroup.add(lblTamu);
        leftGroup.add(guestStepperBox);
        leftGroup.add(btnCariMeja);
        leftGroup.add(mejaStatusBox);

        bar.add(leftGroup, BorderLayout.WEST);

        // Kanan: Date Pill Putih
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

        bar.add(datePill, BorderLayout.EAST);

        return bar;
    }

    private JButton createMiniStepperBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setForeground(UITheme.TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(24, 24));
        return btn;
    }

    private JPanel buildFilterBar() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setOpaque(false);

        // Search Input Field
        txtSearchMenu = new SearchField("Cari menu...");
        txtSearchMenu.setPreferredSize(new Dimension(240, 36));
        txtSearchMenu.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshMenuDisplay(); }
            public void removeUpdate(DocumentEvent e) { refreshMenuDisplay(); }
            public void changedUpdate(DocumentEvent e) { refreshMenuDisplay(); }
        });

        // Kapsul Filter Tab: [ Semua ] [ Makanan ] [ Minuman ]
        RoundedPanel filterPillContainer = new RoundedPanel(12, new Color(0xDC, 0xE0, 0xE5), null, 0);
        filterPillContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));

        btnFilterSemua = createFilterTabButton("Semua", true);
        btnFilterMakanan = createFilterTabButton("Makanan", false);
        btnFilterMinuman = createFilterTabButton("Minuman", false);

        btnFilterSemua.addActionListener(e -> setKategoriFilter("Semua"));
        btnFilterMakanan.addActionListener(e -> setKategoriFilter("Makanan"));
        btnFilterMinuman.addActionListener(e -> setKategoriFilter("Minuman"));

        filterPillContainer.add(btnFilterSemua);
        filterPillContainer.add(btnFilterMakanan);
        filterPillContainer.add(btnFilterMinuman);

        panel.add(txtSearchMenu, BorderLayout.WEST);
        panel.add(filterPillContainer, BorderLayout.EAST);

        return panel;
    }

    private JButton createFilterTabButton(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 14, 5, 14));
        btn.setForeground(active ? UITheme.PRIMARY_ORANGE : UITheme.TEXT_MUTED);
        return btn;
    }

    private void setKategoriFilter(String kategori) {
        this.kategoriAktif = kategori;
        btnFilterSemua.setForeground("Semua".equals(kategori) ? UITheme.PRIMARY_ORANGE : UITheme.TEXT_MUTED);
        btnFilterMakanan.setForeground("Makanan".equals(kategori) ? UITheme.PRIMARY_ORANGE : UITheme.TEXT_MUTED);
        btnFilterMinuman.setForeground("Minuman".equals(kategori) ? UITheme.PRIMARY_ORANGE : UITheme.TEXT_MUTED);

        btnFilterSemua.repaint();
        btnFilterMakanan.repaint();
        btnFilterMinuman.repaint();

        refreshMenuDisplay();
    }

    private void refreshMenuDisplay() {
        menuGridContainer.removeAll();
        String keyword = txtSearchMenu.getText().trim().toLowerCase();

        List<MenuItem> list = menuManager.getDaftarMenu();
        for (MenuItem item : list) {
            if (!"Semua".equals(kategoriAktif) && !item.getKategori().equalsIgnoreCase(kategoriAktif)) {
                continue;
            }
            if (!keyword.isEmpty()) {
                boolean match = item.getNama().toLowerCase().contains(keyword)
                        || item.getId().toLowerCase().contains(keyword)
                        || item.getKategori().toLowerCase().contains(keyword);
                if (!match) continue;
            }

            menuGridContainer.add(buildMenuCard(item));
        }

        menuGridContainer.revalidate();
        menuGridContainer.repaint();
    }

    /**
     * Membangun Kartu Menu berbentuk Grid (persis seperti mockup Figma)
     */
    private JPanel buildMenuCard(MenuItem item) {
        boolean isMakanan = item instanceof Makanan;

        RoundedPanel card = new RoundedPanel(14, Color.WHITE, UITheme.BORDER_COLOR, 1);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setPreferredSize(new Dimension(170, 195));

        // 1. Thumbnail Makanan / Minuman (Rounded Box dengan ilustrasi menarik)
        JPanel thumbnailPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background gradien lembut
                Color c1 = isMakanan ? new Color(0xFD, 0xBA, 0x74) : new Color(0xBA, 0xE6, 0xFD);
                Color c2 = isMakanan ? new Color(0xFB, 0x92, 0x3C) : new Color(0x7D, 0xD3, 0xFC);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Gambar piring / gelas siluet
                g2.setColor(new Color(255, 255, 255, 180));
                if (isMakanan) {
                    g2.fillOval(getWidth() / 2 - 28, getHeight() / 2 - 28, 56, 56);
                    g2.setColor(new Color(0xEA, 0x58, 0x0C, 190));
                    g2.fillOval(getWidth() / 2 - 20, getHeight() / 2 - 20, 40, 40);
                } else {
                    g2.fillRoundRect(getWidth() / 2 - 14, getHeight() / 2 - 24, 28, 48, 8, 8);
                    g2.setColor(new Color(0x02, 0x84, 0xC7, 190));
                    g2.fillRoundRect(getWidth() / 2 - 11, getHeight() / 2 - 12, 22, 34, 4, 4);
                }

                g2.dispose();
            }
        };
        thumbnailPanel.setPreferredSize(new Dimension(0, 95));
        thumbnailPanel.setOpaque(false);

        // 2. Info Menu (Nama & Harga)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblNama = new JLabel(item.getNama());
        lblNama.setFont(UITheme.FONT_BOLD);
        lblNama.setForeground(UITheme.TEXT_DARK);

        JLabel lblHarga = new JLabel(UITheme.formatRupiah(item.getHarga()));
        lblHarga.setFont(UITheme.FONT_SMALL);
        lblHarga.setForeground(UITheme.TEXT_MUTED);

        infoPanel.add(lblNama);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(lblHarga);

        // 3. Bottom Bar: Badge Kategori di Kiri & Tombol Tambah di Kanan
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        BadgeLabel catBadge = new BadgeLabel(
                item.getKategori(),
                isMakanan ? UITheme.BADGE_MAKANAN_BG : UITheme.BADGE_MINUMAN_BG,
                isMakanan ? UITheme.BADGE_MAKANAN_FG : UITheme.BADGE_MINUMAN_FG
        );

        JLabel btnTambah = new JLabel("Tambah", SwingConstants.RIGHT);
        btnTambah.setFont(UITheme.FONT_BOLD);
        btnTambah.setForeground(UITheme.TEXT_DARK);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cur = keranjang.getOrDefault(item, 0);
                updateItemQty(item, cur + 1);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnTambah.setForeground(UITheme.PRIMARY_ORANGE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnTambah.setForeground(UITheme.TEXT_DARK);
            }
        });

        bottomBar.add(catBadge, BorderLayout.WEST);
        bottomBar.add(btnTambah, BorderLayout.EAST);

        card.add(thumbnailPanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(bottomBar, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildCartPanel() {
        RoundedPanel cartCard = new RoundedPanel(20, Color.WHITE, UITheme.BORDER_COLOR, 1);
        cartCard.setLayout(new BorderLayout(0, 14));
        cartCard.setPreferredSize(new Dimension(320, 600));
        cartCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Header Keranjang
        JPanel cartHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cartHeader.setOpaque(false);

        JLabel iconReceipt = new JLabel(VectorIcons.getReceiptIcon(20, UITheme.PRIMARY_ORANGE));
        JLabel titleReceipt = new JLabel("Keranjang Pesanan");
        titleReceipt.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleReceipt.setForeground(UITheme.PRIMARY_ORANGE);

        cartHeader.add(iconReceipt);
        cartHeader.add(titleReceipt);

        // Separator di bawah header
        JSeparator sepTop = new JSeparator();
        sepTop.setForeground(UITheme.BORDER_DARK);

        JPanel headerBox = new JPanel(new BorderLayout(0, 10));
        headerBox.setOpaque(false);
        headerBox.add(cartHeader, BorderLayout.NORTH);
        headerBox.add(sepTop, BorderLayout.SOUTH);

        cartCard.add(headerBox, BorderLayout.NORTH);

        // List Item Keranjang
        cartItemsContainer = new JPanel();
        cartItemsContainer.setLayout(new BoxLayout(cartItemsContainer, BoxLayout.Y_AXIS));
        cartItemsContainer.setBackground(Color.WHITE);

        JScrollPane scrollCart = new JScrollPane(cartItemsContainer);
        scrollCart.setBorder(null);
        scrollCart.setOpaque(false);
        scrollCart.getViewport().setOpaque(false);
        scrollCart.getVerticalScrollBar().setUnitIncrement(12);

        cartCard.add(scrollCart, BorderLayout.CENTER);

        // Footer Keranjang (Sub Total, PPN 10%, TOTAL, Tombol Bayar Sekarang & Batal)
        JPanel cartFooter = new JPanel();
        cartFooter.setLayout(new BoxLayout(cartFooter, BoxLayout.Y_AXIS));
        cartFooter.setOpaque(false);

        JSeparator sepBottom = new JSeparator();
        sepBottom.setForeground(UITheme.BORDER_DARK);
        cartFooter.add(sepBottom);
        cartFooter.add(Box.createRigidArea(new Dimension(0, 12)));

        lblSubtotal = new JLabel("Rp. 0");
        lblPajak = new JLabel("Rp. 0");
        lblTotal = new JLabel("Rp. 0");
        lblTotal.setFont(UITheme.FONT_PRICE_BIG);
        lblTotal.setForeground(UITheme.PRIMARY_ORANGE);

        cartFooter.add(createSummaryRow("Sub Total", lblSubtotal, false));
        cartFooter.add(Box.createRigidArea(new Dimension(0, 4)));
        cartFooter.add(createSummaryRow("PPN 10%", lblPajak, false));
        cartFooter.add(Box.createRigidArea(new Dimension(0, 12)));

        JSeparator sepTotal = new JSeparator();
        sepTotal.setForeground(UITheme.BORDER_DARK);
        cartFooter.add(sepTotal);
        cartFooter.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel lblTotalText = new JLabel("TOTAL");
        lblTotalText.setFont(UITheme.FONT_BOLD);
        lblTotalText.setForeground(UITheme.TEXT_DARK);
        totalRow.add(lblTotalText, BorderLayout.WEST);
        totalRow.add(lblTotal, BorderLayout.EAST);
        cartFooter.add(totalRow);
        cartFooter.add(Box.createRigidArea(new Dimension(0, 16)));

        // Tombol Bayar Sekarang (Oranye Emas) & Batal (Outline)
        btnBayar = new PrimaryButton("Bayar Sekarang", 12);
        btnBayar.setPreferredSize(new Dimension(280, 42));
        btnBayar.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        btnBayar.addActionListener(e -> bukaDialogPembayaran());

        SecondaryButton btnBatal = new SecondaryButton("Batal", 12);
        btnBatal.setPreferredSize(new Dimension(280, 38));
        btnBatal.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
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
            cartItemsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        double pajak = subtotal * 0.10;
        double total = subtotal + pajak;

        lblSubtotal.setText(UITheme.formatRupiah(subtotal));
        lblPajak.setText(UITheme.formatRupiah(pajak));
        lblTotal.setText(UITheme.formatRupiah(total));

        btnBayar.setEnabled(totalItems > 0);

        cartItemsContainer.revalidate();
        cartItemsContainer.repaint();
    }

    private JPanel buildCartItemRow(MenuItem item, int qty, double lineTotal) {
        JPanel row = new JPanel(new BorderLayout(0, 6));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 65));

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

        JLabel lblHargaSatuan = new JLabel(UITheme.formatRupiah(item.hitungHarga(1)) + "/porsi");
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

        // Container dengan separator bawah
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.add(row, BorderLayout.CENTER);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xEE, 0xF0, 0xF2));
        wrapper.add(sep, BorderLayout.SOUTH);

        return wrapper;
    }

    private void alokasikanMejaOtomatis() {
        try {
            if (jumlahTamu <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah tamu minimal 1 orang.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (mejaTerpilih != null) {
                mejaManager.kosongkanMeja(mejaTerpilih.getNomor());
            }

            mejaTerpilih = mejaManager.alokasiMeja(jumlahTamu);
            lblMejaInfo.setText("Meja " + mejaTerpilih.getNomor() + " (Kapasitas " + mejaTerpilih.getKapasitas() + " orang)");
            lblMejaStatus.setColors(UITheme.STATUS_SUCCESS_BG, UITheme.STATUS_SUCCESS_FG);
            lblMejaStatus.setText("Tersedia");

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
                lblMejaStatus.setText("Pilih Meja");
            }
            keranjang.clear();
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

        double tempSubtotal = 0;
        for (Map.Entry<MenuItem, Integer> e : keranjang.entrySet()) {
            tempSubtotal += e.getKey().hitungHarga(e.getValue());
        }
        final double finalSubtotal = tempSubtotal;
        final double finalTotal = finalSubtotal + (finalSubtotal * 0.10);

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
            Order order = new Order("ORD-" + System.currentTimeMillis() % 10000, jumlahTamu, mejaTerpilih);

            for (Map.Entry<MenuItem, Integer> e : keranjang.entrySet()) {
                order.tambahItem(e.getKey(), e.getValue());
            }

            Transaksi trx = orderManager.prosesBayar(order, pembayaran);

            try {
                FileHandler.simpanTransaksi(orderManager.getRiwayatTransaksi());
                FileHandler.simpanMeja(mejaManager.getDaftarMeja());
            } catch (IOException ex) {
                System.err.println("Gagal simpan data: " + ex.getMessage());
            }

            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            ReceiptDialog receiptDialog = new ReceiptDialog(parentWindow, trx);
            receiptDialog.setVisible(true);

            keranjang.clear();
            mejaTerpilih = null;
            lblMejaInfo.setText("Belum pilih meja");
            lblMejaStatus.setText("Pilih Meja");
            updateCartSummary();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
