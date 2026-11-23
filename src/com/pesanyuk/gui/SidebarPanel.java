package com.pesanyuk.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Sidebar kiri persis seperti mockup Figma:
 * - Logo PesanYuk (oranye)
 * - Section MENU UTAMA
 * - Item Navigasi Kasir / Menu / Meja / Riwayat
 * - Profil pengguna & tombol Keluar di bagian bawah
 */
public class SidebarPanel extends JPanel {

    public interface NavListener {
        void onNavSelected(String navId);
        void onLogout();
    }

    private String currentRole; // "Kasir" atau "Admin"
    private String currentUsername;
    private String activeNavId;
    private NavListener navListener;

    private JPanel navItemsContainer;
    private List<NavItemButton> navButtons = new ArrayList<>();

    public SidebarPanel(String role, String username, String initialNavId, NavListener listener) {
        this.currentRole = role;
        this.currentUsername = username;
        this.activeNavId = initialNavId;
        this.navListener = listener;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(200, 700));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER_COLOR));

        initComponents();
    }

    private void initComponents() {
        // --- BAGIAN ATAS (Logo & Menu) ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(24, 20, 20, 20));

        // 1. Logo Brand PesanYuk
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icon oranye 3 bar
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY_ORANGE);
                // 3 batang oranye membulat
                g2.fillRoundRect(2, 6, 5, 16, 4, 4);
                g2.fillRoundRect(10, 2, 5, 20, 4, 4);
                g2.fillRoundRect(18, 8, 5, 14, 4, 4);
                g2.dispose();
            }
        };
        logoIcon.setPreferredSize(new Dimension(26, 26));
        logoIcon.setOpaque(false);

        JLabel brandName = new JLabel("PesanYuk");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandName.setForeground(UITheme.TEXT_DARK);

        brandPanel.add(logoIcon);
        brandPanel.add(brandName);

        topContainer.add(brandPanel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 32)));

        // 2. Section Header: MENU UTAMA
        JLabel sectionLabel = new JLabel("MENU UTAMA");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sectionLabel.setForeground(UITheme.TEXT_LIGHT);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topContainer.add(sectionLabel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. Tombol-tombol Navigasi
        navItemsContainer = new JPanel();
        navItemsContainer.setLayout(new BoxLayout(navItemsContainer, BoxLayout.Y_AXIS));
        navItemsContainer.setOpaque(false);
        navItemsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        buildNavItems();
        topContainer.add(navItemsContainer);

        add(topContainer, BorderLayout.NORTH);

        // --- BAGIAN BAWAH (Profil & Keluar) ---
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(new EmptyBorder(16, 20, 24, 20));

        // Profil User Card
        JPanel userProfilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userProfilePanel.setOpaque(false);
        userProfilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar Bulat
        String avatarChar = currentUsername != null && !currentUsername.isEmpty()
                ? currentUsername.substring(0, 1).toUpperCase() : "U";
        JLabel avatarLabel = new JLabel(avatarChar, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFF, 0xED, 0xD5)); // Peach muda
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarLabel.setPreferredSize(new Dimension(36, 36));
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatarLabel.setForeground(UITheme.PRIMARY_ORANGE);

        // Info User (Username + Role Pill)
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);

        JLabel usernameLabel = new JLabel(currentUsername != null ? currentUsername : "User");
        usernameLabel.setFont(UITheme.FONT_BOLD);
        usernameLabel.setForeground(UITheme.TEXT_DARK);

        BadgeLabel roleBadge = new BadgeLabel(
                currentRole,
                currentRole.equalsIgnoreCase("Admin") ? UITheme.BADGE_MAKANAN_BG : UITheme.BADGE_MINUMAN_BG,
                currentRole.equalsIgnoreCase("Admin") ? UITheme.BADGE_MAKANAN_FG : UITheme.BADGE_MINUMAN_FG
        );

        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        userInfoPanel.add(roleBadge);

        userProfilePanel.add(avatarLabel);
        userProfilePanel.add(userInfoPanel);

        bottomContainer.add(userProfilePanel);
        bottomContainer.add(Box.createRigidArea(new Dimension(0, 18)));

        // Tombol Keluar
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoutPanel.setOpaque(false);
        logoutPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel logoutIcon = new JLabel(VectorIcons.getLogoutIcon(15, UITheme.TEXT_MUTED));
        JLabel logoutText = new JLabel("Keluar");
        logoutText.setFont(UITheme.FONT_REGULAR);
        logoutText.setForeground(UITheme.TEXT_MUTED);

        logoutPanel.add(logoutIcon);
        logoutPanel.add(logoutText);

        logoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (navListener != null) {
                    navListener.onLogout();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logoutText.setForeground(UITheme.DANGER_FG);
                logoutIcon.setIcon(VectorIcons.getLogoutIcon(15, UITheme.DANGER_FG));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutText.setForeground(UITheme.TEXT_MUTED);
                logoutIcon.setIcon(VectorIcons.getLogoutIcon(15, UITheme.TEXT_MUTED));
            }
        });

        bottomContainer.add(logoutPanel);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    public void setRoleAndUser(String role, String username, String newActiveNav) {
        this.currentRole = role;
        this.currentUsername = username;
        this.activeNavId = newActiveNav;
        removeAll();
        initComponents();
        revalidate();
        repaint();
    }

    private void buildNavItems() {
        navButtons.clear();
        navItemsContainer.removeAll();

        if ("Admin".equalsIgnoreCase(currentRole)) {
            addNavButton("NAV_MENU", "Menu", "MENU");
            addNavButton("NAV_MEJA", "Meja", "MEJA");
            addNavButton("NAV_RIWAYAT", "Riwayat", "RIWAYAT");
        } else {
            // Kasir
            addNavButton("NAV_KASIR", "Kasir", "KASIR");
            addNavButton("NAV_RIWAYAT", "Riwayat", "RIWAYAT");
        }
    }

    private void addNavButton(String id, String label, String iconType) {
        NavItemButton btn = new NavItemButton(id, label, iconType, id.equals(activeNavId));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(160, 40));
        btn.setPreferredSize(new Dimension(160, 40));

        btn.addActionListener(e -> {
            setActiveNav(id);
            if (navListener != null) {
                navListener.onNavSelected(id);
            }
        });

        navButtons.add(btn);
        navItemsContainer.add(btn);
        navItemsContainer.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    public void setActiveNav(String navId) {
        this.activeNavId = navId;
        for (NavItemButton btn : navButtons) {
            btn.setActive(btn.navId.equals(navId));
        }
    }

    // Kelas internal untuk Tombol Navigasi dengan style Figma
    private static class NavItemButton extends JButton {
        private final String navId;
        private final String iconType;
        private boolean isActive;
        private boolean isHovered;

        public NavItemButton(String navId, String label, String iconType, boolean isActive) {
            super(label);
            this.navId = navId;
            this.iconType = iconType;
            this.isActive = isActive;

            setFont(UITheme.FONT_BOLD);
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(10);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            updateAppearance();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            updateAppearance();
            repaint();
        }

        private void updateAppearance() {
            Color color = isActive ? UITheme.PRIMARY_ORANGE : UITheme.TEXT_MUTED;
            setForeground(color);

            if ("KASIR".equals(iconType)) {
                setIcon(VectorIcons.getGridIcon(16, color));
            } else if ("MENU".equals(iconType)) {
                setIcon(VectorIcons.getMenuIcon(16, color));
            } else if ("MEJA".equals(iconType)) {
                setIcon(VectorIcons.getTableIcon(16, color));
            } else if ("RIWAYAT".equals(iconType)) {
                setIcon(VectorIcons.getClockIcon(16, color));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(UITheme.PRIMARY_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            } else if (isHovered) {
                g2.setColor(new Color(0xF9, 0xFA, 0xFB));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
