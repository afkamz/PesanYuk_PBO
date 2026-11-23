package com.pesanyuk.gui;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.manager.MejaManager;
import com.pesanyuk.model.Meja;
import com.pesanyuk.util.FileHandler;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Tampilan Kelola Meja: Grid visual meja dengan status Tersedia (Hijau) / Terisi (Merah).
 */
public class AdminMejaViewPanel extends JPanel {

    private MejaManager mejaManager;
    private JPanel gridContainer;
    private JLabel lblTotalMeja;
    private JLabel lblMejaKosong;
    private JLabel lblMejaTerisi;

    public AdminMejaViewPanel(MejaManager mejaManager) {
        this.mejaManager = mejaManager;

        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initComponents();
        refreshMejaGrid();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Kelola Meja Restoran");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Pantau keterisian meja dan atur kapasitas ruangan");
        lblSubtitle.setFont(UITheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(lblSubtitle);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);

        SecondaryButton btnResetSemua = new SecondaryButton("Kosongkan Semua Meja", 12);
        btnResetSemua.setPreferredSize(new Dimension(180, 40));
        btnResetSemua.addActionListener(e -> resetSemuaMeja());

        PrimaryButton btnTambahMeja = new PrimaryButton("+ Tambah Meja", 12);
        btnTambahMeja.setPreferredSize(new Dimension(140, 40));
        btnTambahMeja.addActionListener(e -> dialogTambahMeja());

        headerActions.add(btnResetSemua);
        headerActions.add(btnTambahMeja);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(headerActions, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main Card
        RoundedPanel cardPanel = new RoundedPanel(16, UITheme.CARD_BG, UITheme.BORDER_COLOR, 1);
        cardPanel.setLayout(new BorderLayout(0, 16));
        cardPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Ringkasan Status Meja (Badges)
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        statusBar.setOpaque(false);

        lblTotalMeja = new JLabel("Total Meja: 0");
        lblTotalMeja.setFont(UITheme.FONT_BOLD);

        lblMejaKosong = new BadgeLabel("0 Tersedia", UITheme.STATUS_SUCCESS_BG, UITheme.STATUS_SUCCESS_FG);
        lblMejaTerisi = new BadgeLabel("0 Terisi", UITheme.DANGER_BG, UITheme.DANGER_FG);

        statusBar.add(lblTotalMeja);
        statusBar.add(lblMejaKosong);
        statusBar.add(lblMejaTerisi);

        cardPanel.add(statusBar, BorderLayout.NORTH);

        // Grid Kartu Meja
        gridContainer = new JPanel(new GridLayout(0, 4, 16, 16));
        gridContainer.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(gridContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        cardPanel.add(scroll, BorderLayout.CENTER);

        add(cardPanel, BorderLayout.CENTER);
    }

    public void refreshMejaGrid() {
        gridContainer.removeAll();
        List<Meja> daftarMeja = mejaManager.getDaftarMeja();

        int kosong = 0;
        int terisi = 0;

        for (Meja m : daftarMeja) {
            if (m.isTerisi()) terisi++;
            else kosong++;

            gridContainer.add(buildMejaCard(m));
        }

        lblTotalMeja.setText("Total Meja: " + daftarMeja.size());
        lblMejaKosong.setText(kosong + " Tersedia");
        lblMejaTerisi.setText(terisi + " Terisi");

        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private JPanel buildMejaCard(Meja meja) {
        boolean isTerisi = meja.isTerisi();

        Color bgCard = isTerisi ? new Color(0xFE, 0xF2, 0xF2) : new Color(0xF0, 0xFD, 0xF4);
        Color borderCard = isTerisi ? new Color(0xFE, 0xCD, 0xCD) : new Color(0xBB, 0xF7, 0xD0);

        RoundedPanel card = new RoundedPanel(12, bgCard, borderCard, 1);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.setPreferredSize(new Dimension(160, 120));

        // Header: Nomor Meja & Badge Status
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblNo = new JLabel("Meja #" + meja.getNomor());
        lblNo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNo.setForeground(UITheme.TEXT_DARK);

        BadgeLabel badge = new BadgeLabel(
                isTerisi ? "Terisi" : "Tersedia",
                isTerisi ? UITheme.DANGER_BG : UITheme.STATUS_SUCCESS_BG,
                isTerisi ? UITheme.DANGER_FG : UITheme.STATUS_SUCCESS_FG
        );

        top.add(lblNo, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);

        // Center: Info Kapasitas
        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        center.setOpaque(false);

        JLabel lblCap = new JLabel("Kapasitas: " + meja.getKapasitas() + " Orang");
        lblCap.setFont(UITheme.FONT_REGULAR);
        lblCap.setForeground(UITheme.TEXT_MUTED);
        center.add(lblCap);

        // Bottom: Tombol Ganti Status Manual
        JButton btnToggle = new JButton(isTerisi ? "Kosongkan" : "Tandai Terisi");
        btnToggle.setFont(UITheme.FONT_SMALL);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.addActionListener(e -> {
            meja.setTerisi(!meja.isTerisi());
            simpanKeFile();
            refreshMejaGrid();
        });

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(btnToggle, BorderLayout.SOUTH);

        return card;
    }

    private void dialogTambahMeja() {
        JTextField txtNomor = new JTextField();
        JTextField txtKapasitas = new JTextField();

        Object[] fields = {
                "Nomor Meja:", txtNomor,
                "Kapasitas Kursi:", txtKapasitas
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Tambah Meja Baru", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int no = Integer.parseInt(txtNomor.getText().trim());
                int cap = Integer.parseInt(txtKapasitas.getText().trim());
                if (no <= 0 || cap <= 0) {
                    JOptionPane.showMessageDialog(this, "Nomor dan kapasitas harus lebih besar dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                mejaManager.tambahMeja(new Meja(no, cap));
                simpanKeFile();
                refreshMejaGrid();
                JOptionPane.showMessageDialog(this, "Meja #" + no + " berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input harus berupa angka bulat!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InputTidakValidException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetSemuaMeja() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Kosongkan semua meja kembali menjadi 'Tersedia'?",
                "Konfirmasi Reset", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            for (Meja m : mejaManager.getDaftarMeja()) {
                m.setTerisi(false);
            }
            simpanKeFile();
            refreshMejaGrid();
        }
    }

    private void simpanKeFile() {
        try {
            FileHandler.simpanMeja(mejaManager.getDaftarMeja());
        } catch (IOException ex) {
            System.err.println("Gagal menyimpan data meja: " + ex.getMessage());
        }
    }
}
