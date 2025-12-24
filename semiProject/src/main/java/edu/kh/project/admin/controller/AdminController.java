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
import edu.kh.project.admin.model.service.AdminService;
import edu.kh.project.board.model.dto.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 회원 관리 페이지
    @GetMapping("member")
    public String memberManage(
            @RequestParam(value = "cp", defaultValue = "1") int cp,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "query", required = false) String query,
            Model model) {

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
    
    @PostMapping("member/updateStatus")
    public String updateMemberStatus(
    		@RequestParam("memberNo") int memberNo,
    		@RequestParam("memberDelFl") String memberDelfl,
    		RedirectAttributes ra) {
    	
    	Map<String, Object> paramMap = new HashMap<>();
    	paramMap.put("memberNo", memberNo);
    	paramMap.put("memberDelFl", memberDelfl);
    	
    	// 강제 탈퇴 서비스 호출
    	int result = adminService.updateMemberStatus(paramMap);
    	
    	String message = null;
    	
    	if(result > 0) {
    		message = "해당 회원이 탈퇴 처리되었습니다.";
    	} else {
    		message = "회원 탈퇴 실패";
    	}
    	
    	ra.addFlashAttribute("message", message);
    	
    	return "redirect:/admin/member";
    }
    
    
    
    
    
    
    
    
    
    
    
}
