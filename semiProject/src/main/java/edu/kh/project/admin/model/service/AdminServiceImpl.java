package edu.kh.project.admin.model.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.kh.project.admin.dto.AdminMember;
import edu.kh.project.admin.dto.AdminNotice;
import edu.kh.project.admin.dto.AdminSupport;
import edu.kh.project.admin.dto.Report;
import edu.kh.project.admin.model.mapper.AdminMapper;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
	
	private final AdminMapper mapper;
	
	// 회원 관리
	@Override
	public int getMemberCount(Map<String, Object> paramMap) {
		return mapper.getMemberCount(paramMap);
	}
	
	// 회원 관리 - 목록 조회
	@Override
	public List<AdminMember> selectMemberList(Map<String, Object> paramMap) {
		return mapper.selectMemberList(paramMap);
	}
	
	// 회원 관리 - 강제 탈퇴 & 복구
    @Override
    public int updateMemberStatus(Map<String, Object> paramMap) {
        return mapper.updateMemberStatus(paramMap);
    }
	
	
	// 공지사항 목록 조회
    @Override
    public Map<String, Object> selectNoticeList(
            int cp,
            Map<String, Object> paramMap) {

        // 1. 공지사항 전체 개수 조회 (검색 조건 포함)
        int noticeCount = mapper.getNoticeCount(paramMap);

        // 2. 페이지네이션 객체 생성
        Pagination pagination = new Pagination(cp, noticeCount);

        // 3. offset / limit 계산
        paramMap.put(
            "offset",
            (pagination.getCurrentPage() - 1) * pagination.getLimit()
        );
        paramMap.put("limit", pagination.getLimit());

        // 4. 공지사항 목록 조회
        List<AdminNotice> freeBoardList =
                mapper.selectNoticeList(paramMap);

        // 5. Controller로 반환할 Map 구성
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pagination", pagination);
        resultMap.put("freeBoardList", freeBoardList);

        return resultMap;
    }
    
    // 공지사항 삭제/복구
    @Override
    public int updateNoticeStatus(Map<String, Object> paramMap) {
    	return mapper.updateNoticeStatus(paramMap);
    }
    
    
    
    // 고객지원 게시글 목록 조회
    @Override
    public Map<String, Object> selectSupportList(int cp, Map<String, Object> paramMap) {
    	
    	// 검색 파라미터 정리
        String key = (String) paramMap.get("key");
        String query = (String) paramMap.get("query");

        if (query == null || query.trim().isEmpty()) {
            // 검색어가 없으면 검색 조건 자체를 제거
            paramMap.put("key", null);
            paramMap.put("query", null);
        }
        
        // 1. 고객지원 게시글 전체 개수 조회
        //    (검색 조건이 있다면 함께 적용)
        int supportCount = mapper.getSupportCount(paramMap);
        
        if (supportCount == 0) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("supportList", Collections.emptyList());
            resultMap.put("pagination", null); // 검색 결과 없으면 페이지네이션 없음 
            return resultMap;
        }

        // 2. 전체 개수를 기반으로 Pagination 객체 생성
        Pagination pagination = new Pagination(cp, supportCount);

        // 3. SQL에서 사용할 offset / limit 계산
        //    → Mapper는 Map 하나만 받으므로 paramMap에 추가
        paramMap.put(
            "offset",
            (pagination.getCurrentPage() - 1) * pagination.getLimit()
        );
        paramMap.put("limit", pagination.getLimit());

        // 4. 고객지원 게시글 목록 조회
        List<AdminSupport> supportList =
                mapper.selectSupportList(paramMap);

        // 5. Controller로 반환할 데이터 구성
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pagination", pagination);
        resultMap.put("supportList", supportList);

        return resultMap;
    }
	
    // 고객지원 게시글 삭제/복구
    @Override
    public int updateSupportStatus(Map<String, Object> paramMap) {
    	return mapper.updateSupportStatus(paramMap);
    }
    
    @Override
    public Map<String, Object> selectReportList(Map<String, Object> paramMap) {

        int cp = (int) paramMap.get("cp");

        // 검색 파라미터 정리
        String key = (String) paramMap.get("key");
        String query = (String) paramMap.get("query");

        if (query == null || query.trim().isEmpty()) {
            paramMap.put("key", null);
            paramMap.put("query", null);
        }

        // 1. 신고글 전체 개수 조회
        int reportCount = mapper.getReportCount(paramMap);

        if (reportCount == 0) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("reportList", Collections.emptyList());
            resultMap.put("pagination", null);
            return resultMap;
        }

        // 2. 페이지네이션 생성
        Pagination pagination = new Pagination(cp, reportCount);

        // 3. offset / limit 계산
        paramMap.put(
            "offset",
            (pagination.getCurrentPage() - 1) * pagination.getLimit()
        );
        paramMap.put("limit", pagination.getLimit());

        // 4. 신고글 목록 조회
        List<Report> reportList
            = mapper.selectReportList(paramMap);

        // 5. 반환 Map 구성
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("reportList", reportList);
        resultMap.put("pagination", pagination);

        return resultMap;
    }

    
	
}
