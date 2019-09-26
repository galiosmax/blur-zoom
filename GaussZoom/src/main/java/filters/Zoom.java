package filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Zoom {

    public static BufferedImage getZoomed(BufferedImage image, int times) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage zoomed = new BufferedImage(width * times, height * times, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        long time = System.currentTimeMillis();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color first = new Color(pixels[y * width + x]);
                Color second;
                if (x < width - 1) {
                    second = new Color(pixels[y * width + x + 1]);
                } else {
                    second = first;
                }

                int red = first.getRed();
                int green = first.getGreen();
                int blue = first.getBlue();

                int dR = (second.getRed() - first.getRed()) / times;
                int dG = (second.getGreen() - first.getGreen()) / times;
                int dB = (second.getBlue() - first.getBlue()) / times;

                for (int k = 0; k < times; k++) {
                    zoomed.setRGB(x * times + k, y * times, new Color(red, green, blue).getRGB());

                    red += dR;
                    green += dG;
                    blue += dB;
                }
            }
        }

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width * times; ++x) {
                Color first = new Color(zoomed.getRGB(x, y * times));
                Color second;
                if (y < height - 1) {
                    second = new Color(zoomed.getRGB(x, y * times + times));
                } else {
                    second = first;
                }
                int red = first.getRed();
                int green = first.getGreen();
                int blue = first.getBlue();

                int dR = (second.getRed() - first.getRed()) / times;
                int dG = (second.getGreen() - first.getGreen()) / times;
                int dB = (second.getBlue() - first.getBlue()) / times;

                for (int k = 0; k < times; k++) {
                    zoomed.setRGB(x, y * times + k, new Color(red, green, blue).getRGB());

                    red += dR;
                    green += dG;
                    blue += dB;
                }
            }
        }

        System.out.println("Zoom taken " + (System.currentTimeMillis() - time) / 1000d + " sec");

        return zoomed;
    }

}
