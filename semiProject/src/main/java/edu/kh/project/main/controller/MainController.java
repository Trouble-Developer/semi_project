package edu.kh.project.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.kh.project.main.model.Member;
import edu.kh.project.main.model.service.MainService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	
	@Autowired
	private MainService service;
	
	@GetMapping("/")
	public String mainPage() {
		
		Member member = service.testData();
		log.debug("테스트 데이터 : " + member);
		return "common/main";
	}
}
