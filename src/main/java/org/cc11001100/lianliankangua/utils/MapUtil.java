package org.cc11001100.lianliankangua.utils;

import org.cc11001100.lianliankangua.entity.Point;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 图案地图相关工具类
 *
 * @author CC11001100
 */
public class MapUtil {

    /**
     * 在将图像地图转为数字矩阵的时候表示背景颜色色块的数字
     */
    public static final Integer BACKGROUND_DIGIT = -1;

    /**
     * 图像矩阵转换为数值矩阵
     *
     * @param imgMatrix
     * @return
     */
    public static int[][] imageToDigitMatrix(BufferedImage[][] imgMatrix) {

        assert imgMatrix.length > 0;

        Map<Integer, Integer> imgHashCodeToDigitMapping = new HashMap<>();
        AtomicInteger currentMaxDigit = new AtomicInteger(1);
        int[][] digitMatrix = new int[imgMatrix.length][imgMatrix[0].length];

        for (int i = 0; i < imgMatrix.length; i++) {
            for (int j = 0; j < imgMatrix[i].length; j++) {

                // 先判断是不是背景色块，背景色块使用固定的数字表示 TODO 怀疑这里有问题
                if (ImageUtil.isMostThatSimilarColor(imgMatrix[i][j], 0X304D6F, 0.6)) {
                    digitMatrix[i][j] = BACKGROUND_DIGIT;
                    continue;
                }

                // 其它色块统一使用一个逻辑处理
                int imgHashCode = ImageUtil.imageHashCode(imgMatrix[i][j]);
                imgHashCodeToDigitMapping.computeIfAbsent(imgHashCode, key -> currentMaxDigit.getAndIncrement());
                digitMatrix[i][j] = currentMaxDigit.get();
            }
        }

        return digitMatrix;
    }

    /**
     * 从地图上搜索某个点能够到达的第一个其它相同点
     *
     * @param map
     * @param x
     * @param y
     * @return 返回找到的点，找不到返回null
     */
    public static Point findReachable(int[][] map, int x, int y) {
        int[][] book = new int[map.length][map[0].length];
        book[x][y] = 1;
        return innerFindReachable(map, book, x, y, map[x][y], 0, 3, "");
    }

    private static Next[] next = new Next[]{
            new Next(0, -1, "up"),
            new Next(1, 0, "right"),
            new Next(0, 1, "down"),
            new Next(-1, 0, "left")
    };

    public static class Next {
        int x;
        int y;
        String direct;

        Next(int x, int y, String direct) {
            this.x = x;
            this.y = y;
            this.direct = direct;
        }
    }

    /**
     * @param map           原始棋盘
     * @param book          已遍历标记
     * @param x             当前x坐标
     * @param y             当前y坐标
     * @param target        搜索目标值
     * @param currentDeep   当前深度
     * @param maxDeep       最大搜索深度
     * @param currentDirect 当前前进方向
     * @return
     */
    private static Point innerFindReachable(int[][] map, int[][] book, int x, int y, int target,
                                            int currentDeep, int maxDeep, String currentDirect) {

        if (currentDeep > maxDeep) {
            return null;
        }

        for (Next localNext : next) {

            int nextX = x + localNext.x;
            int nextY = y + localNext.y;
            String nextDirect = localNext.direct;
            int nextDeep = nextDirect.equals(currentDirect) ? currentDeep : currentDeep + 1;

            if (nextDeep > maxDeep) {
                continue;
            }

            if (nextX < 0 || nextX >= map.length || nextY < 0 || nextY >= map[x].length) {
                continue;
            }

            // 下一块跟自己同色并且不是起点
            if (book[nextX][nextY] == 0 && map[nextX][nextY] == target) {
                return new Point(nextX, nextY);
            }

            // 下一块是背景颜色并且没有被遍历过
            if (book[nextX][nextY] == 0 && map[nextX][nextY] == BACKGROUND_DIGIT) {
                book[nextX][nextY] = 1;

                Point p = innerFindReachable(map, book, nextX, nextY, target, nextDeep, maxDeep, nextDirect);
                if (p != null) {
                    return p;
                }
            }
        }

        return null;
    }

}
