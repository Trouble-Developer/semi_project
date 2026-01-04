const updateBtn = document.querySelector("#update-btn");
const deleteBtn = document.querySelector("#delete-btn");
const likeBtn = document.querySelector("#like-btn");
if (updateBtn !== null) {
  updateBtn.addEventListener("click", () => {
    const url = location.pathname.replace("board", "editBoard") + "/update";
    location.href = url;
  });
}
console.log(`보드 정보 = ${JSON.stringify(boardInfo)}`);
if (deleteBtn !== null) {
  deleteBtn.addEventListener("click", () => {
    // /editBoard/2/1997/delete?cp=1
    if (!confirm("정말로 삭제하시겠습니까?")) {
      alert("삭제가 취소되었습니다.");
      return;
    }
    const url = location.pathname.replace("board", "editBoard") + "/delete";
    // /board/2/1997?cp=1

    const queryString = location.search; // ? cp=1
    location.href = url + queryString;
    //   /editBoard/2/1997/delete?cp=1
  });
}

if (likeBtn !== null) {
  const icon = likeBtn.querySelector("i");
  likeBtn.addEventListener("click", () => {
    if (memberNo == null) {
      alert("로그인 후 이용 가능합니다.");
      return;
    }
    console.log(
      `memberNo = ${memberNo}, boardNo = ${boardNo}, likeCheck = ${likeCheck}`
    );
    const param = {
      memberNo: memberNo,
      boardNo: boardNo,
      likeCheck: likeCheck,
    };

    fetch("/board/like", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(param),
    })
      .then((resp) => resp.text())
      .then((result) => {
        if (result == -1) {
          console.log("좋아요 처리 실패..!");
          return;
        }
        likeCheck = likeCheck === 1 ? 0 : 1;

        icon.classList.toggle("fa-solid", likeCheck === 1);
        icon.classList.toggle("fa-regular", likeCheck === 0);

        document.querySelector("#likeCount").innerText = result;
      });
  });
}
const scrapBtn = document.querySelector("#scrap-btn");

if (scrapBtn) {
  const icon = scrapBtn.querySelector("i");

  scrapBtn.addEventListener("click", () => {
    console.log("스크랩 버튼 클릭");
    if (memberNo == null) {
      alert("로그인 후 이용 가능합니다.");
      return;
    }

    fetch("/board/scrap", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        boardNo,
        memberNo,
        scrapCheck,
      }),
    })
      .then((resp) => resp.text())
      .then((result) => {
        if (result == -1) {
          console.log("스크랩 처리 실패");
          return;
        }

        scrapCheck = scrapCheck === 1 ? 0 : 1;

        icon.classList.toggle("fa-solid", scrapCheck === 1);
        icon.classList.toggle("fa-regular", scrapCheck === 0);

        scrapBtn.classList.toggle("scrapped", scrapCheck === 1);
      });
  });
}

const reportBtn = document.querySelector("#report-btn");
if (reportBtn !== null) {
  reportBtn.addEventListener("click", () => {
    const url = location.pathname + "/report";
    // /board/2/1997?cp=1

    const queryString = location.search; // ? cp=1
    location.href = url + queryString;
  });
}
