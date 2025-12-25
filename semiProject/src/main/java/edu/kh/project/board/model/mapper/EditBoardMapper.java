package edu.kh.project.board.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EditBoardMapper {

	int boardDelete(int boardNo);


	int boardInsert(Map<String, Object> map);
}
