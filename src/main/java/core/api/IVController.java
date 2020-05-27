package core.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		form.setUserId(1);
		ivService.registerImage(form);
		return "redirect:/";
	}

	// 画像ファイルをダウンロード
	@GetMapping("/download")
	public String downloadImage(@RequestParam(name="id", required=false)long id, HttpServletResponse response) {
        ivService.downloadImage(id, response);
        return "redirect:/";
	}

	// 画像リストを並べ替え
	@GetMapping("/sort")
	public String sortImages(@RequestParam(name="target", required=false) String target, @RequestParam(name="sortType", required=false) String sortType, Model model) {
		ImageTagList list = ivService.getImageListBySort(target, sortType);
		model.addAttribute("imageList", list.getImageList());
		model.addAttribute("tagList", list.getTagList());
		return "image_list";
	}

	// タグ名で検索
	@GetMapping("/search")
	public String searchByTagName(@RequestParam(name="tagId", required=false) long tagId, Model model) {

		ImageTagList list = ivService.searchByTag(tagId);
		model.addAttribute("imageList", list.getImageList());
		model.addAttribute("tagList", list.getTagList());
		return "image_list";
	}

	// 削除依頼を申請
	@GetMapping("/request/deletion")
	public String requestDeletion(@RequestParam(name="id", required=false)long id) {
		ivService.requestDeletion(id);
		return "redirect:/";
	}

	// いいねを増やす
	@GetMapping("/count/good")
	public String countGood(@RequestParam(name="id", required=false)long id) {
		ivService.incrementGoodCount(id);
		return "redirect:/";
	}
}
