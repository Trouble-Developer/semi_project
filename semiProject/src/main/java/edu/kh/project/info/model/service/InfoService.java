package edu.kh.project.info.model.service;

import java.util.Map;
import edu.kh.project.info.model.dto.InfoBoard;

public interface InfoService {

    /** 1365 데이터 수집 (성공 건수 반환)  */
    int syncFrom1365() throws Exception;

    /** * 봉사 목록 조회 (페이지네이션 적용) 
     * @param cp : 현재 페이지 번호
     * @param map : 검색 조건
     * @return : pagination(계산기) + infoList(10개 게시글) 가 담긴 Map
     */
    Map<String, Object> selectInfoList(int cp, Map<String, Object> map);

    /** 상세 조회  */
    InfoBoard selectInfoBoard(int infoBoardNo);
}