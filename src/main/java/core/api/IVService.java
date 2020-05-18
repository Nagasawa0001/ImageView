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

		// 0：未登録のタグ　0以外：登録済のタグ
		if(tag == null ) {
			// タグ登録
			tagId = ivMapper.insertTag(tagName);
			// 登録したタグ取得
			//tag = ivMapper.selectTagId(tagName);
		} else {
			tagId = tag.getId();
		}

		Image image = new Image();
		int dirCode = this.checkDir(userId);
		String filepath;
		for(MultipartFile file : files) {
			// ファイルパス&フォルダパス取得
			filepath = this.getFilepath(dirCode, userId, file.getOriginalFilename());
			try {
				this.saveImageFiles(file, filepath);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			image.setUserId(userId);
			image.setTagId(tagId);
			image.setPath(filepath);
			// ファイル情報をDB格納
			ivMapper.insertImages(image);
		}
	}
}
