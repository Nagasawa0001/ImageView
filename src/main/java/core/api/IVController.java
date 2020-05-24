package core.api;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import core.api.entity.Image;
import core.api.entity.ImageRegisterForm;

@Controller
public class IVController {

	@Autowired
	IVService ivService;

	@GetMapping("/")
	public String getListPage(Model model) {
		List<Image> imageList = ivService.getImageList();
		model.addAttribute("imageList", imageList);
		return "image_list";
	}

	@PostMapping("/")
	public String registerImage(@ModelAttribute ImageRegisterForm form) throws IOException {
		ivService.registerImage(form);
		return "redirect:/";
	}
}
