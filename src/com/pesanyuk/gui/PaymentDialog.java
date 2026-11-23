package com.pesanyuk.gui;

import com.pesanyuk.model.Meja;
import com.pesanyuk.model.Pembayaran;
import com.pesanyuk.model.PembayaranQRIS;
import com.pesanyuk.model.PembayaranTunai;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Modal dialog pembayaran persis seperti Mockup Figma Gambar/kasir/payment.png & payment (1).png:
 * - Card putih rounded dengan header dan tombol close (X)
 * - Pilihan Tab [ Tunai ] (oranye) dan [ QRIS ] (outline)
 * - Box Total Pembayaran (kuning lembut)
 * - Tab Tunai: Input Uang Diterima & Box Hijau Kembalian
 * - Tab QRIS: QR Code visual + petunjuk scan
 * - Tombol "Konfirmasi Pembayaran" warna oranye emas membulat
 */
public class PaymentDialog extends JDialog {

    public interface PaymentCallback {
        void onPaymentSuccess(Pembayaran pembayaran);
    }

    private double totalTagihan;
    private Meja meja;
    private PaymentCallback callback;

    private boolean isTunaiMode = true;

    // Komponen Tab
    private JButton btnTabTunai;
    private JButton btnTabQris;
    private JPanel tabContentContainer;
    private CardLayout tabCardLayout;

    // Tunai
    private JTextField txtUangTunai;
    private JLabel lblKembalian;
    private PrimaryButton btnKonfirmasiTunai;

    public PaymentDialog(Window parent, double totalTagihan, Meja meja, PaymentCallback callback) {
        super(parent, "Pembayaran", ModalityType.APPLICATION_MODAL);
        this.totalTagihan = totalTagihan;
        this.meja = meja;
        this.callback = callback;

        setUndecorated(true); // Modal custom tanpa titlebar OS bawaan
        setSize(480, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0)); // Transparan untuk shadow/rounded

        initComponents();
    }

    private void initComponents() {
        RoundedPanel mainCard = new RoundedPanel(24, Color.WHITE, UITheme.BORDER_COLOR, 1);
        mainCard.setLayout(new BorderLayout(0, 16));
        mainCard.setBorder(new EmptyBorder(24, 28, 24, 28));

        // --- 1. HEADER (Judul + Subtitle + Tombol X) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Pembayaran");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Selesaikan transaksi pelanggan");
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(lblTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(lblSub);

        // Tombol Silang (X) Bulat Abu-abu
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
        btnClose.setPreferredSize(new Dimension(32, 32));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

        // --- 2. TAB SELECTION [ Tunai ] vs [ QRIS ] ---
        JPanel tabButtonsPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        tabButtonsPanel.setOpaque(false);
        tabButtonsPanel.setPreferredSize(new Dimension(0, 42));

        btnTabTunai = createTabButton("Tunai", true);
        btnTabQris = createTabButton("QRIS", false);

        btnTabTunai.addActionListener(e -> switchTab(true));
        btnTabQris.addActionListener(e -> switchTab(false));

        tabButtonsPanel.add(btnTabTunai);
        tabButtonsPanel.add(btnTabQris);

        // --- 3. BOX TOTAL PEMBAYARAN (Kuning Lembut) ---
        RoundedPanel boxTotal = new RoundedPanel(14, new Color(0xFF, 0xF7, 0xED), new Color(0xFF, 0xED, 0xD5), 1);
        boxTotal.setLayout(new BorderLayout());
        boxTotal.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblTotalTitle = new JLabel("Total Pembayaran");
        lblTotalTitle.setFont(UITheme.FONT_BOLD);
        lblTotalTitle.setForeground(UITheme.TEXT_MUTED);

        JLabel lblTotalVal = new JLabel(UITheme.formatRupiah(totalTagihan));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalVal.setForeground(UITheme.PRIMARY_ORANGE);

        boxTotal.add(lblTotalTitle, BorderLayout.WEST);
        boxTotal.add(lblTotalVal, BorderLayout.EAST);

        // Header Group (Header + Tabs + Box Total)
        JPanel northGroup = new JPanel();
        northGroup.setLayout(new BoxLayout(northGroup, BoxLayout.Y_AXIS));
        northGroup.setOpaque(false);
        northGroup.add(headerPanel);
        northGroup.add(Box.createRigidArea(new Dimension(0, 16)));
        northGroup.add(tabButtonsPanel);
        northGroup.add(Box.createRigidArea(new Dimension(0, 14)));
        northGroup.add(boxTotal);

        mainCard.add(northGroup, BorderLayout.NORTH);

        // --- 4. TAB BODY: TUNAI vs QRIS ---
        tabCardLayout = new CardLayout();
        tabContentContainer = new JPanel(tabCardLayout);
        tabContentContainer.setOpaque(false);

        tabContentContainer.add(buildTunaiContent(), "TUNAI");
        tabContentContainer.add(buildQrisContent(), "QRIS");

        mainCard.add(tabContentContainer, BorderLayout.CENTER);

        setContentPane(mainCard);
    }

    private JButton createTabButton(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(UITheme.PRIMARY_ORANGE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(UITheme.BORDER_DARK);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(active ? Color.WHITE : UITheme.TEXT_MUTED);
        return btn;
    }

    private void switchTab(boolean tunai) {
        this.isTunaiMode = tunai;
        if (tunai) {
            btnTabTunai.setForeground(Color.WHITE);
            btnTabQris.setForeground(UITheme.TEXT_MUTED);
            tabCardLayout.show(tabContentContainer, "TUNAI");
        } else {
            btnTabTunai.setForeground(UITheme.TEXT_MUTED);
            btnTabQris.setForeground(Color.WHITE);
            tabCardLayout.show(tabContentContainer, "QRIS");
        }
        btnTabTunai.repaint();
        btnTabQris.repaint();
    }

    private JPanel buildTunaiContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblUang = new JLabel("Uang Diterima");
        lblUang.setFont(UITheme.FONT_BOLD);
        lblUang.setForeground(UITheme.TEXT_DARK);
        lblUang.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUangTunai = new JTextField(String.valueOf((long) totalTagihan));
        txtUangTunai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtUangTunai.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        txtUangTunai.setPreferredSize(new Dimension(420, 44));
        txtUangTunai.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        txtUangTunai.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Box Hijau Kembalian
        RoundedPanel boxKembalian = new RoundedPanel(14, UITheme.STATUS_SUCCESS_BG, new Color(0xBB, 0xF7, 0xD0), 1);
        boxKembalian.setLayout(new BorderLayout());
        boxKembalian.setBorder(new EmptyBorder(14, 18, 14, 18));
        boxKembalian.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        boxKembalian.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblKembalianTitle = new JLabel("Kembalian");
        lblKembalianTitle.setFont(UITheme.FONT_BOLD);
        lblKembalianTitle.setForeground(UITheme.STATUS_SUCCESS_FG);

        lblKembalian = new JLabel("Rp. 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblKembalian.setForeground(UITheme.STATUS_SUCCESS_FG);

        boxKembalian.add(lblKembalianTitle, BorderLayout.WEST);
        boxKembalian.add(lblKembalian, BorderLayout.EAST);

        // Tombol Konfirmasi Pembayaran
        btnKonfirmasiTunai = new PrimaryButton("Konfirmasi Pembayaran", 14);
        btnKonfirmasiTunai.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnKonfirmasiTunai.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));
        btnKonfirmasiTunai.setPreferredSize(new Dimension(420, 46));
        btnKonfirmasiTunai.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnKonfirmasiTunai.addActionListener(e -> submitTunai());

        txtUangTunai.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { hitungKembalian(); }
            public void removeUpdate(DocumentEvent e) { hitungKembalian(); }
            public void changedUpdate(DocumentEvent e) { hitungKembalian(); }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblUang);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(txtUangTunai);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(boxKembalian);
        panel.add(Box.createRigidArea(new Dimension(0, 24)));
        panel.add(btnKonfirmasiTunai);

        hitungKembalian();

        return panel;
    }

    private void hitungKembalian() {
        try {
            double bayar = Double.parseDouble(txtUangTunai.getText().trim());
            double sisa = bayar - totalTagihan;
            if (sisa >= 0) {
                lblKembalian.setText(UITheme.formatRupiah(sisa));
                lblKembalian.setForeground(UITheme.STATUS_SUCCESS_FG);
                btnKonfirmasiTunai.setEnabled(true);
            } else {
                lblKembalian.setText("Kurang " + UITheme.formatRupiah(Math.abs(sisa)));
                lblKembalian.setForeground(UITheme.DANGER_FG);
                btnKonfirmasiTunai.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("Rp. 0");
            btnKonfirmasiTunai.setEnabled(false);
        }
    }

    private void submitTunai() {
        try {
            double bayar = Double.parseDouble(txtUangTunai.getText().trim());
            PembayaranTunai tunai = new PembayaranTunai(bayar);
            dispose();
            if (callback != null) {
                callback.onPaymentSuccess(tunai);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Jumlah uang tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildQrisContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Kotak Simulasi QRIS
        JPanel qrBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Pola QR Code
                g2.setColor(new Color(0x1F, 0x24, 0x2B));
                // 3 Kotak Sudut Utama
                g2.fillRect(20, 20, 34, 34);
                g2.fillRect(getWidth() - 54, 20, 34, 34);
                g2.fillRect(20, getHeight() - 54, 34, 34);

                g2.setColor(Color.WHITE);
                g2.fillRect(26, 26, 22, 22);
                g2.fillRect(getWidth() - 48, 26, 22, 22);
                g2.fillRect(26, getHeight() - 48, 22, 22);

                g2.setColor(new Color(0x1F, 0x24, 0x2B));
                g2.fillRect(31, 31, 12, 12);
                g2.fillRect(getWidth() - 43, 31, 12, 12);
                g2.fillRect(31, getHeight() - 43, 12, 12);

                // Pola Pixel Grid
                for (int x = 62; x < getWidth() - 58; x += 11) {
                    for (int y = 20; y < getHeight() - 20; y += 11) {
                        if ((x * 3 + y * 7) % 5 == 0) {
                            g2.fillRect(x, y, 8, 8);
                        }
                    }
                }
                g2.dispose();
            }
        };
        qrBox.setPreferredSize(new Dimension(170, 170));
        qrBox.setMaximumSize(new Dimension(170, 170));
        qrBox.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true));
        qrBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPetunjuk = new JLabel("Pindai untuk membayar", SwingConstants.CENTER);
        lblPetunjuk.setFont(UITheme.FONT_SMALL);
        lblPetunjuk.setForeground(UITheme.TEXT_MUTED);
        lblPetunjuk.setAlignmentX(Component.CENTER_ALIGNMENT);

        PrimaryButton btnKonfirmasiQRIS = new PrimaryButton("Konfirmasi Pembayaran", 14);
        btnKonfirmasiQRIS.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnKonfirmasiQRIS.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));
        btnKonfirmasiQRIS.setPreferredSize(new Dimension(420, 46));
        btnKonfirmasiQRIS.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKonfirmasiQRIS.addActionListener(e -> {
            PembayaranQRIS qris = new PembayaranQRIS("QRIS-" + System.currentTimeMillis() % 100000);
            dispose();
            if (callback != null) {
                callback.onPaymentSuccess(qris);
            }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(qrBox);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(lblPetunjuk);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(btnKonfirmasiQRIS);

        return panel;
    }
}
