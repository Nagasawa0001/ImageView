package core.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import core.api.entity.Image;

@Controller
public class IVController {

	@Autowired
	IVService ivService;

	@GetMapping("/")
	public String getListPage(Model model) {
		return "image_list";
	}

	@GetMapping("/image/register")
	public String getRegisterPage() {
		return "image_register";
	}

	@PostMapping("/")
	public String registerImage(@RequestParam("uploadFiles") MultipartFile[] multipartFiles, @RequestParam("userId") long userId, @RequestParam("tagName") String tagName) throws IOException {
		List<Image> imageList = new ArrayList<Image>();
		for(MultipartFile multipartFile : multipartFiles) {
			System.out.println(multipartFile.getOriginalFilename());
			Image image = new Image();
			image.setUserId(userId);
			image.setTagName(tagName);
			image.setImage(multipartFile);
			imageList.add(image);
		}
		ivService.registerImage(multipartFiles, userId, tagName);


		return "redirect:/";
	}
}
