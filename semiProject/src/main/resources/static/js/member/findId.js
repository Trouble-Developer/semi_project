// ===== 아이디 찾기 페이지 JavaScript =====

// DOM 요소 가져오기
const memberEmail = document.querySelector("#memberEmail");
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");
const authKey = document.querySelector("#authKey");
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn");
const emailMessage = document.querySelector("#emailMessage");
const authKeyMessage = document.querySelector("#authKeyMessage");
const resultArea = document.querySelector("#resultArea");
const resultId = document.querySelector("#resultId");

// 인증번호 발송 여부 체크
let authKeyCheck = false;

// ----- 1. 인증번호 발송 버튼 클릭 시 -----
sendAuthKeyBtn.addEventListener("click", () => {
    
    // 이메일 유효성 검사 (간단하게)
    if(memberEmail.value.trim().length == 0) {
        emailMessage.innerText = "이메일을 입력해주세요.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");
        return;
    }

    // TODO: AJAX로 이메일 인증번호 발송 요청
    console.log("인증번호 발송 요청");
    
});

// ----- 2. 인증번호 확인 버튼 클릭 시 -----
checkAuthKeyBtn.addEventListener("click", () => {
    
    // 인증번호 입력 확인
    if(authKey.value.trim().length == 0) {
        authKeyMessage.innerText = "인증번호를 입력해주세요.";
        authKeyMessage.classList.add("error");
        authKeyMessage.classList.remove("confirm");
        return;
    }

    // TODO: AJAX로 인증번호 확인 + 아이디 찾기 요청
    console.log("아이디 찾기 요청");
    
});