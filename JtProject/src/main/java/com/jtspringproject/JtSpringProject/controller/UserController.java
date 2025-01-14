package com.jtspringproject.JtSpringProject.controller;

import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.services.userService;
import com.jtspringproject.JtSpringProject.services.productService;

@Controller
public class UserController {

	@Autowired
	private userService userService;
	@Autowired
	private productService productService;

	@GetMapping("/register")
	public String registerUser() {
		return "register";
	}

	@GetMapping("/buy")
	public String buy() {
		return "buy";
	}

	@GetMapping("/")
	public ModelAndView mainRedirect(Model model, HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();

		// If the user is logged in, redirect them to the home page
		if (cookies != null) {
			String username = "";
			String password = "";

			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				String value = cookie.getValue();

				if (name.equalsIgnoreCase("username")) {
					username = value;
				} else if (name.equalsIgnoreCase("password")) {
					password = value;
				}
			}

			User u = this.userService.checkLogin(username, password);

			if (u.getUsername() != null) {
				ModelAndView mView = new ModelAndView("index");
				List<Product> products = this.productService.getProducts();

				mView.addObject("products", products);
				mView.addObject("user", u);

				return mView;
			}
		}

		ModelAndView mView = new ModelAndView("index");
		mView.addObject("msg", "Please enter correct email and password");

		return new ModelAndView("userLogin");
	}

	@GetMapping("/userlogout")
	public ModelAndView userlogout(Model model, HttpServletRequest req, HttpServletResponse res) {
		ModelAndView mView = new ModelAndView("redirect:/");

		res.addCookie(new Cookie("username", ""));

		return mView;
	}

	@RequestMapping(value = "userloginvalidate", method = RequestMethod.POST)
	public ModelAndView userlogin(@RequestParam("username") String username, @RequestParam("password") String pass,
			Model model, HttpServletResponse res) {

		User u = this.userService.checkLogin(username, pass);
		ModelAndView mView = new ModelAndView("redirect:/");

		if (u.getUsername() != null) {

			res.addCookie(new Cookie("username", u.getUsername()));

			// DO NOT DO THIS IN REAL LIFE
			// This is a quick hack to get the user to the next page, but it's terribly not
			// secure
			// An authentication token should be generated and stored in the database,
			// accompanied by a full authentication system
			res.addCookie(new Cookie("password", u.getPassword()));

			mView.addObject("user", u);
			List<Product> products = this.productService.getProducts();

			if (products.isEmpty()) {
				mView.addObject("msg", "No products are available");
			} else {
				mView.addObject("products", products);
			}
			return mView;

		} else {
			mView.addObject("msg", "Please enter correct email and password");
			return mView;
		}

	}

	@GetMapping("/user/products")
	public ModelAndView getproduct() {

		ModelAndView mView = new ModelAndView("uproduct");

		List<Product> products = this.productService.getProducts();

		if (products.isEmpty()) {
			mView.addObject("msg", "No products are available");
		} else {
			mView.addObject("products", products);
		}

		return mView;
	}

	@RequestMapping(value = "newuserregister", method = RequestMethod.POST)
	public String newUseRegister(@ModelAttribute User user) {

		System.out.println(user.getEmail());
		user.setRole("ROLE_NORMAL");
		this.userService.addUser(user);

		return "redirect:/";
	}
}
