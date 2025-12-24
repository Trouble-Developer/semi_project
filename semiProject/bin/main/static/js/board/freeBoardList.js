const searchBtn = document.querySelector("#search-btn");
const searchInput = document.querySelector("search-input");

searchBtn.addEventListener("click", () => {
  if (searchInput.value.trim() == "") {
    alert("검색어를 입력해주세요!");
    return;
  }
});
