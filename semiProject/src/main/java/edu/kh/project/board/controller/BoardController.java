package edu.kh.project.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.project.board.model.service.BoardService;

@Controller
@RequestMapping("board")
public class BoardController {
	
	// 정보게시판 아예 별개 컨트롤러로
	// 1번 : 자유게시판
	// 2번 : 정보 게시판
	// 3번 : 봉사후기
	// 4번 : 공지사항
	// 5번 : 고객지원
	// 6번 : 관리자
	@Autowired
	private BoardService service;
}
