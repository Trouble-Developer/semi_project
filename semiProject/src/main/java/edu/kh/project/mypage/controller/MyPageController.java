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
import org.springframework.web.bind.annotation.SessionAttributes;
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
            @RequestParam("currentPw") String currentPw,
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
        
        // 3. 주소 합치기 (구분자 ,, 사용)
        if(postcode != null && address != null) {
            String memberAddress = String.join(",,", postcode, address, detailAddress);
            updateMember.setMemberAddress(memberAddress);
        }

        // 4. 서비스 호출
        int result = service.updateProfile(updateMember, profileImg, profileWebPath, profileFolderPath, currentPw);

        String message = null;

        if(result > 0) {
            message = "회원 정보가 수정되었습니다.";

            // 5. 세션 갱신 (참조 주소가 같아서 setter만 호출해도 세션 값 바뀜)
            loginMember.setMemberAddress(updateMember.getMemberAddress());
            loginMember.setMemberNickname(updateMember.getMemberNickname());
            loginMember.setMemberTel(updateMember.getMemberTel()); 
            
            if(updateMember.getProfileImg() != null) {
                loginMember.setProfileImg(updateMember.getProfileImg());
            }

        } else if(result == -1) {
             message = "현재 비밀번호가 일치하지 않습니다.";
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
		
		// 위에서 @RequestParam으로 받아온 key, query를 맵에 담는 거임
		paramMap.put("key", key);
		paramMap.put("query", query);
		
		// 서비스에 paramMap 추가로 넘기기 (Service 인터페이스랑 구현체 수정했지?)
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
}