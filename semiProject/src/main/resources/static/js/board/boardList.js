const searchBtn = document.querySelector("#search-btn");
const searchInput = document.querySelector("#search-input");
const writeBtn = document.querySelector(".write-btn");

searchBtn.addEventListener("click", (e) => {
  if (searchInput.value.trim() == "") {
    location.href = `/board/${boardCode}`;
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
    const writerNo = a.getAttribute("data-writer-no");

    const isAdmin = loginMemberAuthority == 2;
    const isWriter = loginMemberNo == writerNo;

    if (boardCode == 5 && isLock) {
      if (!isWriter && !isAdmin) {
        e.preventDefault();
        alert("비밀글은 작성자만 확인할 수 있습니다.");
      }
    }
  });
});
