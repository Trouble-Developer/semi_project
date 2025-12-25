const writeBtn = document.querySelector("#write-btn");
const summernoteWrite = document.querySelector("#summernote-write");

summernoteWrite.addEventListener("submit", (e) => {
  const boardTitle = document.querySelector("#board-title");
  const summernote = document.querySelector("#summernote");

  if (boardTitle.value.trim() === "") {
    e.preventDefault();
    alert("제목을 입력해주세요!");
    boardTitle.focus();
    return;
  }
  if (summernote.value.trim() === "") {
    e.preventDefault();
    alert("내용을 입력해주세요!");
    summernote.focus();
    return;
  }
});
