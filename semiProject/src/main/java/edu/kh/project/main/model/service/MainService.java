package edu.kh.project.main.model.service;

import java.util.List;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.info.model.dto.InfoBoard;

public interface MainService {

	List<Board> getVolunteerList(int boardNo);

	List<Board> getFreeBoardList();

	// List<InfoBoard> getInfoBoardList();


	List<Board> getNoticeBoardList();

	Board getReviewBestUser();

}
