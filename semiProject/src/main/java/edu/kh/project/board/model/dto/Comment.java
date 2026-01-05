package edu.kh.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    private int commentNo;          // 댓글 번호 (PK)
    private String commentContent;  // 댓글 내용
    private String commentWriteDate;// 댓글 작성일
    private String commentDelFl;    // 댓글 삭제 여부 (Y/N)
    private int boardNo;            // 게시글 번호 (FK)
    private int memberNo;           // 회원 번호 (FK)
    private int parentCommentNo;    // 부모 댓글 번호 (답글용)
    private String profileImg;      // 회원 프로필 이미지
    private String memberNickname;  // 회원 닉네임
    
    // [고객지원 게시판 권한 체크용] 추가
    private int boardCode;          // 게시판 코드 (권한 체크용)
    private int boardWriter;        // 게시글 작성자 번호 (권한 체크용)
}