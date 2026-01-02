package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.board.model.dto.BoardReport;
import edu.kh.project.board.model.mapper.BoardMapper;
// ⭐⭐⭐ 추가 1: import 2줄 추가 ⭐⭐⭐
import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.mapper.CommentMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper mapper;

	// ⭐⭐⭐ 추가 2: CommentMapper 의존성 주입 ⭐⭐⭐
	@Autowired
	private CommentMapper commentMapper;

	@Autowired
	private BCryptPasswordEncoder bcrypt;

	/**
	 * dev.안재훈 게시판 종류별 데이터 가져오기
	 */
	public List<Map<String, Object>> selectBoardTypeList() {
		return mapper.selectBoardTypeList();
	}

	/**
	 * dev. 안재훈 자유게시판 list 가져오기
	 */
	/**
	 * 게시판 목록 조회
	 */
	@Override
	public Map<String, Object> getBoardList(int boardCode, int cp) {
		// 1. 해당 게시판의 전체 게시글 수 조회
		int listCount = mapper.getBoardListCount(boardCode);

		// 2. 게시판 타입별 limit 먼저 결정
		// 봉사후기(3번)는 8개, 나머지는 10개
		int limit = (boardCode == 3) ? 8 : 10;

		// 3. 결정된 limit를 생성자에 직접 전달하여 Pagination 객체 생성
		// 이렇게 해야 내부 calculate() 메서드가 정확한 maxPage를 산출합니다.
		Pagination pagination = new Pagination(cp, listCount, limit, 10);

		
		// 4. MyBatis RowBounds 설정 (DTO와 동일한 limit 사용)
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		List<Board> boardList = mapper.getBoardList(boardCode, rowBounds);

		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("freeBoardList", boardList);

		log.debug("목록 개수(listCount): " + listCount);
		log.debug("페이지당 게시글 수(limit): " + limit);
		log.debug("계산된 마지막 페이지(maxPage): " + pagination.getMaxPage());
		return map;
	}

	/**
	 * 게시판 검색 목록 조회
	 */
	@Override
	public Map<String, Object> searchBoardList(Map<String, Object> paramMap, int cp) {
		int listCount = mapper.getSearchCount(paramMap);

		// 검색 시에도 boardCode를 확인하여 동일한 limit 적용
		int boardCode = Integer.parseInt(String.valueOf(paramMap.get("boardCode")));
		int limit = (boardCode == 3) ? 8 : 10;

		// 생성 시점에 정확한 limit 주입
		Pagination pagination = new Pagination(cp, listCount, limit, 10);

		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		List<Board> freeBoardList = mapper.getFreeList(paramMap, rowBounds);

		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("freeBoardList", freeBoardList);

		return map;
	}

	// ⭐⭐⭐ 추가 3: 이 메서드 전체를 아래 코드로 교체! ⭐⭐⭐
	@Override
	public Board freeBoardDetil(Board board) {
		// 1. 게시글 조회
		Board result = mapper.freeBoardDetil(board);

		// 2. 댓글 목록 조회 (게시글이 존재하는 경우에만)
		if (result != null) {
			List<Comment> commentList = commentMapper.selectCommentList(result.getBoardNo());
			result.setCommentList(commentList);
		}

		return result;
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

	@Override
	public int boardReport(Map<String, Object> report) {
		return mapper.boardReport(report);
	}

	@Override
	public BoardReport getReport(Map<String, Object> report) {
		return mapper.getReport(report);
	}

	@Override
	public int updateReadCount(int boardNo) {
		int result = mapper.updateReadCount(boardNo);

		if (result > 0) {
			return mapper.selectReadCount(boardNo);
		}
		return -1;
	}

	@Override
	public int checkBoardPw(Board board) {

		Board selectedBoard = mapper.freeBoardDetil(board);

		if (bcrypt.matches(board.getBoardPw(), selectedBoard.getBoardPw())) {
			return 1;
		}
		return 0;
	}
}