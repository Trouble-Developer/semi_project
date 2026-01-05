package edu.kh.project.main.model.service;

import java.util.List;

import edu.kh.project.board.model.dto.Board;

public interface MainService {

	List<Board> getVolunteerList(int boardNo);

	List<Board> getFreeBoardList();

	List<Board> getInfoBoardList();


	List<Board> getNoticeBoardList();

	Board getReviewBestUser();

}
