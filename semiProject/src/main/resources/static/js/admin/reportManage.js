// 신고 기각
function rejectReport(reportNo) {

  if (!confirm("이 신고를 기각하시겠습니까?")) return;

  location.href = "/admin/report/reject?reportNo=" + reportNo;
}

// 게시글 / 댓글 삭제
function deleteReport(btn) {

  const targetNo = btn.dataset.targetNo;
  const reportNo = btn.dataset.reportNo;
  const reportType = btn.dataset.reportType;

  if (!confirm("게시글을 삭제하시겠습니까?")) return;

  location.href =
    "/admin/report/delete"
    + "?targetNo=" + targetNo
    + "&reportNo=" + reportNo
    + "&reportType=" + reportType;
}

// 신고 유형 분류
document.getElementById("reportTypeSelect").addEventListener("change", function () {

    const reportType = this.value;

    fetch(`/admin/report?reportType=${reportType}`, {
        method: "GET"
    })
    .then(resp => resp.text())
    .then(html => {

        // 신고글 테이블 영역만 교체
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");

        const newTable = doc.querySelector("#reportTableArea");
        document.querySelector("#reportTableArea").innerHTML = newTable.innerHTML;
    });
});