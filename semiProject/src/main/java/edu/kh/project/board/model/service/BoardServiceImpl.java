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

	@Override
	public Board freeBoardDetil(Board board) {
		// TODO Auto-generated method stub
		return mapper.freeBoardDetil(board);
	}

	@Override
	public Board getPrevBoard(Board board) {
		// TODO Auto-generated method stub
		return mapper.getPrevBoard(board);
	}

	@Override
	public Board getNextBoard(Board board) {
		// TODO Auto-generated method stub
		return mapper.getNextBoard(board);
	}

	@Override
	public int boardLike(Map<String, Integer> map) {
		int result = 0;

		if (map.get("likeCheck") == 1) {
			result = mapper.deleteBoardLike(map);
		} else {
			result = mapper.insertBoardLike(map);
		}
		if (result > 0) {
			return mapper.selectLikeCount(map.get("boardNo"));
		}
		return -1;
	}

	@Override
	public int boardScrap(Map<String, Integer> map) {
		int result;

		if (map.get("scrapCheck") == 1) {
			result = mapper.deleteScrap(map);
		} else {
			result = mapper.insertScrap(map);
		}

		return result > 0 ? 1 : -1;
	}
}
