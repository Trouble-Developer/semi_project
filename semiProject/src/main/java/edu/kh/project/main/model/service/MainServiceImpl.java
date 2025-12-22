package edu.kh.project.main.model.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.main.model.mapper.MainMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class MainServiceImpl implements MainService{
	@Autowired
	private MainMapper mapper;


	
	
}
