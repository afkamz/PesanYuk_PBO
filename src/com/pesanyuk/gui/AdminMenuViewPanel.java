package com.pesanyuk.gui;

import com.pesanyuk.exception.MenuNotFoundException;
import com.pesanyuk.manager.MenuManager;
import com.pesanyuk.model.Makanan;
import com.pesanyuk.model.MenuItem;
import com.pesanyuk.model.Minuman;
import com.pesanyuk.util.FileHandler;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tampilan Kelola Menu Admin persis seperti Mockup Figma Gambar/admin/riwayat (1).png:
 * - Banner gelap atas bertuliskan "ADMIN"
 * - Header: Judul "Kelola Menu", Subtitle "Kelola daftar makanan dan minuman restoran"
 *   serta tombol "+ Tambah menu" warna gelap (#262D35) di sisi kanan
 * - Card tabel putih dengan search bar "Cari id atau menu..."
 * - Kolom: Id (pill badge cream), Nama, Kategori, Harga, Aksi (Edit pensil & Hapus sampah)
 */
public class AdminMenuViewPanel extends JPanel {

    private MenuManager menuManager;
    private SearchField txtSearch;
    private JPanel tableBody;

    public AdminMenuViewPanel(MenuManager menuManager) {
        this.menuManager = menuManager;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // --- 1. BANNER GELAP ATAS "ADMIN" ---
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

        JLabel lblBannerTitle = new JLabel("ADMIN");
        lblBannerTitle.setFont(UITheme.FONT_BANNER);
        lblBannerTitle.setForeground(Color.WHITE);
        bannerPanel.add(lblBannerTitle);

        add(bannerPanel, BorderLayout.NORTH);

        // --- 2. KONTEN UTAMA ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(16, 24, 20, 24));

        // Header: Judul "Kelola Menu" + Tombol Gelap "+ Tambah menu"
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Kelola Menu");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Kelola daftar makanan dan minuman restoran");
        lblSubtitle.setFont(UITheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(lblSubtitle);

        // Tombol Tambah Menu Gelap (#262D35)
        JButton btnTambahMenu = new JButton("+ Tambah menu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.DARK_BUTTON_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnTambahMenu.setFont(UITheme.FONT_BOLD);
        btnTambahMenu.setForeground(Color.WHITE);
        btnTambahMenu.setContentAreaFilled(false);
        btnTambahMenu.setBorderPainted(false);
        btnTambahMenu.setFocusPainted(false);
        btnTambahMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahMenu.setPreferredSize(new Dimension(150, 40));
        btnTambahMenu.addActionListener(e -> bukaDialogForm(null));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnTambahMenu, BorderLayout.EAST);

        mainContent.add(headerPanel, BorderLayout.NORTH);

        // --- 3. MAIN CARD TABEL MENU ---
        RoundedPanel cardPanel = new RoundedPanel(20, Color.WHITE, UITheme.BORDER_COLOR, 1);
        cardPanel.setLayout(new BorderLayout(0, 14));
        cardPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Search Bar
        txtSearch = new SearchField("Cari id atau menu...");
        txtSearch.setPreferredSize(new Dimension(320, 38));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });
        cardPanel.add(txtSearch, BorderLayout.NORTH);

        // Table Header & Body
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        JPanel tableHeader = buildTableHeader();

        tableBody = new JPanel();
        tableBody.setLayout(new BoxLayout(tableBody, BoxLayout.Y_AXIS));
        tableBody.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tableBody);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setColumnHeaderView(tableHeader);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        cardPanel.add(tableContainer, BorderLayout.CENTER);

        mainContent.add(cardPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel buildTableHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0xF9, 0xFA, 0xFB));
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(8, 14, 8, 14)));

        String[] cols = {"Id", "Nama", "Kategori", "Harga", "Aksi"};
        double[] weights = {0.14, 0.36, 0.22, 0.18, 0.10};

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

        List<MenuItem> list = menuManager.getDaftarMenu();
        double[] weights = {0.14, 0.36, 0.22, 0.18, 0.10};

        for (MenuItem item : list) {
            if (!keyword.isEmpty()) {
                boolean match = item.getNama().toLowerCase().contains(keyword)
                        || item.getId().toLowerCase().contains(keyword)
                        || item.getKategori().toLowerCase().contains(keyword);
                if (!match) continue;
            }

            tableBody.add(buildTableRow(item, weights));
        }

        tableBody.revalidate();
        tableBody.repaint();
    }

    private JPanel buildTableRow(MenuItem item, double[] weights) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF3, 0xF4, 0xF6)),
                new EmptyBorder(12, 14, 12, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 4, 0, 4);

        // 1. Id (Badge rounded cream)
        gbc.gridx = 0;
        gbc.weightx = weights[0];
        BadgeLabel idBadge = new BadgeLabel(item.getId(), new Color(0xFE, 0xEB, 0xDF), UITheme.TEXT_DARK);
        JPanel pId = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pId.setOpaque(false);
        pId.add(idBadge);
        row.add(pId, gbc);

        // 2. Nama
        gbc.gridx = 1;
        gbc.weightx = weights[1];
        JLabel lblNama = new JLabel(item.getNama());
        lblNama.setFont(UITheme.FONT_REGULAR);
        lblNama.setForeground(UITheme.TEXT_DARK);
        row.add(lblNama, gbc);

        // 3. Kategori
        gbc.gridx = 2;
        gbc.weightx = weights[2];
        JLabel lblKat = new JLabel(item.getKategori());
        lblKat.setFont(UITheme.FONT_REGULAR);
        lblKat.setForeground(UITheme.TEXT_DARK);
        row.add(lblKat, gbc);

        // 4. Harga
        gbc.gridx = 3;
        gbc.weightx = weights[3];
        JLabel lblHarga = new JLabel(UITheme.formatRupiah(item.getHarga()));
        lblHarga.setFont(UITheme.FONT_BOLD);
        lblHarga.setForeground(UITheme.TEXT_DARK);
        row.add(lblHarga, gbc);

        // 5. Aksi (Icon Pensil Hijau & Icon Sampah Merah)
        gbc.gridx = 4;
        gbc.weightx = weights[4];
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setOpaque(false);

        JLabel btnEdit = new JLabel(VectorIcons.getEditIcon(16, UITheme.EDIT_FG));
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bukaDialogForm(item);
            }
        });

        JLabel btnDelete = new JLabel(VectorIcons.getTrashIcon(16, UITheme.DANGER_FG));
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                konfirmasiHapus(item);
            }
        });

        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        row.add(actionPanel, gbc);

        return row;
    }

    private void bukaDialogForm(MenuItem itemToEdit) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        MenuFormDialog dialog = new MenuFormDialog(parent, itemToEdit, menuManager, new MenuFormDialog.FormCallback() {
            @Override
            public void onSaved() {
                try {
                    FileHandler.simpanMenu(menuManager.getDaftarMenu());
                } catch (IOException ex) {
                    System.err.println("Gagal menyimpan menu ke file: " + ex.getMessage());
                }
                refreshTable();
            }
        });
        dialog.setVisible(true);
    }

    private void konfirmasiHapus(MenuItem item) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus menu '" + item.getNama() + "' (" + item.getId() + ")?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                menuManager.hapus(item.getId());
                FileHandler.simpanMenu(menuManager.getDaftarMenu());
                refreshTable();
                JOptionPane.showMessageDialog(this, "Menu berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (MenuNotFoundException | IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus menu: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
