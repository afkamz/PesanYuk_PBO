package com.pesanyuk.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Halaman Penuh Login yang tampil pertama kali saat aplikasi dibuka:
 * - Didesain simetris dan terpusat rapi (centered)
 * - Username default: "kasir" (untuk Kasir) dan "admin" (untuk Admin)
 * - Password default: "123"
 */
public class LoginPanel extends JPanel {

    public interface LoginListener {
        void onLoginSuccess(String role, String username);
    }

    private LoginListener loginListener;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JRadioButton rbKasir;
    private JRadioButton rbAdmin;

    public LoginPanel(LoginListener listener) {
        this.loginListener = listener;

        setLayout(new GridBagLayout());
        setBackground(UITheme.BG_MAIN);

        initComponents();
    }

    private void initComponents() {
        // Kartu Utama
        RoundedPanel card = new RoundedPanel(20, Color.WHITE, UITheme.BORDER_COLOR, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(440, 550));
        card.setMaximumSize(new Dimension(440, 550));
        card.setBorder(new EmptyBorder(36, 40, 36, 40));

        // 1. Logo Brand PesanYuk (Center)
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY_ORANGE);
                g2.fillRoundRect(2, 6, 6, 18, 4, 4);
                g2.fillRoundRect(11, 2, 6, 22, 4, 4);
                g2.fillRoundRect(20, 8, 6, 16, 4, 4);
                g2.dispose();
            }
        };
        logoIcon.setPreferredSize(new Dimension(28, 28));
        logoIcon.setOpaque(false);

        JLabel brandName = new JLabel("PesanYuk");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brandName.setForeground(UITheme.PRIMARY_ORANGE);

        brandPanel.add(logoIcon);
        brandPanel.add(brandName);

        // Header Text
        JLabel lblTitle = new JLabel("Masuk ke Sistem", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Silakan masukkan akun Anda untuk melanjutkan", SwingConstants.CENTER);
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(brandPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 26)));

        // Form Container Terpusat Simetris (Lebar 340px)
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.setMaximumSize(new Dimension(340, 320));
        formContainer.setPreferredSize(new Dimension(340, 320));

        // 2. Pemilihan Role (Kasir vs Admin)
        JLabel lblRole = new JLabel("Pilih Peran:");
        lblRole.setFont(UITheme.FONT_BOLD);
        lblRole.setForeground(UITheme.TEXT_DARK);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbKasir = new JRadioButton("Kasir", true);
        rbAdmin = new JRadioButton("Admin");
        ButtonGroup bgRole = new ButtonGroup();
        bgRole.add(rbKasir);
        bgRole.add(rbAdmin);

        for (JRadioButton rb : new JRadioButton[]{rbKasir, rbAdmin}) {
            rb.setFont(UITheme.FONT_REGULAR);
            rb.setBackground(Color.WHITE);
            rb.setFocusPainted(false);
            rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        rbKasir.addActionListener(e -> txtUsername.setText("kasir"));
        rbAdmin.addActionListener(e -> txtUsername.setText("admin"));

        JPanel roleGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        roleGroup.setOpaque(false);
        roleGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleGroup.setMaximumSize(new Dimension(340, 30));
        roleGroup.add(rbKasir);
        roleGroup.add(rbAdmin);

        formContainer.add(lblRole);
        formContainer.add(Box.createRigidArea(new Dimension(0, 6)));
        formContainer.add(roleGroup);
        formContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // 3. Input Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(UITheme.TEXT_DARK);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField("kasir");
        txtUsername.setFont(UITheme.FONT_REGULAR);
        txtUsername.setMaximumSize(new Dimension(340, 38));
        txtUsername.setPreferredSize(new Dimension(340, 38));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContainer.add(lblUser);
        formContainer.add(Box.createRigidArea(new Dimension(0, 6)));
        formContainer.add(txtUsername);
        formContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // 4. Input Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField("123");
        txtPassword.setFont(UITheme.FONT_REGULAR);
        txtPassword.setMaximumSize(new Dimension(340, 38));
        txtPassword.setPreferredSize(new Dimension(340, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContainer.add(lblPass);
        formContainer.add(Box.createRigidArea(new Dimension(0, 6)));
        formContainer.add(txtPassword);
        formContainer.add(Box.createRigidArea(new Dimension(0, 22)));

        // 5. Tombol Masuk
        PrimaryButton btnLogin = new PrimaryButton("Masuk ke Aplikasi  >", 12);
        btnLogin.setFont(UITheme.FONT_BOLD);
        btnLogin.setMaximumSize(new Dimension(340, 44));
        btnLogin.setPreferredSize(new Dimension(340, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> prosesLogin());

        txtPassword.addActionListener(e -> prosesLogin());
        txtUsername.addActionListener(e -> prosesLogin());

        formContainer.add(btnLogin);
        formContainer.add(Box.createRigidArea(new Dimension(0, 12)));

        // Info Demo
        JLabel lblNote = new JLabel("Demo: Kasir (kasir / 123) | Admin (admin / 123)", SwingConstants.CENTER);
        lblNote.setFont(UITheme.FONT_SMALL);
        lblNote.setForeground(UITheme.TEXT_LIGHT);
        lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(lblNote);

        card.add(formContainer);

        add(card);
    }

    private void prosesLogin() {
        String role = rbKasir.isSelected() ? "Kasir" : "Admin";
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

        // Login sukses
        if (loginListener != null) {
            loginListener.onLoginSuccess(role, username);
        }
    }

    public void resetForm() {
        rbKasir.setSelected(true);
        txtUsername.setText("kasir");
        txtPassword.setText("123");
    }
}
