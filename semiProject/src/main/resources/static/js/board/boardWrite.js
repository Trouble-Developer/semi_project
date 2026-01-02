const form = document.querySelector("#summernote-write");
const secretWrapper = document.querySelector("#secret-wrapper");
const boardTypeSelect = document.querySelector("#board-type");

form.addEventListener("submit", (e) => {
  const boardTitle = document.querySelector("#board-title");
  const secretCheck = document.querySelector("#checkbox");
  const boardPw = document.querySelector("#board-pw");

  if (secretCheck.checked) {
    if (boardPw.value.trim() === "") {
      e.preventDefault();
      alert("비밀번호를 입력해주세요!");
      return;
    }
  }

  if (boardTitle.value.trim() === "") {
    e.preventDefault();
    alert("제목을 입력해주세요!");
    boardTitle.focus();
    return;
  }
  if ($("#summernote").summernote("isEmpty")) {
    e.preventDefault();
    alert("내용을 입력해주세요!");
    return;
  }
});
function toggleSecret() {
  // boardCode는 타임리프 인라인 스크립트에서 전역 변수로 이미 선언되어 있어야 합니다.
  if (boardCode == 5) {
    // 고객지원 게시판인 경우
    secretWrapper.style.display = "block";
  } else {
    secretWrapper.style.display = "none";
    const checkbox = document.querySelector("#checkbox");
    if (checkbox) checkbox.checked = false;
  }
}
toggleSecret();
boardTypeSelect.addEventListener("change", toggleSecret);
