package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.Board;

public interface BoardService {

	List<Map<String, Object>> selectBoardTypeList();

	Map<String, Object> getFreeBoardList(int boardCode, int cp);

	Map<String, Object> searchFreeBoardList(Map<String, Object> paramMap, int cp);

	Board freeBoardDetil(Map<String, Integer> map);

	Board getPrevBoard(Map<String, Integer> map);

	Board getNextBoard(Map<String, Integer> map);
}
