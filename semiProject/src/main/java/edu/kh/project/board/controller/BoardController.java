package edu.kh.project.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;

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
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			RedirectAttributes ra) {
		// 제목, 작성자, 작성일, 조회수, 콘텐츠, 좋아요
		int boardCode = 1;

		// 로그인 멤버에 대한 것은 추후 로그인 기능 완료되면 구현
		Map<String, Integer> map = new HashMap<>();
		map.put("boardNo", boardNo);
		map.put("boardCode", boardCode);

		Board selectedBoard = service.freeBoardDetil(map);

		String path = null;
		String message = null;

		if (selectedBoard == null) {
			// 게시글이 존재하지 않을 때
			message = "존재하지 않는 게시글입니다.";
			path = "redirect:/board/1?" + "cp=" + cp;
			ra.addFlashAttribute("message", message);
		} else {
			// 이전글, 다음글 가져오기
			// 만약 게시글이 500번째 중 500번 글, 즉 마지막 글이라면
			// 다음 글이 존재하지 않는다

			// 만약 게시글이 500번 째중 1번 글, 즉 첫번 째 글이라면
			// 이전글이 존재하지 않는다

			Board prevBoard = service.getPrevBoard(map);
			Board nextBoard = service.getNextBoard(map);

			model.addAttribute("prevBoard", prevBoard);
			model.addAttribute("nextBoard", nextBoard);
			model.addAttribute("boardInfo", selectedBoard);
			model.addAttribute("boardCode", boardCode);
			model.addAttribute("cp", cp);
			path = "board/freeBoardDetail";
		}

		return path;
	}
	
	
	@GetMapping("3")
	public String getVolunteerBoardList() {
		return "board/volunteerReviewList";
	}
}
