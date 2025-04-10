package net.cheat.cat.ui.impl;

import net.cheat.cat.process.ProcessEntry;
import net.cheat.cat.process.utils.ProcessUtils;
import net.cheat.cat.storage.ProcessContainer;
import net.cheat.cat.ui.IUI;
import net.cheat.cat.ui.template.impl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class UIProcessSelector extends JPanel implements IUI {
    private JFrame frame;
    private TemplateText info;
    private TemplateTextBox filter_name;
    private TemplateButton select_p;
    private TemplateListBox proc_list;

    public UIProcessSelector() {
        filter_name = new TemplateTextBox(25, 40, 350, 30, "Enter A Process Name");
        info = new TemplateText("Select A Process", 150, 30, Color.WHITE, new Font("Segoe UI", Font.BOLD, 15), false);
        select_p = new TemplateButton("Confirm", 150, 320, 100, 30);
        proc_list = new TemplateListBox(10, 80, 375, 230, new String[]{"Pid", "Name"});

        select_p.setOnClickAction(new Runnable() {
            @Override
            public void run() {
                String[] selectedEntry = proc_list.getSelectedEntry();

                if (selectedEntry != null && selectedEntry.length > 0) {
                    String pid = selectedEntry[0];
                    ProcessContainer.set_pid(Integer.parseInt(pid));
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Please Select A Process.");
                }
            }
        });


        updateProcessList("");

        setLayout(null);
        setPreferredSize(new Dimension(600, 500));
        setVisible(true);
    }

    public void updateProcessList(String filter) {
        List<ProcessEntry> processEntries = ProcessUtils.getRunningProcesses();
        proc_list.clear();

        processEntries.stream()
                .sorted((a, b) -> {
                    int aScore = computeMatch(a.name.toLowerCase(), filter.toLowerCase());
                    int bScore = computeMatch(b.name.toLowerCase(), filter.toLowerCase());
                    return Integer.compare(bScore, aScore);
                })
                .filter(entry -> entry.name.toLowerCase().contains(filter.toLowerCase()))
                .forEach(entry -> proc_list.addEntry(String.valueOf(entry.pid), entry.name));
    }

    private int computeMatch(String name, String filter) {
        if (filter.isEmpty()) return 1;
        if (name.startsWith(filter)) return 100;
        if (name.contains(filter)) return 75;
        return 0;
    }

    @Override
    public void start() throws IOException {
        JFrame frame = new JFrame("Process Selector");

        UIProcessSelector panel = new UIProcessSelector();
        panel.frame = frame;
        frame.add(panel);
        frame.setSize(400, 410);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.setFocusable(true);
        panel.requestFocusInWindow();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                panel.filter_name.keyTyped(e);
                panel.updateProcessList(panel.filter_name.getText());
                panel.repaint();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                panel.filter_name.keyPressed(e);
                panel.updateProcessList(panel.filter_name.getText());
                panel.repaint();
            }
        });

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                panel.proc_list.mouseScrolled(e);
                panel.repaint();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                panel.repaint();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel.filter_name.mouseClicked(e.getX(), e.getY());
                panel.proc_list.mouseClicked(e);
                panel.select_p.mouse_clicked(e.getX(), e.getY());
                panel.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                panel.proc_list.mousePressed(e);
                panel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                panel.proc_list.mouseReleased(e);
                panel.repaint();
            }
        });
    }

    @Override
    public void stop() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        info.draw(g);
        select_p.draw(g);
        proc_list.draw(g);
        filter_name.draw(g);
    }
}
