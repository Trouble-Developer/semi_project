package edu.kh.project.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policy")
public class PolicyController {

	@GetMapping("/terms")
	public String terms() {
		return "common/terms";
	}

	@GetMapping("/privacy")
	public String privacy() {
		return "common/privacy";
	}

	@GetMapping("/copyright")
	public String copyright() {
		return "common/copyright";
	}
}