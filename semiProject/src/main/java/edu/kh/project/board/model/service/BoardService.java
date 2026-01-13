package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardReport;

public interface BoardService {

	List<Map<String, Object>> selectBoardTypeList();

	Map<String, Object> getBoardList(int boardCode, int cp);

	Map<String, Object> searchBoardList(Map<String, Object> paramMap, int cp);

	Board freeBoardDetil(Board board);

	Board getPrevBoard(Board board);

	Board getNextBoard(Board board);

	int boardLike(Map<String, Integer> map);

	int boardScrap(Map<String, Integer> map);

	int boardReport(Map<String, Object> report);

	BoardReport getReport(Map<String, Object> report);

	int updateReadCount(int boardNo);

	int checkBoardPw(Board board);

	List<String> selectDbImageList();
}
