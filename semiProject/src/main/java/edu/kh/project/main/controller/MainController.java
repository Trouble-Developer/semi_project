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

		List<Board> freeBoardList = service.getFreeBoardList();
		List<Board> infoBoardList = service.getInfoBoardList();
		List<Board> noticeBoardList = service.getNoticeBoardList();
		Board reviewBestUser = service.getReviewBestUser();
	
		
		// 봉사후기 (최신순 10개)
		model.addAttribute("boardList", boardList);
		// 자유게시판(좋아요 순 5개)
		model.addAttribute("freeBoardList", freeBoardList);
		// 정보게시판(조회수 순 5개)
		model.addAttribute("infoBoardList", infoBoardList);
		// 공지사항(최신순 5개)
		model.addAttribute("noticeBoardList", noticeBoardList);

		// 이달의 봉사왕
		model.addAttribute("reviewBestUser", reviewBestUser);
		return "common/main";
	}

}
