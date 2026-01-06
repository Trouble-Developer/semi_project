package edu.kh.project.info.model.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import edu.kh.project.info.model.dto.AreaCode;
import edu.kh.project.info.model.dto.InfoBoard;

@Mapper
public interface InfoMapper {

    /** [기능: API 데이터 MERGE] */
    int mergeInfoBoard(InfoBoard info);

    /** [기능: 필터링된 글 개수 조회] */
    int getListCount(Map<String, Object> map);

    /** [기능: 봉사 목록 조회] */
    List<InfoBoard> selectInfoList(Map<String, Object> map);

    /** [기능: 상세 조회] */
    InfoBoard selectInfoBoard(int infoBoardNo);

    /** [기능: 시도 목록 조회 (DTO 리스트 반환)] */
    List<AreaCode> getSidoList();

    /** [기능: 시군구 목록 조회 (DTO 리스트 반환)] */
    List<AreaCode> getSignList(String sidoCd);
}