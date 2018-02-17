package org.cc11001100.lianliankangua.main;

import org.cc11001100.lianliankangua.entity.Point;
import org.cc11001100.lianliankangua.utils.ImageUtil;
import org.cc11001100.lianliankangua.utils.MapUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author CC11001100
 */
public class Main extends JFrame {

    private JTextField speedInputTextField;

    public static Main instance = new Main();

    public static AtomicBoolean isWorking = new AtomicBoolean(false);

    private Main() {

        JButton button = new JButton("开始");
        button.addActionListener(event -> {

            System.out.println("开始...");
            button.setText("working...");

            try {
                int delay = Integer.parseInt(speedInputTextField.getText());
                isWorking.set(true);
                doMagic(delay);
            } catch (NumberFormatException e) {
//                e.printStackTrace();
                JOptionPane.showMessageDialog(Main.this, "请填入正整数", "警告", JOptionPane.WARNING_MESSAGE);
            } finally {
                isWorking.set(false);
            }

            button.setText("开始");
            System.out.println("结束...");

        });

        JLabel tip = new JLabel("间隔毫秒：");
        speedInputTextField = new JTextField("100");
        speedInputTextField.setColumns(6);

        JPanel jPanel = new JPanel();
        jPanel.add(tip);
        jPanel.add(speedInputTextField);
        jPanel.add(button);

        add(jPanel);

        setSize(250, 80);
        setTitle("连连看外挂");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void start() {
        setVisible(true);
    }


    public static void doMagic(int delay) {

        BufferedImage screenImg = ImageUtil.captureScreenSnapshot();
        Point basePoint = ImageUtil.findBasePoint(screenImg);

        if (basePoint == null) {
            JOptionPane.showMessageDialog(Main.instance, "没有检测到游戏画面！", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BufferedImage[][] imgMatrix = ImageUtil.parseImageToMatrix(screenImg, basePoint);
        int[][] matrix = MapUtil.imageToDigitMatrix(imgMatrix);

        for (boolean isFinished = false; !isFinished; ) {

            isFinished = true;

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (matrix[i][j] != MapUtil.BACKGROUND_DIGIT) {
                        isFinished = false;
                        Point p = MapUtil.findReachable(matrix, i, j);
                        if (p != null) {
                            System.out.printf("(%d, %d) --> (%d,%d)\n", i, j, p.getX(), p.getY());

                            Point p1 = ImageUtil.shouldClickWhere(basePoint, i, j);
                            Point p2 = ImageUtil.shouldClickWhere(basePoint, p.getX(), p.getY());

                            for(int k=0; k<10; k++){
                                ImageUtil.click(delay, p1, p2);
                            }

                            System.out.println(p1 + " " + p2);

                            matrix[i][j] = MapUtil.BACKGROUND_DIGIT;
                            matrix[p.getX()][p.getY()] = MapUtil.BACKGROUND_DIGIT;
                        }
                    }

                    // 立即停止
                    if (!Main.isWorking.get()) {
                        return;
                    }

                }

            }
        }
    }


    public static void main(String[] args) throws InterruptedException {

        instance.start();

    }

}
