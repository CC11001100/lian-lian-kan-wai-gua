package org.cc11001100.lianliankangua.utils;

import org.cc11001100.lianliankangua.entity.Point;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

/**
 * @author CC11001100
 */
public class ImageUtil {

    /**
     * 将图像解析为单个元素的二维矩阵
     * <p>
     * 将每个块切割出来，每张图片的大小是 30 * 34
     * <p>
     * begin: (x, y += 63)
     *
     * @param image
     * @return
     */
    public static BufferedImage[][] parseImageToMatrix(BufferedImage image, Point basePoint) {
        BufferedImage[][] blockImage = new BufferedImage[11][19];
        for (int i = 0; i < blockImage.length; i++) {
            for (int j = 0; j < blockImage[i].length; j++) {
                blockImage[i][j] = image
                        // x轴无需偏移，y轴基础偏移量是163, 然后每张图的大小是 30 * 34
                        .getSubimage(basePoint.getX() + j * (30 + 1), basePoint.getY() + 163 + i * (34 + 1), 30, 34);
            }
        }
        return blockImage;
    }

    /**
     * 找到基准点（以logo为基准点）
     *
     * @param img
     * @return
     */
    public static Point findBasePoint(BufferedImage img) {
        for (int i = 0; i < img.getWidth() - 1; i++) {
            for (int j = 0; j < img.getHeight() - 1; j++) {

                // 用三个特征像素来定位logo
                if (rgbEquals(img, i, j, 0XD83C38) //
                        && rgbEquals(img, i + 1, j, 0XB84418) //
                        && rgbEquals(img, i, j + 1, 0X887C58)) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    public static boolean rgbEquals(BufferedImage image, int x, int y, int rgb) {
        int srcRgb = image.getRGB(x, y);
        return rgbEquals(srcRgb, rgb);
    }

    public static boolean rgbEquals(int rgb1, int rgb2) {
        return (rgb1 & 0XFFFFFF) == (rgb2 & 0XFFFFFF);
    }

    /**
     * 比较两个rgb像素是否近似相等，在一定距离内则认为是近似相等
     *
     * @param rgb1
     * @param rgb2
     * @param distance 三个通道的距离在3*distance以内认为是相等
     * @return
     */
    public static boolean rgbSimilar(int rgb1, int rgb2, int distance) {
        int r = Math.abs((rgb1 & 0XFF0000) - (rgb2 & 0XFF0000)) >> 16;
        int g = Math.abs((rgb1 & 0X00FF00) - (rgb2 & 0X00FF00)) >> 8;
        int b = Math.abs((rgb1 & 0X0000FF) - (rgb2 & 0X0000FF));
        return r <= distance && g <= distance && b <= distance;
    }

    /**
     * 获取屏幕快照
     *
     * @return
     */
    public static BufferedImage captureScreenSnapshot() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        try {
            Robot robot = new Robot();
            return robot.createScreenCapture(screenRectangle);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算图像的hash值
     *
     * @param img
     * @return
     */
    public static int imageHashCode(BufferedImage img) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                sb.append(i).append("|").append(j).append("|").append(img.getRGB(i, j)).append("|");
            }
        }
        return sb.toString().hashCode();
    }

    /**
     * 判断一个图像是否大多数都是某种相近的颜色
     *
     * @param img     在一个图像中
     * @param rgb     这个颜色的相似颜色
     * @param percent 占比超过多少时
     * @return 会返回true
     */
    public static boolean isMostThatSimilarColor(BufferedImage img, int rgb, double percent) {
        int w = img.getWidth();
        int h = img.getHeight();
        int bgSimilarCount = 0;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (rgbSimilar(img.getRGB(i, j), rgb, 20)) {
                    bgSimilarCount++;
                }
            }
        }
        return 1.0 * bgSimilarCount / (w * h) > percent;
    }

    public static Point shouldClickWhere(Point basePoint, int x, int y) {
        return new Point(basePoint.getX() + y * (30 + 1) + 17, basePoint.getY() + 163 + x * (34 + 1) + 18);
    }

    public static void click(int delay, Point p1, Point p2) {
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(delay);

            robot.mouseMove(p1.getX(), p1.getY());
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            robot.mouseMove(p2.getX(), p2.getY());
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}
