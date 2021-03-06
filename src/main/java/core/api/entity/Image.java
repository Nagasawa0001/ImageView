package core.api.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Image {
	private long id;
	private long userId;
	private String path;
	private String title;
	private long tagId;
	private String tagName;
	private long goodCount;
	private boolean preDeleteFlag;
	Timestamp createdAt;
	Timestamp deleteRequestDate;
}
