package com.pesanyuk.gui;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Palet warna, tipografi, dan konstanta desain PesanYuk
 * yang disesuaikan persis dengan Mockup Figma terbaru di folder Gambar/.
 */
public final class UITheme {

    // --- Warna Utama & Aksen Brand ---
    public static final Color PRIMARY_ORANGE = new Color(0xEA, 0xA3, 0x44); // #EAA344 (Gold/Oranye Hangat khas Mockup)
    public static final Color PRIMARY_HOVER  = new Color(0xD8, 0x93, 0x36);
    public static final Color PRIMARY_LIGHT  = new Color(0xFF, 0xF7, 0xED); // Cream sangat muda

    // --- Dark Sidebar & Dark Banner Header ---
    public static final Color SIDEBAR_BG     = new Color(0x26, 0x2D, 0x35); // #262D35 (Dark slate/charcoal)
    public static final Color SIDEBAR_HOVER  = new Color(0x32, 0x3A, 0x44);
    public static final Color BANNER_BG      = new Color(0x1F, 0x24, 0x2B); // #1F242B (Banner gelap atas)
    public static final Color DARK_BUTTON_BG = new Color(0x26, 0x2D, 0x35); // Tombol gelap seperti "+ Tambah Menu"

    // --- Backgrounds Konten ---
    public static final Color BG_MAIN        = new Color(0xEB, 0xEE, 0xF1); // #EBEEF1 (Abu-abu lembut modern)
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color BORDER_COLOR   = new Color(0xEE, 0xF0, 0xF2);
    public static final Color BORDER_DARK    = new Color(0xDC, 0xE0, 0xE5);

    // --- Tipografi Colors ---
    public static final Color TEXT_DARK      = new Color(0x1E, 0x25, 0x2E);
    public static final Color TEXT_MUTED     = new Color(0x6E, 0x77, 0x87);
    public static final Color TEXT_LIGHT     = new Color(0x9E, 0xA7, 0xB5);
    public static final Color TEXT_SIDEBAR   = new Color(0xD1, 0xD7, 0xDE); // Teks menu sidebar

    // --- Badges Kategori ---
    public static final Color BADGE_MAKANAN_BG = new Color(0xFE, 0xEB, 0xDF); // Peach lembut
    public static final Color BADGE_MAKANAN_FG = new Color(0xEA, 0x58, 0x0C); // Oranye teks

    public static final Color BADGE_MINUMAN_BG = new Color(0xDF, 0xF0, 0xFD); // Biru muda pastel
    public static final Color BADGE_MINUMAN_FG = new Color(0x02, 0x84, 0xC7); // Biru teks

    // --- Status Tersedia / Meja (Hijau Muda) ---
    public static final Color STATUS_SUCCESS_BG = new Color(0xD8, 0xF6, 0xE4);
    public static final Color STATUS_SUCCESS_FG = new Color(0x15, 0x98, 0x48);

    // --- Action & Error ---
    public static final Color DANGER_BG  = new Color(0xFE, 0xF0, 0xF0);
    public static final Color DANGER_FG  = new Color(0xEF, 0x44, 0x44);
    public static final Color EDIT_BG    = new Color(0xF0, 0xFD, 0xF4);
    public static final Color EDIT_FG    = new Color(0x16, 0xA3, 0x4A); // Hijau untuk edit sesuai mockup admin

    // --- Fonts ---
    public static final Font FONT_BANNER     = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_TITLE      = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION    = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD       = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_PRICE_BIG  = new Font("Segoe UI", Font.BOLD, 22);

    public static String formatRupiah(double amount) {
        return String.format("Rp. %,d", Math.round(amount)).replace(',', '.');
    }

    private UITheme() {}
}
