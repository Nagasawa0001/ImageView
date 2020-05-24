package core.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import core.api.entity.Tag;

public class IVCommon {

	@Autowired
	IVMapper ivMapper;

	// タグ存在チェック + 新規タグ登録
	public long checkExistTag(String tagName) {
		Tag tag = ivMapper.selectTagId(tagName);
		long tagId;
		if(tag == null ) {
			// タグ登録 + ID取得
			ivMapper.insertTag(tagName);
			tagId = ivMapper.selectTagId(tagName).getId();
		} else {
			tagId = tag.getId();
		}
		return tagId;
	}

	// 保存先ファイルパス生成
	public String getUploadpath() {
			return "uploaded/" + this.getUniqueId() + ".png";
	}

	// Content-Typeを判定
	public String checkContentType(MultipartFile file) {
		String contentType = file.getContentType();
		String[] checkList = { "image/jpeg", "image/png", "image/gif"};
		if(Arrays.asList(checkList).contains(contentType)) {
			return contentType;
		}
		throw new RuntimeException("This is an Invalid Content-Type");
	}

	// 画像ファイルをS3にアップロード
	public String uploadAWSS3(MultipartFile image) throws IOException, AmazonClientException, InterruptedException {
		// リージョン
      Regions clientRegion = Regions.AP_NORTHEAST_1;
      // バケット名
      String bucketName = "image-view";
      // オブジェクトキー
      String keyName = this.getUploadpath();
      InputStream is = image.getInputStream();
      ObjectMetadata om = new ObjectMetadata();
      om.setContentType(this.checkContentType(image));
      String imagePath = "https://image-view.s3-ap-northeast-1.amazonaws.com/" + keyName;


      try {
      	// すべてのデフォルトが設定されたビルダーの新しいインスタンスを作成します。
          AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
          		// クライアントが使用するリージョンを設定します。
                  .withRegion(clientRegion)
                  // クライアントが使用するAWSCredentialsProviderを設定します。
                  .withCredentials(new ProfileCredentialsProvider())
                  .build();
          // すべてのデフォルトが設定されたビルダーの新しいインスタンスを作成します。
          TransferManager tm = TransferManagerBuilder.standard()
          		// Amazon S3へのサービス呼び出しを行うために使用される低レベルのクライアントを設定します。
                  .withS3Client(s3Client)
                  .build();

          // TransferManager processes all transfers asynchronously,
          // so this call returns immediately.
          Upload upload = tm.upload(new PutObjectRequest(bucketName, keyName, is, om));
          System.out.println("Object upload started");

          // Optionally, wait for the upload to finish before continuing.
          upload.waitForCompletion();
          System.out.println("Object upload complete");

          return imagePath;
      } catch (AmazonServiceException e) {
          // The call was transmitted successfully, but Amazon S3 couldn't process
          // it, so it returned an error response.
          e.printStackTrace();
      } catch (SdkClientException e) {
          // Amazon S3 couldn't be contacted for a response, or the client
          // couldn't parse the response from Amazon S3.
          e.printStackTrace();
      }
	return imagePath;

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
