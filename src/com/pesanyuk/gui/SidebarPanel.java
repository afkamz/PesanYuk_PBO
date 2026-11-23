package com.pesanyuk.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Sidebar kiri persis seperti mockup Figma terbaru:
 * - Warna background Dark Charcoal (#262D35)
 * - Logo PesanYuk dengan ikon Tudung Saji (Cloche) oranye emas
 * - Section MENU UTAMA warna emas/oranye
 * - Navigasi aktif berupa pill putih rounded dengan icon & teks oranye
 * - Profil pengguna di bawah dengan avatar bulat & tombol Keluar
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
        setBackground(UITheme.SIDEBAR_BG);
        setPreferredSize(new Dimension(205, 720));
        setBorder(null);

        initComponents();
    }

    private void initComponents() {
        // --- BAGIAN ATAS (Logo & Menu) ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(24, 18, 20, 18));

        // 1. Logo Brand PesanYuk (Icon cloche oranye dalam rounded box)
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY_ORANGE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        logoBadge.setLayout(new GridBagLayout());
        logoBadge.setPreferredSize(new Dimension(32, 32));
        logoBadge.setOpaque(false);
        JLabel clocheLbl = new JLabel(VectorIcons.getClocheIcon(18, Color.WHITE));
        logoBadge.add(clocheLbl);

        JLabel brandName = new JLabel("PesanYuk");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandName.setForeground(Color.WHITE);

        brandPanel.add(logoBadge);
        brandPanel.add(brandName);

        topContainer.add(brandPanel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 36)));

        // 2. Section Header: MENU UTAMA (Oranye / Gold khas Mockup)
        JLabel sectionLabel = new JLabel("MENU UTAMA");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sectionLabel.setForeground(UITheme.PRIMARY_ORANGE);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topContainer.add(sectionLabel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 14)));

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
        bottomContainer.setBorder(new EmptyBorder(16, 18, 24, 18));

        // Garis tipis pemisah sebelum user profile
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x3E, 0x47, 0x54));
        sep.setBackground(new Color(0x3E, 0x47, 0x54));
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        bottomContainer.add(sep);
        bottomContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // Profil User Card
        JPanel userProfilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userProfilePanel.setOpaque(false);
        userProfilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar Bulat
        JLabel avatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFD, 0xBA, 0x74)); // Peach terang
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(new Color(0x78, 0x35, 0x0F));
                // Gambar ikon siluet user kecil
                g2.fillOval(getWidth() / 2 - 4, 8, 8, 8);
                g2.fillArc(getWidth() / 2 - 8, 17, 16, 14, 0, 180);
                g2.dispose();
            }
        };
        avatarLabel.setPreferredSize(new Dimension(36, 36));

        // Info User (Username + Role Pill)
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);

        JLabel usernameLabel = new JLabel(currentUsername != null ? currentUsername : "Fulan");
        usernameLabel.setFont(UITheme.FONT_BOLD);
        usernameLabel.setForeground(Color.WHITE);

        BadgeLabel roleBadge = new BadgeLabel(
                currentRole,
                new Color(0xFE, 0xEB, 0xDF),
                new Color(0xEA, 0x58, 0x0C)
        );

        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        userInfoPanel.add(roleBadge);

        userProfilePanel.add(avatarLabel);
        userProfilePanel.add(userInfoPanel);

        bottomContainer.add(userProfilePanel);
        bottomContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // Tombol Keluar (-> Keluar)
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoutPanel.setOpaque(false);
        logoutPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel logoutIcon = new JLabel(VectorIcons.getLogoutIcon(15, UITheme.PRIMARY_ORANGE));
        JLabel logoutText = new JLabel("Keluar");
        logoutText.setFont(UITheme.FONT_BOLD);
        logoutText.setForeground(UITheme.PRIMARY_ORANGE);

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
                logoutText.setForeground(Color.WHITE);
                logoutIcon.setIcon(VectorIcons.getLogoutIcon(15, Color.WHITE));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutText.setForeground(UITheme.PRIMARY_ORANGE);
                logoutIcon.setIcon(VectorIcons.getLogoutIcon(15, UITheme.PRIMARY_ORANGE));
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
        btn.setMaximumSize(new Dimension(170, 42));
        btn.setPreferredSize(new Dimension(170, 42));

        btn.addActionListener(e -> {
            setActiveNav(id);
            if (navListener != null) {
                navListener.onNavSelected(id);
            }
        });

        navButtons.add(btn);
        navItemsContainer.add(btn);
        navItemsContainer.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    public void setActiveNav(String navId) {
        this.activeNavId = navId;
        for (NavItemButton btn : navButtons) {
            btn.setActive(btn.navId.equals(navId));
        }
    }

    // Tombol Navigasi: Saat aktif berupa Pill Putih dengan Ikon & Teks Oranye
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
            setIconTextGap(12);
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
            // Aktif: Teks oranye di atas pill putih
            // Nonaktif: Teks abu-abu muda di atas background gelap
            Color color = isActive ? UITheme.PRIMARY_ORANGE : UITheme.TEXT_SIDEBAR;
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
                // Pill putih membulat khas mockup Figma
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            } else if (isHovered) {
                g2.setColor(UITheme.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
