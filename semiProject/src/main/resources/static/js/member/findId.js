/**
 * 아이디 찾기 페이지 JavaScript
 * 
 * [기능 설명]
 * - 이름, 이메일 유효성 검사
 * - 이메일 인증번호 발송 및 확인
 * - 인증 완료 후 아이디 찾기 자동 실행
 * - 아이디 찾기 성공 시에만 모든 입력/버튼 비활성화
 * - 아이디 찾기 실패 시 모든 입력/버튼 재활성화 (재시도 가능)
 * 
 * [동작 흐름]
 * 1. 사용자가 이름, 이메일 입력
 * 2. "인증번호 발송" 버튼 클릭 → 이메일로 인증번호 발송
 * 3. 5분 타이머 시작
 * 4. 사용자가 인증번호 입력 후 "확인" 버튼 클릭
 * 5. 인증 성공 시 → 인증 확인 버튼만 비활성화 → 아이디 찾기 자동 실행
 * 6-1. 아이디 찾기 성공 시 → 모든 입력/버튼 비활성화 → 결과 화면 표시
 * 6-2. 아이디 찾기 실패 시 → 모든 입력/버튼 재활성화 (처음부터 재시도)
 * 
 */

// ===========================================================================================
// 1. DOM 요소 가져오기
// ===========================================================================================

// ----- 입력 필드 -----
const memberName = document.querySelector("#memberName");       // 이름 입력칸
const memberEmail = document.querySelector("#memberEmail");     // 이메일 입력칸
const authKey = document.querySelector("#authKey");             // 인증번호 입력칸

// ----- 버튼 -----
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");   // 인증번호 발송 버튼
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn"); // 인증번호 확인 버튼

// ----- 메시지 영역 (에러/성공 메시지 표시) -----
const nameMessage = document.querySelector("#nameMessage");         // 이름 메시지
const emailMessage = document.querySelector("#emailMessage");       // 이메일 메시지
const authKeyMessage = document.querySelector("#authKeyMessage");   // 인증번호 메시지

// ----- 결과 표시 영역 -----
const resultArea = document.querySelector("#resultArea");       // 결과 전체 영역
const resultId = document.querySelector("#resultId");           // 찾은 아이디 표시
const enrollDate = document.querySelector("#enrollDate");       // 가입일자 표시


// ===========================================================================================
// 2. 전역 변수 선언
// ===========================================================================================

let authKeyCheck = false;   // 이메일 인증 완료 여부 (true: 완료, false: 미완료)

let authTimer;              // setInterval()이 반환하는 타이머 ID
let authMin = 4;            // 남은 시간(분) - 4분 59초부터 시작
let authSec = 59;           // 남은 시간(초)


// ===========================================================================================
// 3. 유효성 검사 함수
// ===========================================================================================

/**
 * 이름 유효성 검사
 * - 조건: 한글 2글자 이상
 * @returns {boolean} true: 통과, false: 실패
 */
const validateName = () => {
    
    // 입력값 가져오기 (앞뒤 공백 제거)
    const nameValue = memberName.value.trim();
    
    // 정규표현식: 한글만 2글자 이상
    const regExp = /^[가-힣]{2,}$/;
    
    // 검사 1: 빈 값 체크
    if(nameValue.length === 0) {
        nameMessage.innerText = "이름을 입력해주세요.";
        nameMessage.classList.add("error");
        nameMessage.classList.remove("confirm");
        return false;
    }
    
    // 검사 2: 정규표현식 체크
    if(!regExp.test(nameValue)) {
        nameMessage.innerText = "한글 2글자 이상 입력해주세요.";
        nameMessage.classList.add("error");
        nameMessage.classList.remove("confirm");
        return false;
    }
    
    // 통과
    nameMessage.innerText = "";
    nameMessage.classList.remove("error");
    return true;
};


/**
 * 이메일 유효성 검사
 * - 조건: 이메일 형식 (예: abc@example.com)
 * @returns {boolean} true: 통과, false: 실패
 */
const validateEmail = () => {
    
    const emailValue = memberEmail.value.trim();
    
    // 검사 1: 빈 값 체크
    if(emailValue.length === 0) {
        emailMessage.innerText = "이메일을 입력해주세요.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");
        return false;
    }
    
    // 이메일 형식 정규표현식
    const regExp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    
    // 검사 2: 정규표현식 체크
    if(!regExp.test(emailValue)) {
        emailMessage.innerText = "올바른 이메일 형식이 아닙니다.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");
        return false;
    }
    
    // 통과
    emailMessage.innerText = "";
    emailMessage.classList.remove("error");
    return true;
};


// ===========================================================================================
// 4. 타이머 함수
// ===========================================================================================

/**
 * 인증번호 시간 제한 카운트다운
 * - 1초마다 자동 실행 (setInterval로 호출)
 * - 화면에 남은 시간을 "MM:SS" 형식으로 표시
 * - 시간 종료 시 인증 불가능하게 처리
 */
function checkTime() {
    
    // 1초 감소
    authSec--;
    
    // 초가 0 미만이 되면 분 감소
    if(authSec < 0) {
        authMin--;
        authSec = 59;
    }
    
    // 시간이 모두 소진되면
    if(authMin < 0) {
        
        clearInterval(authTimer);   // 타이머 중지
        
        authKeyMessage.innerText = "인증 시간이 만료되었습니다.";
        authKeyMessage.classList.add("error");
        authKeyMessage.classList.remove("confirm");
        
        checkAuthKeyBtn.disabled = true;    // 확인 버튼 비활성화
        authKeyCheck = false;               // 인증 실패 처리
        
        return;
    }
    
    // 남은 시간 표시 (예: "04:59", "03:30", "00:05")
    authKeyMessage.innerText = 
        String(authMin).padStart(2, '0') + ":" + 
        String(authSec).padStart(2, '0');
}


// ===========================================================================================
// 5. 아이디 찾기 함수
// ===========================================================================================

/**
 * 아이디 찾기 실행 함수
 * - 인증 완료 후 자동으로 호출됨
 * - 이름 + 이메일을 서버로 전송
 * - 일치하는 회원의 아이디와 가입일자를 받아서 화면에 표시
 * - 아이디 찾기 성공 시에만 모든 입력/버튼 비활성화 처리
 * - 아이디 찾기 실패 시 모든 입력/버튼 재활성화 (처음부터 재시도)
 */
function findMemberId() {
    
    // URLSearchParams로 파라미터 생성 (주민번호 제거)
    const params = new URLSearchParams();
    params.append("memberName", memberName.value.trim());
    params.append("memberEmail", memberEmail.value.trim());
    
    // 디버깅용 로그
    console.log("=== 아이디 찾기 요청 데이터 ===");
    console.log("이름:", memberName.value.trim());
    console.log("이메일:", memberEmail.value.trim());
    
    // AJAX 요청
    fetch("/member/findId", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params
    })
    .then(response => {
        if(response.ok) {
            return response.text();  // 먼저 text로 받기 (null 응답 대비)
        }
        throw new Error("아이디 조회 실패");
    })
    .then(text => {
        console.log("=== 서버 응답 원본 ===");
        console.log(text);
        
        // 응답이 비어있으면 null 반환
        if(!text || text.trim() === "") {
            return null;
        }
        
        // JSON으로 파싱
        return JSON.parse(text);
    })
    .then(data => {
        console.log("=== 파싱된 데이터 ===");
        console.log(data);
        
        // ========================================================================
        // ✅ 아이디 찾기 성공
        // ========================================================================
        if(data != null && data.memberId) {
            console.log("✓ 아이디 찾기 성공!");
            
            // 결과 화면에 아이디와 가입일자 표시
            resultId.innerText = data.memberId;
            enrollDate.innerText = data.enrollDate;
            resultArea.style.display = "block";
            
            // ====================================================================
            // 🔒 아이디 찾기 성공 시에만 모든 입력/버튼 비활성화
            // ====================================================================
            
            // 1) 인증번호 발송 버튼 비활성화
            sendAuthKeyBtn.disabled = true;
            sendAuthKeyBtn.style.backgroundColor = "#cccccc";
            sendAuthKeyBtn.style.cursor = "not-allowed";
            
            // 2) 인증 확인 버튼 비활성화 (이미 비활성화되어 있지만 확실하게)
            checkAuthKeyBtn.disabled = true;
            checkAuthKeyBtn.style.backgroundColor = "#cccccc";
            checkAuthKeyBtn.style.cursor = "not-allowed";
            
            // 3) 인증번호 입력창 비활성화
            authKey.disabled = true;
            authKey.style.backgroundColor = "#f5f5f5";
            
            // 4) 이메일 입력창 비활성화
            memberEmail.disabled = true;
            memberEmail.style.backgroundColor = "#f5f5f5";
            
            // 5) 이름 입력창 비활성화
            memberName.disabled = true;
            memberName.style.backgroundColor = "#f5f5f5";
            
            console.log("✓ 아이디 찾기 성공 - 모든 입력/버튼 비활성화 완료");
            
        } 
        // ========================================================================
        // ❌ 아이디 찾기 실패 (일치하는 회원 없음)
        // ========================================================================
        else {
            console.log("✗ 일치하는 회원 없음");
            
            alert("입력하신 정보와 일치하는 회원이 없습니다.\n정보를 다시 확인하고 재시도해주세요.");
            
            // ====================================================================
            // 🔓 아이디 찾기 실패 시 모든 입력/버튼 재활성화 (처음부터 재시도)
            // ====================================================================
            
            // 1) 이름 입력창 재활성화
            memberName.disabled = false;
            memberName.style.backgroundColor = "#ffffff";
            memberName.value = "";  // 입력값 초기화
            
            // 2) 이메일 입력창 재활성화
            memberEmail.disabled = false;
            memberEmail.style.backgroundColor = "#ffffff";
            memberEmail.value = "";  // 입력값 초기화
            
            // 3) 인증번호 입력창 재활성화
            authKey.disabled = false;
            authKey.style.backgroundColor = "#ffffff";
            authKey.value = "";  // 입력값 초기화
            
            // 4) 인증번호 발송 버튼 재활성화
            sendAuthKeyBtn.disabled = false;
            sendAuthKeyBtn.style.backgroundColor = "";
            sendAuthKeyBtn.style.cursor = "pointer";
            
            // 5) 인증 확인 버튼 재활성화
            checkAuthKeyBtn.disabled = false;
            checkAuthKeyBtn.style.backgroundColor = "";
            checkAuthKeyBtn.style.cursor = "pointer";
            
            // 6) 타이머 중지 및 메시지 초기화
            clearInterval(authTimer);
            authKeyMessage.innerText = "";
            authKeyMessage.classList.remove("confirm");
            authKeyMessage.classList.remove("error");
            
            // 7) 인증 플래그 초기화
            authKeyCheck = false;
            
            // 8) 이름 입력칸으로 포커스 이동
            memberName.focus();
            
            console.log("✗ 아이디 찾기 실패 - 모든 입력/버튼 재활성화 완료 (재시도 가능)");
        }
    })
    .catch(err => {
        console.error("✗ 아이디 찾기 에러:", err);
        alert("아이디 찾기 중 문제가 발생했습니다.");
    });
    
}   // findMemberId() 함수 끝


// ===========================================================================================
// 6. 이벤트 리스너 - 인증번호 발송 버튼
// ===========================================================================================

/**
 * 인증번호 발송 버튼 클릭 이벤트
 * 
 * [동작 순서]
 * 1. 이름, 이메일 유효성 검사
 * 2. 모두 통과하면 이메일로 인증번호 발송 (AJAX)
 * 3. 발송 성공 시 5분 타이머 시작
 * 4. 인증 확인 버튼 활성화 및 스타일 초기화 (재발송 대응)
 */
sendAuthKeyBtn.addEventListener("click", () => {
    
    // 유효성 검사 (하나라도 실패하면 중단)
    if(!validateName()) return;
    if(!validateEmail()) return;
    
    // AJAX: 인증번호 발송 요청
    fetch("/email/sendAuthKey", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: memberEmail.value
        })
    })
    .then(response => {
        if(response.ok) {
            return response.text();
        }
        throw new Error("인증번호 발송 실패");
    })
    .then(result => {
        console.log("인증번호 발송 결과:", result);
        
        alert("인증번호가 발송되었습니다.\n이메일을 확인해주세요.");
        
        authKey.focus();    // 인증번호 입력칸으로 포커스 이동
        
        // ----- 타이머 시작 -----
        
        // 초기 시간 표시
        authKeyMessage.innerText = "05:00";
        authKeyMessage.classList.add("confirm");
        authKeyMessage.classList.remove("error");
        
        // 기존 타이머 중지
        if(authTimer != undefined) {
            clearInterval(authTimer);
        }
        
        // 타이머 변수 초기화
        authMin = 4;
        authSec = 59;
        
        // 1초마다 checkTime() 실행
        authTimer = setInterval(checkTime, 1000);
        
        // ====================================================================
        // 🔧 인증 확인 버튼 활성화 및 스타일 초기화 (재발송 대응)
        // ====================================================================
        // - 첫 발송 시: 기본적으로 활성화
        // - 재발송 시: 이전에 비활성화된 버튼을 다시 활성화
        // - 스타일도 함께 초기화하여 시각적으로도 정상 버튼으로 보이게 함
        
        checkAuthKeyBtn.disabled = false;
        checkAuthKeyBtn.style.backgroundColor = "";  // 스타일 초기화 ✅
        checkAuthKeyBtn.style.cursor = "pointer";     // 커서 스타일 초기화 ✅
        
        console.log("✓ 인증번호 발송/재발송 - 인증 확인 버튼 활성화 및 스타일 초기화 완료");
    })
    .catch(err => {
        console.error("에러 발생:", err);
        alert("인증번호 발송 중 문제가 발생했습니다.");
    });
    
});


// ===========================================================================================
// 7. 이벤트 리스너 - 인증번호 확인 버튼
// ===========================================================================================

/**
 * 인증번호 확인 버튼 클릭 이벤트
 * 
 * [동작 순서]
 * 1. 인증번호 입력 확인
 * 2. 유효성 검사 재확인
 * 3. 서버에 인증번호 확인 요청 (AJAX)
 * 4. 인증 성공 시 아이디 찾기 자동 실행 (버튼 비활성화 하지 않음!)
 * 5. 아이디 찾기 성공/실패 여부에 따라 findMemberId() 함수에서 비활성화 처리
 * 
 * [중요]
 * - 인증 성공 시 버튼 비활성화 하지 않음!
 * - 이메일이 틀릴 수도 있으므로 아이디 찾기 결과를 봐야 함
 * - 아이디 찾기 성공 시에만 findMemberId()에서 모든 버튼 비활성화
 * - 아이디 찾기 실패 시 모든 버튼 활성 유지 (재시도 가능)
 */
checkAuthKeyBtn.addEventListener("click", () => {
    
    // 인증번호 입력 확인
    if(authKey.value.trim().length == 0) {
        authKeyMessage.innerText = "인증번호를 입력해주세요.";
        authKeyMessage.classList.add("error");
        authKeyMessage.classList.remove("confirm");
        return;
    }
    
    // 유효성 검사 재확인
    if(!validateName()) return;
    if(!validateEmail()) return;
    
    // AJAX: 인증번호 확인 요청
    const params = new URLSearchParams({
        email: memberEmail.value,
        authKey: authKey.value
    });
    
    fetch("/email/checkAuthKey?" + params, {
        method: "GET"
    })
    .then(response => response.text())
    .then(result => {
        console.log("인증 확인 결과:", result);
        
        if(result > 0) {
            // ✅ 인증 성공
            clearInterval(authTimer);   // 타이머 중지
            
            authKeyMessage.innerText = "✓ 인증되었습니다.";
            authKeyMessage.classList.add("confirm");
            authKeyMessage.classList.remove("error");
            
            authKeyCheck = true;
            
            // ====================================================================
            // ⚠️ 인증 성공 시 버튼 비활성화 하지 않음!
            // ====================================================================
            // [이유]
            // - 인증번호는 맞지만 이메일이 틀릴 수 있음
            // - 아이디 찾기 결과를 봐야 정확한 판단 가능
            // - 아이디 찾기 성공 시에만 findMemberId()에서 비활성화
            // - 아이디 찾기 실패 시 재시도 가능하도록 활성 유지
            
            // ❌ 제거: checkAuthKeyBtn.disabled = true;
            // ❌ 제거: checkAuthKeyBtn.style.backgroundColor = "#cccccc";
            // ❌ 제거: checkAuthKeyBtn.style.cursor = "not-allowed";
            
            console.log("✓ 인증 완료 - 버튼 활성 유지 (아이디 찾기 결과 대기)");
            console.log("✓ 아이디 찾기 자동 실행 시작...");
            
            // ====================================================================
            // 아이디 찾기 자동 실행
            // - 성공 시: findMemberId() 함수에서 모든 입력/버튼 비활성화
            // - 실패 시: findMemberId() 함수에서 모든 입력/버튼 활성 유지
            // ====================================================================
            findMemberId();
            
        } else {
            // ❌ 인증 실패
            alert("인증번호가 일치하지 않습니다.");
            authKeyCheck = false;
        }
    })
    .catch(err => {
        console.error("인증 확인 에러:", err);
        alert("인증 확인 중 문제가 발생했습니다.");
    });
    
});


// ===========================================================================================
// 8. 이벤트 리스너 - 실시간 유효성 검사
// ===========================================================================================

// 이름 입력 시 실시간 검사
memberName.addEventListener("input", validateName);

// 이메일 입력 시 실시간 검사
memberEmail.addEventListener("input", validateEmail);


// ===========================================================================================
// 9. 이벤트 리스너 - 페이지 이동 버튼
// ===========================================================================================

// ----- 로그인 페이지로 이동 -----
const goToLoginBtn = document.querySelector("#goToLoginBtn");

if(goToLoginBtn) {
    goToLoginBtn.addEventListener("click", () => {
        location.href = "/member/login";
    });
}

// ----- 비밀번호 찾기 페이지로 이동 -----
const goToFindPwBtn = document.querySelector("#goToFindPwBtn");

if(goToFindPwBtn) {
    goToFindPwBtn.addEventListener("click", () => {
        // 찾은 아이디를 쿼리스트링으로 전달
        const memberId = resultId.innerText;
        location.href = "/member/findPw?memberId=" + memberId;
    });
}