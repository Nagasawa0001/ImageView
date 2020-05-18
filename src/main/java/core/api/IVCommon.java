package core.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

public class IVCommon {

	// 保存先ファイルパス生成
	public String getFilepath() {
			return "src/main/resources/static/uploaded/" + this.getUniqueId() + ".png";
	}

	// 画像ファイルを指定パスに保存
	public void saveImageFiles(MultipartFile image, String filepath) throws IOException {
		BufferedImage bi = ImageIO.read(image.getInputStream());
		ImageIO.write(bi, "png", new File(filepath));
	}

	// ファイル名用のユニーク文字列を作成
	public String getUniqueId() {
		Calendar calendar = GregorianCalendar.getInstance();
		String uniqueId = Integer.toString(calendar.get(Calendar.YEAR));
		uniqueId += Integer.toString(calendar.get(Calendar.MONTH) + 1);
		uniqueId += Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		uniqueId += Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		uniqueId += Integer.toString(calendar.get(Calendar.MINUTE));
		uniqueId += Integer.toString(calendar.get(Calendar.SECOND));
		return uniqueId;
	}
}
