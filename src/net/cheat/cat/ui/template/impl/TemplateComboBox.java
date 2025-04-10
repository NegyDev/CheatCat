package net.cheat.cat.ui.template.impl;

import net.cheat.cat.ui.template.Template;

import java.awt.*;
import java.awt.event.MouseEvent;

public class TemplateComboBox implements Template {

    private int x, y, width, height;
    private String[] options;
    private int selectedIndex;
    private boolean isOpen = false;

    public TemplateComboBox(int x, int y, int width, int height, String[] options, int defaultIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.options = options;
        this.selectedIndex = defaultIndex;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isOpen) {
            g2.setColor(new Color(45, 45, 45));
            g2.fillRoundRect(x, y, width, height, 15, 15);
        } else {
            g2.setColor(new Color(45, 45, 45));
            g2.fillRect(x, y, width, height);
        }

        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(2));

        if (!isOpen) {
            g2.drawRoundRect(x, y, width, height, 15, 15);
        } else {
            g2.drawRect(x, y, width, height);
        }

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(Color.WHITE);

        String displayText = (selectedIndex == -1) ? "Select an option" : options[selectedIndex];
        g2.drawString(displayText, x + 10, y + height / 2 + 5);

        if (isOpen) {
            for (int i = 0; i < options.length; i++) {
                int itemY = y + height + (i * height);
                g2.setColor(new Color(45, 45, 45));
                g2.fillRect(x, itemY, width, height);

                g2.setColor(i == selectedIndex ? new Color(75, 110, 175) : new Color(80, 80, 80));
                g2.fillRect(x, itemY, width, height);

                g2.setColor(Color.WHITE);
                g2.drawString(options[i], x + 10, itemY + height / 2 + 5);
            }
        }
        g2.setColor(Color.WHITE);
        g2.drawLine(x + width - 15, y + height / 2, x + width - 5, y + height / 2);
        g2.drawLine(x + width - 10, y + height / 2 - 5, x + width - 10, y + height / 2 + 5);

        g2.dispose();
    }

    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (isInside(mx, my)) {
            isOpen = !isOpen;
        }

        if (isOpen) {
            for (int i = 0; i < options.length; i++) {
                int itemY = y + height + (i * height);
                if (mx >= x && mx <= x + width && my >= itemY && my <= itemY + height) {
                    selectedIndex = i;
                    isOpen = false;
                    break;
                }
            }
        }
    }

    private boolean isInside(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    public String getSelectedItem() {
        if (selectedIndex == -1) {
            return "No option selected";
        } else {
            return options[selectedIndex];
        }
    }
}
