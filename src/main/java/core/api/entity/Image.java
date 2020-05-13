package core.api.entity;

import lombok.Data;

@Data
public class Image {
	private long id;
	private long userId;
	private String path;
	private long tagId;
	private long viewCount;
	private long favoriteCount;
	private long goodCount;
	private long createdAt;
	private long updatedAt;
}
