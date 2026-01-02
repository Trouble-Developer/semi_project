package edu.kh.project.info.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class InfoBoard {
    // 1. 기본 식별 정보
    private int infoBoardNo;         // INFO_BOARD_NO (PK)
    private String url;              // URL (1365 상세페이지 링크, 중복체크용)
    
    // 2. 프로그램 주요 정보
    private String progrmNm;         // PROGRM_NM (봉사명)
    private String progrmCn;         // PROGRM_CN (상세내용)
    private String actRm;            // ACT_RM (활동분야/키워드)
    private String nanmmByNm;        // NANMM_BY_NM (등록기관명)
    
    // 3. 일시 및 장소
    private String progrmBgnde;      // PROGRM_BGNDE (봉사 시작일)
    private String progrmEndde;      // PROGRM_ENDDE (봉사 종료일)
    private String noticeBgnDe;      // NOTICE_BGN_DE (모집 시작일)
    private String noticeEndDe;      // NOTICE_END_DE (모집 종료일)
    private String actBeginTm;       // ACT_BEGIN_TM (시작 시간)
    private String actEndTm;         // ACT_END_TM (종료 시간)
    private String actPlace;         // ACT_PLACE (봉사 장소)
    
    // 4. 대상 및 분류
    private String adultPosblAt;     // ADULT_POSBL_AT (성인 가능 여부)
    private String yngBgsPosblAt;    // YNG_BGS_POSBL_AT (청소년 가능 여부)
    private String schSido;          // SCH_SIDO (시도 코드)
    private String schSign;          // SCH_SIGN (시군구 코드)
}