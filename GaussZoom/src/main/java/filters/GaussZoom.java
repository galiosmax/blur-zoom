package filters;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class GaussZoom {

    private static final double NEEDED_PROPORTION = 16d / 9d;

    public static BufferedImage createFinalImage(BufferedImage original) {

        long time = System.currentTimeMillis();

        int height = original.getHeight();
        int width = countFinalWidth(height);
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage toProcessLeft = cutSubImage(original, width, true);
        BufferedImage toProcessRight = cutSubImage(original, width, false);

        BufferedImage processedLeft = processImage(toProcessLeft);
        BufferedImage processedRight = processImage(toProcessRight);

        finalImage.setRGB(0, 0, processedLeft.getWidth(), processedLeft.getHeight(), processedLeft.getRGB(0, 0, processedLeft.getWidth(), processedLeft.getHeight(), null, 0, processedLeft.getWidth()), 0, processedLeft.getWidth());
        finalImage.setRGB(processedRight.getWidth() + original.getWidth() - 1, 0, processedRight.getWidth(), processedRight.getHeight(), processedRight.getRGB(0, 0, processedRight.getWidth(), processedRight.getHeight(), null, 0, processedRight.getWidth()), 0, processedRight.getWidth());
        finalImage.setRGB(width - original.getWidth() - processedLeft.getWidth() - 1, 0, original.getWidth(), height, original.getRGB(0, 0, original.getWidth(), original.getHeight(), null, 0, original.getWidth()), 0, original.getWidth());
        System.out.println("gausszoom taken " + (System.currentTimeMillis() - time) / 1000d + " sec");

        return finalImage;
    }

    private static int countFinalWidth(int originalHeight) {
        return (int) Math.floor(originalHeight * NEEDED_PROPORTION);
    }

    private static BufferedImage cutSubImage(@NotNull BufferedImage original, int width, boolean left) {
        int cutWidth = (width - original.getWidth()) / 4;
        int cutHeight = original.getHeight() / 2;

        if (left) {
            return original.getSubimage(0, cutHeight / 2, cutWidth, cutHeight);
        } else {
            return original.getSubimage(original.getWidth() - cutWidth - 1, cutHeight / 2, cutWidth, cutHeight);
        }
    }

    private static BufferedImage processImage(BufferedImage image) {
        return Gauss.getGauss(Zoom.getZoomed(image, 2), 3, true);
    }
}
