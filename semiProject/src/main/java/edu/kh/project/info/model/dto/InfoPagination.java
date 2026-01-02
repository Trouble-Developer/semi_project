package edu.kh.project.info.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 정보 게시판 전용 페이지네이션 계산 클래스
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class InfoPagination {
    private int currentPage;      // 현재 사용자가 보고 있는 페이지 번호
    private int listCount;        // DB에 저장된 전체 게시글 수
    private int limit = 10;       // 한 페이지에 보여줄 게시글 수 (10개씩 끊어서 보여줌)
    private int pageSize = 10;    // 하단에 보여줄 페이지 번호 개수 (1, 2, 3... 10까지 보여줌)
    
    private int maxPage;          // 전체 게시글 수에 따른 마지막 페이지 번호
    private int startPage;        // 현재 페이지 목록의 시작 번호 (예: 11, 21...)
    private int endPage;          // 현재 페이지 목록의 끝 번호 (예: 20, 30...)
    private int prevPage;         // 이전 페이지 목록의 마지막 번호 (이전 버튼 클릭 시 이동)
    private int nextPage;         // 다음 페이지 목록의 시작 번호 (다음 버튼 클릭 시 이동)

    /**
     * 필수 정보를 받아 페이지 계산을 시작하는 생성자
     * @param currentPage : 현재 페이지
     * @param listCount   : 전체 게시글 수
     */
    public InfoPagination(int currentPage, int listCount) {
        this.currentPage = currentPage;
        this.listCount = listCount;
        calculate(); // 객체 생성 시 자동으로 계산 로직 실행
    }

    /**
     * 페이지네이션에 필요한 모든 수치를 계산하는 핵심 로직
     */
    private void calculate() {
        // 1. 마지막 페이지(maxPage) 계산: 전체 게시글 / 한 페이지 개수를 올림 처리
        maxPage = (int) Math.ceil((double) listCount / limit);
        
        // 2. 하단 시작 페이지(startPage) 계산: 1, 11, 21...
        startPage = (currentPage - 1) / pageSize * pageSize + 1;
        
        // 3. 하단 끝 페이지(endPage) 계산: 10, 20, 30... (단, maxPage를 넘을 수 없음)
        endPage = startPage + pageSize - 1;
        if (endPage > maxPage) endPage = maxPage;
        
        // 4. 이전 페이지(prevPage)로 이동할 번호 계산
        if (currentPage <= 10) prevPage = 1; // 10페이지 이하면 1페이지로
        else prevPage = startPage - 1;
        
        // 5. 다음 페이지(nextPage)로 이동할 번호 계산
        if (endPage == maxPage) nextPage = maxPage; // 마지막이면 더이상 못 감
        else nextPage = endPage + 1;
    }
}