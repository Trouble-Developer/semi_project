package edu.kh.project.info.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * [1365 나눔포털 봉사참여정보 데이터 객체]
 * 역할: Open API 응답 데이터를 담아 DB(INFO_BOARD)에 전달하거나 
 * 화면(UI) 출력 시 데이터를 전달하는 바구니 역할을 함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class InfoBoard {
    
    // 1. 시스템 관리 및 고유 식별 정보
    private int infoBoardNo;         // INFO_BOARD_NO (DB 내부 관리용 시퀀스 PK)
    
    private String url;              // URL 컬럼 활용
                                     // 역할: API의 'progrmRegistNo'(프로그램등록번호)를 저장
                                     // 이유: 1365 API에서 각 봉사활동을 식별하는 유일한 고유번호이므로 
                                     //       DB MERGE 시 중복 체크의 기준 키로 사용됨
    
    // 2. 프로그램 주요 내용 (API 응답 데이터 매핑)
    private String progrmNm;         // PROGRM_NM (가이드: progrmSj / 봉사 제목)
    
    private String progrmCn;         // PROGRM_CN (가이드: progrmCn / 상세 설명 내용)
                                     // 특징: 데이터가 매우 길 수 있어 DB의 CLOB 타입과 매핑됨
    
    private String actRm;            // ACT_RM (가이드: srvcClCode 등 / 활동 분야 및 키워드)
    
    private String nanmmByNm;        // NANMM_BY_NM (가이드: nanmmbyNm / 모집 기관명)
    
    // 3. 일시 및 장소 관련 정보
    private String progrmBgnde;      // PROGRM_BGNDE (가이드: progrmBgnde / 봉사 시작일: YYYYMMDD)
    
    private String progrmEndde;      // PROGRM_ENDDE (가이드: progrmEndde / 봉사 종료일: YYYYMMDD)
    
    private String noticeBgnDe;      // NOTICE_BGN_DE (가이드: noticeBgnde / 모집 시작일)
    
    private String noticeEndDe;      // NOTICE_END_DE (가이드: noticeEndde / 모집 종료일)
    
    private int actBeginTm;          // ACT_BEGIN_TM (가이드: actBeginTm / 시작 시간: 0~24)
    
    private int actEndTm;            // ACT_END_TM (가이드: actEndTm / 종료 시간: 0~24)
    
    private String actPlace;         // ACT_PLACE (가이드: actPlace / 실제 봉사 장소)
    
    // 4. 참여 대상 및 지역 코드 (검색 및 필터링용)
    private String adultPosblAt;     // ADULT_POSBL_AT (가이드: adultPosblAt / 성인 가능 여부: Y/N)
    
    private String yngBgsPosblAt;    // YNG_BGS_POSBL_AT (가이드: yngBgsPosblAt / 청소년 가능 여부: Y/N)
    
    private String schSido;          // SCH_SIDO (가이드: sidoCd / 시도 행정구역 코드)
    
    private String schSign;          // SCH_SIGN (가이드: gugunCd / 시군구 행정구역 코드)
}