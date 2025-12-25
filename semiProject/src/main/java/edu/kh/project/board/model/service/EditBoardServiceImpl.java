package edu.kh.project.board.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.mapper.EditBoardMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class EditBoardServiceImpl implements EditBoardService{
	@Autowired
	private EditBoardMapper mapper;

	@Override
	public int boardDelete(int boardNo) {
		
		return mapper.boardDelete(boardNo);
	}

	@Override
	public int boardInsert(Map<String, Object> map) {
		return mapper.boardInsert(map);
	}



}
