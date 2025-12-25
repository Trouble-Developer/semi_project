package edu.kh.project.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.board.model.service.EditBoardService;
import edu.kh.project.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("editBoard")
@Slf4j
public class EditBoardController {

	@Autowired
	private EditBoardService service;

	@Autowired
	private BoardService boardService;

	/**
	 * dev.안재훈 게시판 삭제
	 * 
	 * @return
	 */
	@RequestMapping("{boardCode}/{boardNo}/delete")
	public String boardDelete(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			RedirectAttributes ra, @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		// 추후 로그인한 회원이 맞는지 로직 짜기
		// "삭제는 로그인한 회원만 가능합니다."
		// 로그인 멤버에 대한 것은 추후 로그인 기능 완료되면 구현
		Map<String, Integer> map = new HashMap<>();
		map.put("boardNo", boardNo);
		map.put("boardCode", boardCode);
		String message = null;
		String path = null;
		// 로그인 안 한 경우
		if (loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		// 관리자만이 공지사항 및 고객지원 삭제 가능
		if ((boardCode == 4 || boardCode == 5) && loginMember.getAuthority() == 1) {
			message = "공지사항 및 고객지원 게시글은 관리자만 삭제할 수 있습니다.";
			ra.addFlashAttribute("message", message);
			return "redirect:/board/" + boardCode + "?cp=" + cp;
		}

		Board selectedBoard = boardService.freeBoardDetil(map);

		// 본인이 작성한 글만 삭제 가능
		if (loginMember != null && (loginMember.getMemberNo() != selectedBoard.getMemberNo())) {
			message = "본인이 작성한 글만 삭제할 수 있습니다.";
			ra.addFlashAttribute("message", message);
			return "redirect:/board/" + boardCode + "?cp=" + cp;
		}
		int result = service.boardDelete(boardNo);

		if (result > 0) {
			message = "삭제를 성공했습니다!";
			path = String.format("/board/%d?cp=%d", boardCode, cp);
		} else {
			message = "삭제 실패..";
			path = String.format("/board/%d/%d?cp=%d", boardCode, boardNo, cp);
		}
		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}

	@GetMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode, Model model) {
		model.addAttribute("boardCode", boardCode);
		return "board/boardWrite";
	}

	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode, @RequestParam Map<String, Object> map,
			RedirectAttributes ra, @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute("loginMember") Member loginMember) {
		log.info("섬머노트 데이터 : {}", map);

		// 비밀글 체크 했을 때 : 섬머노트 데이터 : {boardType=3, title=안녕하심꽈 테스트 글임돠!, checkbox=on,
		// editordata=<p>테스트1</p>, files=}
		// 비밀글 체크 안했을 때 : 섬머노트 데이터 : {boardType=3, title=ㅁㅁㅁㅁ, editordata=<p>ㅁㅁㅁㅁ</p>,
		// files=}
		// boardType
		String message = null;
		String path = null;
		String content = (String) map.get("editordata");

		if (content.indexOf("<img") == -1) {
			log.debug("img 없음");
			// int result = service.boardInsert(boardCode, content, loginMember.getMemberNo());

			// if (result > 0) {
			// 	message = "게시글이 등록되었습니다.";
			// 	path = "redirect:/board/" + boardCode + "?cp=" + cp;
			// } else {
			// 	message = "게시글이 등록되었습니다.";
			// 	path = "redirect:/" + boardCode + "/insert";
			// }

		} else {
			log.debug("img 존재함");
		}
		return "redirect:/";
	}

}
