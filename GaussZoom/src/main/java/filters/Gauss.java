package filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Gauss {

    public static BufferedImage getGauss(BufferedImage image, int radius, boolean fast) {

        if (fast) {
            return getFast(image, radius);
        } else {
            return getUsual(image, radius);
        }

    }

    private static BufferedImage getUsual(BufferedImage image, int radius) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage gauss = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        double[][] weights = getWeights(radius);

        double usualSum = 0;
        for (int x = 0; x < radius; x++) {
            for (int y = 0; y < radius; y++) {
                usualSum += weights[x][y];
            }
        }

        long time = System.currentTimeMillis();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                double[][] red = new double[radius][radius];
                double[][] green = new double[radius][radius];
                double[][] blue = new double[radius][radius];

                if (x > radius && x < width - radius - 1 && y > radius && y < height - radius - 1) {
                    for (int j = 0; j < radius; ++j) {
                        for (int i = 0; i < radius; ++i) {

                            int sampleX = x + j - (radius / 2);
                            int sampleY = y + i - (radius / 2);

                            double currentWeight = weights[j][i] / usualSum;
                            Color sampledColor = new Color(pixels[sampleY * width + sampleX]);

                            red[j][i] = currentWeight * sampledColor.getRed();
                            green[j][i] = currentWeight * sampledColor.getGreen();
                            blue[j][i] = currentWeight * sampledColor.getBlue();
                        }
                    }
                } else {
                    double sum = 0;
                    for (int j = 0; j < radius; ++j) {
                        for (int i = 0; i < radius; ++i) {

                            int sampleX = x + j - (radius / 2);
                            int sampleY = y + i - (radius / 2);

                            if (sampleX >= 0 && sampleX < width && sampleY >= 0 && sampleY < height) {
                                double currentWeight = weights[j][i];
                                sum += currentWeight;
                                Color sampledColor = new Color(pixels[sampleY * width + sampleX]);

                                red[j][i] = currentWeight * sampledColor.getRed();
                                green[j][i] = currentWeight * sampledColor.getGreen();
                                blue[j][i] = currentWeight * sampledColor.getBlue();
                            } else {
                                red[j][i] = 0;
                                green[j][i] = 0;
                                blue[j][i] = 0;
                            }
                        }
                    }
                    for (int i = 0; i < radius; ++i) {
                        for (int j = 0; j < radius; ++j) {
                            red[j][i] /= sum;
                            green[j][i] /= sum;
                            blue[j][i] /= sum;
                        }
                    }
                }

                double summationRed = 0, summationGreen = 0, summationBlue = 0;

                for (int i = 0; i < radius; ++i) {
                    for (int j = 0; j < radius; ++j) {

                        summationRed += red[i][j];
                        summationGreen += green[i][j];
                        summationBlue += blue[i][j];
                    }
                }
                gauss.setRGB(x, y, new Color((int) summationRed, (int) summationGreen, (int) summationBlue).getRGB());
            }
        }

        System.out.println("Usual gauss taken " + (System.currentTimeMillis() - time) / 1000d + " sec");
        return gauss;

    }

    private static BufferedImage getFast(BufferedImage image, int radius) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage gauss = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        double[] weights = getXWeights(radius);

        double usualSum = 0;
        for (int x = 0; x < radius; x++) {
            usualSum += weights[x];
        }

        long time = System.currentTimeMillis();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                double[] red = new double[radius];
                double[] green = new double[radius];
                double[] blue = new double[radius];

                if (x > radius && x < width - radius - 1) {
                    for (int i = 0; i < radius; ++i) {

                        int sampleX = x + i - (radius / 2);

                        double currentWeight = weights[i] / usualSum;
                        Color sampledColor = new Color(pixels[y * width + sampleX]);

                        red[i] = currentWeight * sampledColor.getRed();
                        green[i] = currentWeight * sampledColor.getGreen();
                        blue[i] = currentWeight * sampledColor.getBlue();
                    }

                } else {
                    double sum = 0;
                    for (int i = 0; i < radius; ++i) {

                        int sampleX = x + i - (radius / 2);

                        if (sampleX >= 0 && sampleX < width) {
                            double currentWeight = weights[i];
                            sum += currentWeight;
                            Color sampledColor = new Color(pixels[y * width + sampleX]);

                            red[i] = currentWeight * sampledColor.getRed();
                            green[i] = currentWeight * sampledColor.getGreen();
                            blue[i] = currentWeight * sampledColor.getBlue();
                        } else {
                            red[i] = 0;
                            green[i] = 0;
                            blue[i] = 0;
                        }
                    }
                    for (int i = 0; i < radius; ++i) {
                        red[i] /= sum;
                        green[i] /= sum;
                        blue[i] /= sum;
                    }
                }

                double summationRed = 0, summationGreen = 0, summationBlue = 0;

                for (int i = 0; i < radius; ++i) {
                        summationRed += red[i];
                        summationGreen += green[i];
                        summationBlue += blue[i];
                }
                gauss.setRGB(x, y, new Color((int) summationRed, (int) summationGreen, (int) summationBlue).getRGB());
            }
        }

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                double[] red = new double[radius];
                double[] green = new double[radius];
                double[] blue = new double[radius];

                if (y > radius && y < height - radius - 1) {
                    for (int i = 0; i < radius; ++i) {

                        int sampleY = y + i - (radius / 2);

                        double currentWeight = weights[i] / usualSum;
                        Color sampledColor = new Color(gauss.getRGB(x, sampleY));

                        red[i] = currentWeight * sampledColor.getRed();
                        green[i] = currentWeight * sampledColor.getGreen();
                        blue[i] = currentWeight * sampledColor.getBlue();
                    }

                } else {
                    double sum = 0;
                    for (int i = 0; i < radius; ++i) {

                        int sampleY = y + i - (radius / 2);

                        if (sampleY >= 0 && sampleY < height) {
                            double currentWeight = weights[i];
                            sum += currentWeight;
                            Color sampledColor = new Color(gauss.getRGB(x, sampleY));

                            red[i] = currentWeight * sampledColor.getRed();
                            green[i] = currentWeight * sampledColor.getGreen();
                            blue[i] = currentWeight * sampledColor.getBlue();
                        } else {
                            red[i] = 0;
                            green[i] = 0;
                            blue[i] = 0;
                        }
                    }
                    for (int i = 0; i < radius; ++i) {
                        red[i] /= sum;
                        green[i] /= sum;
                        blue[i] /= sum;
                    }
                }

                double summationRed = 0, summationGreen = 0, summationBlue = 0;

                for (int i = 0; i < radius; ++i) {
                    summationRed += red[i];
                    summationGreen += green[i];
                    summationBlue += blue[i];
                }
                gauss.setRGB(x, y, new Color((int) summationRed, (int) summationGreen, (int) summationBlue).getRGB());
            }
        }

        System.out.println("Fast gauss taken " + (System.currentTimeMillis() - time) / 1000d + " sec");
        return gauss;

    }

    private static double[][] getWeights(int radius) {
        double[][] weights = new double[radius][radius];
        for (int x = 0; x < radius; x++) {
            for (int y = 0; y < radius; y++) {
                weights[x][y] = gaussian(x - radius / 2.0, y - radius / 2.0);
            }
        }
        return weights;
    }

    private static double[] getXWeights(int radius) {
        double[] weights = new double[radius];
        for (int x = 0; x < radius; x++) {
            weights[x] = gaussian(x - radius / 2.0);
        }
        return weights;
    }

    private static double gaussian(double x, double y) {
        double sigma = 0.1;
        return (Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / 2.0 * Math.pow(sigma, 2))) / (2.0 * Math.PI * Math.pow(sigma, 2));
    }

    private static double gaussian(double x) {
        double sigma = 0.1;
        return (Math.exp(-Math.pow(x, 2) / 2.0 * Math.pow(sigma, 2))) / (2.0 * Math.PI * Math.pow(sigma, 2));
    }
}
