package edu.kh.project.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.admin.dto.AdminMember;
import edu.kh.project.admin.dto.AdminNotice;
import edu.kh.project.admin.dto.AdminReportComment;
import edu.kh.project.admin.dto.AdminSupport;
import edu.kh.project.admin.dto.Report;
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

        // 관리자 체크
        if (!isAdmin(session, ra)) {
            return "redirect:/";
        }

        // 1️. 검색 여부 판단
        boolean isSearch = key != null && query != null && !query.trim().isEmpty();

        // 검색 파라미터 Map
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", isSearch ? key : null);
        paramMap.put("query", isSearch ? query : null);

        // 2️. 회원번호 검색 + 문자열 방어
        if (isSearch && "memberNo".equals(key)) {

            boolean isNumber = true;

            for (int i = 0; i < query.length(); i++) {
                if (!Character.isDigit(query.charAt(i))) {
                    isNumber = false;
                    break;
                }
            }

            // 숫자가 아니면 → 결과 없음 처리
            if (!isNumber) {

                model.addAttribute("content", "admin/memberManage");
                model.addAttribute("pageTitle", "회원 관리");
                model.addAttribute("menu", "member");
                model.addAttribute("memberList", List.of());
                model.addAttribute("pagination", null);

                return "admin/adminLayout";
            }
        }

        // 3️. 회원 수 조회
        int memberCount = adminService.getMemberCount(paramMap);

        // 4️. 검색 결과 없는 경우
        if (memberCount == 0) {

            model.addAttribute("content", "admin/memberManage");
            model.addAttribute("pageTitle", "회원 관리");
            model.addAttribute("menu", "member");
            model.addAttribute("memberList", List.of());
            model.addAttribute("pagination", null);

            return "admin/adminLayout";
        }

        // 5️. 페이지네이션 생성
        Pagination pagination = new Pagination(cp, memberCount);

        paramMap.put("offset",
                (pagination.getCurrentPage() - 1) * pagination.getLimit());
        paramMap.put("limit", pagination.getLimit());

        // 6️. 회원 목록 조회
        List<AdminMember> memberList =
                adminService.selectMemberList(paramMap);

        model.addAttribute("content", "admin/memberManage");
        model.addAttribute("pageTitle", "회원 관리");
        model.addAttribute("menu", "member");
        model.addAttribute("memberList", memberList);
        model.addAttribute("pagination", pagination);

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

        model.addAttribute("freeBoardList", (List<AdminNotice>) resultMap.get("freeBoardList"));
        model.addAttribute("pagination", (Pagination) resultMap.get("pagination"));

        return "admin/adminLayout";
    }

    // 공지사항 삭제/복구
    @PostMapping("notice/updateStatus")
    public String updateNoticeStatus(
            @RequestParam("boardNo") int boardNo,
            @RequestParam("boardDelFl") String boardDelFl,
            HttpSession session,
            RedirectAttributes ra) {

        if (!isAdmin(session, ra)) {
        	return "redirect:/";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("boardNo", boardNo);
        paramMap.put("boardDelFl", boardDelFl);

        int result = adminService.updateNoticeStatus(paramMap);

        if (result > 0) {
            ra.addFlashAttribute("message",
                "Y".equals(boardDelFl)
                    ? "공지사항이 삭제되었습니다."
                    : "공지사항이 복구되었습니다.");
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
   
    
    // 신고글 목록
    @GetMapping("report")
    public String reportManage(
            @RequestParam(value = "cp", defaultValue = "1") int cp,
            @RequestParam(value = "reportType", defaultValue = "all") String reportType,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "query", required = false) String query,
            Model model,
            HttpSession session,
            RedirectAttributes ra) {

        // 관리자 외 접근 제한
        if (!isAdmin(session, ra)) {
            return "redirect:/";
        }

        // Service로 전달할 파라미터 Map 구성
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("cp", cp);
        paramMap.put("reportType", reportType);
        paramMap.put("key", key);
        paramMap.put("query", query);

        // 신고글 목록 조회
        Map<String, Object> resultMap
            = adminService.selectReportList(paramMap);

        model.addAttribute("reportList", resultMap.get("reportList"));
        model.addAttribute("pagination", resultMap.get("pagination"));

        // 검색 / 분류 상태 유지
        model.addAttribute("reportType", reportType);
        model.addAttribute("key", key);
        model.addAttribute("query", query);

        // adminLayout 전달 값
        model.addAttribute("pageTitle", "신고글 관리");
        model.addAttribute("menu", "report");
        
        model.addAttribute("content", "admin/reportManage");

        return "admin/adminLayout";
    }
    
    // 신고 기각
    @GetMapping("report/reject")
    public String rejectReport(
            @RequestParam("reportNo") int reportNo,
            HttpSession session,
            RedirectAttributes ra) {

        if (!isAdmin(session, ra)) return "redirect:/";

        adminService.rejectReport(reportNo);

        ra.addFlashAttribute("message", "신고가 기각되었습니다.");
        return "redirect:/admin/report";
    }
    
    // 신고글 삭제
    @GetMapping("report/delete")
    public String deleteReport(
            @RequestParam("targetNo") int targetNo,
            @RequestParam("reportNo") int reportNo,
            @RequestParam("reportType") String reportType,
            HttpSession session,
            RedirectAttributes ra) {

        if (!isAdmin(session, ra)) return "redirect:/";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("targetNo", targetNo);
        paramMap.put("reportNo", reportNo);
        paramMap.put("reportType", reportType);

        adminService.deleteReport(paramMap);

        ra.addFlashAttribute("message", "삭제 처리되었습니다.");
        return "redirect:/admin/report";
    }
    
    
    // 신고글 상세 조회 페이지
    // 신고글 상세 조회 (BOARD / COMMENT 완전 분리)
    @GetMapping("report/{reportType}/{reportNo}")
    public String reportDetail(
            @PathVariable("reportType") String reportType,
            @PathVariable("reportNo") int reportNo,
            Model model,
            HttpSession session,
            RedirectAttributes ra) {

        // 관리자 체크
        if (!isAdmin(session, ra)) return "redirect:/";

        Report report;

        // 신고 유형에 따른 분기
        if ("BOARD".equals(reportType)) {
            report = adminService.selectBoardReportDetail(reportNo);

        } else if ("COMMENT".equals(reportType)) {
            report = adminService.selectCommentReportDetail(reportNo);

        } else {
            ra.addFlashAttribute("message", "잘못된 접근입니다.");
            return "redirect:/admin/report";
        }

        if (report == null) {
            ra.addFlashAttribute("message", "존재하지 않거나 처리된 신고입니다.");
            return "redirect:/admin/report";
        }

        // 게시글 상세
        Board board = adminService.selectBoardDetailForReport(report.getBoardNo());

        if (board == null) {
            ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
            return "redirect:/admin/report";
        }

        // 댓글 목록
        List<AdminReportComment> commentList =
                adminService.selectCommentListForReport(report.getBoardNo());
        
        // 이전글, 다음글
        Integer prevReportNo = adminService.selectPrevReportNo(reportNo);
        Integer nextReportNo = adminService.selectNextReportNo(reportNo);

        String prevReportTitle = null;
        String nextReportTitle = null;

        if (prevReportNo != null) {
            prevReportTitle =
                adminService.selectReportTitle(prevReportNo, reportType);
        }

        if (nextReportNo != null) {
            nextReportTitle =
                adminService.selectReportTitle(nextReportNo, reportType);
        }
        

        // View 전달
        model.addAttribute("report", report);
        model.addAttribute("board", board);
        model.addAttribute("commentList", commentList);

        model.addAttribute("reportType", reportType);
        model.addAttribute("reportNo", reportNo);
        
        model.addAttribute("prevReportNo", prevReportNo);
        model.addAttribute("nextReportNo", nextReportNo);
        model.addAttribute("prevReportTitle", prevReportTitle);
        model.addAttribute("nextReportTitle", nextReportTitle);
        

        return "admin/reportDetail";
    }
    
    
    
    
    
}
