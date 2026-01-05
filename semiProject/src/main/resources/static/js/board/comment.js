/**
 * 댓글(Comment) 관련 JavaScript
 * 
 * [보안 강화 버전]
 * - 클라이언트는 UX(버튼 숨김)만 처리
 * - 실제 권한은 서버에서 완전히 제어
 * - memberNo, boardWriter는 서버에서 검증하므로 전송 안 함
 */

/* =========================================================
 *                   1. 댓글 목록 조회
 * =========================================================*/

const selectCommentList = () => {

  fetch("/comment?boardNo=" + boardNo)
    .then(response => response.json())
    .then(commentList => {
      
      console.log("댓글 목록 조회 결과:", commentList);

      const ul = document.querySelector("#commentList");
      ul.innerHTML = "";

      for (let comment of commentList) {

        const commentRow = document.createElement("li");
        commentRow.classList.add("comment-row");

        if (comment.parentCommentNo != 0) {
          commentRow.classList.add("child-comment");
        }

        if (comment.commentDelFl == 'Y') {
          const hasChild = commentList.some(c => c.parentCommentNo == comment.commentNo);
          
          if (hasChild) {
            commentRow.innerText = "삭제된 댓글입니다";
            ul.append(commentRow);
          }
          continue;
        }

        const commentWriter = document.createElement("p");
        commentWriter.classList.add("comment-writer");

        const profileImg = document.createElement("img");
        profileImg.src = comment.profileImg ? comment.profileImg : userDefaultImage;

        const nickname = document.createElement("span");
        nickname.innerText = comment.memberNickname;

        const commentDate = document.createElement("span");
        commentDate.classList.add("comment-date");
        commentDate.innerText = comment.commentWriteDate;

        commentWriter.append(profileImg, nickname, commentDate);
        commentRow.append(commentWriter);

        const content = document.createElement("p");
        content.classList.add("comment-content");
        content.innerText = comment.commentContent;
        commentRow.append(content);

        const commentBtnArea = document.createElement("div");
        commentBtnArea.classList.add("comment-btn-area");

        // [UX] 고객지원 게시판: 작성자 또는 관리자만 답글 버튼 표시
        // (실제 권한은 서버에서 검증)
        if (boardCode != 5 || 
            loginMemberNo == boardWriter || 
            (loginAuthority != null && loginAuthority == 2)) {
          
          const childCommentBtn = document.createElement("button");
          childCommentBtn.innerText = "답글";
          childCommentBtn.setAttribute("onclick", 
            `showInsertComment(${comment.commentNo}, this)`);
          commentBtnArea.append(childCommentBtn);
        }

        if (loginMemberNo != null && loginMemberNo == comment.memberNo) {
          
          const updateBtn = document.createElement("button");
          updateBtn.innerText = "수정";
          updateBtn.setAttribute("onclick", 
            `showUpdateComment(${comment.commentNo}, this)`);

          const deleteBtn = document.createElement("button");
          deleteBtn.innerText = "삭제";
          deleteBtn.setAttribute("onclick", 
            `deleteComment(${comment.commentNo})`);

          commentBtnArea.append(updateBtn, deleteBtn);
        }

        commentRow.append(commentBtnArea);
        ul.append(commentRow);
      }
    })
    .catch(error => {
      console.error("댓글 조회 실패:", error);
    });
};


/* =========================================================
 *                   2. 댓글 등록
 * =========================================================*/

const addContent = document.querySelector("#addComment");
const commentContent = document.querySelector("#commentContent");

addContent?.addEventListener("click", e => {

  console.log("=== 댓글 등록 버튼 클릭 ===");

  // 1) 로그인 체크
  if (loginMemberNo == null) {
    alert("로그인 후 이용해 주세요");
    return;
  }

  // 2) [UX] 고객지원 게시판 권한 체크 (사용자 편의용)
  // 실제 권한은 서버에서 검증하므로, 이건 친절한 안내용
  if (boardCode == 5) {
    if (loginAuthority != 2 && loginMemberNo != boardWriter) {
      alert("고객지원 게시판은 게시글 작성자와 관리자만 댓글을 작성할 수 있습니다");
      return;
    }
  }

  // 3) 유효성 검사
  if (commentContent.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해 주세요");
    commentContent.focus();
    return;
  }

  // 4) 전송 데이터 (보안 강화: memberNo, boardWriter 제거!)
  const data = {
    "commentContent": commentContent.value,
    "boardNo": boardNo,
    "boardCode": boardCode,
    "parentCommentNo": 0
    // memberNo는 서버에서 세션으로 설정
    // boardWriter는 서버에서 DB 조회
  };

  console.log("전송 데이터:", data);

  // 5) AJAX 요청
  fetch("/comment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
  .then(response => response.text())
  .then(result => {
    
    console.log("서버 응답:", result);
    
    if (result > 0) {
      alert("댓글이 등록되었습니다");
      commentContent.value = "";
      selectCommentList();
    } else if (result == -1) {
      alert("권한이 없습니다");
    } else {
      alert("댓글 등록 실패");
    }
  })
  .catch(err => {
    console.error("댓글 등록 에러:", err);
    alert("댓글 등록 중 오류가 발생했습니다");
  });
});


/* =========================================================
 *                   3. 답글(대댓글) 등록
 * =========================================================*/

const showInsertComment = (parentCommentNo, btn) => {

  const existingTextarea = document.getElementsByClassName("commentInsertContent");

  if (existingTextarea.length > 0) {
    if (confirm("다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성하시겠습니까?")) {
      existingTextarea[0].nextElementSibling.remove();
      existingTextarea[0].remove();
    } else {
      return;
    }
  }

  const textarea = document.createElement("textarea");
  textarea.classList.add("commentInsertContent");
  btn.parentElement.after(textarea);

  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");

  const insertBtn = document.createElement("button");
  insertBtn.innerText = "등록";
  insertBtn.setAttribute("onclick", `insertChildComment(${parentCommentNo}, this)`);

  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "insertCancel(this)");

  commentBtnArea.append(insertBtn, cancelBtn);
  textarea.after(commentBtnArea);
};

const insertCancel = (cancelBtn) => {
  cancelBtn.parentElement.previousElementSibling.remove();
  cancelBtn.parentElement.remove();
};

const insertChildComment = (parentCommentNo, btn) => {

  const textarea = btn.parentElement.previousElementSibling;

  if (textarea.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해 주세요");
    textarea.focus();
    return;
  }

  // 보안 강화: memberNo 제거
  const data = {
    "commentContent": textarea.value,
    "boardNo": boardNo,
    "boardCode": boardCode,
    "parentCommentNo": parentCommentNo
  };

  fetch("/comment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
  .then(response => response.text())
  .then(result => {
    
    if (result > 0) {
      alert("답글이 등록되었습니다");
      selectCommentList();
    } else if (result == -1) {
      alert("권한이 없습니다");
    } else {
      alert("답글 등록 실패");
    }
  })
  .catch(err => console.error("답글 등록 에러:", err));
};


/* =========================================================
 *                   4. 댓글 삭제
 * =========================================================*/

const deleteComment = (commentNo) => {

  if (!confirm("삭제하시겠습니까?")) return;

  fetch("/comment", {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: commentNo
  })
  .then(response => response.text())
  .then(result => {
    
    if (result > 0) {
      alert("삭제되었습니다");
      selectCommentList();
    } else {
      alert("삭제 실패");
    }
  })
  .catch(err => console.error("댓글 삭제 에러:", err));
};


/* =========================================================
 *                   5. 댓글 수정
 * =========================================================*/

let beforeCommentRow;

const showUpdateComment = (commentNo, btn) => {

  const existingTextarea = document.querySelector(".update-textarea");

  if (existingTextarea != null) {
    if (confirm("수정 중인 댓글이 있습니다. 현재 댓글을 수정하시겠습니까?")) {
      const commentRow = existingTextarea.parentElement;
      commentRow.after(beforeCommentRow);
      commentRow.remove();
    } else {
      return;
    }
  }

  const commentRow = btn.closest("li");
  beforeCommentRow = commentRow.cloneNode(true);
  const beforeContent = commentRow.children[1].innerText;

  commentRow.innerHTML = "";

  const textarea = document.createElement("textarea");
  textarea.classList.add("update-textarea");
  textarea.value = beforeContent;
  commentRow.append(textarea);

  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");

  const updateBtn = document.createElement("button");
  updateBtn.innerText = "수정";
  updateBtn.setAttribute("onclick", `updateComment(${commentNo}, this)`);

  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "updateCancel(this)");

  commentBtnArea.append(updateBtn, cancelBtn);
  commentRow.append(commentBtnArea);
};

const updateCancel = (btn) => {

  if (confirm("취소하시겠습니까?")) {
    const commentRow = btn.closest("li");
    commentRow.after(beforeCommentRow);
    commentRow.remove();
  }
};

const updateComment = (commentNo, btn) => {

  const textarea = btn.parentElement.previousElementSibling;

  if (textarea.value.trim().length == 0) {
    alert("댓글 작성 후 수정 버튼을 클릭해 주세요");
    textarea.focus();
    return;
  }

  const data = {
    "commentNo": commentNo,
    "commentContent": textarea.value
  };

  fetch("/comment", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
  .then(response => response.text())
  .then(result => {
    
    if (result > 0) {
      alert("댓글이 수정되었습니다");
      selectCommentList();
    } else {
      alert("댓글 수정 실패");
    }
  })
  .catch(err => console.error("댓글 수정 에러:", err));
};


/* =========================================================
 *              6. 페이지 로드 시 초기화
 * =========================================================*/

document.addEventListener("DOMContentLoaded", selectCommentList);