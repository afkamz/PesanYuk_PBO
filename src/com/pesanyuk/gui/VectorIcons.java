package com.pesanyuk.gui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Pustaka Icon Vektor murni digambar dengan Java Graphics2D.
 * Menghasilkan tampilan yang tajam di semua DPI dan bebas masalah kotak tanda tanya (tofu box) di Windows.
 */
public final class VectorIcons {

    private VectorIcons() {}

    /**
     * Logo resmi PesanYuk: Cloche (Tudung Saji Restoran) oranye emas.
     */
    public static Icon getClocheIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                float scale = size / 24.0f;
                g2.translate(x, y);
                g2.scale(scale, scale);

                // Handle tudung saji di atas (lingkaran kecil)
                g2.fillOval(10, 4, 4, 4);

                // Kubah tudung saji (arc)
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(4, 7, 16, 14, 0, 180);

                // Garis piring di bawah
                g2.drawLine(2, 16, 22, 16);

                // Kaki piring
                g2.drawLine(4, 18, 20, 18);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Grid (4 kotak) untuk Menu Kasir
     */
    public static Icon getGridIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(1, 1, 5, 5, 2, 2);
                g2.drawRoundRect(9, 1, 5, 5, 2, 2);
                g2.drawRoundRect(1, 9, 5, 5, 2, 2);
                g2.drawRoundRect(9, 9, 5, 5, 2, 2);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Riwayat (Panah memutar / Counter-Clockwise Arrow dengan jarum jam)
     */
    public static Icon getClockIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Lingkaran terbuka riwayat
                g2.drawArc(2, 2, 12, 12, 45, 270);
                // Jarum jam
                g2.drawLine(8, 8, 8, 5);
                g2.drawLine(8, 8, 11, 8);
                // Ujung panah
                g2.drawLine(2, 6, 2, 2);
                g2.drawLine(2, 2, 6, 2);

                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Meja Restoran
     */
    public static Icon getTableIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(2, 5, 14, 5); // Meja atas
                g2.drawLine(4, 5, 4, 14); // Kaki kiri
                g2.drawLine(12, 5, 12, 14); // Kaki kanan
                g2.drawLine(4, 9, 12, 9); // Palang
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Kelola Menu (List bar / Sendok Garpu)
     */
    public static Icon getMenuIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(2, 4, 14, 4);
                g2.drawLine(2, 8, 14, 8);
                g2.drawLine(2, 12, 14, 12);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Search Kaca Pembesar
     */
    public static Icon getSearchIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(2, 2, 8, 8);
                g2.drawLine(8, 8, 14, 14);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Kertas Nota / Keranjang Pesanan (Kuning di mockup)
     */
    public static Icon getReceiptIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Dokumen
                g2.drawRoundRect(2, 1, 12, 14, 2, 2);
                // Garis teks di dokumen
                g2.drawLine(5, 5, 11, 5);
                g2.drawLine(5, 8, 11, 8);
                g2.drawLine(5, 11, 9, 11);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Tempat Sampah (Merah)
     */
    public static Icon getTrashIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(3, 4, 13, 4); // Garis tutup
                g2.drawLine(6, 2, 10, 2); // Pegangan tutup
                g2.drawLine(4, 4, 5, 14); // Sisi kiri
                g2.drawLine(12, 4, 11, 14); // Sisi kanan
                g2.drawLine(5, 14, 11, 14); // Dasar
                g2.drawLine(7, 7, 7, 11); // Garis dalam
                g2.drawLine(9, 7, 9, 11);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Edit Pensil (Hijau)
     */
    public static Icon getEditIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(10, 2, 14, 6);
                g2.drawLine(10, 2, 3, 9);
                g2.drawLine(14, 6, 7, 13);
                g2.drawLine(3, 9, 2, 14);
                g2.drawLine(2, 14, 7, 13);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Keluar (Panah Oranye)
     */
    public static Icon getLogoutIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Pintu / Box
                g2.drawLine(7, 2, 2, 2);
                g2.drawLine(2, 2, 2, 14);
                g2.drawLine(2, 14, 7, 14);
                // Panah keluar
                g2.drawLine(6, 8, 14, 8);
                g2.drawLine(11, 5, 14, 8);
                g2.drawLine(11, 11, 14, 8);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Kalender
     */
    public static Icon getCalendarIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(2, 3, 12, 11, 2, 2);
                g2.drawLine(2, 6, 14, 6);
                g2.drawLine(5, 1, 5, 4);
                g2.drawLine(11, 1, 11, 4);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Cetak / Printer
     */
    public static Icon getPrintIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                float s = size / 16.0f;
                g2.translate(x, y);
                g2.scale(s, s);

                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Kertas atas
                g2.drawRect(4, 2, 8, 4);
                // Body printer
                g2.drawRoundRect(2, 6, 12, 6, 2, 2);
                // Kertas keluar bawah
                g2.drawRect(4, 9, 8, 5);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
}
