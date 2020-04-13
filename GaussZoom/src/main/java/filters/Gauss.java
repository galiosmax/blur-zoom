package filters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Gauss {

    public static BufferedImage getGauss(BufferedImage image, int radius, boolean fast) {

        if (fast) {
            return getLinear(image, radius);
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

    private static BufferedImage getLinear(BufferedImage image, int radius) {
        long time = System.currentTimeMillis();

        int width = image.getWidth();
        int height = image.getHeight();

        int[] source = image.getRGB(0, 0, width, height, null, 0, width);

        int[] redSource = new int[width * height];
        int[] greenSource = new int[width * height];
        int[] blueSource = new int[width * height];

        for (int i = 0; i < source.length; ++i) {
            Color color = new Color(source[i]);
            redSource[i] = color.getRed();
            greenSource[i] = color.getGreen();
            blueSource[i] = color.getBlue();
        }

        int[] redDest = new int[width * height];
        int[] greenDest = new int[width * height];
        int[] blueDest = new int[width * height];

        gaussBlur_4(redSource, redDest, width, height, radius);
        gaussBlur_4(greenSource, greenDest, width, height, radius);
        gaussBlur_4(blueSource, blueDest, width, height, radius);

        int[] res = new int[width * height];

        for (int i = 0; i < res.length; ++i) {
            redDest[i] = Math.min(255, Math.max(0, redDest[i]));
            greenDest[i] = Math.min(255, Math.max(0, greenDest[i]));
            blueDest[i] = Math.min(255, Math.max(0, blueDest[i]));

            res[i] = new Color(redDest[i], greenDest[i], blueDest[i]).getRGB();
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        result.setRGB(0, 0, width, height, res, 0, width);
        System.out.println("Linear gauss taken " + (System.currentTimeMillis() - time) / 1000d + " sec");
        return result;
    }

    private static int[] boxesForGauss(float sigma, int n) {
        double wIdeal = Math.sqrt((12d * sigma * sigma / n) + 1);
        int wl = (int) Math.floor(wIdeal);
        if (wl % 2 == 0) {
            wl--;
        }
        int wu = wl + 2;

        double mIdeal = (double) (12 * sigma * sigma - n * wl * wl - 4 * n * wl - 3 * n) / (-4 * wl - 4);
        int m = (int) Math.round(mIdeal);

        List<Integer> sizes = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            sizes.add(i < m ? wl : wu);
        }
        return sizes.stream().mapToInt(i -> i).toArray();
    }

    private static void boxBlur_4(int[] source, int[] dest, int w, int h, int r) {
        System.arraycopy(source, 0, dest, 0, source.length);
        boxBlurH_4(dest, source, w, h, r);
        boxBlurT_4(source, dest, w, h, r);
    }

    private static void boxBlurH_4(int[] source, int[] dest, int w, int h, int r) {
        double iarr = 1d / (r + r + 1);
        for (int i = 0; i < h; ++i) {
            int ti = i * w;
            int li = ti;
            int ri = ti + r;
            int fv = source[ti];
            int lv = source[ti + w - 1];
            int val = (r + 1) * fv;
            for (int j = 0; j < r; ++j) {
                val += source[ti + j];
            }
            for (int j = 0; j <= r; ++j) {
                val += source[ri++] - fv;
                dest[ti++] = (int) Math.round(val * iarr);
            }
            for (int j = r + 1; j < w - r; ++j) {
                val += source[ri++] - source[li++];
                dest[ti++] = (int) Math.round(val * iarr);
            }
            for (int j = w - r; j < w; j++) {
                val += lv - source[li++];
                dest[ti++] = (int) Math.round(val * iarr);
            }
        }
    }

    private static void boxBlurT_4(int[] source, int[] dest, int w, int h, int r) {
        double iarr = 1d / (r + r + 1);
        for (int i = 0; i < w; ++i) {
            int ti = i;
            int li = ti;
            int ri = ti + r * w;
            int fv = source[ti];
            int lv = source[ti + w * (h - 1)];
            int val = (r + 1) * fv;
            for (int j = 0; j < r; ++j) {
                val += source[ti + j * w];
            }
            for (int j = 0; j <= r; ++j) {
                val += source[ri] - fv;
                dest[ti] = (int) Math.round(val * iarr);
                ri += w;
                ti += w;
            }
            for (int j = r + 1; j < h - r; ++j) {
                val += source[ri] - source[li];
                dest[ti] = (int) Math.round(val * iarr);
                li += w;
                ri += w;
                ti += w;
            }
            for (int j = h - r; j < h; j++) {
                val += lv - source[li];
                dest[ti] = (int) Math.round(val * iarr);
                li += w;
                ti += w;
            }
        }
    }

    private static void gaussBlur_4(int[] source, int[] dest, int w, int h, int r) {
        var bxs = boxesForGauss(r, 3);
        boxBlur_4(source, dest, w, h, (bxs[0] - 1) / 2);
        boxBlur_4(dest, source, w, h, (bxs[1] - 1) / 2);
        boxBlur_4(source, dest, w, h, (bxs[2] - 1) / 2);
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
