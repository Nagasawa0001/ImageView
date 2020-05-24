package core.api.entity;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ImageRegisterForm {
	private MultipartFile[] uploadFiles;
	private long userId;
	private String path;
	private String title;
	private long tagId;
	private String tagName;
}
