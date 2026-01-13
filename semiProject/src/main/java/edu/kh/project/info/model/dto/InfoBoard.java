package edu.kh.project.info.model.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.*;

/**
 * [봉사 정보 데이터 객체]
 * 1365 API 데이터를 담아 DB에 저장하거나 화면에 출력하는 역할을 수행함
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @Builder
public class InfoBoard {
    
    // 1. 시스템 및 식별 정보
    private int infoBoardNo;         // 내부 관리용 PK (시퀀스)
    private String url;              // 1365 상세페이지 주소 (중복 체크 키)
    
    // 2. 프로그램 상세 내용
    private String progrmNm;         // 봉사 제목
    private String progrmCn;         // 상세 내용 (CLOB)
    private String actRm;            // 활동 분야 코드/명칭
    private String nanmmByNm;        // 모집 기관명
    private int rcritNmpr;           // [기능추가] 총 모집 인원 수
    
    // 3. 기간 및 시간 정보
    private String progrmBgnde;      // 봉사 시작일 (YYYYMMDD)
    private String progrmEndde;      // 봉사 종료일 (YYYYMMDD)
    private String noticeBgnDe;      // 모집 시작일
    private String noticeEndDe;      // 모집 종료일
    private int actBeginTm;          // 시작 시간 (0~24)
    private int actEndTm;            // 종료 시간 (0~24)
    private String actPlace;         // 실제 봉사 장소
    
    // 4. 필터링 전용 지역 정보
    private String adultPosblAt;     // 성인 가능 여부 (Y/N)
    private String yngBgsPosblAt;    // 청소년 가능 여부 (Y/N)
    private String schSido;          // 행정구역 시도 코드
    private String schSign;          // 행정구역 시군구 코드

    // 5. 검색용 임시 필드 (DB 저장 안함)
    private String searchTime;       // 활동시간 필터 조건
    private String volsType;         // 봉사자 대상 필터 조건

    // [기능추가] 스크랩 여부 확인 필드
    private int scrapCheck;          // 로그인한 회원이 해당 게시글을 스크랩했는지 여부 (1:함, 0:안함)

    /**
     * [비즈니스 로직: D-Day 계산]
     */
    public long getDDay() {
        if (noticeEndDe == null || noticeEndDe.trim().isEmpty()) return 0;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(noticeEndDe, formatter));
        } catch (Exception e) { return 0; }
    }
}