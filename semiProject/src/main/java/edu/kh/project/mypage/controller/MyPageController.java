package edu.kh.project.mypage.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.mypage.model.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@SessionAttributes({"loginMember"})
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Controller
public class MyPageController {

    private final MyPageService service;

    // config.properties에서 경로 가져오기
    @Value("${profile.image.web-path}")
    private String profileWebPath;   

    @Value("${profile.image.folder-path}")
    private String profileFolderPath; 


    /**
     * 프로필 관리 페이지 이동
     */
    @GetMapping("/profile")
    public String profile(HttpSession session, RedirectAttributes ra, Model model) {
        
        // 1. 로그인 검사 
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
            return "redirect:/member/login";
        }

        // -----------------------------------------------------------
        // 여기부터는 로그인 된 경우
        
        // 2. 로그인 회원의 주소 가져오기
        String memberAddress = loginMember.getMemberAddress();
        
        // 3. 주소가 있다면 쪼개서 model에 담기 (구분자 ,, 사용)
        if(memberAddress != null) {
            String[] arr = memberAddress.split(",,");
            
            if(arr.length > 0) model.addAttribute("postcode", arr[0]);
            if(arr.length > 1) model.addAttribute("address", arr[1]);
            if(arr.length > 2) model.addAttribute("detailAddress", arr[2]);
        }
        
        return "mypage/profile";
    }

    
    /**
     * 프로필 정보 수정
     */
    @PostMapping("/update")
    public String updateProfile(
            HttpSession session, 
            Member updateMember, 
            @RequestParam("uploadFile") MultipartFile profileImg,
            @RequestParam(value="postcode", required=false) String postcode,
            @RequestParam(value="address", required=false) String address,
            @RequestParam(value="detailAddress", required=false) String detailAddress,
            RedirectAttributes ra) throws IOException {

        // 1. 로그인 검사 
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            ra.addFlashAttribute("message", "로그인 세션이 만료되었습니다.");
            return "redirect:/member/login";
        }

        // 2. 로그인한 회원의 번호(PK) 세팅 
        updateMember.setMemberNo(loginMember.getMemberNo());
        
        // 3. 주소 합치기 
        if(postcode != null && address != null) {
            String memberAddress = String.join(",,", postcode, address, detailAddress);
            updateMember.setMemberAddress(memberAddress);
        }

        // 4. 서비스 호출 
        int result = service.updateProfile(updateMember, profileImg, profileWebPath, profileFolderPath);

        String message = null;

        if(result > 0) {
            message = "회원 정보가 수정되었습니다.";

            // 5. 세션 갱신
            loginMember.setMemberAddress(updateMember.getMemberAddress());
            loginMember.setMemberNickname(updateMember.getMemberNickname());
            loginMember.setMemberTel(updateMember.getMemberTel()); 
            
            if(updateMember.getProfileImg() != null) {
                loginMember.setProfileImg(updateMember.getProfileImg());
            }

        } else {
            message = "회원 정보 수정 실패";
        }

        ra.addFlashAttribute("message", message);

        return "redirect:/mypage/profile";
    }
    
    /**
     * 내 작성글 목록 조회
     * @param cp : 현재 페이지 (기본값 1)
     */
    @GetMapping("/posts")
	public String myPosts(
			HttpSession session,
			@RequestParam(value="cp", required=false, defaultValue="1") int cp,
			@RequestParam(value="key", required=false) String key,
			@RequestParam(value="query", required=false) String query,
			Model model,
			RedirectAttributes ra) {
		
		Member loginMember = (Member) session.getAttribute("loginMember");
		if (loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}

		// 2. 서비스 호출 (회원번호, 현재페이지, 검색조건Map 전달)
		Map<String, Object> paramMap = new HashMap<>();
		
		// 위에서 @RequestParam으로 받아온 key, query를 맵에 담기
		paramMap.put("key", key);
		paramMap.put("query", query);
		
		// 서비스에 paramMap 추가로 넘기기
		Map<String, Object> map = service.selectPostList(loginMember.getMemberNo(), cp, paramMap);

		model.addAttribute("map", map);

		return "mypage/posts"; 
	}
    
    /**
	 * 내가 댓글 단 글 목록 조회
	 */
	@GetMapping("/comments")
	public String myComments(
			HttpSession session,
			@RequestParam(value="cp", required=false, defaultValue="1") int cp,
			@RequestParam(value="key", required=false) String key,
			@RequestParam(value="query", required=false) String query,
			Model model,
			RedirectAttributes ra) {
		
		Member loginMember = (Member) session.getAttribute("loginMember");
		if (loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("key", key);
		paramMap.put("query", query);
		
		// 서비스 호출
		Map<String, Object> map = service.selectCommentPostList(loginMember.getMemberNo(), cp, paramMap);
		
		model.addAttribute("map", map);
		
		return "mypage/comments"; 
	}
	
	@GetMapping("/scraps")
	public String myScraps(
	        HttpSession session,
	        @RequestParam(value="cp", required=false, defaultValue="1") int cp,
	        @RequestParam(value="key", required=false) String key,
	        @RequestParam(value="query", required=false) String query,
	        Model model,
	        RedirectAttributes ra) {
	    
	    Member loginMember = (Member) session.getAttribute("loginMember");
	    if (loginMember == null) {
	        ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/member/login";
	    }

	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("key", key);
	    paramMap.put("query", query);
	    
	    Map<String, Object> map = service.selectScrapList(loginMember.getMemberNo(), cp, paramMap);
	    
	    model.addAttribute("map", map);
	    
	    return "mypage/scraps";
	}
	
	// 비밀번호 변경 페이지 이동
	@GetMapping("changePw")
	public String changePw(
			@SessionAttribute(value="loginMember", required=false) Member loginMember,
			RedirectAttributes ra) {
		
		// 1. 로그인 안 했으면 로그인 페이지로 보냄
		if(loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		
		// 2. 로그인 했으면 정상적으로 화면 보여줌
		return "mypage/changePw";
	}

	// 비밀번호 변경 수행
	@PostMapping("changePw")
	public String changePw(
			@RequestParam("currentPw") String currentPw,
			@RequestParam("newPw") String newPw,
			@SessionAttribute(value="loginMember", required=false) Member loginMember,
			RedirectAttributes ra) {
		
		// 1. 로그인 검사
		if(loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		
		// 2. 로그인한 회원의 번호
		int memberNo = loginMember.getMemberNo();
		
		// 3. 서비스 호출
		int result = service.changePw(currentPw, newPw, memberNo);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "비밀번호가 변경되었습니다.";
			path = "redirect:/mypage/profile"; // 내 정보 페이지로
		} else {
			message = "현재 비밀번호가 일치하지 않습니다.";
			path = "redirect:/mypage/changePw"; // 다시 변경 페이지로
		}
		
		ra.addFlashAttribute("message", message);
		return path;
	}

	/**
	 * 회원 탈퇴 페이지 이동
	 */
	@GetMapping("/withdraw")
	public String withdraw(
			@SessionAttribute(value="loginMember", required=false) Member loginMember,
			RedirectAttributes ra) {
		
		if(loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		
		return "mypage/withdraw";
	}

	/**
	 * 회원 탈퇴 수행
	 */
	@PostMapping("/withdraw")
	public String withdrawProcess(
			@RequestParam("memberPw") String memberPw,
			@SessionAttribute(value="loginMember", required=false) Member loginMember,
			RedirectAttributes ra,
			HttpSession session,
			SessionStatus status) {
		
		if(loginMember == null) {
			ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
			return "redirect:/member/login";
		}
		
		int memberNo = loginMember.getMemberNo();
		int result = service.withdraw(memberPw, memberNo);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "회원 탈퇴가 완료되었습니다.";
			status.setComplete();  // @SessionAttributes 세션 정리
			session.invalidate();   // HttpSession 무효화
			path = "redirect:/";
		} else {
			message = "비밀번호가 일치하지 않습니다.";
			path = "redirect:/mypage/withdraw";
		}
		
		ra.addFlashAttribute("message", message);
		return path;
		}
}