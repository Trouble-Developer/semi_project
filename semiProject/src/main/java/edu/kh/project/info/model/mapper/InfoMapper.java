package edu.kh.project.info.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.info.model.dto.InfoBoard;

@Mapper
public interface InfoMapper {

    /** 중복체크 후 MERGE(Insert/Update) */
    int mergeInfoBoard(InfoBoard info);

    /** 목록 조회 (검색/필터 적용) */
    List<InfoBoard> selectInfoListWithFilter(String schSido, String actRm, String progrmNm,
                                             String adultPosblAt, String yngBgsPosblAt, String noticeStatus);

    /** 단일 게시글 상세 조회 */
    InfoBoard selectInfoBoard(int infoBoardNo);
}
