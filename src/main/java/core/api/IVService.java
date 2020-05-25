package core.api;

import java.io.IOException;

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

	// 全
	public ImageTagList getImageAndTagList() {
		ImageTagList list = new ImageTagList();
		list.setImageList(ivMapper.selectImageList());
		list.setTagList(ivMapper.selectTagList());
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

	// ファイルアップロード
	public void registerImage(ImageRegisterForm form) {
		long tagId = this.checkExistTag(form.getTagName());
		System.out.println(tagId);

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
}
