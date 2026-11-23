package com.pesanyuk.gui;

import java.awt.*;
import javax.swing.*;

/**
 * Menyediakan Icon berbasis Java 2D Vector yang tajam dan dijamin 100% tidak akan
 * menjadi kotak kosong ("□" tofu box) di Windows maupun OS mana pun.
 */
public final class VectorIcons {

    private VectorIcons() {}

    /**
     * Icon Kaca Pembesar (Search)
     */
    public static Icon getSearchIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int r = (int) (size * 0.58);
                g2.drawOval(x + 1, y + 1, r, r);

                int x1 = x + 1 + (int) (r * 0.85);
                int y1 = y + 1 + (int) (r * 0.85);
                int x2 = x + size - 2;
                int y2 = y + size - 2;
                g2.drawLine(x1, y1, x2, y2);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Grid Kasir (4 kotak kecil 2x2)
     */
    public static Icon getGridIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                int boxSize = (size - 4) / 2;
                g2.fillRoundRect(x, y, boxSize, boxSize, 3, 3);
                g2.fillRoundRect(x + boxSize + 3, y, boxSize, boxSize, 3, 3);
                g2.fillRoundRect(x, y + boxSize + 3, boxSize, boxSize, 3, 3);
                g2.fillRoundRect(x + boxSize + 3, y + boxSize + 3, boxSize, boxSize, 3, 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Menu Bar (3 garis horizontal)
     */
    public static Icon getMenuIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int step = size / 4;
                g2.drawLine(x + 1, y + step, x + size - 1, y + step);
                g2.drawLine(x + 1, y + step * 2, x + size - 1, y + step * 2);
                g2.drawLine(x + 1, y + step * 3, x + size - 1, y + step * 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Meja Restoran (permukaan meja + 2 kaki)
     */
    public static Icon getTableIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Permukaan Meja
                g2.fillRoundRect(x + 1, y + 2, size - 2, 4, 2, 2);

                // Kaki Meja Kiri dan Kanan
                g2.drawLine(x + 3, y + 6, x + 3, y + size - 1);
                g2.drawLine(x + size - 4, y + 6, x + size - 4, y + size - 1);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Jam Riwayat
     */
    public static Icon getClockIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                g2.drawOval(x + 1, y + 1, size - 3, size - 3);

                int cx = x + size / 2;
                int cy = y + size / 2;
                g2.drawLine(cx, cy, cx, cy - (size / 4));
                g2.drawLine(cx, cy, cx + (size / 4), cy);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Keranjang / Receipt
     */
    public static Icon getReceiptIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Garis kertas struk
                int w = size - 4;
                int h = size - 2;
                g2.drawRoundRect(x + 2, y + 1, w, h, 3, 3);

                // Garis-garis tulisan di struk
                g2.drawLine(x + 5, y + 5, x + size - 5, y + 5);
                g2.drawLine(x + 5, y + 8, x + size - 5, y + 8);
                g2.drawLine(x + 5, y + 11, x + size - 8, y + 11);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Tong Sampah (Trash)
     */
    public static Icon getTrashIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Tutup sampah
                g2.drawLine(x + 2, y + 4, x + size - 2, y + 4);
                g2.drawLine(x + (size / 2) - 2, y + 2, x + (size / 2) + 2, y + 2);

                // Badan tong sampah
                g2.drawRoundRect(x + 3, y + 4, size - 6, size - 6, 2, 2);

                // Garis vertikal dalam tong
                g2.drawLine(x + (size / 2) - 2, y + 7, x + (size / 2) - 2, y + size - 4);
                g2.drawLine(x + (size / 2) + 2, y + 7, x + (size / 2) + 2, y + size - 4);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Pensil (Edit)
     */
    public static Icon getEditIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Garis diagonal pensil
                g2.drawLine(x + size - 3, y + 3, x + 5, y + size - 5);
                g2.drawLine(x + size - 5, y + 1, x + 3, y + size - 7);
                // Ujung pensil
                g2.drawLine(x + 3, y + size - 7, x + 1, y + size - 1);
                g2.drawLine(x + 1, y + size - 1, x + 5, y + size - 5);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Icon Keluar (Logout - Pintu + Panah)
     */
    public static Icon getLogoutIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Bingkai pintu
                g2.drawLine(x + 5, y + 2, x + 1, y + 2);
                g2.drawLine(x + 1, y + 2, x + 1, y + size - 2);
                g2.drawLine(x + 1, y + size - 2, x + 5, y + size - 2);

                // Panah keluar
                int cy = y + size / 2;
                g2.drawLine(x + 4, cy, x + size - 2, cy);
                g2.drawLine(x + size - 5, cy - 3, x + size - 2, cy);
                g2.drawLine(x + size - 5, cy + 3, x + size - 2, cy);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
}
