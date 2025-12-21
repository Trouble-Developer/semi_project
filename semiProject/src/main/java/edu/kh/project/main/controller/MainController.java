package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class MainController {
	@GetMapping("/")
	public String mainPage() {
		return "common/main";
	}
}
