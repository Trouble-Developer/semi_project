const searchBtn = document.querySelector("#search-btn");
const searchInput = document.querySelector("#search-input");
const writeBtn = document.querySelector(".write-btn");

searchBtn.addEventListener("click", (e) => {
  if (searchInput.value.trim() == "") {
    e.preventDefault();
    alert("검색어를 입력해주세요!");
    return;
  }
});

if (writeBtn !== null) {
  writeBtn.addEventListener("click", () => {
    const url = location.pathname.replace("board", "editBoard") + "/insert";
    location.href = url;
  });
}

const boardLinks = document.querySelectorAll(".board-link");

boardLinks.forEach((a) => {
  a.addEventListener("click", (e) => {
    const isLock = a.getAttribute("data-lock") === "Y";
    const isWriter = a.getAttribute("data-writer") === "true";
    const boardNo = a.getAttribute("data-board-no"); // 글 번호 가져오기

    // 관리자 여부 (script 영역에서 정의했다고 가정)
    const isAdmin = loginMemberAuthority == 2;

    if (boardCode == 5 && isLock && !isWriter && !isAdmin) {
      e.preventDefault(); // 일단 상세페이지 이동 차단

      const inputPw = prompt("비밀번호를 입력해주세요.");

      if (inputPw != null && inputPw.trim() !== "") {
        // 서버에 글번호와 입력한 비밀번호를 보내서 확인
        fetch("/board/checkPw", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            boardNo: boardNo,
            boardPw: inputPw,
            boardCode: boardCode,
          }),
        })
          .then((resp) => resp.json())
          .then((result) => {
            if (result > 0) {
              // 일치하면 상세페이지로 이동
              location.href = a.href;
            } else {
              alert("비밀번호가 일치하지 않습니다.");
            }
          })
          .catch((err) => console.log(err));
      }
    }
  });
});
