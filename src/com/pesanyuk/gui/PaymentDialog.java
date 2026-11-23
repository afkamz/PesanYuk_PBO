package com.pesanyuk.gui;

import com.pesanyuk.model.Meja;
import com.pesanyuk.model.Pembayaran;
import com.pesanyuk.model.PembayaranQRIS;
import com.pesanyuk.model.PembayaranTunai;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Modal dialog pembayaran modern dengan pilihan Tunai (hitung kembalian live) dan QRIS (simulasi QR).
 */
public class PaymentDialog extends JDialog {

    public interface PaymentCallback {
        void onPaymentSuccess(Pembayaran pembayaran);
    }

    private double totalTagihan;
    private Meja meja;
    private PaymentCallback callback;

    private JTextField txtUangTunai;
    private JLabel lblKembalian;
    private PrimaryButton btnBayarTunai;

    public PaymentDialog(Window parent, double totalTagihan, Meja meja, PaymentCallback callback) {
        super(parent, "Proses Pembayaran", ModalityType.APPLICATION_MODAL);
        this.totalTagihan = totalTagihan;
        this.meja = meja;
        this.callback = callback;

        setSize(460, 540);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 16));

        initComponents();
    }

    private void initComponents() {
        // --- 1. Header (Info Total & Meja) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel("Konfirmasi Pembayaran");
        lblTitle.setFont(UITheme.FONT_SECTION);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblMeja = new JLabel("Nomor Meja: #" + (meja != null ? meja.getNomor() : "-"));
        lblMeja.setFont(UITheme.FONT_REGULAR);
        lblMeja.setForeground(UITheme.TEXT_MUTED);

        JLabel lblTotalText = new JLabel("Total yang harus dibayar:");
        lblTotalText.setFont(UITheme.FONT_SMALL);
        lblTotalText.setForeground(UITheme.TEXT_MUTED);

        JLabel lblTotalVal = new JLabel(UITheme.formatRupiah(totalTagihan));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalVal.setForeground(UITheme.PRIMARY_ORANGE);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblMeja);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        headerPanel.add(lblTotalText);
        headerPanel.add(lblTotalVal);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Body Tabs: Tunai vs QRIS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("💵  Tunai (Cash)", buildTunaiTab());
        tabbedPane.addTab("📱  QRIS (Digital)", buildQrisTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildTunaiTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblInputUang = new JLabel("Jumlah Uang Diterima (Rp):");
        lblInputUang.setFont(UITheme.FONT_REGULAR);
        lblInputUang.setForeground(UITheme.TEXT_DARK);

        txtUangTunai = new JTextField();
        txtUangTunai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtUangTunai.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY_ORANGE, 1, true),
                new EmptyBorder(8, 12, 8, 12)));

        // Quick Amount Buttons
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        quickPanel.setOpaque(false);

        JButton btnPas = new JButton("Uang Pas");
        JButton btn50k = new JButton("+50rb");
        JButton btn100k = new JButton("+100rb");

        for (JButton b : new JButton[]{btnPas, btn50k, btn100k}) {
            b.setFont(UITheme.FONT_SMALL);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        btnPas.addActionListener(e -> txtUangTunai.setText(String.valueOf((long) totalTagihan)));
        btn50k.addActionListener(e -> tambahUangQuick(50000));
        btn100k.addActionListener(e -> tambahUangQuick(100000));

        quickPanel.add(btnPas);
        quickPanel.add(btn50k);
        quickPanel.add(btn100k);

        // Baris Kembalian
        JPanel kembalianPanel = new JPanel(new BorderLayout());
        kembalianPanel.setBackground(new Color(0xF9, 0xFA, 0xFB));
        kembalianPanel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel lblKembalianTitle = new JLabel("Kembalian:");
        lblKembalianTitle.setFont(UITheme.FONT_BOLD);
        lblKembalianTitle.setForeground(UITheme.TEXT_DARK);

        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblKembalian.setForeground(UITheme.STATUS_SUCCESS_FG);

        kembalianPanel.add(lblKembalianTitle, BorderLayout.WEST);
        kembalianPanel.add(lblKembalian, BorderLayout.EAST);

        // Tombol Bayar
        btnBayarTunai = new PrimaryButton("Selesaikan Pembayaran Tunai", 12);
        btnBayarTunai.setPreferredSize(new Dimension(300, 42));
        btnBayarTunai.setEnabled(false);
        btnBayarTunai.addActionListener(e -> submitTunai());

        txtUangTunai.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { hitungKembalian(); }
            public void removeUpdate(DocumentEvent e) { hitungKembalian(); }
            public void changedUpdate(DocumentEvent e) { hitungKembalian(); }
        });

        panel.add(lblInputUang);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(txtUangTunai);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(quickPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(kembalianPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(btnBayarTunai);

        return panel;
    }

    private void tambahUangQuick(double nominal) {
        try {
            double cur = txtUangTunai.getText().isEmpty() ? 0 : Double.parseDouble(txtUangTunai.getText());
            txtUangTunai.setText(String.valueOf((long) (cur + nominal)));
        } catch (NumberFormatException ignored) {
            txtUangTunai.setText(String.valueOf((long) nominal));
        }
    }

    private void hitungKembalian() {
        try {
            double bayar = Double.parseDouble(txtUangTunai.getText().trim());
            double sisa = bayar - totalTagihan;
            if (sisa >= 0) {
                lblKembalian.setText(UITheme.formatRupiah(sisa));
                lblKembalian.setForeground(UITheme.STATUS_SUCCESS_FG);
                btnBayarTunai.setEnabled(true);
            } else {
                lblKembalian.setText("Kurang " + UITheme.formatRupiah(Math.abs(sisa)));
                lblKembalian.setForeground(UITheme.DANGER_FG);
                btnBayarTunai.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("Rp 0");
            btnBayarTunai.setEnabled(false);
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

    private JPanel buildQrisTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Simulasi Kotak QR Code
        JPanel qrBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Pattern QR simulasi
                g2.setColor(Color.BLACK);
                g2.fillRect(15, 15, 30, 30);
                g2.fillRect(getWidth() - 45, 15, 30, 30);
                g2.fillRect(15, getHeight() - 45, 30, 30);

                g2.setColor(Color.WHITE);
                g2.fillRect(22, 22, 16, 16);
                g2.fillRect(getWidth() - 38, 22, 16, 16);
                g2.fillRect(22, getHeight() - 38, 16, 16);

                g2.setColor(Color.BLACK);
                g2.fillRect(26, 26, 8, 8);
                g2.fillRect(getWidth() - 34, 26, 8, 8);
                g2.fillRect(26, getHeight() - 34, 8, 8);

                // Pixel pattern tengah
                for (int x = 55; x < getWidth() - 50; x += 12) {
                    for (int y = 20; y < getHeight() - 20; y += 12) {
                        if ((x + y) % 3 == 0) {
                            g2.fillRect(x, y, 7, 7);
                        }
                    }
                }
                g2.dispose();
            }
        };
        qrBox.setPreferredSize(new Dimension(140, 140));
        qrBox.setMaximumSize(new Dimension(140, 140));
        qrBox.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1));
        qrBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPetunjuk = new JLabel("Scan QRIS Menggunakan Aplikasi Mobile Banking / E-Wallet");
        lblPetunjuk.setFont(UITheme.FONT_SMALL);
        lblPetunjuk.setForeground(UITheme.TEXT_MUTED);
        lblPetunjuk.setAlignmentX(Component.CENTER_ALIGNMENT);

        String nmidCode = "NMID-" + (System.currentTimeMillis() % 100000000);
        JLabel lblRef = new JLabel("Kode Referensi: " + nmidCode);
        lblRef.setFont(UITheme.FONT_BOLD);
        lblRef.setForeground(UITheme.PRIMARY_ORANGE);
        lblRef.setAlignmentX(Component.CENTER_ALIGNMENT);

        PrimaryButton btnKonfirmasiQRIS = new PrimaryButton("Konfirmasi Pembayaran QRIS Berhasil", 12);
        btnKonfirmasiQRIS.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKonfirmasiQRIS.setPreferredSize(new Dimension(320, 42));
        btnKonfirmasiQRIS.setMaximumSize(new Dimension(320, 42));
        btnKonfirmasiQRIS.addActionListener(e -> {
            PembayaranQRIS qris = new PembayaranQRIS(nmidCode);
            dispose();
            if (callback != null) {
                callback.onPaymentSuccess(qris);
            }
        });

        panel.add(qrBox);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblPetunjuk);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(lblRef);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(btnKonfirmasiQRIS);

        return panel;
    }
}
