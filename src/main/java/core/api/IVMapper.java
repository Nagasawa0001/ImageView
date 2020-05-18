package core.api;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import core.api.entity.Image;
import core.api.entity.Tag;

@Mapper
public interface IVMapper {

	// 画像アップロード処理
	@Insert("INSERT INTO images (userId, path, tagId) VALUES (#{userId}, #{path}, #{tagId})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void insertImages(Image image);

	// tagNameがマスタに存在するか判定
	@Select("SELECT id FROM tags WHERE name=#{tagName}")
	public Tag selectTagId(String tagName);

	// 新規tagNameを登録
	@Insert("INSERT INTO tags (name) VALUES (#{tagName})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public long insertTag(String tagName);
}
