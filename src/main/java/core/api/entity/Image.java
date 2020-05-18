package core.api.entity;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Image {
	private MultipartFile image;
	private long id;
	private long userId;
	private String path;
	private long tagId;
	private String tagName;
	private long viewCount;
	private long favoriteCount;
	private long goodCount;
	private long createdAt;
	private long updatedAt;
}
