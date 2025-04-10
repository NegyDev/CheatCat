package net.cheat.cat.ui.template.impl;

import net.cheat.cat.ui.template.Template;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

public class TemplateListBox implements Template {

    private int x, y, width, height;
    private int scrollOffset = 0;
    private int rowHeight = 25;
    private int selectedIndex = -1;

    private List<String[]> allEntries = new ArrayList<>();
    private List<String[]> displayEntries = new ArrayList<>();
    private String[] headers;
    private Font font = new Font("Segoe UI", Font.PLAIN, 14);

    private Color headerColor = new Color(60, 60, 60);
    private Color rowColor = new Color(40, 40, 40);
    private Color selectedColor = new Color(70, 130, 180);
    private Color borderColor = Color.WHITE;
    private Color scrollBarColor = new Color(100, 100, 100, 180);
    private Color scrollBarThumbColor = new Color(180, 180, 180);

    private boolean draggingScrollBar = false;
    private int dragOffsetY;

    private int maxDisplayEntries = 100;

    public TemplateListBox(int x, int y, int width, int height, String[] headers) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.headers = headers;
    }

    public void setMaxDisplayEntries(int maxDisplayEntries) {
        this.maxDisplayEntries = maxDisplayEntries;
        updateDisplayEntries();
    }

    public void addEntry(String... values) {
        allEntries.add(values);
        updateDisplayEntries();
    }

    public void clear() {
        allEntries.clear();
        displayEntries.clear();
    }

    public List<String[]> getAllEntries() {
        return allEntries;
    }

    public List<String[]> getDisplayEntries() {
        return displayEntries;
    }

    public String[] getSelectedEntry() {
        if (selectedIndex >= 0 && selectedIndex < displayEntries.size()) {
            return displayEntries.get(selectedIndex);
        }
        return null;
    }

    private void updateDisplayEntries() {
        displayEntries.clear();

        int startIndex = scrollOffset;
        int endIndex = Math.min(allEntries.size(), startIndex + maxDisplayEntries);

        for (int i = startIndex; i < endIndex; i++) {
            displayEntries.add(allEntries.get(i));
        }
    }

    public void showEntriesRange(int startIndex, int count) {
        if (startIndex < 0 || startIndex >= allEntries.size()) {
            return;
        }

        displayEntries.clear();
        int endIndex = Math.min(allEntries.size(), startIndex + count);

        for (int i = startIndex; i < endIndex; i++) {
            displayEntries.add(allEntries.get(i));
        }

        scrollOffset = 0;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(borderColor);
        g2.drawRect(x, y, width, height);

        g2.setColor(headerColor);
        g2.fillRect(x, y, width, rowHeight);
        g2.setFont(font);
        g2.setColor(Color.WHITE);

        int headerCount = headers.length;
        int columnWidth = width / headerCount;
        for (int i = 0; i < headerCount; i++) {
            g2.drawString(headers[i], x + columnWidth * i + 10, y + 18);
        }

        int visibleRows = Math.min(displayEntries.size(), (height - rowHeight) / rowHeight);
        for (int i = 0; i < visibleRows; i++) {
            int index = i + scrollOffset;
            if (index >= displayEntries.size()) break;

            int rowY = y + rowHeight * (i + 1);
            String[] entry = displayEntries.get(index);

            if (index == selectedIndex) {
                g2.setColor(selectedColor);
            } else {
                g2.setColor(rowColor);
            }
            g2.fillRect(x, rowY, width, rowHeight);

            g2.setColor(Color.WHITE);
            for (int j = 0; j < Math.min(entry.length, headerCount); j++) {
                g2.drawString(entry[j], x + columnWidth * j + 10, rowY + 18);
            }
        }

        int totalRows = displayEntries.size();
        if (totalRows > visibleRows) {
            int scrollBarHeight = (int) ((height - rowHeight) * ((float) visibleRows / totalRows));
            int scrollBarY = y + rowHeight + (int) ((height - rowHeight - scrollBarHeight) * ((float) scrollOffset / (totalRows - visibleRows)));

            int scrollBarX = x + width - 8;

            g2.setColor(scrollBarColor);
            g2.fillRect(scrollBarX, y + rowHeight, 8, height - rowHeight);

            g2.setColor(scrollBarThumbColor);
            g2.fillRect(scrollBarX, scrollBarY, 8, scrollBarHeight);
        }
    }

    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (mouseX >= x && mouseX <= x + width &&
                mouseY >= y + rowHeight && mouseY <= y + height) {
            int relativeY = mouseY - y - rowHeight;
            int index = relativeY / rowHeight + scrollOffset;
            if (index >= 0 && index < displayEntries.size()) {
                selectedIndex = index;
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        int visibleRows = (height - rowHeight) / rowHeight;
        if (displayEntries.size() <= visibleRows) return;

        int mouseX = e.getX();
        int mouseY = e.getY();
        int scrollBarX = x + width - 8;

        int scrollBarHeight = (int) ((height - rowHeight) * ((float) visibleRows / displayEntries.size()));
        int scrollBarY = y + rowHeight + (int) ((height - rowHeight - scrollBarHeight) * ((float) scrollOffset / (displayEntries.size() - visibleRows)));

        if (mouseX >= scrollBarX && mouseX <= scrollBarX + 8 &&
                mouseY >= scrollBarY && mouseY <= scrollBarY + scrollBarHeight) {
            draggingScrollBar = true;
            dragOffsetY = mouseY - scrollBarY;
        }
    }

    public void mouseReleased(MouseEvent e) {
        draggingScrollBar = false;
    }

    public void mouseDragged(MouseEvent e) {
        if (draggingScrollBar) {
            int mouseY = e.getY();
            int visibleRows = (height - rowHeight) / rowHeight;

            int scrollBarTrackHeight = height - rowHeight;
            int scrollBarHeight = (int) (scrollBarTrackHeight * ((float) visibleRows / displayEntries.size()));
            int maxOffset = displayEntries.size() - visibleRows;

            int newY = mouseY - y - rowHeight - dragOffsetY;
            float percent = Math.max(0, Math.min(1f, (float) newY / (scrollBarTrackHeight - scrollBarHeight)));
            scrollOffset = (int) (percent * maxOffset);

            updateDisplayEntries();
        }
    }

    public void mouseScrolled(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        int visibleRows = (height - rowHeight) / rowHeight;
        int maxOffset = Math.max(0, displayEntries.size() - visibleRows);
        scrollOffset = Math.max(0, Math.min(scrollOffset + notches, maxOffset));
        updateDisplayEntries();
    }
    public void updateEntry(int index, String... newValues) {
        if (index >= 0 && index < allEntries.size()) {
            allEntries.set(index, newValues);
            updateDisplayEntries();
        }
    }
    public void updateEntry(String identifier, String... newValues) {
        for (int i = 0; i < allEntries.size(); i++) {
            String[] entry = allEntries.get(i);

            if (entry[0].equals(identifier)) {
                allEntries.set(i, newValues);
                updateDisplayEntries();
                break;
            }
        }
    }



    public boolean isInside(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }
}
