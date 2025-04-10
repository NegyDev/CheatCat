package net.cheat.cat.ui.template.impl;

import net.cheat.cat.ui.template.Template;

import java.awt.*;

public class TemplateCheckBox implements Template {

    private boolean checked;
    private String label;
    private int x, y;

    public TemplateCheckBox(String label, boolean checked, int x, int y) {
        this.label = label;
        this.checked = checked;
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString(label, x - 30, y + 15);

        g.setColor(Color.BLACK);
        g.fillRoundRect(x, y, 20, 20, 10, 10);

        if (checked) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(x + 2, y + 2, 16, 16, 8, 8);
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, 20, 20, 10, 10);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.drawRoundRect(x, y, 20, 20, 10, 10);
        }
    }

    public void toggle() {
        checked = !checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20;
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if (isInside(mouseX, mouseY)) {
            toggle();
        }
    }
}
