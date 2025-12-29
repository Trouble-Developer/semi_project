package edu.kh.project.main.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.main.model.service.MainService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	
	@Autowired
	private MainService service;
	
	@GetMapping("/")
	public String mainPage(Model model) {
//		throw new RuntimeException("강제 500 에러");
		
		// boardNo
		int boardNo = 3;
		List<Board> boardList = service.getVolunteerList(boardNo);
		
		model.addAttribute("boardList", boardList);
		
		return "common/main";
	}
}
