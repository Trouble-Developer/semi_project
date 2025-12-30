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
	//=============================
	// 아이디/ 비밀번호 찾기 관련 기능
	// =============================
	
	/* 아이디 찾기 페이지로 이동
	 * 
	 * <p> 회원이 아이디를 잊어버렸을 때 접근하는 페이지</p>
	 * <p> 이름, 주민번호 앞자리(생년월일), 이메일을 입력 받아 아이디를 찾아줌</p>
	 * 
	 * @return "member/findId" : 아이디 찾기 페이지로 이동
	 * */
	
	/** 아이디 찾기 처리 (AJAX)
	 * 
	 * /** 아이디 찾기 처리 (비동기 요청)
	 * 
	 * <p>사용자가 입력한 이름, 주민번호 앞자리, 이메일 정보를 받아서
	 * DB에서 일치하는 회원 정보를 조회한 후 아이디와 가입일자를 반환</p>
	 * 
	 * <p><strong>[처리 흐름]</strong></p>
	 * <ol>
	 *   <li>클라이언트에서 AJAX로 POST 요청 (이름, 주민번호 앞자리, 이메일)</li>
	 *   <li>Controller가 파라미터를 받아서 Service로 전달</li>
	 *   <li>Service/Mapper를 통해 DB에서 회원 조회</li>
	 *   <li>조회된 Member 객체를 JSON 형태로 응답</li>
	 *   <li>클라이언트에서 아이디와 가입일자를 화면에 표시</li>
	 * </ol>
	 * 
	 * @param memberNickname : 이름
	 * @param memberRrn1 : 주민번호 앞자리 (생년월일)
	 * @param memberEmail : 이메일
	 * @return Member 객체 (아이디, 가입일자) 또는 null
	 */
	 @ResponseBody  // 리턴값을 HTTP 응답 본문(JSON)으로 변환 (AJAX 요청이므로 필수!)
	 @PostMapping("findId")  // /member/findId POST 요청 처리
	 public Member findId(
			 @RequestParam("memberName") String memberName,
		    @RequestParam("memberRrn1") String memberRrn1,          // 주민번호 앞자리 파라미터
		    @RequestParam("memberEmail") String memberEmail         // 이메일 파라미터
		) {
	    
	    // Service의 findId 메서드 호출하여 DB 조회
	    // - 3개 파라미터가 모두 일치하는 회원 찾기
	    // - 조회 성공 시: Member 객체 리턴 (memberId, enrollDate 포함)
	    // - 조회 실패 시: null 리턴
		 Member findMember = service.findId(memberName, memberRrn1, memberEmail);
	    
	    // @ResponseBody 덕분에 Member 객체가 자동으로 JSON으로 변환되어 응답됨
	    // 예시: { "memberId": "user123", "enrollDate": "2024년 01월 15일", ... }
	    return findMember;
	}

	
	// ========================================
	// 비밀번호 찾기 관련 기능
	// ========================================

	/** 비밀번호 찾기 페이지 이동
	 * 
	 * <p>회원이 비밀번호를 잊어버렸을 때 접근하는 페이지</p>
	 * <p>아이디, 이름, 주민번호, 이메일을 입력받아 본인 확인 후 비밀번호 재설정</p>
	 * 
	 * @return "member/findPw" : 비밀번호 찾기 페이지로 이동
	 */
	@GetMapping("findPw")
	public String findPwPage() {
	    // Thymeleaf가 /templates/member/findPw.html 을 찾아서 보여줌
	    return "member/findPw";
	}
	
	/**
	 * @param memberId: 회원 아이디
	 * @param memberName : 회원 이름
	 * @param memberRrn1 : 주민번호 앞자리 (생년월일)
	 * @param memberEmail : 이메일
	 * @return Member 객체 (조회 성공) 또는 null (조회 실패)
	 */
	@ResponseBody
	@PostMapping("findPw")
	public Member findPw(
		@RequestParam("memberId") String memberId,
		@RequestParam("memberName") String memberName,
		@RequestParam("memberRrn1") String memberRrn1,
		@RequestParam("memberEmail") String memberEmail
	) {
		/* Serivce의 findPw 메서드 호출하여 DB 조회
		 * 4개 파라미터가 모두 일치하는 회원 찾기
		 * 조회 성공 : Member 객체 리턴
		 * 조회 실패 : null 리턴
		 * */
		Member findMember = service.findPw(memberId, memberName, memberRrn1, memberEmail);
		
		return findMember; 
	}
		/** 비밀번호 재설정 처리 (AJAX)
		 * 
		 * @param memberId : 회원 아디
		 * @param newPw : 새 비번
		 * @return result (1: 성공, 0: 실패)
		 */
		@ResponseBody
		@PostMapping("resetPw")
		public int resetPw(
			@RequestParam("memberId") String memberId,
			@RequestParam("newPw") String newPw
		) {
		
			// Service의  resetPw 메서드 호출
			// 비밀번호 암호화 하고 DB 업데이트 해야함
			// 성공 시: 1 리턴
			// 실패 시: 0 리턴
		
		int result = service.resetPw(memberId, newPw);
		
		return result;
		
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

		// 1. 주소 하나로 합치기 (우편번호,,도로명/지번,,상세주소)
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