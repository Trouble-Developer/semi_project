package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardReport;

@Mapper
public interface BoardMapper {

	List<Map<String, Object>> selectBoardTypeList();

	int getBoardListCount(int boardCode);

	List<Board> getBoardList(int boardCode, RowBounds rowBounds);

	int getSearchCount(Map<String, Object> paramMap);

	List<Board> getFreeList(Map<String, Object> paramMap, RowBounds rowBounds);

	Board freeBoardDetil(Board board);

	Board getPrevBoard(Board board);

	Board getNextBoard(Board board);

	int deleteBoardLike(Map<String, Integer> map);

	int insertBoardLike(Map<String, Integer> map);

	int selectLikeCount(int boardNo);

	int deleteScrap(Map<String, Integer> map);

	int insertScrap(Map<String, Integer> map);

	int boardReport(Map<String, Object> report);

	BoardReport getReport(Map<String, Object> report);

	int updateReadCount(int boardNo);

	int selectReadCount(int boardNo);

	List<String> selectDbImageList();

}
