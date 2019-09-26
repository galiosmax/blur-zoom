package panels;

import filters.Gauss;
import filters.Zoom;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyPanel extends JPanel {

    private MainFrame parent;

    private int width;
    private int height;

    private ZoneA zoneA;
    private ZoneB zoneB;

    private BufferedImage originalImage;

    MyPanel(MainFrame frame) {

        parent = frame;

        setLayout(new FlowLayout());

        JPanel imagePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        setPreferredSize(new Dimension(1500, 750));

        zoneA = new ZoneA();
        zoneB = new ZoneB();

        JScrollPane scrollPaneA = new JScrollPane(zoneA, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane scrollPaneB = new JScrollPane(zoneB, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneA.setPreferredSize(new Dimension(700, 700));
        scrollPaneB.setPreferredSize(new Dimension(700, 700));

        imagePanel.add(scrollPaneA);
        imagePanel.add(scrollPaneB);

        add(imagePanel);
    }

    void setImage(BufferedImage image) {
        originalImage = image;

        width = originalImage.getWidth();
        height = originalImage.getHeight();
        zoneA.setImage(originalImage);
        repaint();
    }

    void clear() {
        originalImage = null;
        zoneA.setImage(null);
        zoneB.setImage(null);
        repaint();
    }

    void copyAToB() {
        BufferedImage imageA = zoneA.getImage();
        if (imageA != null) {
            zoneB.setImage(imageA);
            setImageToSave(imageA);
        }
        repaint();
    }

    void copyBToA() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            zoneA.setImage(imageB);
        }
        repaint();
    }

    private void setImageToSave(BufferedImage image) {
        parent.setImageToSave(image);
    }

    private int getValue(JTextField textField, int minVal, int maxVal, int defaultValue) {

        int val;
        try {
            val = Integer.parseInt(textField.getText());
            if (val < minVal || val > maxVal) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            val = defaultValue;
        }
        return val;
    }

    void zoom() {

        BufferedImage imageA = zoneA.getImage();
        if (imageA == null) {
            return;
        }

        JFrame zoomFrame = new JFrame("Zoom");
        JPanel panel = new JPanel(new FlowLayout());

        JPanel fields = new JPanel(new GridLayout(1, 2, 10, 10));
        fields.setBorder(BorderFactory.createTitledBorder("Zoom parameters"));
        JPanel buttons = new JPanel();

        JLabel zoomLabel = new JLabel("Zoom Level");

        JTextField zoomField = new JTextField();
        zoomField.setText(String.valueOf(2));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        fields.add(zoomLabel);
        fields.add(zoomField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(fields);
        panel.add(buttons);

        okButton.addActionListener(e -> {
            int zoomVal = getValue(zoomField, 2, 10, 2);

            BufferedImage imageB = Zoom.getZoomed(imageA, zoomVal);
            zoneB.setImage(imageB);
            setImageToSave(imageB);

            zoomFrame.setVisible(false);
            zoomFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            zoomFrame.setVisible(false);
            zoomFrame.dispose();
        });

        zoomFrame.add(panel);
        zoomFrame.pack();
        zoomFrame.setVisible(true);
    }

    private void gauss(int radius, int count, boolean fast) {
        BufferedImage imageA = zoneA.getImage();
        if (imageA != null) {
            for (int i = 0; i < count; ++i) {
                imageA = Gauss.getGauss(imageA, radius, fast);
            }
            zoneB.setImage(imageA);
            setImageToSave(imageA);
        }
        repaint();
    }

    void blurFilter() {

        BufferedImage imageA = zoneA.getImage();
        if (imageA == null) {
            return;
        }

        JFrame gaussFrame = new JFrame("Gauss");
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JPanel gauss = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel times = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Gauss parameters"));
        JPanel buttons = new JPanel();

        JLabel gaussLabel = new JLabel("Gauss Radius");
        JTextField gaussField = new JTextField();
        JSlider gaussSlider = new JSlider(1, 101);

        JLabel timesLabel = new JLabel("Times");
        JTextField timesField = new JTextField();
        JSlider timesSlider = new JSlider(1, 10);

        gaussField.setText(String.valueOf(5));
        gaussSlider.setValue(5);
        timesField.setText(String.valueOf(1));
        timesSlider.setValue(1);

        JButton usualButton = new JButton("Usual");
        JButton fastButton = new JButton("Fast");
        JButton cancelButton = new JButton("Cancel");

        gauss.add(gaussLabel);
        gauss.add(gaussField);
        times.add(timesLabel);
        times.add(timesField);

        buttons.add(usualButton);
        buttons.add(fastButton);
        buttons.add(cancelButton);

        panel.add(gauss);
        panel.add(gaussSlider);
        panel.add(times);
        panel.add(timesSlider);
        panel.add(buttons);

        gaussField.addActionListener(e -> {
            int val = getValue(gaussField, 0, 101, 5);
            gaussSlider.setValue(val);
        });

        gaussSlider.addChangeListener(e -> gaussField.setText(String.valueOf(gaussSlider.getValue())));

        timesField.addActionListener(e -> {
            int val = getValue(timesField, 1, 10, 1);
            timesSlider.setValue(val);
        });

        timesSlider.addChangeListener(e -> timesField.setText(String.valueOf(timesSlider.getValue())));

        usualButton.addActionListener(e -> {
            int radius = getValue(gaussField, 0, 101, 5);
            int count = getValue(timesField, 1, 10, 1);
            gauss(radius, count, false);
            gaussFrame.setVisible(false);
            gaussFrame.dispose();

            repaint();
        });

        fastButton.addActionListener(e -> {
            int radius = getValue(gaussField, 0, 101, 5);
            int count = getValue(timesField, 1, 10, 1);
            gauss(radius, count, true);
            gaussFrame.setVisible(false);
            gaussFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            gaussFrame.setVisible(false);
            gaussFrame.dispose();
        });

        gaussFrame.add(panel);
        gaussFrame.pack();
        gaussFrame.setVisible(true);
    }

}
