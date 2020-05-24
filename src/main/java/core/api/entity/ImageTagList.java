package core.api.entity;

import java.util.List;

import lombok.Data;

@Data
public class ImageTagList {
	List<Image> imageList;
	List<Tag> tagList;
}
