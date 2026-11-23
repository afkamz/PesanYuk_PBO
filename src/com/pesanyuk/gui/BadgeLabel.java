package com.pesanyuk.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Label kapsul bersudut tumpul untuk kategori (Makanan/Minuman/Tersedia/Role).
 */
public class BadgeLabel extends JLabel {
    private Color bgColor;
    private Color fgColor;
    private int radius = 12;

    public BadgeLabel(String text, Color bg, Color fg) {
        super(text, SwingConstants.CENTER);
        this.bgColor = bg;
        this.fgColor = fg;
        setFont(UITheme.FONT_SMALL.deriveFont(Font.BOLD));
        setForeground(fg);
        setBorder(new EmptyBorder(3, 10, 3, 10));
        setOpaque(false);
    }

    public void setColors(Color bg, Color fg) {
        this.bgColor = bg;
        this.fgColor = fg;
        setForeground(fg);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
