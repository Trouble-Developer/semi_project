package edu.kh.project.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 DTO 클래스
 * - 댓글 정보를 저장하고 전달하는 객체
 * - COMMENT 테이블과 매핑됨
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    
    // COMMENT 테이블 컬럼과 매핑되는 필드
    private int commentNo;          // 댓글 번호 (PK)
    private String commentContent;  // 댓글 내용
    private String commentWriteDate;// 댓글 작성일
    private String commentDelFl;    // 댓글 삭제 여부 (Y/N)
    private int boardNo;            // 게시글 번호 (FK)
    private int memberNo;           // 회원 번호 (FK)
    private int parentCommentNo;    // 부모 댓글 번호 (답글용, 0이면 일반 댓글)
    
    // 댓글 조회 시 MEMBER 테이블과 JOIN해서 가져올 데이터를 담을 필드
    private String profileImg;      // 회원 프로필 이미지
    private String memberNickname;  // 회원 닉네임
}