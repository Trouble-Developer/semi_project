package edu.kh.project.info.model.service;

import java.util.List;
import edu.kh.project.info.model.dto.InfoBoard;

public interface InfoService {

    /** 공공데이터 OpenAPI를 통해 DB 동기화 */
    int syncFrom1365(String keyword) throws Exception;

    /** 정보게시판 목록 조회 (검색/필터 적용 가능) */
    List<InfoBoard> selectInfoList(String schSido, String actRm, String progrmNm,
                                   String adultPosblAt, String yngBgsPosblAt, String noticeStatus);

    /** 게시글 상세 조회 */
    InfoBoard selectInfoBoard(int infoBoardNo);
}
