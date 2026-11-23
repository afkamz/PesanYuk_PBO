package com.pesanyuk.gui;

import com.pesanyuk.exception.InputTidakValidException;
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
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tampilan Kelola Menu Admin persis seperti Mockup Figma 2:
 * - Header dengan judul "Kelola Menu" dan tombol "+ Tambah Menu" oranye
 * - Card tabel putih dengan search bar "Cari nama atau ID menu..."
 * - Kolom ID, Nama, Kategori (Pill Makanan/Minuman), Harga, Atribut, Aksi (Edit & Hapus)
 */
public class AdminMenuViewPanel extends JPanel {

    private MenuManager menuManager;
    private SearchField txtSearch;
    private JLabel lblMenuCount;
    private JPanel tableBody;

    public AdminMenuViewPanel(MenuManager menuManager) {
        this.menuManager = menuManager;

        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // --- 1. Top Header: Judul + Tombol "+ Tambah Menu" ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Kelola Menu");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Atur daftar makanan dan minuman restoran");
        lblSubtitle.setFont(UITheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(lblSubtitle);

        PrimaryButton btnTambahMenu = new PrimaryButton("+ Tambah Menu", 12);
        btnTambahMenu.setPreferredSize(new Dimension(150, 40));
        btnTambahMenu.addActionListener(e -> bukaDialogForm(null));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnTambahMenu, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Main Card Tabel Menu ---
        RoundedPanel cardPanel = new RoundedPanel(16, UITheme.CARD_BG, UITheme.BORDER_COLOR, 1);
        cardPanel.setLayout(new BorderLayout(0, 14));
        cardPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Sub-header di dalam kartu: Search Bar & Label Counter
        JPanel cardTop = new JPanel(new BorderLayout());
        cardTop.setOpaque(false);

        txtSearch = new SearchField("Cari nama atau ID menu...");
        txtSearch.setPreferredSize(new Dimension(280, 38));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });

        lblMenuCount = new JLabel("0 menu tersedia");
        lblMenuCount.setFont(UITheme.FONT_SMALL);
        lblMenuCount.setForeground(UITheme.TEXT_MUTED);

        cardTop.add(txtSearch, BorderLayout.WEST);
        cardTop.add(lblMenuCount, BorderLayout.EAST);

        cardPanel.add(cardTop, BorderLayout.NORTH);

        // Header Kolom Tabel & Konten
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

        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel buildTableHeader() {
        // Grid 6 kolom: ID, Nama, Kategori, Harga, Atribut, Aksi
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0xF9, 0xFA, 0xFB));
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(8, 14, 8, 14)));

        String[] cols = {"ID", "NAMA", "KATEGORI", "HARGA", "ATRIBUT", "AKSI"};
        double[] weights = {0.10, 0.28, 0.16, 0.18, 0.16, 0.12};

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
        int count = 0;

        double[] weights = {0.10, 0.28, 0.16, 0.18, 0.16, 0.12};

        for (MenuItem item : list) {
            if (!keyword.isEmpty()) {
                boolean match = item.getNama().toLowerCase().contains(keyword)
                        || item.getId().toLowerCase().contains(keyword)
                        || item.getKategori().toLowerCase().contains(keyword);
                if (!match) continue;
            }

            count++;
            JPanel row = buildTableRow(item, weights);
            tableBody.add(row);
        }

        lblMenuCount.setText(count + " menu tersedia");
        tableBody.revalidate();
        tableBody.repaint();
    }

    private JPanel buildTableRow(MenuItem item, double[] weights) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF3, 0xF4, 0xF6)),
                new EmptyBorder(10, 14, 10, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 4, 0, 4);

        boolean isMakanan = item instanceof Makanan;

        // 1. ID
        gbc.gridx = 0;
        gbc.weightx = weights[0];
        JLabel lblId = new JLabel(item.getId());
        lblId.setFont(UITheme.FONT_SMALL);
        lblId.setForeground(UITheme.TEXT_MUTED);
        row.add(lblId, gbc);

        // 2. Nama
        gbc.gridx = 1;
        gbc.weightx = weights[1];
        JLabel lblNama = new JLabel(item.getNama());
        lblNama.setFont(UITheme.FONT_BOLD);
        lblNama.setForeground(UITheme.TEXT_DARK);
        row.add(lblNama, gbc);

        // 3. Kategori (Pill)
        gbc.gridx = 2;
        gbc.weightx = weights[2];
        JPanel catPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        catPanel.setOpaque(false);
        BadgeLabel catBadge = new BadgeLabel(
                item.getKategori(),
                isMakanan ? UITheme.BADGE_MAKANAN_BG : UITheme.BADGE_MINUMAN_BG,
                isMakanan ? UITheme.BADGE_MAKANAN_FG : UITheme.BADGE_MINUMAN_FG
        );
        catPanel.add(catBadge);
        row.add(catPanel, gbc);

        // 4. Harga
        gbc.gridx = 3;
        gbc.weightx = weights[3];
        JLabel lblHarga = new JLabel(UITheme.formatRupiah(item.getHarga()));
        lblHarga.setFont(UITheme.FONT_BOLD);
        lblHarga.setForeground(UITheme.TEXT_DARK);
        row.add(lblHarga, gbc);

        // 5. Atribut (Pedas: X / Ukuran: Y)
        gbc.gridx = 4;
        gbc.weightx = weights[4];
        String atributStr = isMakanan
                ? "Pedas: " + ((Makanan) item).getTingkatPedas()
                : "Ukuran: " + ((Minuman) item).getUkuran();
        JLabel lblAtr = new JLabel(atributStr);
        lblAtr.setFont(UITheme.FONT_REGULAR);
        lblAtr.setForeground(UITheme.TEXT_MUTED);
        row.add(lblAtr, gbc);

        // 6. Aksi (Edit Pencil & Delete Trash)
        gbc.gridx = 5;
        gbc.weightx = weights[5];
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        actionPanel.setOpaque(false);

        // Tombol Edit Biru (Vector Icon)
        JLabel btnEdit = createActionButton(VectorIcons.getEditIcon(14, UITheme.EDIT_FG), UITheme.EDIT_BG);
        btnEdit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bukaDialogForm(item);
            }
        });

        // Tombol Hapus Merah (Vector Icon)
        JLabel btnDelete = createActionButton(VectorIcons.getTrashIcon(14, UITheme.DANGER_FG), UITheme.DANGER_BG);
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

    private JLabel createActionButton(Icon icon, Color bg) {
        JLabel btn = new JLabel(icon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
