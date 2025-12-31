package edu.kh.project.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.admin.dto.AdminMember;
import edu.kh.project.admin.dto.AdminSupport;
import edu.kh.project.admin.model.service.AdminService;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 관리자 접근 제어
    private boolean isAdmin(HttpSession session,
            				RedirectAttributes ra) {

    	Member loginMember = (Member) session.getAttribute("loginMember");

    	if (loginMember == null || loginMember.getAuthority() != 2) {
    			ra.addFlashAttribute("message", "관리자만 접근 가능합니다.");
    						
    			return false;
    	}
    	return true;
    }
    
    // 회원 관리 페이지
    @GetMapping("member")
    public String memberManage(
            @RequestParam(value = "cp", defaultValue = "1") int cp,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "query", required = false) String query,
            Model model,
            HttpSession session,
            RedirectAttributes ra) {

    	if (!isAdmin(session, ra)) {
            return "redirect:/";
        }
    	
        // 검색 파라미터 Map (mapper가 매개변수를 하나밖에 못 받아서)
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", key);
        paramMap.put("query", query);

        // 회원 수 조회
        int memberCount = adminService.getMemberCount(paramMap);

        // 페이지네이션
        Pagination pagination = new Pagination(cp, memberCount);

        paramMap.put("offset",
                (pagination.getCurrentPage() - 1) * pagination.getLimit());
        paramMap.put("limit", pagination.getLimit());

        // 회원 목록 조회
        List<AdminMember> memberList =
                adminService.selectMemberList(paramMap);

        // layout에 전달할 값
        model.addAttribute("content", "admin/memberManage");
        model.addAttribute("pageTitle", "회원 관리");
        model.addAttribute("menu", "member");
        model.addAttribute("pagination", pagination);
        model.addAttribute("memberList", memberList);

        return "admin/adminLayout";
    }
    
    // 회원 관리 - 강제 탈퇴 & 복구
    @PostMapping("member/updateStatus")
    public String updateMemberStatus(
            @RequestParam("memberNo") int memberNo,
            @RequestParam("memberDelFl") String memberDelFl,
            HttpSession session,
            RedirectAttributes ra) {

        if (!isAdmin(session, ra)) {
            return "redirect:/";
        }
    	
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("memberNo", memberNo);
        paramMap.put("memberDelFl", memberDelFl);

        int result = adminService.updateMemberStatus(paramMap);
        

        if (result > 0) {
            if ("Y".equals(memberDelFl)) {
                ra.addFlashAttribute("message", "해당 회원이 탈퇴 처리되었습니다.");
            } else {
                ra.addFlashAttribute("message", "해당 회원이 복구되었습니다.");
            }
        } else {
            ra.addFlashAttribute("message", "회원 상태 변경에 실패했습니다.");
        }

        return "redirect:/admin/member";
    }
    
    
    // 공지사항
    @GetMapping("notice")
    public String noticeManage(
            @RequestParam(value = "cp", defaultValue = "1") int cp,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "query", required = false) String query,
            Model model,
            HttpSession session,
            RedirectAttributes ra) {

        if (!isAdmin(session, ra)) {
            return "redirect:/";
        }
    	
        // 검색 파라미터 Map
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", key);
        paramMap.put("query", query);

        // 공지사항 목록 + 페이지네이션 조회
        Map<String, Object> resultMap =
                adminService.selectNoticeList(cp, paramMap);

        model.addAttribute("content", "admin/noticeManage");
        model.addAttribute("pageTitle", "공지사항 관리");
        model.addAttribute("menu", "notice");

        model.addAttribute("freeBoardList", (List<Board>) resultMap.get("freeBoardList"));
        model.addAttribute("pagination", (Pagination) resultMap.get("pagination"));

        return "admin/adminLayout";
    }

    // 공지사항 삭제
    @PostMapping("notice/delete")
    public String deleteNotice(
    		@RequestParam("boardNo") int boardNo,
            HttpSession session,
            RedirectAttributes ra) {

        if (!isAdmin(session, ra)) {
            return "redirect:/";
        }
    	
        int result = adminService.deleteNotice(boardNo);

        if (result > 0) {
            ra.addFlashAttribute("message", "공지사항이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("message", "공지사항 삭제에 실패했습니다.");
        }

        return "redirect:/admin/notice";
    }
    
    
    // 고객지원 - 게시글 관리 페이지
    @GetMapping("support")
    public String supportManage(
            // 현재 페이지 번호 (기본값 1)
            @RequestParam(value = "cp", defaultValue = "1") int cp,
            // 검색 조건 (title / content / writer)
            @RequestParam(value = "key", required = false) String key,
            // 검색어
            @RequestParam(value = "query", required = false) String query,
            Model model,
            HttpSession session,
            RedirectAttributes ra) {

        if (!isAdmin(session, ra)) {
            return "redirect:/";
        }
    	
        // Mapper는 매개변수 1개만 받을 수 있으므로
        // 검색 조건을 Map으로 묶어서 전달
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", key);
        paramMap.put("query", query);

        Map<String, Object> resultMap =
                adminService.selectSupportList(cp, paramMap);

        // adminLayout.html에서 불러올 본문 페이지
        model.addAttribute("content", "admin/supportManage");

        // 페이지 상단 title
        model.addAttribute("pageTitle", "고객지원 관리");

        // 사이드바 메뉴 active 처리용
        model.addAttribute("menu", "support");

        // 고객지원 게시글 목록
        model.addAttribute("supportList",
                (List<AdminSupport>) resultMap.get("supportList"));

        // 페이지네이션 정보
        model.addAttribute("pagination",
                (Pagination) resultMap.get("pagination"));

        return "admin/adminLayout";
    }
    
    // 고객지원 게시글 삭제 & 복구
    @PostMapping("support/updateStatus")
    public String updateSupportStatus(
    				@RequestParam("boardNo") int boardNo,
    				@RequestParam("boardDelFl") String boardDelFl,
    				HttpSession session,
    				RedirectAttributes ra) {
    	
    	// 관지라 외 접근 제한
    	if (!isAdmin(session, ra)) {
            return "redirect:/";
        }
    	
    	Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("boardNo", boardNo);
        paramMap.put("boardDelFl", boardDelFl);
        
        int result = adminService.updateSupportStatus(paramMap);
        
        String message = "";
        
        if (result > 0) {
            if ("Y".equals(boardDelFl)) { // 게시글 상태가 없는 상태
                message = "게시글이 삭제되었습니다.";
            
            } else { // 게시글이 있는 상태
            	message = "게시글이 복구되었습니다.";
            }
            
        } else {
        	message = "게시글 변경 실패";
        }
    	
        ra.addFlashAttribute("message", message);
    	
    	return "redirect:/admin/support";
    }
   
    
    
    
    
    
    
    
}
