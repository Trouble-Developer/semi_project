const form = document.querySelector("#container");

form.addEventListener("submit", (e) => {
  e.preventDefault();

  const checkedReason = document.querySelector(
    'input[name="reportReason"]:checked'
  );
  const reasonDetailContent = document.querySelector("#reason-detail-content");

  if (!checkedReason) {
    alert("신고 사유를 선택해주세요.");
    return;
  }
  if (confirm("신고를 진행하시겠습니까?")) {
    const url = location.pathname;

    const param = {
      reportReason: checkedReason.value,
      reportReasonDetail: reasonDetailContent.value,
    };
    fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(param),
    })
      .then((resp) => resp.text())
      .then((result) => {
        if (result > 0) {
          alert(`신고가 완료되었습니다.`);
        } else if (result == -2) {
          alert("이미 신고한 게시글입니다.");
        } else {
          alert("신고 실패..");
        }
        location.href = `/board/${boardCode}/${boardNo}?cp=${cp}`;
      });
  } else {
    alert("신고를 취소합니다");
  }
});
