const searchBtn = document.querySelector("#search-btn");
const searchInput = document.querySelector("#search-input");
const writeBtn = document.querySelector(".write-btn");

searchBtn.addEventListener("click", () => {
  if (searchInput.value.trim() == "") {
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

const boardLink = document.querySelectorAll(".board-link");

boardLink.map((a) => {
  a.addEventListener("click", () => {
    const isLock = a.getAttribute("data-lock") === "Y";
    const isWriter = a.getAttribute("data-writer");

    if (boardCode == 5 && isLock && !isWriter && !isAdmin) {
      e.preventDefault();

      const inputPw = prompt("비밀번호를 입력해주세요.");

      if (inputPw != null) {
        fetch("/board/checkPw", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(inputPw),
        })
          .then((resp) => resp.text())
          .then((result) => {});
      }
    }
  });
});
