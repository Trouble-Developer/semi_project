package edu.kh.project.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("member")
@RequiredArgsConstructor
@Slf4j
@SessionAttributes({ "loginMember" })
public class MemberController {

	private final MemberService service;

	/** 로그인 페이지 이동 */
	@GetMapping("login")
    public String loginPage(HttpSession session, RedirectAttributes ra) {
        
        // 세션에 "loginMember"가 있다? -> 이미 로그인한 상태
        if(session.getAttribute("loginMember") != null) {
            ra.addFlashAttribute("message", "이미 로그인 상태입니다.");
            return "redirect:/"; // 메인 페이지로 튕겨내기
        }
        
        return "member/login";
    }
	/** 로그인 요청 처리 */
	@PostMapping("login")
	public String login(Member inputMember, Model model,
			@RequestParam(value = "saveId", required = false) String saveId, HttpServletResponse resp,
			RedirectAttributes ra) {

		// 서비스 호출
		Member loginMember = service.login(inputMember);

		if (loginMember != null) { // 로그인 성공

			// 1. 세션에 로그인 정보 올리기
			model.addAttribute("loginMember", loginMember);

			// 2. [쿠키 로직] 아이디 저장
			Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
			cookie.setPath("/");

			if (saveId != null) { // 체크박스 켜짐 -> 아이디가 7일간 저장됨
				cookie.setMaxAge(60 * 60 * 24 * 7);
			} else { // 체크박스 꺼짐 -> 쿠키 삭제
				cookie.setMaxAge(0);
			}

			resp.addCookie(cookie);

			return "redirect:/"; // 메인 페이지로

		} else { // 로그인 실패
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
			return "redirect:/member/login";
		}
	}
	
	// [로그아웃 기능]
	@GetMapping("logout") // HTML에서 /member/logout 으로 요청 보냄
	public String logout(SessionStatus status) {
	    
	    // @SessionAttributes로 등록된 세션(loginMember)을 만료시킴
	    // 이걸 호출해야 세션에 있는 회원 정보가 싹 날아감
	    status.setComplete(); 
	    
	    // 로그아웃 했으면 메인 페이지(/)로 이동
	    return "redirect:/";
	}

	
	/** 회원가입 페이지 이동
	 * @param session
	 * @param ra
	 * @return
	 */
	@GetMapping("signup")
    public String signupPage(HttpSession session, RedirectAttributes ra) {
        
        if(session.getAttribute("loginMember") != null) {
            ra.addFlashAttribute("message", "이미 로그인 상태입니다.");
            return "redirect:/"; 
        }
        
        return "member/signup";
    }
	 
	/** 아이디 찾기 페이지 이동
	 * @return "member/findId"
	 */
	@GetMapping("findId")
	public String findIdPage() {
		return "member/findId";
	}
	
	
	/**
	 * 회원가입 진행 (POST)
	 * 
	 * @param inputMember   : 커맨드 객체 (제출된 파라미터가 필드에 자동 세팅됨)
	 * @param memberAddress : 주소 입력값 3개가 배열로 들어옴 (우편번호, 도로명/지번, 상세주소)
	 * @param ra            : 리다이렉트 시 메시지 전달용
	 * @return
	 */
	@PostMapping("signup")
	public String signup(Member inputMember, @RequestParam("memberAddress") String[] memberAddress,
			RedirectAttributes ra) {

		// 1. 주소 하나로 합치기 (우편번호^^^도로명/지번^^^상세주소)
		// 만약 주소를 입력 안 했으면 memberAddress 배열 요소가 비어있을 수 있음.

		if (inputMember.getMemberAddress() != null) { // 주소 입력값이 넘어왔다면
			// memberAddress 배열을 String 하나로 합쳐서 DTO에 세팅
			String addr = String.join(",,", memberAddress);
			inputMember.setMemberAddress(addr);
		}

		// 2. 서비스 호출
		int result = service.signup(inputMember);

		String path = "redirect:";
		String message = null;

		if (result > 0) { // 가입 성공
			path += "/"; // 메인 페이지로 이동
			message = inputMember.getMemberNickname() + "님의 가입을 환영합니다.";
		} else { // 가입 실패
			path += "signup"; // 다시 회원가입 페이지로
			message = "회원가입 실패";
		}

		ra.addFlashAttribute("message", message);
		return path;
	}

	// -----------------------------------------
	// 비동기(AJAX) 요청 처리 구역
	// -----------------------------------------

	/**
	 * 아이디 중복 검사
	 * 
	 * @param memberId
	 * @return count (1:중복, 0:사용가능)
	 */
	@ResponseBody
	@GetMapping("checkId")
	public int checkId(@RequestParam("memberId") String memberId) {
		return service.checkId(memberId);
	}

	/**
	 * 닉네임 중복 검사
	 * 
	 * @param memberNickname
	 * @return count
	 */
	@ResponseBody
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}

	/**
	 * 이메일 중복 검사
	 * 
	 * @param memberEmail
	 * @return count
	 */
	@ResponseBody
	@GetMapping("checkEmail")
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}

}