package core.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;

import core.api.entity.ImageRegisterForm;
import core.api.entity.ImageTagList;

@Service
@Transactional
public class IVService extends IVCommon {

	@Autowired
	IVMapper ivMapper;

	// 全画像リストを取得
	public ImageTagList getImageAndTagList() {
		ImageTagList list = new ImageTagList();
		list.setImageList(ivMapper.selectImageList());
		list.setTagList(ivMapper.selectTagList());
		list.getImageList().forEach(image -> this.deleteImages(image.getDeleteRequestDate(), image.getId()));
		return list;
	}

	// タグ名検索
	public ImageTagList searchByTag(long tagId) {
		ImageTagList list = new ImageTagList();
		list.setImageList(ivMapper.selectImageListByTag(tagId));
		list.setTagList(ivMapper.selectTagList());
		return list;
	}

	// 並び替え画像リスト取得
	public ImageTagList getImageListBySort(String target, String sortType) {
		ImageTagList list = new ImageTagList();
		list.setImageList(ivMapper.getImageListBySort(target, sortType));
		list.setTagList(ivMapper.selectTagList());
		return list;
	}

	// いいね数追加
	public void incrementGoodCount(long id) {
		ivMapper.updateGoodCount(id);
	}

	// 削除依頼
	public void requestDeletion(long id) {
		ivMapper.updatePreDeleteFlag(id);

	}

	// ファイルアップロード
	public void registerImage(ImageRegisterForm form) {
		long tagId = this.checkExistTag(form.getTagName());

		String filepath;
		for(MultipartFile file : form.getUploadFiles()) {
			try {
				filepath = this.uploadAWSS3(file);
				form.setUserId(form.getUserId());
				form.setTagId(tagId);
				form.setPath(filepath);
				// ファイル情報をDB格納
				ivMapper.insertImages(form);
			} catch (AmazonClientException | IOException | InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	// ファイルダウンロード
	public void downloadImage(long id, HttpServletResponse response) {
		String objectKey = ivMapper.selectImagePath(id).replace("https://image-view.s3-ap-northeast-1.amazonaws.com/", "");
		String outputName = objectKey.replace("uploaded/", "");
//		response.setContentType("image/png");
//        response.setHeader("Content-Disposition", "attachment; filename=" + outputName);
//		OutputStream os = null;
		try {
			this.downloadImage(objectKey, outputName);
//			byte[] byteArray = IOUtils.toByteArray(obj.getObjectContent());
//			os = response.getOutputStream();
//			os.write(byteArray);
//			os.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
