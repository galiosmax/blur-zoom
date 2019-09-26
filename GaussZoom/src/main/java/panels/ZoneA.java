package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ZoneA extends JPanel {

    private BufferedImage image;

    ZoneA() {
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            g2.drawImage(image, null, null);
        } else {
            removeAll();
        }
    }

    void setImage(BufferedImage image) {
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        setBounds(0, 0, image.getWidth(), image.getHeight());
        this.image = image;
    }


    BufferedImage getImage() {
        return image;
    }
}
