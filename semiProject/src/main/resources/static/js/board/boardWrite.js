const writeBtn = document.querySelector("#write-btn");
const summernoteWrite = document.querySelector("#summernote-write");

summernoteWrite.addEventListener("submit", (e) => {
  const boardTitle = document.querySelector("#board-title");

  e.preventDefault();

  if (boardTitle.value.trim() === "") {
    alert("제목을 입력해주세요!");
    return;
  }
});
