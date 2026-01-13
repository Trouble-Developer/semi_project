package edu.kh.project.board.model.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface EditBoardService {

	int boardInsert(Map<String, Object> map, MultipartFile thumbnail) throws Exception;

	int boardDelete(int boardNo);

	int boardUpdate(Map<String, Object> paramMap, MultipartFile thumbnail) throws Exception;

}
