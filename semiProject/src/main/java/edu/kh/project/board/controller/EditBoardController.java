package edu.kh.project.board.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.kh.project.admin.controller.AdminController;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.board.model.service.EditBoardService;
import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("editBoard")
@Slf4j
public class EditBoardController {

	private final AdminController adminController;

	@Autowired
	private EditBoardService service;

	@Autowired
	private BoardService boardService;

	@Value("${board.image.web-path}")
	private String boardImageWebPath;

	@Value("${board.image.folder-path}")
	private String boardImageFolderPath;

	EditBoardController(AdminController adminController) {
		this.adminController = adminController;
	}

	/**
	 * dev.안재훈 게시판 삭제
	 * 
	 * @return
	 */
	@RequestMapping("{boardCode}/{boardNo}/delete")
	public String boardDelete(Board board, RedirectAttributes ra,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		// 추후 로그인한 회원이 맞는지 로직 짜기
		// "삭제는 로그인한 회원만 가능합니다."
		// 로그인 멤버에 대한 것은 추후 로그인 기능 완료되면 구현
		Map<String, Integer> map = new HashMap<>();
		map.put("boardNo", board.getBoardNo());
		map.put("boardCode", board.getBoardCode());
		String message = null;
		String path = null;
		// 로그인 안 한 경우
		if (loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		// 관리자만이 공지사항 및 고객지원 삭제 가능
		if ((board.getBoardCode() == 4 || board.getBoardCode() == 5) && loginMember.getAuthority() == 1) {
			message = "공지사항 및 고객지원 게시글은 관리자만 삭제할 수 있습니다.";
			ra.addFlashAttribute("message", message);
			return "redirect:/board/" + board.getBoardCode() + "?cp=" + cp;
		}

		Board selectedBoard = boardService.freeBoardDetil(board);

		// 본인이 작성한 글만 삭제 가능
		if (loginMember != null && (loginMember.getMemberNo() != selectedBoard.getMemberNo())) {
			message = "본인이 작성한 글만 삭제할 수 있습니다.";
			ra.addFlashAttribute("message", message);
			return "redirect:/board/" + board.getBoardCode() + "?cp=" + cp;
		}
		int result = service.boardDelete(board.getBoardNo());

		if (result > 0) {
			message = "삭제를 성공했습니다!";
			path = String.format("/board/%d?cp=%d", board.getBoardCode(), cp);
		} else {
			message = "삭제 실패..";
			path = String.format("/board/%d/%d?cp=%d", board.getBoardCode(), board.getBoardNo(), cp);
		}
		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}

	/* ===================== 글 작성 페이지 ===================== */
	@GetMapping("{boardCode:[0-9]+}/insert")
	public String insertForm(@PathVariable("boardCode") int boardCode,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes ra,
			Model model) {

		if (loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요");
			return "redirect:/member/login";
		}

		model.addAttribute("boardCode", boardCode);
		return "board/boardWrite";
	}

	/** 글 등록
	 * @param boardCode
	 * @param paramMap
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode, @RequestParam Map<String, Object> paramMap,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes ra) {

		paramMap.put("content", paramMap.get("editordata"));
		paramMap.put("memberNo", loginMember.getMemberNo());
		paramMap.put("boardLock", paramMap.get("checkbox") != null ? "Y" : "N");

		int result = service.boardInsert(paramMap);
		String message = null;

		if (result > 0) {
			message = "게시글이 등록되었습니다.";
		} else {
			message = "게시글 등록 실패..";
		}
		ra.addFlashAttribute("message", message);

		return "redirect:/board/" + boardCode;
	}

	/** 이미지 서버 컴퓨터에 업로드 (Summernote) 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping("image/upload")
	@ResponseBody
	public Map<String, Object> imageUpload(@RequestParam("file") MultipartFile file) throws Exception {

		String rename = Utility.fileRename(file.getOriginalFilename());

		File target = new File(boardImageFolderPath + rename);
		file.transferTo(target);

		Map<String, Object> map = new HashMap<>();
		map.put("url", boardImageWebPath + rename); // /upload/board/xxx.jpg 형식으로 서버컴퓨터에 저장
		return map;
	}

	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes ra) {

		Board board = new Board();
		board.setBoardCode(boardCode);
		board.setBoardNo(boardNo);

		Board selectedBoard = boardService.freeBoardDetil(board);

		if (loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요");
			return "redirect:/member/login";
		}

		if (selectedBoard == null) {
			ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
			return "redirect:/board/" + boardCode;
		}

		if (loginMember.getMemberNo() != selectedBoard.getMemberNo()) {
			ra.addFlashAttribute("message", "본인이 작성한 글만 수정할 수 있습니다.");
			return "redirect:/board/" + boardCode + "/" + boardNo + "?cp=" + cp;
		}

		model.addAttribute("boardInfo", selectedBoard);
		model.addAttribute("boardCode", boardCode);

		return "board/boardWrite";
	}

	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember,
			RedirectAttributes ra, @RequestParam Map<String, Object> paramMap) {

		String message = null;

		if (loginMember == null) {
			message = "로그인 후 이용해주세요.";
			ra.addFlashAttribute("message", message);
			return "redirect:/member/login";
		}

		paramMap.put("boardNo", boardNo);
		paramMap.put("boardCode", boardCode);
		paramMap.put("memberNo", loginMember.getMemberNo());
		paramMap.put("content", paramMap.get("editordata"));
		paramMap.put("boardLock", paramMap.get("checkbox") != null ? "Y" : "N");

		int result = service.boardUpdate(paramMap);

		if (result > 0) {
			message = "게시글이 수정되었습니다.";
		} else {
			message = "게시글 수정 실패..";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:/board/" + boardCode + "/" + boardNo;
	}
}
