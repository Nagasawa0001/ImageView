package core.api;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;

import core.api.entity.Image;
import core.api.entity.ImageRegisterForm;

@Service
@Transactional
public class IVService extends IVCommon {

	@Autowired
	IVMapper ivMapper;

	public List<Image> getImageList() {
		return ivMapper.selectImageList();
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
