/**
 * 댓글(Comment) 관련 JavaScript
 * 
 * - AJAX(Fetch API)를 이용한 비동기 댓글 CRUD
 * - REST API 방식으로 서버와 통신
 * 
 * [REST API 설계]
 * ┌──────────┬─────────────────────┬──────────────┐
 * │  Method  │        URI          │     기능     │
 * ├──────────┼─────────────────────┼──────────────┤
 * │   GET    │ /comment?boardNo=1  │  댓글 조회   │
 * │   POST   │ /comment            │  댓글 등록   │
 * │   PUT    │ /comment            │  댓글 수정   │
 * │  DELETE  │ /comment            │  댓글 삭제   │
 * └──────────┴─────────────────────┴──────────────┘
 * 
 * @author 조창래
 * @since 2024-12-30
 */

/* =========================================================
 *                    전역 변수 선언부
 * =========================================================
 * 
 * boardNo, loginMemberNo, userDefaultImage 변수는
 * 게시글 상세 페이지(boardDetail.html)에서 Thymeleaf로 선언됨
 * 
 * 예시:
 * <script th:inline="javascript">
 *   const boardNo = /*[[${board.boardNo}]]*/ 0;



/* =========================================================
 *                   1. 댓글 목록 조회
 * =========================================================*/

/**
 * 댓글 목록 조회 (AJAX - GET)
 * 
 * - 서버에서 특정 게시글의 댓글 목록을 조회
 * - 조회된 데이터로 화면을 동적으로 렌더링
 * 
 * [호출 시점]
 * 1. 페이지 로드 시
 * 2. 댓글 등록/수정/삭제 성공 후
 */
const selectCommentList = () => {

  // ============ Fetch API 사용법 ============
  // [GET 요청]  : fetch(URL)
  // [POST/PUT/DELETE] : fetch(URL, {method, headers, body})
  // 
  // response.json() : JSON 응답 → JavaScript 객체로 변환
  // response.text() : 텍스트 응답 → 문자열로 변환

  fetch("/comment?boardNo=" + boardNo)  // GET 방식 요청
    .then(response => response.json())   // JSON → JS 객체 변환
    .then(commentList => {
      
      console.log("댓글 목록 조회 결과:", commentList);

      // 댓글 목록을 감싸는 ul 요소 선택
      const ul = document.querySelector("#commentList");
      
      // 기존 댓글 목록 초기화 (새로 렌더링하기 위해)
      ul.innerHTML = "";

      /* ========== 조회된 댓글 목록 렌더링 ========== */
      for (let comment of commentList) {

        // ----- 삭제된 댓글은 화면에 표시하지 않음 -----
        if (comment.commentDelFl == 'Y') {
          continue;  // 다음 댓글로 건너뛰기
        }

        // ----- 1) 댓글 행(li) 생성 -----
        const commentRow = document.createElement("li");
        commentRow.classList.add("comment-row");

        // ----- 2) 대댓글(답글)인 경우 클래스 추가 -----
        // parentCommentNo가 0이 아니면 답글 → 들여쓰기 스타일 적용
        if (comment.parentCommentNo != 0) {
          commentRow.classList.add("child-comment");
        }

        // ===== 정상 댓글 렌더링 =====

        /* ----- 작성자 정보 영역 ----- */
        const commentWriter = document.createElement("p");
        commentWriter.classList.add("comment-writer");

        // 프로필 이미지
        const profileImg = document.createElement("img");
        profileImg.src = comment.profileImg 
                         ? comment.profileImg      // 등록된 이미지
                         : userDefaultImage;       // 기본 이미지

        // 닉네임
        const nickname = document.createElement("span");
        nickname.innerText = comment.memberNickname;

        // 작성일
        const commentDate = document.createElement("span");
        commentDate.classList.add("comment-date");
        commentDate.innerText = comment.commentWriteDate;

        // 작성자 영역에 요소들 추가
        commentWriter.append(profileImg, nickname, commentDate);
        commentRow.append(commentWriter);

        /* ----- 댓글 내용 영역 ----- */
        const content = document.createElement("p");
        content.classList.add("comment-content");
        content.innerText = comment.commentContent;
        commentRow.append(content);

        /* ----- 버튼 영역 (답글/수정/삭제) ----- */
        const commentBtnArea = document.createElement("div");
        commentBtnArea.classList.add("comment-btn-area");

        // 답글 버튼 (모든 사용자에게 표시)
        const childCommentBtn = document.createElement("button");
        childCommentBtn.innerText = "답글";
        childCommentBtn.setAttribute("onclick", 
          `showInsertComment(${comment.commentNo}, this)`);
        commentBtnArea.append(childCommentBtn);

        // 수정/삭제 버튼 (본인 댓글인 경우에만 표시)
        if (loginMemberNo != null && loginMemberNo == comment.memberNo) {
          
          // 수정 버튼
          const updateBtn = document.createElement("button");
          updateBtn.innerText = "수정";
          updateBtn.setAttribute("onclick", 
            `showUpdateComment(${comment.commentNo}, this)`);

          // 삭제 버튼
          const deleteBtn = document.createElement("button");
          deleteBtn.innerText = "삭제";
          deleteBtn.setAttribute("onclick", 
            `deleteComment(${comment.commentNo})`);

          commentBtnArea.append(updateBtn, deleteBtn);
        }

        commentRow.append(commentBtnArea);

        // 댓글 목록(ul)에 행(li) 추가
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

// 댓글 등록 버튼, 입력창 요소 선택
const addContent = document.querySelector("#addComment");      // 등록 버튼
const commentContent = document.querySelector("#commentContent"); // textarea

/**
 * 댓글 등록 버튼 클릭 이벤트 핸들러
 * 
 * [처리 순서]
 * 1. 로그인 여부 확인
 * 2. 댓글 내용 유효성 검사
 * 3. AJAX로 서버에 등록 요청
 * 4. 성공 시 목록 새로고침
 */
addContent?.addEventListener("click", e => {

  // ----- 1) 로그인 체크 -----
  if (loginMemberNo == null) {
    alert("로그인 후 이용해 주세요");
    return;
  }

  // ----- 2) 유효성 검사 (빈 내용 체크) -----
  if (commentContent.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해 주세요");
    commentContent.focus();
    return;
  }

  // ----- 3) 전송할 데이터 객체 생성 -----
  const data = {
    "commentContent": commentContent.value,  // 댓글 내용
    "boardNo": boardNo,                       // 게시글 번호
    "memberNo": loginMemberNo                 // 작성자 회원번호
    // parentCommentNo는 0 (일반 댓글) - 서버에서 기본값 처리
  };

  // ----- 4) AJAX 요청 (POST) -----
  fetch("/comment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)  // JS 객체 → JSON 문자열 변환
  })
  .then(response => response.text())  // 응답을 텍스트로 변환
  .then(result => {
    
    if (result > 0) {
      alert("댓글이 등록되었습니다");
      commentContent.value = "";  // 입력창 초기화
      selectCommentList();        // 댓글 목록 새로고침
    } else {
      alert("댓글 등록 실패");
    }
  })
  .catch(err => console.error("댓글 등록 에러:", err));
});


/* =========================================================
 *                   3. 답글(대댓글) 등록
 * =========================================================*/

/**
 * 답글 작성 영역 표시
 * 
 * - 답글 버튼 클릭 시 해당 댓글 아래에 입력창 생성
 * - 한 번에 하나의 답글 입력창만 열리도록 제어
 * 
 * @param {number} parentCommentNo - 부모 댓글 번호
 * @param {HTMLElement} btn - 클릭된 답글 버튼 요소
 */
const showInsertComment = (parentCommentNo, btn) => {

  // ----- 기존 답글 입력창이 있는지 확인 -----
  const existingTextarea = document.getElementsByClassName("commentInsertContent");

  if (existingTextarea.length > 0) {
    // 이미 열린 입력창이 있는 경우
    if (confirm("다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성하시겠습니까?")) {
      // 기존 입력창 제거 (버튼 영역 → textarea 순서로 삭제)
      existingTextarea[0].nextElementSibling.remove();  // 버튼 영역 삭제
      existingTextarea[0].remove();                      // textarea 삭제
    } else {
      return;  // 취소 시 함수 종료
    }
  }

  // ----- 답글 입력 textarea 생성 -----
  const textarea = document.createElement("textarea");
  textarea.classList.add("commentInsertContent");
  
  // 버튼의 부모 요소 뒤에 textarea 추가
  btn.parentElement.after(textarea);

  // ----- 버튼 영역 생성 (등록/취소) -----
  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");

  // 등록 버튼
  const insertBtn = document.createElement("button");
  insertBtn.innerText = "등록";
  insertBtn.setAttribute("onclick", `insertChildComment(${parentCommentNo}, this)`);

  // 취소 버튼
  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "insertCancel(this)");

  // 버튼 영역에 버튼 추가 후, textarea 뒤에 배치
  commentBtnArea.append(insertBtn, cancelBtn);
  textarea.after(commentBtnArea);
};


/**
 * 답글 작성 취소
 * 
 * @param {HTMLElement} cancelBtn - 취소 버튼 요소
 */
const insertCancel = (cancelBtn) => {
  // textarea 삭제 (취소 버튼 → 부모(버튼영역) → 이전 요소(textarea))
  cancelBtn.parentElement.previousElementSibling.remove();
  // 버튼 영역 삭제
  cancelBtn.parentElement.remove();
};


/**
 * 답글(대댓글) 등록 처리
 * 
 * @param {number} parentCommentNo - 부모 댓글 번호
 * @param {HTMLElement} btn - 등록 버튼 요소
 */
const insertChildComment = (parentCommentNo, btn) => {

  // 답글 내용이 작성된 textarea
  const textarea = btn.parentElement.previousElementSibling;

  // ----- 유효성 검사 -----
  if (textarea.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해 주세요");
    textarea.focus();
    return;
  }

  // ----- 전송할 데이터 (부모 댓글 번호 포함) -----
  const data = {
    "commentContent": textarea.value,
    "boardNo": boardNo,
    "memberNo": loginMemberNo,
    "parentCommentNo": parentCommentNo  // ★ 부모 댓글 번호
  };

  // ----- AJAX 요청 (POST) -----
  fetch("/comment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
  .then(response => response.text())
  .then(result => {
    
    if (result > 0) {
      alert("답글이 등록되었습니다");
      selectCommentList();  // 목록 새로고침
    } else {
      alert("답글 등록 실패");
    }
  })
  .catch(err => console.error("답글 등록 에러:", err));
};


/* =========================================================
 *                   4. 댓글 삭제
 * =========================================================*/

/**
 * 댓글 삭제 (논리적 삭제)
 * 
 * - 실제 DELETE가 아닌 COMMENT_DEL_FL = 'Y'로 UPDATE
 * - 답글이 있는 댓글은 "삭제된 댓글입니다"로 표시됨
 * 
 * @param {number} commentNo - 삭제할 댓글 번호
 */
const deleteComment = (commentNo) => {

  // ----- 삭제 확인 -----
  if (!confirm("삭제하시겠습니까?")) return;

  // ----- AJAX 요청 (DELETE) -----
  fetch("/comment", {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: commentNo  // 댓글 번호만 전송 (숫자)
  })
  .then(response => response.text())
  .then(result => {
    
    if (result > 0) {
      alert("삭제되었습니다");
      selectCommentList();  // 목록 새로고침
    } else {
      alert("삭제 실패");
    }
  })
  .catch(err => console.error("댓글 삭제 에러:", err));
};


/* =========================================================
 *                   5. 댓글 수정
 * =========================================================*/

// 수정 취소 시 원래 상태로 복원하기 위한 백업 변수
let beforeCommentRow;

/**
 * 댓글 수정 화면으로 전환
 * 
 * - 댓글 내용을 textarea로 변경
 * - 원본 상태를 백업해두어 취소 시 복원 가능
 * 
 * @param {number} commentNo - 수정할 댓글 번호
 * @param {HTMLElement} btn - 수정 버튼 요소
 */
const showUpdateComment = (commentNo, btn) => {

  // ----- 이미 수정 중인 댓글이 있는지 확인 -----
  const existingTextarea = document.querySelector(".update-textarea");

  if (existingTextarea != null) {
    if (confirm("수정 중인 댓글이 있습니다. 현재 댓글을 수정하시겠습니까?")) {
      // 기존 수정 중인 댓글을 백업으로 복원
      const commentRow = existingTextarea.parentElement;
      commentRow.after(beforeCommentRow);  // 백업을 뒤에 추가
      commentRow.remove();                  // 수정 중인 행 삭제
    } else {
      return;
    }
  }

  // ----- 현재 댓글 행 선택 및 백업 -----
  const commentRow = btn.closest("li");
  
  // cloneNode(true) : 자식 요소까지 모두 복제
  beforeCommentRow = commentRow.cloneNode(true);

  // 기존 댓글 내용 저장
  const beforeContent = commentRow.children[1].innerText;

  // ----- 수정 화면으로 변환 -----
  commentRow.innerHTML = "";  // 내부 요소 모두 삭제

  // 수정용 textarea 생성
  const textarea = document.createElement("textarea");
  textarea.classList.add("update-textarea");
  textarea.value = beforeContent;  // 기존 내용 세팅
  commentRow.append(textarea);

  // 버튼 영역 생성
  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");

  // 수정 버튼
  const updateBtn = document.createElement("button");
  updateBtn.innerText = "수정";
  updateBtn.setAttribute("onclick", `updateComment(${commentNo}, this)`);

  // 취소 버튼
  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "updateCancel(this)");

  commentBtnArea.append(updateBtn, cancelBtn);
  commentRow.append(commentBtnArea);
};


/**
 * 댓글 수정 취소
 * 
 * - 백업해둔 원본 상태로 복원
 * 
 * @param {HTMLElement} btn - 취소 버튼 요소
 */
const updateCancel = (btn) => {

  if (confirm("취소하시겠습니까?")) {
    const commentRow = btn.closest("li");
    commentRow.after(beforeCommentRow);  // 백업을 뒤에 추가
    commentRow.remove();                  // 현재 행 삭제 → 백업이 위치 차지
  }
};


/**
 * 댓글 수정 처리
 * 
 * @param {number} commentNo - 수정할 댓글 번호
 * @param {HTMLElement} btn - 수정 버튼 요소
 */
const updateComment = (commentNo, btn) => {

  // 수정된 내용 가져오기
  const textarea = btn.parentElement.previousElementSibling;

  // ----- 유효성 검사 -----
  if (textarea.value.trim().length == 0) {
    alert("댓글 작성 후 수정 버튼을 클릭해 주세요");
    textarea.focus();
    return;
  }

  // ----- 전송할 데이터 -----
  const data = {
    "commentNo": commentNo,
    "commentContent": textarea.value
  };

  // ----- AJAX 요청 (PUT) -----
  fetch("/comment", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
  .then(response => response.text())
  .then(result => {
    
    if (result > 0) {
      alert("댓글이 수정되었습니다");
      selectCommentList();  // 목록 새로고침
    } else {
      alert("댓글 수정 실패");
    }
  })
  .catch(err => console.error("댓글 수정 에러:", err));
};


/* =========================================================
 *              6. 페이지 로드 시 초기화
 * =========================================================*/

// 페이지 로드 시 댓글 목록 조회 실행
document.addEventListener("DOMContentLoaded", selectCommentList);