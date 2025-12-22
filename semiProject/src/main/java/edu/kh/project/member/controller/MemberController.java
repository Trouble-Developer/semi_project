package edu.kh.project.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService service;

    /** 회원가입 페이지 이동
     * @return "member/signup"
     */
    @GetMapping("signup")
    public String signupPage() {
        return "member/signup";
    }

    /** 회원가입 진행 (POST)
     * @param inputMember : 커맨드 객체 (제출된 파라미터가 필드에 자동 세팅됨)
     * @param memberAddress : 주소 입력값 3개가 배열로 들어옴 (우편번호, 도로명/지번, 상세주소)
     * @param ra : 리다이렉트 시 메시지 전달용
     * @return
     */
    @PostMapping("signup")
    public String signup(Member inputMember, 
                         @RequestParam("memberAddress") String[] memberAddress,
                         RedirectAttributes ra) {
        
        // 1. 주소 하나로 합치기 (우편번호^^^도로명/지번^^^상세주소)
        // 구분자는 나중에 쪼개기 편한 걸로 아무거나 써도 됨. (보통 ^^, ||, ,, 등을 씀)
        // 만약 주소를 입력 안 했으면 memberAddress 배열 요소가 비어있을 수 있음.
        
        if(inputMember.getMemberAddress() != null) { // 주소 입력값이 넘어왔다면
            // memberAddress 배열을 String 하나로 합쳐서 DTO에 세팅
            // 예: "04540,,서울시 중구,,101호" -> String.join(",,", 배열) 쓰면 편함
            String addr = String.join(",,", memberAddress);
            inputMember.setMemberAddress(addr);
        }

        // 2. 서비스 호출
        int result = service.signup(inputMember);
        
        String path = "redirect:";
        String message = null;

        if(result > 0) { // 가입 성공
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

    /** 아이디 중복 검사
     * @param memberId
     * @return count (1:중복, 0:사용가능)
     */
    @ResponseBody
    @GetMapping("checkId")
    public int checkId(@RequestParam("memberId") String memberId) {
        return service.checkId(memberId);
    }

    /** 닉네임 중복 검사
     * @param memberNickname
     * @return count
     */
    @ResponseBody
    @GetMapping("checkNickname")
    public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
        return service.checkNickname(memberNickname);
    }
    
    /** 이메일 중복 검사
     * @param memberEmail
     * @return count
     */
    @ResponseBody
    @GetMapping("checkEmail")
    public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
        return service.checkEmail(memberEmail);
    }

}