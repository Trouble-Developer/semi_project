package edu.kh.project.info.model.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import edu.kh.project.info.model.dto.AreaCode;
import edu.kh.project.info.model.dto.InfoBoard;

@Mapper
public interface InfoMapper {

	/**
	 * [기능: 시도 목록 조회] 역할: TB_AREA_CODE에서 부모가 없는 최상위 지역을 가져옴
	 */
	List<AreaCode> getSidoList();

	/**
	 * [기능: 시군구 목록 조회] 역할: 선택된 시도 코드(sidoCd)를 부모로 가진 지역을 가져옴
	 */
	List<AreaCode> getSignList(String sidoCd);

	/**
	 * [기능: 조건에 맞는 게시글 총 개수 조회] 역할: 페이징 처리를 위해 검색 필터가 적용된 전체 행 개수를 반환
	 */
	int getListCount(Map<String, Object> paramMap);

	/**
	 * [기능: 페이징 처리된 게시글 목록 조회] 역할: 현재 페이지에 보여줄 10개의 데이터를 필터링하여 가져옴
	 */
	List<InfoBoard> selectInfoList(Map<String, Object> paramMap);

	/**
	 * [기능: 봉사 상세 정보 조회] 역할: 특정 번호(infoBoardNo)의 게시글 정보를 가져옴
	 */
	InfoBoard selectInfoBoard(int infoBoardNo);

	/**
	 * [기능: 봉사 정보 MERGE] 역할: API 중복 체크 후 저장/수정
	 */
	int mergeInfoBoard(InfoBoard info);

	/**
	 * [기능: 지역 자동 MERGE] 역할: API 수집 시 발견된 새로운 시군구 코드를 TB_AREA_CODE에 저장
	 */
	int mergeAreaCode(AreaCode area);

	/**
	 * 봉사 상세 조회 SQL 호출 (로그인 회원 번호 포함)
	 * * @param paramMap (infoBoardNo, memberNo)
	 * @return InfoBoard
	 */
	InfoBoard selectInfoDetail(Map<String, Object> paramMap);

	/**
	 * [기능추가] 관심 봉사 등록(스크랩)
	 * @param paramMap
	 * @return result
	 */
	int insertScrap(Map<String, Object> paramMap);

	/**
	 * [기능추가] 관심 봉사 등록 해제(스크랩 취소)
	 * @param paramMap
	 * @return result
	 */
	int deleteScrap(Map<String, Object> paramMap);
}