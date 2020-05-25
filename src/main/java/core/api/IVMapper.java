package core.api;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import core.api.entity.Image;
import core.api.entity.ImageRegisterForm;
import core.api.entity.Tag;

@Mapper
public interface IVMapper {

	// 画像アップロード処理
	@Insert("INSERT INTO images (userId, path, tagId) VALUES (#{userId}, #{path}, #{tagId})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void insertImages(ImageRegisterForm form);

	// tagNameがマスタに存在するか判定
	@Select("SELECT id FROM tags WHERE name=#{tagName}")
	public Tag selectTagId(String tagName);

	// 新規tagNameを登録
	@Insert("INSERT INTO tags (name) VALUES (#{tagName})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public long insertTag(String tagName);

	// 画像リストを取得
	@Select("SELECT images.id, images.userId, images.tagId, tags.name AS tagName, images.title, images.path, images.viewCount, images.favoriteCount, images.goodCount, images.preDeleteFlag, images.createdAt FROM images INNER JOIN tags ON images.tagId=tags.id")
	public List<Image> selectImageList();

	@Select("SELECT * FROM tags")
	public List<Tag> selectTagList();

	// タグ名で絞り込んだ画像リストを取得
	@Select("SELECT images.id, images.userId, images.tagId, tags.name AS tagName, images.title, images.path, images.viewCount, images.favoriteCount, images.goodCount, images.preDeleteFlag, images.createdAt FROM images INNER JOIN tags ON images.tagId=tags.id WHERE images.tagId=#{tagId}")
	public List<Image> selectImageListByTag(long tagId);

	// 並び替えした画像リストを取得
	@Select("SELECT images.id, images.userId, images.tagId, tags.name AS tagName, images.title, images.path, images.viewCount, images.favoriteCount, images.goodCount, images.preDeleteFlag, images.createdAt FROM images INNER JOIN tags ON images.tagId=tags.id ORDER BY ${target} ${sortType}")
	public List<Image> getImageListBySort(@Param("target")String target, @Param("sortType")String sortType);

}
