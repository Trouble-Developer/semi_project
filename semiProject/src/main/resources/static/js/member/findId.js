// ===== 1. DOM 요소 가져오기 =====
// HTML에서 각 input 태그를 변수에 저장 (querySelector로 id 기준 선택)

const memberName = document.querySelector("#memberName");     // 이름 입력칸
const memberRrn1 = document.querySelector("#memberRrn1");     // 주민번호 앞 6자리
const memberRrn2 = document.querySelector("#memberRrn2");     // 주민번호 뒤 1자리
const memberEmail = document.querySelector("#memberEmail");   // 이메일 입력칸
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn"); // 인증번호 발송 버튼
const authKey = document.querySelector("#authKey");           // 인증번호 입력칸
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn"); // 확인 버튼

// 에러 메시지를 표시할 <span> 태그들
const nameMessage = document.querySelector("#nameMessage");       // 이름 메시지
const rrnMessage = document.querySelector("#rrnMessage");         // 주민번호 메시지
const emailMessage = document.querySelector("#emailMessage");     // 이메일 메시지
const authKeyMessage = document.querySelector("#authKeyMessage"); // 인증번호 메시지

// 결과 화면 관련 요소들
const resultArea = document.querySelector("#resultArea");     // 결과 전체 영역
const resultId = document.querySelector("#resultId");         // 찾은 아이디 표시 영역
const enrollDate = document.querySelector("#enrollDate");     // 가입일자 표시 영역


// ===== 2. 전역 변수 선언 =====

// 인증번호 발송 여부를 체크하는 변수
// true: 인증번호 발송됨, false: 발송 안 됨
let authKeyCheck = false;


// ===== 3. 유효성 검사 함수 정의 =====

/** 이름 유효성 검사 함수
 * - 한글 2글자 이상이어야 함
 * - 공백 제거 후 검사
 * @returns {boolean} true: 통과, false: 실패
 */
const validateName = () => {
    
    // 입력값 가져오기 (앞뒤 공백 제거)
    const nameValue = memberName.value.trim();
    
    // 정규표현식: 한글만 2글자 이상
    // ^: 시작, [가-힣]: 한글, {2,}: 2개 이상, $: 끝
    const regExp = /^[가-힣]{2,}$/;
    
    // 검사 1: 입력값이 비어있는지 확인
    if(nameValue.length === 0) {
        nameMessage.innerText = "이름을 입력해주세요.";
        nameMessage.classList.add("error");      // 빨간색 표시
        nameMessage.classList.remove("confirm");  // 초록색 제거
        return false; // 실패
    }
    
    // 검사 2: 정규표현식 통과하는지 확인
    if(!regExp.test(nameValue)) {
        nameMessage.innerText = "한글 2글자 이상 입력해주세요.";
        nameMessage.classList.add("error");
        nameMessage.classList.remove("confirm");
        return false; // 실패
    }
    
    // 모든 검사 통과 → 에러 메시지 숨기기
    nameMessage.innerText = "";
    nameMessage.classList.remove("error");
    return true; // 성공
};


/** 주민번호 유효성 검사 함수
 * - 앞자리: 6자리 숫자 (생년월일)
 * - 뒷자리: 1자리 숫자 (성별)
 * @returns {boolean} true: 통과, false: 실패
 */
const validateRrn = () => {
    
    // 입력값 가져오기 (앞뒤 공백 제거)
    const rrn1 = memberRrn1.value.trim(); // 앞 6자리
    const rrn2 = memberRrn2.value.trim(); // 뒤 1자리
    
    // 검사 1: 앞자리가 6자리 숫자인지 확인
    // length !== 6: 6자리가 아니면
    // isNaN(rrn1): 숫자가 아니면 (NaN = Not a Number)
    if(rrn1.length !== 6 || isNaN(rrn1)) {
        rrnMessage.innerText = "생년월일 6자리를 정확히 입력해주세요.";
        rrnMessage.classList.add("error");
        rrnMessage.classList.remove("confirm");
        return false; // 실패
    }
    
    // 검사 2: 뒷자리가 1자리 숫자인지 확인
    if(rrn2.length !== 1 || isNaN(rrn2)) {
        rrnMessage.innerText = "뒤 1자리를 입력해주세요.";
        rrnMessage.classList.add("error");
        rrnMessage.classList.remove("confirm");
        return false; // 실패
    }
    
    // 모든 검사 통과 → 에러 메시지 숨기기
    rrnMessage.innerText = "";
    rrnMessage.classList.remove("error");
    return true; // 성공
};


/** 이메일 유효성 검사 함수
 * - 이메일 형식이 올바른지 확인 (예: abc@example.com)
 * @returns {boolean} true: 통과, false: 실패
 */
const validateEmail = () => {
    
    // 입력값 가져오기 (앞뒤 공백 제거)
    const emailValue = memberEmail.value.trim();
    
    // 검사 1: 입력값이 비어있는지 확인
    if(emailValue.length === 0) {
        emailMessage.innerText = "이메일을 입력해주세요.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");
        return false; // 실패
    }
    
    // 이메일 형식 정규표현식
    // abc123@example.com 같은 형식인지 확인
    // [a-zA-Z0-9._-]+: 영문/숫자/._- 1개 이상
    // @: @ 필수
    // [a-zA-Z0-9.-]+: 도메인
    // \.: . 필수
    // [a-zA-Z]{2,}: 최상위 도메인 (com, net 등 2글자 이상)
    const regExp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    
    // 검사 2: 이메일 형식이 맞는지 확인
    if(!regExp.test(emailValue)) {
        emailMessage.innerText = "올바른 이메일 형식이 아닙니다.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");
        return false; // 실패
    }
    
    // 모든 검사 통과 → 에러 메시지 숨기기
    emailMessage.innerText = "";
    emailMessage.classList.remove("error");
    return true; // 성공
};


// ===== 4. 이벤트 리스너 등록 =====

/** 인증번호 발송 버튼 클릭 이벤트
 * 동작 순서:
 * 1. 이름, 주민번호, 이메일 유효성 검사
 * 2. 모두 통과하면 이메일로 인증번호 발송 (AJAX)
 * 3. 발송 성공 시 authKeyCheck = true
 */
sendAuthKeyBtn.addEventListener("click", () => {
    
    // 단계 1: 유효성 검사 (하나라도 실패하면 중단)
    // return: 함수 즉시 종료 (아래 코드 실행 안 됨)
    if(!validateName()) return;    // 이름 검사 실패 시 중단
    if(!validateRrn()) return;     // 주민번호 검사 실패 시 중단
    if(!validateEmail()) return;   // 이메일 검사 실패 시 중단
    
    // 모든 검사 통과 시 이 코드 실행
    
    // TODO: AJAX로 이메일 인증번호 발송 요청
    // 나중에 구현할 부분 (3단계 이후)
    
    // 개발 중 확인용 콘솔 출력
    console.log("===== 인증번호 발송 요청 =====");
    console.log("이름:", memberName.value);
    console.log("주민번호:", memberRrn1.value + "-" + memberRrn2.value);
    console.log("이메일:", memberEmail.value);
    console.log("=============================");
    
    /* 나중에 추가할 AJAX 코드 예시:
    fetch("/email/sendAuthKey", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            email: memberEmail.value
        })
    })
    .then(response => response.text())
    .then(authKey => {
        if(authKey != null) {
            alert("인증번호가 발송되었습니다.");
            authKeyCheck = true; // 발송 성공
        }
    });
    */
    
});


    /** 확인 버튼 클릭 이벤트 (아이디 찾기 실행)
     * 동작 순서:
     * 1. 인증번호 입력 확인
     * 2. 이름, 주민번호, 이메일 재확인
     * 3. 서버에 아이디 찾기 요청 (AJAX)
     * 4. 결과 화면 표시
     */
    checkAuthKeyBtn.addEventListener("click", () => {
    
    // 단계 1: 인증번호 입력했는지 확인
    if(authKey.value.trim().length == 0) {
        authKeyMessage.innerText = "인증번호를 입력해주세요.";
        authKeyMessage.classList.add("error");
        authKeyMessage.classList.remove("confirm");
        return; // 중단
    }
    
    // 단계 2: 유효성 검사 재확인 (사용자가 값을 바꿨을 수도 있음)
    if(!validateName()) return;
    if(!validateRrn()) return;
    if(!validateEmail()) return;
    
    // 단계 3: 모든 검사 통과 시
    
    // TODO: AJAX로 인증번호 확인 + 아이디 찾기 요청
    // 나중에 구현할 부분 (4단계 이후)
    
    // 개발 중 확인용 콘솔 출력
    console.log("===== 아이디 찾기 요청 =====");
    console.log("이름:", memberName.value);
    console.log("주민번호:", memberRrn1.value + "-" + memberRrn2.value);
    console.log("이메일:", memberEmail.value);
    console.log("인증번호:", authKey.value);
    console.log("===========================");
    
    /* 나중에 추가할 AJAX 코드 예시:
    fetch("/member/findId", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            memberName: memberName.value,
            memberRrn: memberRrn1.value + "-" + memberRrn2.value,
            memberEmail: memberEmail.value,
            authKey: authKey.value
        })
    })
    .then(response => response.json())
    .then(result => {
        if(result.memberId != null) {
            // 아이디 찾기 성공
            resultId.innerText = result.memberId;     // abc***
            enrollDate.innerText = result.enrollDate;  // 2024-01-15
            resultArea.style.display = "block";        // 결과 영역 보이기
        } else {
            // 실패 (일치하는 회원 없음)
            alert("일치하는 회원 정보가 없습니다.");
        }
    });
    */
    
    // 임시 테스트용 - 결과 화면 표시 (실제 개발 시 삭제)
    // resultArea.style.display = "block";
    // resultId.innerText = "abc***";
    // enrollDate.innerText = "2024-01-15";
    
});


// ===== 5. 실시간 유효성 검사 이벤트 리스너 =====
// 사용자가 입력할 때마다 실시간으로 검사

/** 이름 입력 시 실시간 검사
 * input 이벤트: 입력값이 바뀔 때마다 발생
 * 예: 사용자가 "홍" 입력 → validateName() 실행
 *     사용자가 "홍길" 입력 → validateName() 실행
 *     사용자가 "홍길동" 입력 → validateName() 실행
 */
memberName.addEventListener("input", validateName);

/** 주민번호 입력 시 실시간 검사
 * 앞자리, 뒷자리 둘 다 같은 검사 함수 사용
 */
memberRrn1.addEventListener("input", validateRrn);
memberRrn2.addEventListener("input", validateRrn);

/** 이메일 입력 시 실시간 검사 */
memberEmail.addEventListener("input", validateEmail);


// ===== 코드 끝 =====
// 다음 단계에서 AJAX 코드 추가 예정