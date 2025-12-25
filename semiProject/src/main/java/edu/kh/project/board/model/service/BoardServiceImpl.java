package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.board.model.mapper.BoardMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper mapper;

	/**
	 * dev.안재훈 게시판 종류별 데이터 가져오기
	 */
	public List<Map<String, Object>> selectBoardTypeList() {
		return mapper.selectBoardTypeList();
	}

	/**
	 * dev. 안재훈 자유게시판 list 가져오기
	 */
	@Override
	public Map<String, Object> getBoardList(int boardCode, int cp) {
		int listCount = mapper.getBoardListCount(boardCode);

		Pagination pagination = new Pagination(cp, listCount);

		int limit = 0;

		// 봉사후기일 때 8개로
		if (boardCode == 3) {
			limit = 8;
		} else {
			limit = pagination.getLimit();
		}
		int offset = (cp - 1) * limit;

		RowBounds rowBounds = new RowBounds(offset, limit);

		List<Board> boardList = mapper.getBoardList(boardCode, rowBounds);

		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("freeBoardList", boardList);
		return map;
	}

	@Override
	public Map<String, Object> searchBoardList(Map<String, Object> paramMap, int cp) {
		int listCount = mapper.getSearchCount(paramMap);

		Pagination pagination = new Pagination(cp, listCount);

		// 봉사후기일 때 8개로
		int limit = 0;
		if ((int) paramMap.get("boardCode") == 3) {
			limit = 8;
		} else {
			limit = pagination.getLimit();
		}
		int offset = (cp - 1) * limit;

		RowBounds rowBounds = new RowBounds(offset, limit);

		List<Board> freeBoardList = mapper.getFreeList(paramMap, rowBounds);

		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("freeBoardList", freeBoardList);

		return map;
	}

	/**
	 * map : boardNo, boardCode 존재
	 */
	@Override
	public Board freeBoardDetil(Map<String, Integer> map) {

		return mapper.freeBoardDetil(map);
	}

	@Override
	public Board getPrevBoard(Map<String, Integer> map) {
		return mapper.getPrevBoard(map);
	}

	@Override
	public Board getNextBoard(Map<String, Integer> map) {
		return mapper.getNextBoard(map);
	}
}
