import org.cc11001100.lianliankangua.entity.Point;
import org.cc11001100.lianliankangua.utils.ImageUtil;
import org.cc11001100.lianliankangua.utils.MapUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author CC11001100
 */
public class ImageUtilTest {

	private static BufferedImage image;

	@BeforeClass
	public static void beforeClass() {
		try {
			image = ImageIO.read(new File("D:/test/LianLianKan/analysisPicture/000111.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void findBasePoint() {
		System.out.println(ImageUtil.findBasePoint(image));
	}

	@Test
	public void testParseImageToMatrix() {
		Point basePoint = ImageUtil.findBasePoint(image);
		BufferedImage[][] block = ImageUtil.parseImageToMatrix(image, basePoint);
		for (int i = 0; i < block.length; i++) {
			for (int j = 0; j < block[i].length; j++) {
				try {
					String fileName = "D:/test/LianLianKan/analysisPicture/block/" + i + "_" + j + ".jpg";
					ImageIO.write(block[i][j], "jpg", new File(fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	public void subImage() throws IOException {
		Point point = ImageUtil.findBasePoint(image);
		BufferedImage subImage = image.getSubimage(point.getX(), point.getY() + 163, 200, 300);
		String fileName = "D:/test/LianLianKan/analysisPicture/block/subImage.jpg";
		ImageIO.write(subImage, "jpg", new File(fileName));
	}

	@Test
	public void testImageToDigit() {
		Point basePoint = ImageUtil.findBasePoint(image);
		BufferedImage[][] block = ImageUtil.parseImageToMatrix(image, basePoint);
		Map<Integer, Integer> digitCountMap = new HashMap<>();
		int[][] digitMatrix = MapUtil.imageToDigitMatrix(block);
		for (int i = 0; i < digitMatrix.length; i++) {
			for (int j = 0; j < digitMatrix[i].length; j++) {
				int digit = digitMatrix[i][j];
				System.out.printf("%d\t", digit);
				digitCountMap.put(digit, digitCountMap.getOrDefault(digit, 0) + 1);
			}
			System.out.println();
		}
		System.out.println();

		// 如果有奇数的话，那就有点尴尬了...
		digitCountMap.forEach((k, v) -> {
			System.out.println(k + "=" + v);
			assert Objects.equals(k, MapUtil.BACKGROUND_DIGIT) || v % 2 == 0;
		});
	}

}
