package edu.kh.project.info.model.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import edu.kh.project.info.model.dto.InfoBoard;

@Mapper
public interface InfoMapper {

    /** 1365 API 데이터를 DB에 병합 (Insert or Update) */
    int mergeInfoBoard(InfoBoard info);

    /**  전체 게시글 수 조회 */
    int getListCount(Map<String, Object> map);

    /** DB에서 봉사 목록 조회 (페이징/검색 포함) */
    List<InfoBoard> selectInfoList(Map<String, Object> map);

    /** 상세 조회 */
    InfoBoard selectInfoBoard(int infoBoardNo);
}