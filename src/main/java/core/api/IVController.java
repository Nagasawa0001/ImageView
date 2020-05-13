package core.api;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class IVController {

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
		System.out.println(userId);
		for(MultipartFile multipartFile : multipartFiles) {
			System.out.println(multipartFile.getOriginalFilename());
			System.out.println(multipartFile.getContentType());
			System.out.println(multipartFile.getName());
			System.out.println(multipartFile.getSize());
			System.out.println(multipartFile.getBytes());
		}

		return "redirect:/";
	}
}
