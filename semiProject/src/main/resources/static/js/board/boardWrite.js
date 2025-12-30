const form = document.querySelector("#summernote-write");
const secretWrapper = document.querySelector("#secret-wrapper");
const boardTypeSelect = document.querySelector("#board-type");

form.addEventListener("submit", (e) => {
  const boardTitle = document.querySelector("#board-title");
  const summernote = document.querySelector("#summernote");
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
  if (boardTypeSelect.value === "5") {
    secretWrapper.style.display = "block";
  } else {
    secretWrapper.style.display = "none";
    const checkbox = document.querySelector("#checkbox");
    if (checkbox) checkbox.checked = false;
  }
}
toggleSecret();
boardTypeSelect.addEventListener("change", toggleSecret);
