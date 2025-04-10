package net.cheat.cat.ui.template.impl;

import net.cheat.cat.ui.template.Template;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TemplateImage implements Template {
    private BufferedImage image;
    private int x, y, width, height;

    public TemplateImage(String resourcePath, int x, int y, int width, int height) throws IOException {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        InputStream stream = ClassLoader.getSystemResourceAsStream(resourcePath);
        image = ImageIO.read(stream);
    }


    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            System.out.println("Image not loaded.");
        }
    }
}
