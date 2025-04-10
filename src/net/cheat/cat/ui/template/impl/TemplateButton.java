package net.cheat.cat.ui.template.impl;

import net.cheat.cat.ui.template.Template;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TemplateButton implements Template {

    private int x, y, width, height;
    private String text;
    private BufferedImage image;
    private boolean hovered;
    private boolean useImage;
    private Runnable action;

    public TemplateButton(String text, int x, int y, int width, int height) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.useImage = false;
    }

    public TemplateButton(BufferedImage image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.useImage = true;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setOnClickAction(Runnable onClickAction) {
        this.action = onClickAction;
    }

    public boolean isInside(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    public void mouse_clicked(int mx, int my) {
        if (isInside(mx, my) && action != null) {
            action.run();
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color background = hovered ? new Color(75, 110, 175) : new Color(60, 63, 65);
        g2.setColor(background);
        g2.fillRoundRect(x, y, width, height, 20, 20);

        if (useImage && image != null) {
            int imgX = x + (width - image.getWidth()) / 2;
            int imgY = y + (height - image.getHeight()) / 2;
            g2.drawImage(image, imgX, imgY, null);
        } else if (text != null) {
            Color textColor = Color.WHITE;
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2.setColor(textColor);
            g2.drawString(text, x + (width - textWidth) / 2, y + (height + textHeight) / 2 - 4);
        }

        g2.dispose();
    }
}