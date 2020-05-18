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
	public String getFilepath(int dirCode, long userId, String fileName) {
		String filepath = "";
		switch(dirCode) {
		case 0:
			filepath = "src/main/resources/static/uploaded/" + userId + "/" + this.getUniqueId() + ".png";
			break;
		case 1:
			filepath = "src/main/resources/static/uploaded/" + userId + "/" + this.getUniqueId() + ".png";
			break;
		case 2:
			filepath = "src/main/resources/static/uploaded/anon/" + this.getUniqueId() + ".png";
			break;
		}
		return filepath;
	}

	// ディレクトリが存在するか確認、無い場合はディレクトリ作成
	public int checkDir(long userId) {
		// 初期値（ログインユーザーでディレクトリが存在する場合）
		int dirCode = 0;
		File dir = new File("src/main/resources/static/uploaded/" + userId);
		// ログインユーザーでディレクトリが存在しない場合（初アップロード）
		if((!dir.exists()) && userId != 1) {
			dir.mkdir();
			dirCode = 1;
		// 未ログインユーザー（匿名）の場合
		} else if(userId == 1){
			dirCode = 2;
		}
		return dirCode;
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
