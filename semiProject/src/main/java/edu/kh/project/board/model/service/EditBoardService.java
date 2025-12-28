package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.BoardImg;

public interface EditBoardService {

	int boardInsert(Map<String, Object> map);

	void insertBoardImg(BoardImg img);

	int boardDelete(int boardNo);

	List<BoardImg> selectBoardImgList(int boardNo);

	int boardUpdate(Map<String, Object> paramMap);
}
