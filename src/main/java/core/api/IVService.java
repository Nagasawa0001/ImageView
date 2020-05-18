package core.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import core.api.entity.Image;
import core.api.entity.Tag;

@Service
@Transactional
public class IVService extends IVCommon {

	@Autowired
	IVMapper ivMapper;

	// ファイルアップロード
	public void registerImage(MultipartFile[] files, long userId, String tagName) {
		Tag tag = ivMapper.selectTagId(tagName);
		long tagId;

		if(tag == null ) {
			// タグ登録
			tagId = ivMapper.insertTag(tagName);
			// 登録したタグ取得
			//tag = ivMapper.selectTagId(tagName);
		} else {
			tagId = tag.getId();
		}

		Image image = new Image();
		for(MultipartFile file : files) {
			try {
				this.saveImageFiles(file, this.getFilepath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			image.setUserId(userId);
			image.setTagId(tagId);
			image.setPath(this.getFilepath());
			// ファイル情報をDB格納
			ivMapper.insertImages(image);
		}
	}
}
