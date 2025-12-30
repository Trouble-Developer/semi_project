package edu.kh.project.board.model.service;

import java.util.Map;

public interface EditBoardService {

	int boardInsert(Map<String, Object> map);

	int boardDelete(int boardNo);

	int boardUpdate(Map<String, Object> paramMap);
}
