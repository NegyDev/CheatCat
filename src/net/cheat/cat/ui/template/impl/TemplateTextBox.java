package net.cheat.cat.ui.template.impl;

import net.cheat.cat.ui.template.Template;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TemplateTextBox implements Template {

    private int x, y, width, height;
    private StringBuilder text = new StringBuilder();
    private boolean focused = false;
    private String placeholder;

    public TemplateTextBox(int x, int y, int width, int height, String placeholder) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.placeholder = placeholder;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public boolean isInside(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    public void keyTyped(KeyEvent e) {
        if (!focused) return;

        char c = e.getKeyChar();
        if (Character.isISOControl(c)) return;

        if (text.length() < 30) {
            text.append(c);
        }
    }

    public void keyPressed(KeyEvent e) {
        if (!focused) return;

        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && text.length() > 0) {
            text.deleteCharAt(text.length() - 1);
        }
    }

    public String getText() {
        return text.toString();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(45, 45, 45));
        g2.fillRoundRect(x, y, width, height, 15, 15);

        g2.setColor(focused ? new Color(75, 110, 175) : new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, height, 15, 15);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        g2.setColor(text.length() == 0 ? new Color(150, 150, 150) : Color.WHITE);
        if (text.length() == 0 && !focused) {
            g2.drawString(placeholder, x + 10, y + height / 2 + 5);
        } else {
            g2.drawString(text.toString(), x + 10, y + height / 2 + 5);
        }

        g2.dispose();
    }

    public void mouseClicked(int mx, int my) {
        if (isInside(mx, my)) {
            focused = !focused;
        } else {
            focused = false;
        }
    }
}
