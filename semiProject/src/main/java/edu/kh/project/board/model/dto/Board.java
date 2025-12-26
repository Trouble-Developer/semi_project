package edu.kh.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriteDate;
	private String boardUpdateDate;
	private int readCount;
	private char boardDelFl;
	private String boardPw;
	private char boardLock;
	private int boardCode;
	private int memberNo;

	// MEMBER 테이블 조인
	private String memberNickname;
	
	private int likeCount; // 좋아요 수
	private int likeCheck; // 좋아요 여부
	
	private int scrapCheck; // 0: 스크랩 X, 1: 스크랩 O
	
	// 추후 이미지 리스트, 댓글 리스트 추가
	// 우선은 글 가져오는 것 부터!
	

}
