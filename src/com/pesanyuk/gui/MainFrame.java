package com.pesanyuk.gui;

import com.pesanyuk.manager.MejaManager;
import com.pesanyuk.manager.MenuManager;
import com.pesanyuk.manager.OrderManager;
import com.pesanyuk.util.FileHandler;

import java.awt.*;
import javax.swing.*;

/**
 * Main Frame Aplikasi GUI PesanYuk.
 * Menampilkan Halaman Login terlebih dahulu, kemudian masuk ke Dashboard Kasir / Admin.
 * Didesain persis dengan mockup terbaru di folder Gambar/.
 */
public class MainFrame extends JFrame {

    private MenuManager menuManager;
    private MejaManager mejaManager;
    private OrderManager orderManager;

    private String currentRole = "Kasir";
    private String currentUsername = "kasir";

    // Layout Root
    private CardLayout rootCardLayout;
    private JPanel rootContainer;

    private LoginPanel loginPanel;
    private JPanel dashboardPanel;

    private SidebarPanel sidebarPanel;
    private JPanel contentContainer;
    private CardLayout contentCardLayout;

    private KasirViewPanel kasirView;
    private AdminMenuViewPanel adminMenuView;
    private AdminMejaViewPanel adminMejaView;
    private RiwayatViewPanel riwayatView;

    public MainFrame() {
        super("PesanYuk - Restaurant Point of Sale (POS)");
        inisialisasiData();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);

        initComponents();
    }

    private void inisialisasiData() {
        mejaManager = new MejaManager();
        menuManager = new MenuManager();
        orderManager = new OrderManager(mejaManager);

        try {
            java.util.ArrayList<com.pesanyuk.model.MenuItem> menuFile = FileHandler.muatMenu();
            if (menuFile.isEmpty()) {
                menuManager.inisialisasiMenuDefault();
            } else {
                for (com.pesanyuk.model.MenuItem item : menuFile) {
                    menuManager.tambah(item);
                }
            }

            java.util.ArrayList<com.pesanyuk.model.Meja> mejaFile = FileHandler.muatMeja();
            if (mejaFile.isEmpty()) {
                mejaManager.inisialisasiMejaDefault();
            } else {
                for (com.pesanyuk.model.Meja m : mejaFile) {
                    mejaManager.tambahMeja(m);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal membaca file data: " + e.getMessage());
            mejaManager.inisialisasiMejaDefault();
            menuManager.inisialisasiMenuDefault();
        }
    }

    private void initComponents() {
        rootCardLayout = new CardLayout();
        rootContainer = new JPanel(rootCardLayout);

        // 1. HALAMAN LOGIN
        loginPanel = new LoginPanel((role, username) -> {
            masukKeAplikasi(role, username);
        });

        // 2. DASHBOARD UTAMA (Sidebar Gelap + Area Konten)
        dashboardPanel = new JPanel(new BorderLayout());

        sidebarPanel = new SidebarPanel(currentRole, currentUsername, "NAV_KASIR", new SidebarPanel.NavListener() {
            @Override
            public void onNavSelected(String navId) {
                switchContentView(navId);
            }

            @Override
            public void onLogout() {
                keluarKeLogin();
            }
        });
        dashboardPanel.add(sidebarPanel, BorderLayout.WEST);

        contentCardLayout = new CardLayout();
        contentContainer = new JPanel(contentCardLayout);
        contentContainer.setBackground(UITheme.BG_MAIN);

        kasirView = new KasirViewPanel(menuManager, mejaManager, orderManager);
        adminMenuView = new AdminMenuViewPanel(menuManager);
        adminMejaView = new AdminMejaViewPanel(mejaManager);
        riwayatView = new RiwayatViewPanel(orderManager);

        contentContainer.add(kasirView, "NAV_KASIR");
        contentContainer.add(adminMenuView, "NAV_MENU");
        contentContainer.add(adminMejaView, "NAV_MEJA");
        contentContainer.add(riwayatView, "NAV_RIWAYAT");

        dashboardPanel.add(contentContainer, BorderLayout.CENTER);

        // Tambahkan ke root container
        rootContainer.add(loginPanel, "CARD_LOGIN");
        rootContainer.add(dashboardPanel, "CARD_DASHBOARD");

        setContentPane(rootContainer);

        // Tampilkan halaman login terlebih dahulu
        rootCardLayout.show(rootContainer, "CARD_LOGIN");
    }

    private void masukKeAplikasi(String role, String username) {
        this.currentRole = role;
        this.currentUsername = username;

        String targetNav = "Admin".equalsIgnoreCase(role) ? "NAV_MENU" : "NAV_KASIR";
        sidebarPanel.setRoleAndUser(role, username, targetNav);
        switchContentView(targetNav);

        rootCardLayout.show(rootContainer, "CARD_DASHBOARD");
    }

    private void keluarKeLogin() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin keluar dari akun ini?",
                "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            loginPanel.resetForm();
            rootCardLayout.show(rootContainer, "CARD_LOGIN");
        }
    }

    public void switchContentView(String navId) {
        if ("NAV_MENU".equals(navId)) {
            adminMenuView.refreshTable();
        } else if ("NAV_MEJA".equals(navId)) {
            adminMejaView.refreshMejaGrid();
        } else if ("NAV_RIWAYAT".equals(navId)) {
            riwayatView.refreshTable();
        }

        contentCardLayout.show(contentContainer, navId);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
            } catch (Exception ignored) {}

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
