package com.jacaranda.tienda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jacaranda.tienda.model.Article;
import com.jacaranda.tienda.model.Category;
import com.jacaranda.tienda.service.ArticleService;
import com.jacaranda.tienda.service.CategoryService;

@Controller
public class ArticleController {

	@Autowired
	ArticleService serv;
	CategoryService catService;
	
	@GetMapping({"/", "/articulo/list"})
	public String articleList(Model model) {
		model.addAttribute("flowers", serv.getArticles());
		return "articleList";
	}
	
	@GetMapping("articulo/add")
	public String addArticle(Model model) {
		Article a = new Article();
		model.addAttribute("newArticle", a);
		return "addArticle";
	}
	@PostMapping("articulo/add")
	public String addSubmit(@ModelAttribute("newArticle") Article a, @RequestParam("color") String colorCode) {
		Category color = catService.get(colorCode);
		if(color != null) {
			a.setColor(color);
			serv.add(a);
			return "redirect:/articulo/list";
		}else {
			return "redirect:/articulo/error";
		}
	}
	
	
	@GetMapping("articulo/delete")
	public String deleteArticle(Model model, @RequestParam("code") Long code) {
		Article flower = serv.get(code);
		model.addAttribute("delArticle", flower);
		return "deleteArticle";
	}
	@PostMapping("articulo/delete")
	public String deleteSubmit(@ModelAttribute("delArticle") Article a) {
		serv.delete(a);
		return "redirect:/articulo/list";
	}
	
	
	@GetMapping("articulo/update")
	public String updateArticle(Model model, @RequestParam("code") Long code) {
		Article flower = serv.get(code);
		model.addAttribute("upArticle", flower);
		return "updateArticle";
	}
	@PostMapping("articulo/update")
	public String updateSubmit(@ModelAttribute("upArticle") Article a) {
		serv.update(a);
		return "redirect:/articulo/list";
	}
	
	
	@GetMapping("articulo/error")
	public String error() {
		return "error";
	}

}