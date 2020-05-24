package core.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import core.api.entity.ImageRegisterForm;
import core.api.entity.ImageTagList;

@Controller
public class IVController {

	@Autowired
	IVService ivService;

	@GetMapping("/")
	public String getListPage(Model model) {
		ImageTagList list = ivService.getImageAndTagList();
		model.addAttribute("imageList", list.getImageList());
		model.addAttribute("tagList", list.getTagList());
		return "image_list";
	}

	@PostMapping("/")
	public String uploadImage(@ModelAttribute ImageRegisterForm form) throws IOException {
		ivService.registerImage(form);
		return "redirect:/";
	}

	// 画像ファイルをダウンロード
	@PostMapping("/download")
	public void downloadImage() {

	}

	// 画像リストを並べ替え
	@GetMapping("/sort")
	public String sortImages() {
		return null;
	}

	// タグ名で検索
	@GetMapping("/search/{tag}")
	public String searchByTagName() {
		return null;
	}

	// 削除依頼を申請
	@PatchMapping("/request/deletion")
	public void requestDeletion() {

	}

	// いいねを増やす
	@PatchMapping("/count/good")
	public void countGood() {

	}
}
