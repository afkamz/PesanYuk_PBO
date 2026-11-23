package com.pesanyuk.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Tombol sekunder dengan latar belakang putih/abu-abu halus dan border rapi.
 */
public class SecondaryButton extends JButton {
    private int radius = 12;
    private Color bgColor = Color.WHITE;
    private Color hoverBg = new Color(0xF3, 0xF4, 0xF6);
    private Color borderColor = UITheme.BORDER_DARK;
    private boolean isHovered = false;

    public SecondaryButton(String text) {
        super(text);
        init();
    }

    public SecondaryButton(String text, int radius) {
        super(text);
        this.radius = radius;
        init();
    }

    private void init() {
        setFont(UITheme.FONT_BOLD);
        setForeground(UITheme.TEXT_DARK);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(isHovered ? hoverBg : bgColor);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
