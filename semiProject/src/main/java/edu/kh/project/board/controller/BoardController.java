package edu.kh.project.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardReport;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("board")
@Slf4j
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
	@GetMapping("{boardCode}")
	public String getBoardList(@PathVariable("boardCode") int boardCode,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@RequestParam Map<String, Object> paramMap) {

		Map<String, Object> map = null;

		if (paramMap.get("key") == null) {
			// 검색창이 아닌 경우
			map = service.getBoardList(boardCode, cp);
		} else {
			// 검색창으로 검색한 경우
			paramMap.put("boardCode", boardCode);

			map = service.searchBoardList(paramMap, cp);
		}

		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("freeBoardList", map.get("freeBoardList"));

		return "board/boardList";
	}

	/**
	 * dev. 안재훈 1. 자유 게시판 detail
	 * 
	 * @param boardNo
	 * @param cp
	 * @return
	 */
	@GetMapping("{boardCode}/{boardNo}")
	public String freeBoardDetil(Board board,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			RedirectAttributes ra, HttpServletRequest req, HttpServletResponse resp) {
		// 제목, 작성자, 작성일, 조회수, 콘텐츠, 좋아요

		// 로그인 멤버에 대한 것은 추후 로그인 기능 완료되면 구현
		if (loginMember != null) {
			board.setMemberNo(loginMember.getMemberNo());
		}
		Board selectedBoard = service.freeBoardDetil(board);

		String path = null;
		String message = null;

		if (selectedBoard == null) {
			// 게시글이 존재하지 않을 때
			message = "존재하지 않는 게시글입니다.";
			path = "redirect:/board/1?" + "cp=" + cp;
			ra.addFlashAttribute("message", message);
		} else {

			// 3. 조회수 증가 로직 (비로그인 또는 본인 글이 아닐 때)
			if (loginMember == null || loginMember.getMemberNo() != selectedBoard.getMemberNo()) {
				Cookie[] cookies = req.getCookies();
				Cookie c = null;

				if (cookies != null) {
					for (Cookie temp : cookies) {
						if (temp.getName().equals("readBoardNo")) {
							c = temp;
							break;
						}
					}
				}

				int result = 0;
				String boardNoTag = "[" + board.getBoardNo() + "]";

				if (c == null) { // 쿠키가 아예 없거나 readBoardNo 쿠키가 없을 때
					c = new Cookie("readBoardNo", boardNoTag);
					result = service.updateReadCount(board.getBoardNo());
				} else {
					// 해당 게시글을 오늘 처음 읽는 경우
					if (c.getValue().indexOf(boardNoTag) == -1) {
						c.setValue(c.getValue() + boardNoTag);
						result = service.updateReadCount(board.getBoardNo());
					}
				}

				if (result > 0) {
					// DB에서 실제 증가된 조회수를 가져와 세팅
					selectedBoard.setReadCount(result);

					c.setPath("/");
					// 자정까지 남은 시간 계산
					long secondsUntilNextDay = Duration
							.between(LocalDateTime.now(),
									LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0))
							.getSeconds();
					c.setMaxAge((int) secondsUntilNextDay);
					resp.addCookie(c);
				}
			}

			Board prevBoard = service.getPrevBoard(board);
			Board nextBoard = service.getNextBoard(board);

			model.addAttribute("prevBoard", prevBoard);
			model.addAttribute("nextBoard", nextBoard);
			model.addAttribute("boardInfo", selectedBoard);
			model.addAttribute("boardCode", board.getBoardCode());
			model.addAttribute("cp", cp);
			log.debug("memberNo = " + selectedBoard.getMemberNo());

			log.debug("board = " + selectedBoard);
			path = "board/boardDetail";
		}

		return path;
	}

	@ResponseBody
	@PostMapping("like")
	public int boardLike(@RequestBody Map<String, Integer> map) {
		log.debug("board : " + map);
		return service.boardLike(map);
	}

	@ResponseBody
	@PostMapping("scrap")
	public int boardScrab(@RequestBody Map<String, Integer> map) {
		return service.boardScrap(map);
	}

	@GetMapping("{boardCode}/{boardNo}/report")
	public String boardReport(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes ra,
			Model model) {
		String message = null;

		if (loginMember == null) {
			message = "로그인 후 이용 가능합니다.";
			ra.addFlashAttribute("message", message);
			return "redirect:/member/login";
		}
		Board board = new Board();
		board.setBoardCode(boardCode);
		board.setBoardNo(boardNo);

		Board selectedBoard = service.freeBoardDetil(board);
		selectedBoard.setBoardNo(boardNo);
		selectedBoard.setBoardCode(boardCode);
		model.addAttribute("cp", cp);
		model.addAttribute("board", selectedBoard);
		// Board(boardNo=2013, boardTitle=관리자가 자유 게시판에 글쓰면 안됨?, boardContent=<p><span
		// style="font-family: 바탕체;">어쩔 저쩔 </span><span style="font-size: 36px;
		// background-color: rgb(255, 255, 0);"><b><i><font color="#000000">내맘대로
		// 쓸꺼임</font></i></b></span></p>, boardWriteDate=2025-12-25,
		// boardUpdateDate=null, readCount=0, boardDelFl=
		return "board/boardReport";
	}

	@ResponseBody
	@PostMapping("{boardCode}/{boardNo}/report")
	public int boardReport(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			RedirectAttributes ra, @RequestBody Map<String, Object> report,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {

		int result = 0;
		// 이미 신고한 게시글인지 확인
		report.put("boardCode", boardCode);
		report.put("boardNo", boardNo);
		report.put("memberNo", loginMember.getMemberNo());

		BoardReport selectedReport = service.getReport(report);
		if (selectedReport != null) {
			result = -2;
		} else {
			result = service.boardReport(report);
		}

		return result;
	}

}
