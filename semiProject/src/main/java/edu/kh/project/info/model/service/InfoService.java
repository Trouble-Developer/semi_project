package edu.kh.project.info.model.service;

import java.util.List;
import java.util.Map;
import edu.kh.project.info.model.dto.AreaCode;
import edu.kh.project.info.model.dto.InfoBoard;

public interface InfoService {

    /** [기능 1] 1365 API 데이터 DB 동기화 */
    int syncFrom1365() throws Exception;

    /** [기능 2] 봉사 목록 조회 (페이징/검색 포함) */
    Map<String, Object> selectInfoList(int cp, Map<String, Object> map);

    /** [기능 3] 봉사 상세 정보 조회 */
    InfoBoard selectInfoBoard(int infoBoardNo);

    /** [기능 4] 시도 목록 조회 (AreaCode DTO 활용) */
    List<AreaCode> getSidoList();

    /** [기능 5] 시군구 목록 조회 (AreaCode DTO 활용) */
    List<AreaCode> getSignList(String sidoCd);
    
    /**
     * 봉사 상세 조회
     * @param infoBoardNo
     * @param memberNo (로그인 안되어있으면 0)
     * @return info
     */
    InfoBoard selectInfoDetail(int infoBoardNo, int memberNo);

    /**
     * [기능추가] 관심 봉사 스크랩 업데이트
     * @param paramMap
     * @return result
     */
	int updateScrap(Map<String, Object> paramMap);
}