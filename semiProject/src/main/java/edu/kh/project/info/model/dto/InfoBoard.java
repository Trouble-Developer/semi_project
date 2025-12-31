package edu.kh.project.info.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InfoBoard {

    private int infoBoardNo;       // PK, 게시글 번호

    private String schSido;        // 봉사지역 (검색/목록)
    private String schSign;        // 기관 코드/명 (상세)

    private String progrmBgnde;    // 봉사 시작일 (목록/상세)
    private String progrmEndde;    // 봉사 종료일 (목록/상세)

    private String adultPosblAt;   // 성인 참여 여부 (목록: 봉사자 유형)
    private String yngBgsPosblAt;  // 청소년 참여 여부 (목록: 봉사자 유형)

    private int actBeginTm;        // 활동 시작 시간
    private int actEndTm;          // 활동 종료 시간

    private String noticeBgnDe;    // 모집 시작일
    private String noticeEndDe;    // 모집 종료일 (모집상태 계산 가능)

    private String actPlace;       // 활동 장소명 (목록/상세)
    private String nanmmByNm;      // 기관명 (상세)
    private String url;            // 관련 URL (상세)

    private String progrmNm;       // 봉사명 (목록/상세/검색)
    private String actRm;          // 활동분야/활동처명 (목록/상세/검색)
}
