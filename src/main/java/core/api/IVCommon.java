package core.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
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

	// 削除依頼日時比較 + レコード削除
	public void deleteImages(Timestamp deleteRequestDate, long id) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 削除依頼日時
			Date reqDate = new Date(deleteRequestDate.getTime());
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(reqDate);
	        // 24時間後に設定
	        calendar.add(Calendar.DAY_OF_MONTH, 1);

	        // DBのデフォルト値
	        Date defaultDate = sdf.parse("1000-01-01 09:00:00");
	        // 削除予定日時
	        Date deleteDate = calendar.getTime();
	        // 現在日時
	        Date  currentDate = new Date(new Timestamp(System.currentTimeMillis()).getTime());
	        int deleteStatus = deleteDate.compareTo(currentDate);
	        if(reqDate.compareTo(defaultDate) != 0 && (deleteStatus == 0 || deleteStatus < 0)) {
	        	ivMapper.deletedImage(id);
	        }

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

    public S3Object downloadImage(String objectKey, String outputName) throws IOException {
        Regions clientRegion = Regions.AP_NORTHEAST_1;
        String bucketName = "image-view";
        String key = objectKey;

        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new ProfileCredentialsProvider())
                    .build();

            // Get an object and print its contents.
            System.out.println("Downloading an object");
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            System.out.println("Content: ");
//            displayTextInputStream(fullObject.getObjectContent());

            // Get a range of bytes from an object and print the bytes.
            GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, key)
                    .withRange(0, 9);
            objectPortion = s3Client.getObject(rangeObjectRequest);
            System.out.println("Printing bytes retrieved.");
//            displayTextInputStream(objectPortion.getObjectContent());

            // Get an entire object, overriding the specified response headers, and print the object's content.
            ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides()
                    .withCacheControl("No-cache")
                    .withContentDisposition("attachment; filename=" + outputName);
            GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(bucketName, key)
                    .withResponseHeaders(headerOverrides);
            headerOverrideObject = s3Client.getObject(getObjectRequestHeaderOverride);
            this.displayTextInputStream(headerOverrideObject, outputName);
            return fullObject;
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if (fullObject != null) {
                fullObject.close();
                return fullObject;
            }
            if (objectPortion != null) {
                objectPortion.close();
            }
            if (headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
		return null;
    }

    public void displayTextInputStream(S3Object headerOverrideObject, String outputName) throws IOException {
        // Read the text input stream one line at a time and display each line.
    	String home = System.getProperty("user.home");
    	String fileType = "";
    	switch(headerOverrideObject.getObjectMetadata().getContentType()) {
    	case "image/png":
    		fileType = "png";
    		break;

    	case "image/jpeg":
    		fileType = "jpeg";
    		break;

    	case "image/gif":
    		fileType = "gif";
    		break;
    	}
    	BufferedImage bi = ImageIO.read(headerOverrideObject.getObjectContent());
    	ImageIO.write(bi, fileType, new File(home + "/Downloads/" + outputName));
    }
}
