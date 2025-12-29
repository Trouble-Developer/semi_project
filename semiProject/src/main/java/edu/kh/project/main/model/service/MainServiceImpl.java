package edu.kh.project.main.model.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.main.model.mapper.MainMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class MainServiceImpl implements MainService{
	@Autowired
	private MainMapper mapper;

	@Override
	public List<Board> getVolunteerList(int boardNo) {
		return mapper.getVolunteerList(boardNo);
	}


	
	
}
