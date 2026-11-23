package com.pesanyuk.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Halaman Penuh Login persis seperti Mockup Figma Gambar/login.png:
 * - Latar belakang gelap (#262D35) dengan piring dekoratif di kiri dan kanan
 * - Card putih di tengah (terpusat simetris dan rapi)
 * - Logo PesanYuk dengan ikon Tudung Saji (Cloche) kuning-oranye
 * - Form input: Username, Password, Dropdown "Masuk sebagai" (Kasir / Admin)
 * - Tombol "masuk" oranye hangat membulat
 */
public class LoginPanel extends JPanel {

    public interface LoginListener {
        void onLoginSuccess(String role, String username);
    }

    private LoginListener loginListener;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;

    public LoginPanel(LoginListener listener) {
        this.loginListener = listener;

        setLayout(new GridBagLayout());
        setBackground(UITheme.SIDEBAR_BG);

        initComponents();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background gelap slate
        g2.setColor(UITheme.SIDEBAR_BG);
        g2.fillRect(0, 0, w, h);

        // Lukis dekorasi piring di tepi kiri dan kanan seperti mockup Gambar/login.png
        // Piring kiri (separuh piring nasi & daging)
        int plateSize = Math.min(380, h / 2 + 100);
        int yPlate = h / 2 - plateSize / 2;

        // Piring Kiri
        g2.setColor(new Color(0x33, 0x3C, 0x47));
        g2.fillOval(-plateSize / 2, yPlate, plateSize, plateSize);
        g2.setColor(new Color(0xE8, 0xEA, 0xED));
        g2.fillOval(-plateSize / 2 + 15, yPlate + 15, plateSize - 30, plateSize - 30);
        g2.setColor(new Color(0xD1, 0x82, 0x3E)); // Ilustrasi makanan nasi & beef
        g2.fillOval(-plateSize / 2 + 50, yPlate + 50, plateSize - 100, plateSize - 100);

        // Piring Kanan (pasta & steak)
        g2.setColor(new Color(0x33, 0x3C, 0x47));
        g2.fillOval(w - plateSize / 2, yPlate, plateSize, plateSize);
        g2.setColor(new Color(0xE8, 0xEA, 0xED));
        g2.fillOval(w - plateSize / 2 + 15, yPlate + 15, plateSize - 30, plateSize - 30);
        g2.setColor(new Color(0xDE, 0xA8, 0x56)); // Pasta
        g2.fillOval(w - plateSize / 2 + 50, yPlate + 50, plateSize - 100, plateSize - 100);

        g2.dispose();
    }

    private void initComponents() {
        // Kartu Utama Putih di Tengah
        RoundedPanel card = new RoundedPanel(24, Color.WHITE, new Color(0xEE, 0xF0, 0xF2), 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 520));
        card.setMaximumSize(new Dimension(420, 520));
        card.setBorder(new EmptyBorder(38, 44, 38, 44));

        // 1. Logo Brand PesanYuk (Center: Kotak cloche oranye + teks PesanYuk)
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY_ORANGE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        logoBadge.setLayout(new GridBagLayout());
        logoBadge.setPreferredSize(new Dimension(42, 42));
        logoBadge.setOpaque(false);
        logoBadge.add(new JLabel(VectorIcons.getClocheIcon(24, Color.WHITE)));

        JLabel brandName = new JLabel("PesanYuk");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brandName.setForeground(UITheme.TEXT_DARK);

        brandPanel.add(logoBadge);
        brandPanel.add(brandName);

        card.add(brandPanel);
        card.add(Box.createRigidArea(new Dimension(0, 32)));

        // Form Container
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.setMaximumSize(new Dimension(330, 330));
        formContainer.setPreferredSize(new Dimension(330, 330));

        // 2. Input Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(UITheme.TEXT_DARK);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField("kasir");
        txtUsername.setFont(UITheme.FONT_REGULAR);
        txtUsername.setMaximumSize(new Dimension(330, 42));
        txtUsername.setPreferredSize(new Dimension(330, 42));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContainer.add(lblUser);
        formContainer.add(Box.createRigidArea(new Dimension(0, 6)));
        formContainer.add(txtUsername);
        formContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 3. Input Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField("123");
        txtPassword.setFont(UITheme.FONT_REGULAR);
        txtPassword.setMaximumSize(new Dimension(330, 42));
        txtPassword.setPreferredSize(new Dimension(330, 42));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContainer.add(lblPass);
        formContainer.add(Box.createRigidArea(new Dimension(0, 6)));
        formContainer.add(txtPassword);
        formContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // 4. Dropdown "Masuk sebagai" (Kasir / Admin)
        JLabel lblRole = new JLabel("Masuk sebagai");
        lblRole.setFont(UITheme.FONT_BOLD);
        lblRole.setForeground(UITheme.TEXT_DARK);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbRole = new JComboBox<>(new String[]{"Kasir", "Admin"});
        cbRole.setFont(UITheme.FONT_REGULAR);
        cbRole.setMaximumSize(new Dimension(330, 42));
        cbRole.setPreferredSize(new Dimension(330, 42));
        cbRole.setBackground(Color.WHITE);
        cbRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbRole.addActionListener(e -> {
            String sel = (String) cbRole.getSelectedItem();
            if ("Admin".equalsIgnoreCase(sel)) {
                txtUsername.setText("admin");
            } else {
                txtUsername.setText("kasir");
            }
        });

        formContainer.add(lblRole);
        formContainer.add(Box.createRigidArea(new Dimension(0, 6)));
        formContainer.add(cbRole);
        formContainer.add(Box.createRigidArea(new Dimension(0, 26)));

        // 5. Tombol Masuk Oranye Hangat
        PrimaryButton btnLogin = new PrimaryButton("masuk", 14);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setMaximumSize(new Dimension(330, 44));
        btnLogin.setPreferredSize(new Dimension(330, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> prosesLogin());

        txtPassword.addActionListener(e -> prosesLogin());
        txtUsername.addActionListener(e -> prosesLogin());

        formContainer.add(btnLogin);
        card.add(formContainer);

        add(card);
    }

    private void prosesLogin() {
        String role = (String) cbRole.getSelectedItem();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama pengguna (username) tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if (loginListener != null) {
            loginListener.onLoginSuccess(role, username);
        }
    }

    public void resetForm() {
        cbRole.setSelectedItem("Kasir");
        txtUsername.setText("kasir");
        txtPassword.setText("123");
    }
}
