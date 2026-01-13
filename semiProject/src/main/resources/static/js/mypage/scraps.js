document.addEventListener("DOMContentLoaded", () => {

  // 1. 검색창 상태 유지 (검색 후에도 옵션이랑 내용 남겨두기)
  const params = new URLSearchParams(location.search);
  const key = params.get("key");     // 제목, 내용, 등등
  const query = params.get("query"); // 검색어

  if (key != null && query != null) {
    const searchKey = document.querySelector("select[name='key']");
    const searchQuery = document.querySelector("input[name='query']");

    if (searchKey) searchKey.value = key;       // select 박스 값 유지
    if (searchQuery) searchQuery.value = query; // input 값 유지
  }


  // 2. 검색 유효성 검사 (빈칸 검색 막기)
  const searchForm = document.querySelector(".search-area");
  
  if (searchForm) {
    searchForm.addEventListener("submit", e => {
      const queryInput = searchForm.querySelector("input[name='query']");
      
      if (queryInput.value.trim().length == 0) {
        e.preventDefault(); // 제출 막기
        alert("검색어를 입력해주세요.");
        queryInput.focus();
      }
    });
  }


  // 3. 페이지네이션 링크에 검색 조건(key, query) 이어 붙이기
  // (HTML에서 cp만 넘기고 있어서 JS로 보정해줘야 함)
  if (key != null && query != null) {
    const pageLinks = document.querySelectorAll(".pagination a");

    pageLinks.forEach(a => {
      // 기존 href 값 가져오기 (예: /mypage/posts?cp=2)
      let href = a.getAttribute("href");
      
      // 이미 쿼리스트링이 있으면 &로 연결, 없으면 ?로 연결 (근데 넌 cp가 무조건 있어서 &임)
      // 결론: /mypage/posts?cp=2&key=title&query=검색어 형태로 변경
      a.setAttribute("href", href + "&key=" + key + "&query=" + query);
    });
  }

});