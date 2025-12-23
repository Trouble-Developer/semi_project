package edu.kh.project.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.service.EditBoardService;

@Controller
@RequestMapping("editBoard")
public class EditBoardController {

	@Autowired
	private EditBoardService service;

	/**
	 * dev.안재훈 게시판 삭제
	 * 
	 * @return
	 */
	@RequestMapping("{boardCode}/{boardNo}/delete")
	public String boardDelete(@PathVariable("boardCode") int boardCode,@PathVariable("boardNo") int boardNo, RedirectAttributes ra,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp) {
		// 추후 로그인한 회원이 맞는지 로직 짜기
		// "삭제는 로그인한 회원만 가능합니다."
		int result = service.boardDelete(boardNo);
		
		String message = null;
		String path = null;

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

}
