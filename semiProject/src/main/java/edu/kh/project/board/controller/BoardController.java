package edu.kh.project.board.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	/**
	 * dev. 안재훈 1. 자유 게시판 forward
	 * 
	 * @return
	 */
	@GetMapping("1")
	public String getFreeBoardList(@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			Model model, @RequestParam Map<String, Object> paramMap) {
		int boardCode = 1;

		Map<String, Object> map = null;

		if (paramMap.get("key") == null) {
			// 검색창이 아닌 경우
			map = service.getFreeBoardList(boardCode, cp);
		} else {
			// 검색창으로 검색한 경우
			paramMap.put("boardCode", boardCode);

			map = service.searchFreeBoardList(paramMap, cp);
		}

		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("freeBoardList", map.get("freeBoardList"));
		model.addAttribute("boardCode", boardCode);

		return "board/freeBoardList";
	}

	/**
	 * dev. 안재훈 1. 자유 게시판 detail
	 * 
	 * @param boardNo
	 * @param cp
	 * @return
	 */
	@GetMapping("1/{boardNo}")
	public String freeBoardDetil(@PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp) {
		return "board/freeBoardDetail";
	}

	@GetMapping("3")
	public String getVolunteerBoardList() {
		return "board/volunteerReviewList";
	}
}
