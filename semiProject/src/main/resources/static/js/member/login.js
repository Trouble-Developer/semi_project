// ----------------------------------------------------------------
// [1] 쿠키 유틸리티 함수
// ----------------------------------------------------------------
const getCookie = (key) => {
  const cookies = document.cookie; // "K=V; K=V; ..."

  // 쿠키가 하나도 없으면 바로 종료
  if (!cookies) return undefined;

  // 쿠키 문자열을 배열 형태로 변환하고, 다시 "=" 기준으로 쪼갬
  const cookieList = cookies.split("; ").map(el => el.split("="));

  // 배열 -> 객체로 변환 (key: value 형태)
  const obj = {};
  for (let i = 0; i < cookieList.length; i++) {
    const k = cookieList[i][0]; // key
    const v = cookieList[i][1]; // value
    obj[k] = v;
  }

  return obj[key]; // key에 해당하는 value 반환
};


// ----------------------------------------------------------------
// [2] HTML 요소 가져오기
// ----------------------------------------------------------------
const loginForm = document.querySelector("#loginForm");
const saveIdCheckbox = document.querySelector("#saveId"); // <input type="checkbox" name="saveId">
const memberIdInput = document.querySelector("#memberId");
const memberPwInput = document.querySelector("#memberPw");
const messageInput = document.querySelector("#loginMessage");


// ----------------------------------------------------------------
// [3] 메인 로직 실행
// ----------------------------------------------------------------

// 1. 쿠키 확인 및 아이디 자동 완성 (페이지 로딩되자마자 실행)
if (memberIdInput != null) {
  // 쿠키에서 "saveId"라는 이름의 값을 꺼내옴
  const saveId = getCookie("saveId");

  // 저장된 아이디가 있으면
  if (saveId != undefined) {
    memberIdInput.value = saveId;  // 아이디 입력창에 채움
    saveIdCheckbox.checked = true; // 체크박스 체크해둠
  }
}

// 2. 로그인 폼 제출 시 유효성 검사 (쿠키 저장 로직 삭제됨!)
if (loginForm != null) {
  loginForm.addEventListener("submit", (e) => {

    // 아이디 공백 검사
    if (memberIdInput.value.trim().length === 0) {
      alert("아이디를 입력해주세요.");
      memberIdInput.focus();
      e.preventDefault(); // 전송 막음
      return;
    }

    // 비밀번호 공백 검사
    if (memberPwInput.value.trim().length === 0) {
      alert("비밀번호를 입력해주세요.");
      memberPwInput.focus();
      e.preventDefault(); // 전송 막음
      return;
    }
  });
}