package net.cheat.cat.ui.impl;

import net.cheat.cat.process.utils.ProcessUtils;
import net.cheat.cat.process.utils.mem.MemoryEntry;
import net.cheat.cat.process.utils.mem.MemoryUtils;
import net.cheat.cat.storage.ProcessContainer;
import net.cheat.cat.ui.IUI;
import net.cheat.cat.ui.template.impl.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.TimerTask;

public class UIMainMenu extends JPanel implements IUI {

    private JFrame frame;
    private TemplateButton proc_select;
    private TemplateButton new_scan;
    private TemplateButton next_scan;
    private TemplateButton memory_view;
    private TemplateButton add_selected_index;
    private TemplateButton add_adress_manually;
    private TemplateText selected_proc_info;
    private TemplateText scan_result_info;
    private TemplateText scan_type_lbl;
    private TemplateText value_type_lbl;
    private TemplateListBox scan_result;
    private TemplateListBox selected_adress_list;
    private TemplateTextBox scan_value;
    private TemplateCheckBox if_hex;
    private TemplateComboBox scan_type;
    private TemplateComboBox value_type;
    private TemplateImage logo;

    public void setupListeners(){
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //At 2 second intervals, we update the values at the addresses in our list. in this way, we can monitor the value changes instantly.
                if (!scan_result.getAllEntries().isEmpty()) {
                    List<String[]> addresses = scan_result.getAllEntries();
                    for (String[] entry : addresses) {
                        String address = entry[0];
                        String previousValue = entry[1];
                        String currentValue =  String.valueOf(MemoryUtils.getValueFromAddress(ProcessContainer.get_pid(),address,value_type.getSelectedItem()));
                        scan_result.updateEntry(address, address,currentValue,previousValue);
                    }
                    repaint();
                }
            }
        }, 0, 2000);
        proc_select.setOnClickAction(new Runnable() {
            @Override
            public void run() {
                UIProcessSelector processSelector = new UIProcessSelector();
                try {
                    processSelector.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        new_scan.setOnClickAction(new Runnable() {
            @Override
            public void run() {
                if(scan_value.getText().isEmpty())return;
                if(ProcessContainer.get_pid() == 0)return;
                scan_result.clear();
                List<MemoryEntry> scan_res = MemoryUtils.scan(ProcessContainer.get_pid(),value_type.getSelectedItem(),scan_value.getText());
                for(MemoryEntry entry : scan_res){
                    scan_result.addEntry(String.valueOf(entry.getAddress()),String.valueOf(entry.getValue()), "");
                }
            }
        });
    }

    @Override
    public void start() throws IOException {
        frame = new JFrame("Cheat Cat | Build Version 1.0");
        frame.setSize(900, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(this);
        InputStream iconStream = ClassLoader.getSystemResourceAsStream("resources/logo.png");
        if (iconStream != null) {
            Image icon = ImageIO.read(iconStream);
            frame.setIconImage(icon);
        }
        frame.setVisible(true);
        scan_result_info = new TemplateText("Found: ",10,80,Color.WHITE,new Font("Segoe UI", Font.BOLD, 15),false);
        selected_proc_info = new TemplateText("Select Process",420,40,Color.WHITE,new Font("Segoe UI", Font.BOLD, 20),false);
        proc_select = new TemplateButton("p",5,10,40,40);
        if_hex = new TemplateCheckBox("Hex",true,460,155);
        scan_type_lbl = new TemplateText("Scan Type",417,210,Color.WHITE,new Font("Segoe UI", Font.BOLD, 13),false);
        value_type_lbl = new TemplateText("Value Type",417,245,Color.WHITE,new Font("Segoe UI", Font.BOLD, 12),false);
        scan_type = new TemplateComboBox(485,190,250,30,new String[]{"Exact Value"},0);
        value_type = new TemplateComboBox(485,225,250,30,new String[]{"4 Bytes", "2 Bytes", "8 Bytes", "String", "Float","Double","All"},0);
        scan_value = new TemplateTextBox(485,150,300,30,"Value");
        new_scan = new TemplateButton("New Scan",430,90,120,30);
        add_adress_manually = new TemplateButton("Add Adress",740,485,140,30);
        memory_view = new TemplateButton("Memory View",10,495,140,20);
        add_selected_index = new TemplateButton("+",415,460,30,30);
        next_scan = new TemplateButton("Next Scan",560,90,120,30);
        scan_result = new TemplateListBox(10,90,400,400,new String[]{"Adress","Value","Previous"});
        selected_adress_list = new TemplateListBox(10,520,870,215,new String[]{"Active","Description","Adress","Type","Value"});
        logo = new TemplateImage("resources/logo.png",735,5,150,150);
        this.setFocusable(true);
        this.requestFocusInWindow();
        setupListeners();

        addMouseWheelListener(e -> {
            scan_result.mouseScrolled(e);
            repaint();
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                scan_value.keyTyped(e);
                repaint();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                scan_value.keyPressed(e);
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                scan_result.mouseDragged(e);
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UIMainMenu.this.requestFocusInWindow();
                scan_result.mouseClicked(e);
                if_hex.mouseClicked(e.getX(),e.getY());
                scan_type.mouseClicked(e);
                value_type.mouseClicked(e);
                proc_select.mouse_clicked(e.getX(),e.getY());
                scan_value.mouseClicked(e.getX(),e.getY());
                new_scan.mouse_clicked(e.getX(),e.getY());
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                scan_result.mousePressed(e);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                scan_result.mouseDragged(e);
                repaint();
            }


            @Override
            public void mouseReleased(MouseEvent e) {
                scan_result.mouseReleased(e);
                repaint();
            }
        });
    }


    @Override
    public void stop() {
        if (frame != null) {
            frame.dispose();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        String procText = selected_proc_info.getText();
        FontMetrics metrics = g2.getFontMetrics(selected_proc_info.getFont());
        int textWidth = metrics.stringWidth(procText);
        int xCentered = (getWidth() - textWidth) / 2;
        selected_proc_info.setPosition(xCentered,40);
        proc_select.draw(g);
        new_scan.draw(g);
        next_scan.draw(g);
        scan_result_info.draw(g);
        scan_result.draw(g);
        scan_result_info.setText("Found: "+scan_result.getAllEntries().size());
        scan_value.draw(g);
        value_type.draw(g);
        memory_view.draw(g);
        add_adress_manually.draw(g);
        add_selected_index.draw(g);
        selected_adress_list.draw(g);
        scan_type.draw(g);
        logo.draw(g);
        value_type_lbl.draw(g);
        scan_type_lbl.draw(g);
        if_hex.draw(g);
        if(!ProcessContainer.p_name().equals("process not selected")){
            selected_proc_info.setText("Process: "+ProcessContainer.p_name());
        }
        selected_proc_info.draw(g);
    }
}
