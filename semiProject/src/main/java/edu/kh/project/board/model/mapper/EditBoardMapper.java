package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.board.model.dto.BoardImg;

@Mapper
public interface EditBoardMapper {

	int boardInsert(Map<String, Object> map);

    int selectLastBoardNo(int memberNo);

    int insertBoardImg(BoardImg img);

    int updateBoardImgBoardNo(int boardNo);

    List<BoardImg> selectBoardImgList(int boardNo);

    int deleteBoardImg(int boardNo);

    int boardDelete(int boardNo);
}
