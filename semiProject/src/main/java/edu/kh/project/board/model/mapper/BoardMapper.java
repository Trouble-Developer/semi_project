package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	List<Map<String, Object>> selectBoardTypeList();

	int getFreeBoardListCount(int boardCode);

	List<Board> getFreeBoardList(int boardCode, RowBounds rowBounds);

	int getSearchCount(Map<String, Object> paramMap);

	List<Board> getFreeList(Map<String, Object> paramMap, RowBounds rowBounds);

}
