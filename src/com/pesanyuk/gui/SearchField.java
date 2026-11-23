package com.pesanyuk.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Text field rounded dengan placeholder teks mirip input web modern.
 */
public class SearchField extends JTextField {
    private String placeholder;
    private int radius = 12;

    public SearchField(String placeholder) {
        this.placeholder = placeholder;
        setFont(UITheme.FONT_REGULAR);
        setForeground(UITheme.TEXT_DARK);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 34, 8, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background putih
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        // Border halus
        g2.setColor(hasFocus() ? UITheme.PRIMARY_ORANGE : UITheme.BORDER_DARK);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        // Gambar Icon Kaca Pembesar Vector
        Icon searchIcon = VectorIcons.getSearchIcon(14, hasFocus() ? UITheme.PRIMARY_ORANGE : UITheme.TEXT_LIGHT);
        searchIcon.paintIcon(this, g2, 12, (getHeight() - 14) / 2);

        super.paintComponent(g);

        // Gambar placeholder jika teks kosong
        if (getText().isEmpty() && !hasFocus() && placeholder != null) {
            g2.setColor(UITheme.TEXT_LIGHT);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 34, y);
        }

        g2.dispose();
    }
}
