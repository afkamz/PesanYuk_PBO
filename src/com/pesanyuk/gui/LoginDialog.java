package com.pesanyuk.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modal Login / Role Switcher sederhana untuk memilih peran Kasir atau Admin.
 */
public class LoginDialog extends JDialog {

    public interface LoginCallback {
        void onLoginSuccess(String role, String username);
    }

    private LoginCallback callback;
    private JTextField txtUsername;
    private JComboBox<String> cbRole;

    public LoginDialog(Frame parent, LoginCallback callback) {
        super(parent, "Login - PesanYuk POS", true);
        this.callback = callback;

        setSize(380, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel card = new RoundedPanel(16, Color.WHITE, UITheme.BORDER_COLOR, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        // Brand Icon & Header
        JLabel icon = new JLabel("PesanYuk", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        icon.setForeground(UITheme.PRIMARY_ORANGE);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Silakan pilih peran untuk masuk ke sistem", SwingConstants.CENTER);
        sub.setFont(UITheme.FONT_SUBTITLE);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(sub);
        card.add(Box.createRigidArea(new Dimension(0, 24)));

        // Input Username
        JLabel lblUser = new JLabel("Nama Pengguna (Username):");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = new JTextField("212121");
        txtUsername.setFont(UITheme.FONT_REGULAR);
        txtUsername.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(6, 10, 6, 10)));

        card.add(lblUser);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(txtUsername);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        // Input Peran (Role)
        JLabel lblRole = new JLabel("Peran (Role):");
        lblRole.setFont(UITheme.FONT_BOLD);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        cbRole = new JComboBox<>(new String[]{"Kasir", "Admin"});
        cbRole.setFont(UITheme.FONT_REGULAR);
        cbRole.setBackground(Color.WHITE);
        cbRole.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));

        cbRole.addActionListener(e -> {
            String sel = (String) cbRole.getSelectedItem();
            if ("Admin".equalsIgnoreCase(sel)) {
                txtUsername.setText("ueuue");
            } else {
                txtUsername.setText("212121");
            }
        });

        card.add(lblRole);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(cbRole);
        card.add(Box.createRigidArea(new Dimension(0, 26)));

        // Tombol Masuk
        PrimaryButton btnMasuk = new PrimaryButton("Masuk ke Aplikasi  ➜", 12);
        btnMasuk.setPreferredSize(new Dimension(Short.MAX_VALUE, 42));
        btnMasuk.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        btnMasuk.addActionListener(e -> {
            String role = (String) cbRole.getSelectedItem();
            String user = txtUsername.getText().trim();
            if (user.isEmpty()) user = "User";

            dispose();
            if (callback != null) {
                callback.onLoginSuccess(role, user);
            }
        });

        card.add(btnMasuk);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(20, 20, 20, 20));
        outer.add(card, BorderLayout.CENTER);

        add(outer, BorderLayout.CENTER);
    }
}
