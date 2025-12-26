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
