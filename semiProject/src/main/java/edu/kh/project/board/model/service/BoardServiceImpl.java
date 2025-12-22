package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.mapper.BoardMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class BoardServiceImpl implements BoardService{
	@Autowired
	private BoardMapper mapper;
	
	public List<Map<String, Object>> selectBoardTypeList(){
		return mapper.selectBoardTypeList();
	}
	
}
