package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class EditBoardServiceImpl implements EditBoardService {
	@Autowired
	private EditBoardMapper mapper;

	@Override
	public int boardInsert(Map<String, Object> map) {

		int result = mapper.boardInsert(map);

		int boardNo = mapper.selectLastBoardNo((int) map.get("memberNo"));

		mapper.updateBoardImgBoardNo(boardNo);

		return result;
	}

	@Override
	public void insertBoardImg(BoardImg img) {
		mapper.insertBoardImg(img);
	}

	@Override
	public List<BoardImg> selectBoardImgList(int boardNo) {
		return mapper.selectBoardImgList(boardNo);
	}

	@Override
	public int boardDelete(int boardNo) {
		mapper.deleteBoardImg(boardNo);
		return mapper.boardDelete(boardNo);
	}

	@Override
	public int boardUpdate(Map<String, Object> paramMap) {
		return mapper.boardUpdate(paramMap);
	}

}
