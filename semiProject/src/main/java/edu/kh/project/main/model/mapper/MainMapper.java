package edu.kh.project.main.model.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.board.model.dto.Board;

@Mapper
public interface MainMapper {

	List<Board> getVolunteerList(int boardNo);

	List<Board> getNoticeBoardList();

	List<Board> getFreeBoardList();

	Board getReviewBestUser();

}
