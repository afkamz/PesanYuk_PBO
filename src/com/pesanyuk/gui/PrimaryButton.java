package com.pesanyuk.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Tombol oranye modern bersudut tumpul (pill button) dengan efek hover.
 */
public class PrimaryButton extends JButton {
    private int radius = 12;
    private Color normalColor = UITheme.PRIMARY_ORANGE;
    private Color hoverColor = UITheme.PRIMARY_HOVER;
    private boolean isHovered = false;

    public PrimaryButton(String text) {
        super(text);
        init();
    }

    public PrimaryButton(String text, int radius) {
        super(text);
        this.radius = radius;
        init();
    }

    private void init() {
        setFont(UITheme.FONT_BOLD);
        setForeground(Color.WHITE);
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

        if (!isEnabled()) {
            g2.setColor(new Color(0xD1, 0xD5, 0xDB));
        } else if (isHovered) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();

        super.paintComponent(g);
    }
}
