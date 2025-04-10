package net.cheat.cat.ui.template.impl;

import net.cheat.cat.ui.template.Template;

import java.awt.*;

public class TemplateText implements Template {

    private String text;
    private int x, y;
    private Color color;
    private Font font;
    private boolean shadow;

    public TemplateText(String text, int x, int y, Color color, Font font, boolean shadow) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.font = font;
        this.shadow = shadow;
    }


    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(font);
        if (shadow) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.drawString(text, x + 2, y + 2);
        }
        g2.setColor(color);
        g2.drawString(text, x, y);
        g2.dispose();
    }

    public Font getFont(){return this.font;}
    public String getText(){return this.text;}

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
