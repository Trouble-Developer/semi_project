/**
 * 비밀번호 찾기 페이지 JavaScript (디버그 버전)
 * 
 * [기능 설명]
 * - 아이디, 이름, 주민번호, 이메일 유효성 검사
 * - 이메일 인증번호 발송 및 확인
 * - 인증 완료 시 회원 정보 확인
 * - 새 비밀번호 입력 및 유효성 검사
 * - 비밀번호 변경 처리
 * 
 * [동작 흐름]
 * 1. 사용자가 아이디, 이름, 주민번호, 이메일 입력
 * 2. "인증번호 발송" 버튼 클릭 → 이메일로 인증번호 발송
 * 3. 5분 타이머 시작
 * 4. 사용자가 인증번호 입력 후 "확인" 버튼 클릭
 * 5. 인증 성공 시 → 회원 정보 확인 → 비밀번호 재설정 영역 표시
 * 6. 새 비밀번호 입력 후 "비밀번호 변경" 버튼 클릭
 * 7. 서버에서 비밀번호 변경 처리 → 로그인 페이지로 이동
 * 
 */

// ===========================================================================================
// 1. DOM 요소 가져오기
// ===========================================================================================

console.log("=== findPw.js 로딩 시작 ===");

// ----- 입력 필드 -----
const memberId = document.querySelector("#memberId");           // 아이디 입력칸
const memberName = document.querySelector("#memberName");       // 이름 입력칸
const memberRrn1 = document.querySelector("#memberRrn1");       // 주민번호 앞 6자리
const memberRrn2 = document.querySelector("#memberRrn2");       // 주민번호 뒤 1자리
const memberEmail = document.querySelector("#memberEmail");     // 이메일 입력칸
const authKey = document.querySelector("#authKey");             // 인증번호 입력칸

// 새 비밀번호 입력 필드
const newPw = document.querySelector("#newPw");                 // 새 비밀번호 입력칸
const newPwConfirm = document.querySelector("#newPwConfirm");   // 새 비밀번호 확인 입력칸

// ----- 버튼 -----
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");   // 인증번호 발송 버튼
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn"); // 인증번호 확인 버튼
const resetPwBtn = document.querySelector("#resetPwBtn");           // 비밀번호 변경 버튼

// ----- 메시지 영역 (에러/성공 메시지 표시) -----
const idMessage = document.querySelector("#idMessage");             // 아이디 메시지
const nameMessage = document.querySelector("#nameMessage");         // 이름 메시지
const rrnMessage = document.querySelector("#rrnMessage");           // 주민번호 메시지
const emailMessage = document.querySelector("#emailMessage");       // 이메일 메시지
const authKeyMessage = document.querySelector("#authKeyMessage");   // 인증번호 메시지
const newPwMessage = document.querySelector("#newPwMessage");       // 새 비밀번호 메시지
const newPwConfirmMessage = document.querySelector("#newPwConfirmMessage"); // 비밀번호 확인 메시지

// ----- 결과 표시 영역 -----
const resultArea = document.querySelector("#resultArea");       // 결과 전체 영역

console.log("DOM 요소 확인:");
console.log("- sendAuthKeyBtn:", sendAuthKeyBtn);
console.log("- memberId:", memberId);
console.log("- memberName:", memberName);
console.log("- memberEmail:", memberEmail);


// ===========================================================================================
// 2. 전역 변수 선언
// ===========================================================================================

let authKeyCheck = false;   // 이메일 인증 완료 여부
let authTimer;              // setInterval() 타이머 ID
let authMin = 4;            // 남은 시간(분)
let authSec = 59;           // 남은 시간(초)

// 비밀번호 유효성 검사 상태 플래그
let pwValidCheck = false;
let pwConfirmCheck = false;


// ===========================================================================================
// 3. 유효성 검사 함수
// ===========================================================================================

/**
 * 아이디 유효성 검사 (간소화 버전)
 * - 조건: 빈 값만 체크, 4~20자
 */
const validateId = () => {
    const idValue = memberId.value.trim();
    
    // 빈 값 체크
    if(idValue.length === 0) {
        idMessage.innerText = "아이디를 입력해주세요.";
        idMessage.classList.add("error");
        idMessage.classList.remove("confirm");
        return false;
    }
    
    // 길이만 체크 (4~20자)
    if(idValue.length < 4 || idValue.length > 20) {
        idMessage.innerText = "아이디는 4~20자로 입력해주세요.";
        idMessage.classList.add("error");
        idMessage.classList.remove("confirm");
        return false;
    }
    
    // 통과
    idMessage.innerText = "";
    idMessage.classList.remove("error");
    return true;
};

/**
 * 이름 유효성 검사
 */
const validateName = () => {
    const nameValue = memberName.value.trim();
    const regExp = /^[가-힣]{2,}$/;
    
    if(nameValue.length === 0) {
        nameMessage.innerText = "이름을 입력해주세요.";
        nameMessage.classList.add("error");
        nameMessage.classList.remove("confirm");
        return false;
    }
    
    if(!regExp.test(nameValue)) {
        nameMessage.innerText = "한글 2글자 이상 입력해주세요.";
        nameMessage.classList.add("error");
        nameMessage.classList.remove("confirm");
        return false;
    }
    
    nameMessage.innerText = "";
    nameMessage.classList.remove("error");
    return true;
};

/**
 * 주민번호 유효성 검사
 */
const validateRrn = () => {
    const rrn1 = memberRrn1.value.trim();
    const rrn2 = memberRrn2.value.trim();
    
    if(rrn1.length !== 6 || isNaN(rrn1)) {
        rrnMessage.innerText = "생년월일 6자리를 정확히 입력해주세요.";
        rrnMessage.classList.add("error");
        rrnMessage.classList.remove("confirm");
        return false;
    }
    
    if(rrn2.length !== 1 || isNaN(rrn2)) {
        rrnMessage.innerText = "뒤 1자리를 입력해주세요.";
        rrnMessage.classList.add("error");
        rrnMessage.classList.remove("confirm");
        return false;
    }
    
    rrnMessage.innerText = "";
    rrnMessage.classList.remove("error");
    return true;
};

/**
 * 이메일 유효성 검사
 */
const validateEmail = () => {
    const emailValue = memberEmail.value.trim();
    
    if(emailValue.length === 0) {
        emailMessage.innerText = "이메일을 입력해주세요.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");
        return false;
    }
    
    const regExp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    
    if(!regExp.test(emailValue)) {
        emailMessage.innerText = "올바른 이메일 형식이 아닙니다.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");
        return false;
    }
    
    emailMessage.innerText = "";
    emailMessage.classList.remove("error");
    return true;
};

/**
 * 새 비밀번호 유효성 검사
 */
const validateNewPw = () => {
    const pwValue = newPw.value.trim();
    
    if(pwValue.length === 0) {
        newPwMessage.innerText = "새 비밀번호를 입력해주세요.";
        newPwMessage.classList.add("error");
        newPwMessage.classList.remove("confirm");
        pwValidCheck = false;
        return false;
    }
    
    const regExp = /^[a-zA-Z0-9!@#$%^&*]{6,20}$/;
    
    if(!regExp.test(pwValue)) {
        newPwMessage.innerText = "영문, 숫자, 특수문자 포함 6~20자로 입력해주세요.";
        newPwMessage.classList.add("error");
        newPwMessage.classList.remove("confirm");
        pwValidCheck = false;
        return false;
    }
    
    newPwMessage.innerText = "✓ 사용 가능한 비밀번호입니다.";
    newPwMessage.classList.add("confirm");
    newPwMessage.classList.remove("error");
    pwValidCheck = true;
    return true;
};

/**
 * 비밀번호 확인 유효성 검사
 */
const validateNewPwConfirm = () => {
    const pwValue = newPw.value.trim();
    const pwConfirmValue = newPwConfirm.value.trim();
    
    if(pwConfirmValue.length === 0) {
        newPwConfirmMessage.innerText = "비밀번호를 다시 입력해주세요.";
        newPwConfirmMessage.classList.add("error");
        newPwConfirmMessage.classList.remove("confirm");
        pwConfirmCheck = false;
        return false;
    }
    
    if(pwValue !== pwConfirmValue) {
        newPwConfirmMessage.innerText = "비밀번호가 일치하지 않습니다.";
        newPwConfirmMessage.classList.add("error");
        newPwConfirmMessage.classList.remove("confirm");
        pwConfirmCheck = false;
        return false;
    }
    
    newPwConfirmMessage.innerText = "✓ 비밀번호가 일치합니다.";
    newPwConfirmMessage.classList.add("confirm");
    newPwConfirmMessage.classList.remove("error");
    pwConfirmCheck = true;
    return true;
};


// ===========================================================================================
// 4. 타이머 함수
// ===========================================================================================

function checkTime() {
    authSec--;
    
    if(authSec < 0) {
        authMin--;
        authSec = 59;
    }
    
    if(authMin < 0) {
        clearInterval(authTimer);
        authKeyMessage.innerText = "인증 시간이 만료되었습니다.";
        authKeyMessage.classList.add("error");
        authKeyMessage.classList.remove("confirm");
        checkAuthKeyBtn.disabled = true;
        authKeyCheck = false;
        return;
    }
    
    authKeyMessage.innerText = 
        String(authMin).padStart(2, '0') + ":" + 
        String(authSec).padStart(2, '0');
}


// ===========================================================================================
// 5. 회원 정보 확인 함수
// ===========================================================================================

function verifyMember() {
    console.log("=== 회원 정보 확인 시작 ===");
    
    const params = new URLSearchParams();
    params.append("memberId", memberId.value.trim());
    params.append("memberName", memberName.value.trim());
    params.append("memberRrn1", memberRrn1.value.trim());
    params.append("memberEmail", memberEmail.value.trim());
    
    console.log("요청 데이터:", {
        memberId: memberId.value.trim(),
        memberName: memberName.value.trim(),
        memberRrn1: memberRrn1.value.trim(),
        memberEmail: memberEmail.value.trim()
    });
    
    fetch("/member/findPw", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params
    })
    .then(response => {
        if(response.ok) {
            return response.text();
        }
        throw new Error("회원 정보 확인 실패");
    })
    .then(text => {
        console.log("서버 응답:", text);
        
        if(!text || text.trim() === "") {
            return null;
        }
        
        return JSON.parse(text);
    })
    .then(data => {
        console.log("파싱된 데이터:", data);
        
        if(data != null && data.memberId) {
            console.log("✅ 회원 정보 확인 성공!");
            resultArea.style.display = "block";
            newPw.focus();
        } else {
            console.log("❌ 일치하는 회원 없음");
            alert("입력하신 정보와 일치하는 회원이 없습니다.");
            // 비밀번호 찾기 초기화면으로 이동
            location.href = "/member/findPw";
        }
    })
    .catch(err => {
        console.error("❌ 회원 정보 확인 에러:", err);
        alert("회원 정보 확인 중 문제가 발생했습니다.");
    });
}


// ===========================================================================================
// 6. 비밀번호 재설정 함수
// ===========================================================================================

function resetPassword() {
    console.log("=== 비밀번호 재설정 시작 ===");
    
    if(!validateNewPw()) return;
    if(!validateNewPwConfirm()) return;
    
    const params = new URLSearchParams();
    params.append("memberId", memberId.value.trim());
    params.append("newPw", newPw.value.trim());
    
    console.log("재설정 요청:", {
        memberId: memberId.value.trim(),
        newPw: "******"
    });
    
    fetch("/member/resetPw", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params
    })
    .then(response => response.text())
    .then(result => {
        console.log("재설정 결과:", result);
        
        if(result > 0) {
            console.log("✅ 비밀번호 재설정 성공!");
            alert("비밀번호가 성공적으로 변경되었습니다.\n새 비밀번호로 로그인해주세요.");
            location.href = "/member/login";
        } else {
            console.log("❌ 비밀번호 재설정 실패");
            alert("비밀번호 재설정에 실패했습니다.");
        }
    })
    .catch(err => {
        console.error("❌ 비밀번호 재설정 에러:", err);
        alert("비밀번호 재설정 중 문제가 발생했습니다.");
    });
}


// ===========================================================================================
// 7. 이벤트 리스너 - 인증번호 발송 버튼
// ===========================================================================================

console.log("이벤트 리스너 등록 시작...");

if(sendAuthKeyBtn) {
    sendAuthKeyBtn.addEventListener("click", () => {
        
        console.log("========== 인증번호 발송 버튼 클릭됨! ==========");
        console.log("입력값 확인:");
        console.log("- 아이디:", memberId.value);
        console.log("- 이름:", memberName.value);
        console.log("- 주민번호 앞:", memberRrn1.value);
        console.log("- 주민번호 뒤:", memberRrn2.value);
        console.log("- 이메일:", memberEmail.value);
        
        console.log("유효성 검사 시작...");
        
        if(!validateId()) {
            console.log("❌ 아이디 유효성 검사 실패!");
            console.log("에러 메시지:", idMessage.innerText);
            return;
        }
        console.log("✅ 아이디 검사 통과");
        
        if(!validateName()) {
            console.log("❌ 이름 유효성 검사 실패!");
            console.log("에러 메시지:", nameMessage.innerText);
            return;
        }
        console.log("✅ 이름 검사 통과");
        
        if(!validateRrn()) {
            console.log("❌ 주민번호 유효성 검사 실패!");
            console.log("에러 메시지:", rrnMessage.innerText);
            return;
        }
        console.log("✅ 주민번호 검사 통과");
        
        if(!validateEmail()) {
            console.log("❌ 이메일 유효성 검사 실패!");
            console.log("에러 메시지:", emailMessage.innerText);
            return;
        }
        console.log("✅ 이메일 검사 통과");
        
        console.log("✅✅✅ 모든 유효성 검사 통과! 이메일 발송 시작...");
        
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
            console.log("이메일 발송 응답:", response);
            if(response.ok) {
                return response.text();
            }
            throw new Error("인증번호 발송 실패");
        })
        .then(result => {
            console.log("인증번호 발송 결과:", result);
            
            alert("인증번호가 발송되었습니다.\n이메일을 확인해주세요.");
            
            authKey.focus();
            
            authKeyMessage.innerText = "05:00";
            authKeyMessage.classList.add("confirm");
            authKeyMessage.classList.remove("error");
            
            if(authTimer != undefined) {
                clearInterval(authTimer);
            }
            
            authMin = 4;
            authSec = 59;
            
            authTimer = setInterval(checkTime, 1000);
            
            checkAuthKeyBtn.disabled = false;
        })
        .catch(err => {
            console.error("❌ 이메일 발송 에러:", err);
            alert("인증번호 발송 중 문제가 발생했습니다.");
        });
        
    });
    console.log("✅ 인증번호 발송 버튼 이벤트 리스너 등록 완료");
} else {
    console.error("❌ sendAuthKeyBtn 버튼을 찾을 수 없음!");
}


// ===========================================================================================
// 8. 이벤트 리스너 - 인증번호 확인 버튼
// ===========================================================================================

if(checkAuthKeyBtn) {
    checkAuthKeyBtn.addEventListener("click", () => {
        
        console.log("========== 인증번호 확인 버튼 클릭됨 ==========");
        
        if(authKey.value.trim().length == 0) {
            authKeyMessage.innerText = "인증번호를 입력해주세요.";
            authKeyMessage.classList.add("error");
            authKeyMessage.classList.remove("confirm");
            return;
        }
        
        if(!validateId()) return;
        if(!validateName()) return;
        if(!validateRrn()) return;
        if(!validateEmail()) return;
        
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
                clearInterval(authTimer);
                
                authKeyMessage.innerText = "✓ 인증되었습니다.";
                authKeyMessage.classList.add("confirm");
                authKeyMessage.classList.remove("error");
                
                authKeyCheck = true;
                checkAuthKeyBtn.disabled = true;
                
                // 회원 정보 확인 자동 실행
                verifyMember();
                
            } else {
                alert("인증번호가 일치하지 않습니다.");
                authKeyCheck = false;
            }
        })
        .catch(err => {
            console.error("인증 확인 에러:", err);
            alert("인증 확인 중 문제가 발생했습니다.");
        });
        
    });
    console.log("✅ 인증번호 확인 버튼 이벤트 리스너 등록 완료");
}


// ===========================================================================================
// 9. 이벤트 리스너 - 비밀번호 재설정 버튼
// ===========================================================================================

if(resetPwBtn) {
    resetPwBtn.addEventListener("click", () => {
        console.log("========== 비밀번호 재설정 버튼 클릭됨 ==========");
        resetPassword();
    });
    console.log("✅ 비밀번호 재설정 버튼 이벤트 리스너 등록 완료");
}


// ===========================================================================================
// 10. 이벤트 리스너 - 실시간 유효성 검사
// ===========================================================================================

if(memberId) memberId.addEventListener("input", validateId);
if(memberName) memberName.addEventListener("input", validateName);
if(memberRrn1) memberRrn1.addEventListener("input", validateRrn);
if(memberRrn2) memberRrn2.addEventListener("input", validateRrn);
if(memberEmail) memberEmail.addEventListener("input", validateEmail);
if(newPw) newPw.addEventListener("input", validateNewPw);
if(newPwConfirm) newPwConfirm.addEventListener("input", validateNewPwConfirm);


// ===========================================================================================
// 11. 페이지 로드 완료
// ===========================================================================================

document.addEventListener("DOMContentLoaded", () => {
    console.log("=== 비밀번호 찾기 페이지 로드 완료 ===");
    
    if(memberId) {
        memberId.focus();
    }
    
    if(resultArea) {
        resultArea.style.display = "none";
    }
    
    if(resetPwBtn) {
        resetPwBtn.disabled = false;
    }
    
    console.log("=== 모든 초기화 완료! ===");
});