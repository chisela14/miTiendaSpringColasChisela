package com.jacaranda.tienda.controller;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jacaranda.tienda.model.User;
import com.jacaranda.tienda.model.UserException;
import com.jacaranda.tienda.service.UserService;

import jakarta.mail.MessagingException;

@Controller
public class UserController {

	@Autowired
	UserService userServ;
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/usuario/list")
	public String userList(Model model, @RequestParam("pageNumber")Optional<Integer> pageNumber,
			@RequestParam("sizeNumber") Optional<Integer> sizeNumber,
			@RequestParam("sortField") Optional<String> sortField,
			@RequestParam("stringFind") Optional<String> stringFind) {
		
		Page<User> page = userServ.getUsers(pageNumber.orElse(1), sizeNumber.orElse(10), sortField.orElse("code"), stringFind.orElse(null));
		
		model.addAttribute("currentPage", pageNumber.orElse(1));
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField.orElse("id"));
		model.addAttribute("keyword", stringFind.orElse(""));
		
		model.addAttribute("users", page.getContent());
		return "userList";
	}
	
	@GetMapping({"usuario/add", "signUp"})
	public String addUser(Model model) {
		User u = new User();
		model.addAttribute("newUser", u);
		return "addUser";
	}
	
	//validaciones que se puedan, la comprobación de que las contraseñas coinciden
	//le corresponde al cliente
	@PostMapping({"usuario/add", "signUp"})
	public String addSubmit( @Validated @ModelAttribute("newUser") User u,
			BindingResult bindingResult) {
		//TO DO la contraseña no coincide
		//puedo hacerlo recogiendo el param por post?
		if (bindingResult.hasErrors()) { 
			return "addUser";
		}else {
			try {
				userServ.add(u, "http://localhost:8080/usuario");
			} catch (UnsupportedEncodingException | MessagingException | UserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//?
			return "redirect:/usuario/list";
		}
	}
	@GetMapping("usuario/verify")
	public String verify (@Param("code") String code) {
		if (userServ.verify(code)) {
			return "verifySuccess";
		} else {
			return "verifyFail";
		}
	}
	
	@GetMapping("usuario/delete")
	public String deleteUser(Model model, @RequestParam("id") String username) {
		User user = userServ.get(username);
		model.addAttribute("userDel", user);
		return "deleteUser";
	}
	
	@PostMapping("usuario/delete")
	public String deleteSubmit(@ModelAttribute("userDel") User user) {
		userServ.delete(user);
		return "redirect:/usuario/list";
	}
	
	@GetMapping("usuario/update")
	public String updateUser(Model model, @RequestParam("id") String username) {
		User user = userServ.get(username);
		model.addAttribute("userUp", user);
		return "updateUser";
	}
	
	@PostMapping("usuario/update")
	public String updateSubmit(@ModelAttribute("userUp") User user) {
		userServ.update(user);
		return "redirect:/usuario/list";
	}
	
	@GetMapping("usuario/admin")
	public String changeAdmin(Model model, @RequestParam("id") String username) {
		User user = userServ.get(username);
		model.addAttribute("userAdm", user);
		return "changeAdmin";
	}
	
	//cuidado con que la contraseña se guarde vacía porque no entrará por aquí
	@PostMapping("usuario/admin")
	public String adminSubmit(@ModelAttribute("userAdm") User user) {
		userServ.update(user);
		return "redirect:/usuario/list";
	}
	


}
