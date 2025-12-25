const updateBtn = document.querySelector("#update-btn");
const deleteBtn = document.querySelector("#delete-btn");

if (updateBtn !== null) {
  updateBtn.addEventListener("click", () => {});
}

if (deleteBtn !== null) {
  deleteBtn.addEventListener("click", () => {
    // /editBoard/2/1997/delete?cp=1
    if (!confirm("정말로 삭제하시겠습니까?")) {
      alert("삭제가 취소되었습니다.");
      return;
    }
    const url = location.pathname.replace("board", "editBoard") + "/delete";
    // /board/2/1997?cp=1

    console.log("클릭됨");
    const queryString = location.search; // ? cp=1
    location.href = url + queryString;
    //   /editBoard/2/1997/delete?cp=1
  });
}
