package com.pesanyuk.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Palet warna, font, dan utilitas visual yang dirancang persis sesuai desain Figma PesanYuk.
 */
public final class UITheme {

    // Warna Utama & Aksen
    public static final Color PRIMARY_ORANGE = new Color(0xEA, 0x58, 0x0C); // #EA580C
    public static final Color PRIMARY_HOVER  = new Color(0xC2, 0x41, 0x0C); // #C2410C
    public static final Color PRIMARY_LIGHT  = new Color(0xFF, 0xF3, 0xEB); // #FFF3EB (Background nav aktif)

    // Backgrounds
    public static final Color BG_MAIN        = new Color(0xFB, 0xF8, 0xF5); // #FBF8F5 (Warm light background)
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color BORDER_COLOR   = new Color(0xF3, 0xF4, 0xF6); // #F3F4F6
    public static final Color BORDER_DARK    = new Color(0xE5, 0xE7, 0xEB); // #E5E7EB

    // Tipografi Colors
    public static final Color TEXT_DARK      = new Color(0x1F, 0x29, 0x37); // #1F2937
    public static final Color TEXT_MUTED     = new Color(0x6B, 0x72, 0x80); // #6B7280
    public static final Color TEXT_LIGHT     = new Color(0x9C, 0xA3, 0xAF); // #9CA3AF

    // Badge Colors: Makanan (Oranye Muda)
    public static final Color BADGE_MAKANAN_BG = new Color(0xFE, 0xEC, 0xE2);
    public static final Color BADGE_MAKANAN_FG = new Color(0xEA, 0x58, 0x0C);

    // Badge Colors: Minuman (Biru Muda)
    public static final Color BADGE_MINUMAN_BG = new Color(0xE0, 0xF2, 0xFE);
    public static final Color BADGE_MINUMAN_FG = new Color(0x02, 0x84, 0xC7);

    // Status Tersedia (Hijau Muda)
    public static final Color STATUS_SUCCESS_BG = new Color(0xDC, 0xFC, 0xE7);
    public static final Color STATUS_SUCCESS_FG = new Color(0x16, 0xA3, 0x4A);
    public static final Color STATUS_BOX_BG     = new Color(0xE6, 0xF8, 0xEF); // Box meja otomatis

    // Action Colors
    public static final Color DANGER_BG  = new Color(0xFE, 0xF2, 0xF2);
    public static final Color DANGER_FG  = new Color(0xEF, 0x44, 0x44);
    public static final Color EDIT_BG    = new Color(0xEF, 0xF6, 0xFF);
    public static final Color EDIT_FG    = new Color(0x3B, 0x82, 0xF6);

    // Fonts (Gunakan Inter/Segoe UI/SansSerif bawaan yang bersih)
    public static final Font FONT_TITLE      = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION    = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD       = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_PRICE_BIG  = new Font("Segoe UI", Font.BOLD, 20);

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public static String formatRupiah(double amount) {
        // Format bersih: Rp 18.000
        return String.format("Rp %,d", Math.round(amount)).replace(',', '.');
    }

    private UITheme() {}
}
