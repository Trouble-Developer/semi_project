package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

public interface BoardService {

	List<Map<String, Object>> selectBoardTypeList();

	Map<String, Object> getFreeBoardList(int boardCode, int cp);

	Map<String, Object> searchFreeBoardList(Map<String, Object> paramMap, int cp);
}
