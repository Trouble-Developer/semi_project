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
let authKeyCheck = false; // 이메일 인증 완료 여부 (true : 완료, false : 미완료)

// 인증번호 시간 제한 둬야지(5분으로 제한)
let authTimer; // setInterval()이 반환하는 타이머 ID 저장
let authMin = 4; // 남은 시간(분) , 4분59초 부터 시작하니깐
let authSec = 59; // 남은 시간 (초)

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

// ========= 4. 타이머 함수 =======

/** 인증번호 시간 제한 카운트다운
 * - 1초마다 자동 실행 (setInterval로 호출)
 * - 화면에 남은 시간을 "MM:SS" 형식으로 표시
 * - 시간 종료 시 인증 불가능하게 처리
 */ 

    function checkTime() {

    // 1초 감소
    authSec--;

    // 초가 0미만이 되면 
    if(authSec < 0) {
        authMin--; // 분을 -1 감소
        authSec = 59; // 초를 59부터 시작하는걸로 재설정
            // 예) 03:00 -> 02:59

    }
         
     // 시간이 모두 소진되면
     if(authMin < 0) {

        // clearInterval() : 타이머 중지
        clearInterval(authTimer);

        // 만료 메시지 표시
        authKeyMessage.innerText = "인증 시간이 만료되었습니다.";
        authKeyMessage.classList.remove("confirm"); 
        // 초록색 제거
        // -> 여기서 초록색 제거란.. 시간이 흐를때를 말하는거

        authKeyMessage.classList.add("error"); // 빨간색 적용

        //  확인 버튼 비활성화
        checkAuthKeyBtn.disabled = true;

        // 인증 실패 처리
        authKeyCheck = false;

        return; // 함수 종료 (아래 코드 실행 안 됨)
     }

      // 남은 시간을 화면에 표시
      // String(): 숫자를 문자열로 변환
      // padStart(2, '0'): 2자리로 만들고, 부족하면 앞에 '0' 추가
      // 원래 4:5 이런식으로 노출되는데 04:05 4분 5초를 이렇게 표시 해준다고 함
      // 예: 5 → "05", 30 → "30"
       authKeyMessage.innerText = 
        String(authMin).padStart(2, '0') + ":" + 
        String(authSec).padStart(2, '0');
       // 결과 예: "04:59", "03:30", "00:05"
     }

    // ===== 5. 이벤트 리스너 등록 =====

    /** 인증번호 발송 버튼 클릭 이벤트
     * 동작 순서:
     * 1. 이름, 주민번호, 이메일 유효성 검사
     * 2. 모두 통과하면 이메일로 인증번호 발송 (AJAX)
     * 3. 발송 성공 시 타이머 시작 (5분 카운트다운)
     */
    sendAuthKeyBtn.addEventListener("click", () => {
    
    // 단계 1: 유효성 검사 (하나라도 실패하면 즉시 중단)
    // return: 함수 즉시 종료 (아래 코드 실행 안 됨)
    if(!validateName()) return;    // 이름 검사 실패 시 중단
    if(!validateRrn()) return;     // 주민번호 검사 실패 시 중단
    if(!validateEmail()) return;   // 이메일 검사 실패 시 중단
    
    
    // ===== AJAX: 서버에 인증번호 발송 요청 =====
    
    // fetch(): JavaScript의 비동기 통신 함수
    // - 서버와 데이터를 주고받을 때 사용
    // - Promise 기반으로 동작 (then, catch로 결과 처리)
    
    fetch("/email/sendAuthKey", { 

        method: "POST", // HTTP 메서드 (POST: 데이터 전송)

        // 요청 헤더 설정
        // Content-Type: 전송하는 데이터 형식 지정
        headers: {"Content-Type": "application/json"},

        // 요청 본문(body): 서버로 보낼 데이터
        // JSON.stringify(): JavaScript 객체 → JSON 문자열 변환
        // 예: {email: "test@test.com"} → '{"email":"test@test.com"}'
        body: JSON.stringify({
            email: memberEmail.value
        })
    })
    .then(response => { 
        // 서버 응답 받기
        // response.ok: HTTP 상태코드가 200~299이면 true
        if(response.ok) {
            return response.text();  // 응답 본문을 텍스트로 변환
        }
        // 에러 발생 시 catch로 이동
        throw new Error("인증번호 발송 실패");
    })
    .then(result => {
        // 발송 성공 시 실행
        console.log("인증번호 발송 결과:", result);
        
        alert("인증번호가 발송되었습니다.\n이메일을 확인해주세요.");
        
        // 인증번호 입력칸에 포커스 (커서 이동)
        authKey.focus();
        
        
        // ===== 타이머 시작 =====
        
        // 초기 시간 표시 (05:00)
        authKeyMessage.innerText = "05:00";
        authKeyMessage.classList.add("confirm");    // 초록색
        authKeyMessage.classList.remove("error");   // 빨간색 제거
        
        // 기존 타이머가 실행 중이면 중지
        // undefined가 아니면 = 이미 타이머가 있으면
        if(authTimer != undefined) {
            clearInterval(authTimer);
        }
        
        // 타이머 변수 초기화
        authMin = 4;   // 4분
        authSec = 59;  // 59초
        
        // setInterval(함수, 시간): 일정 시간마다 함수 반복 실행
        // 1000 = 1000밀리초 = 1초
        // checkTime 함수를 1초마다 실행
        authTimer = setInterval(checkTime, 1000);
        
        // 확인 버튼 활성화
        checkAuthKeyBtn.disabled = false;
    })
     .catch(err => {
        console.error("에러 발생:", err);
        alert("인증번호 발송 중 문제가 발생했습니다.");
    });
    
});  

    /** 확인 버튼 클릭 이벤트 (인증 확인 + 아이디 찾기)
     * 동작 순서:
     * 1. 인증번호 입력 확인
     * 2. 이름, 주민번호, 이메일 재확인
     * 3. 서버에 인증번호 확인 요청 (AJAX)
     * 4. 인증 성공 시 아이디 찾기 자동 실행
     * 5. 결과 화면 표시
     */
    checkAuthKeyBtn.addEventListener("click", () => {
        
    // 단계 1: 인증번호 입력했는지 확인
    // trim(): 공백 제거 후 길이가 0이면 = 입력 안 한 것
    if(authKey.value.trim().length == 0) {
        authKeyMessage.innerText = "인증번호를 입력해주세요.";
        authKeyMessage.classList.add("error");
        authKeyMessage.classList.remove("confirm");
        return; // 중단
    }
    
    // 단계 2: 유효성 검사 재확인
    // 사용자가 인증번호 발송 후 이름, 주민번호, 이메일을 바꿨을 수도 있음
    if(!validateName()) return;
    if(!validateRrn()) return;
    if(!validateEmail()) return;
    
    
    // ===== AJAX: 인증번호 확인 요청 =====
    
    // URLSearchParams: 쿼리스트링을 쉽게 만들어주는 객체
    // 결과: ?email=test@test.com&authKey=123456 형식으로 변환됨
    const params = new URLSearchParams({
        email: memberEmail.value,
        authKey: authKey.value
    });

    // GET 방식으로 인증번호 확인 요청
    fetch("/email/checkAuthKey?" + params, {
        method: "GET"  // GET: 데이터 조회
    })
    .then(response => response.text())  // 응답을 텍스트로 변환
    .then(result => {
        
        // 서버에서 받은 결과 확인
        console.log("인증 확인 결과:", result);
        
        // 서버 응답 값:
        // - 1 이상: 인증 성공 (DB에서 일치하는 인증번호 찾음)
        // - 0: 인증 실패 (인증번호 불일치 또는 만료됨)
        
        if(result > 0) {
            // 인증 성공!
            
            // 타이머 중지 (더 이상 시간 카운트다운 필요 없음)
            clearInterval(authTimer);
            
            // 성공 메시지 표시
            authKeyMessage.innerText = "✓ 인증되었습니다.";
            authKeyMessage.classList.add("confirm");    // 초록색
            authKeyMessage.classList.remove("error");   // 빨간색 제거
            
            // 인증 완료 플래그 설정 (전역변수)
            authKeyCheck = true;
            
            // 확인 버튼 비활성화 (중복 클릭 방지)
            checkAuthKeyBtn.disabled = true;
            
            
            // ===== 인증 성공 시 아이디 찾기 자동 실행 =====
            findMemberId();  // 아이디 찾기 함수 호출
            
        } else {
            //  인증 실패
            alert("인증번호가 일치하지 않습니다.");
            authKeyCheck = false;
        }
    })
    .catch(err => {
        // 에러 발생 시 (네트워크 오류 등)
        console.error("인증 확인 에러:", err);
        alert("인증 확인 중 문제가 발생했습니다.");
    });
    
    });

    // ======== 6. 아이디 찾기 함수 ======

    /** 아이디 찾기 실행 함수
     * - 인증 완료 후 자동으로 실행됨
     * - 이름 + 주민번호 + 이메일을 서버로 전송
     * - 일치하는 회원의 아이디와 가입일자를 받아서 화면에 표시
     * 
     * 동작 흐름:
     * 1. 주민번호 7자리 합치기 (앞 6자리 + 뒤 1자리)
     * 2. 서버에 POST 요청으로 데이터 전송
     * 3. 서버에서 DB 조회 후 결과 반환
     * 4. 성공 시: 아이디 + 가입일자 화면에 표시
     *    실패 시: 에러 메시지 표시
     */
    function findMemberId() {
    
         

    // 서버로 보낼 데이터 객체 생성
    // JavaScript 객체 형태로 먼저 만듦
    const findData = {
        memberName: memberName.value.trim(),   // 이름
        memberRrn: fullRrn,                    // 주민번호 7자리
        memberEmail: memberEmail.value.trim()  // 이메일
    };

    // 디버깅용 - 전송할 데이터 확인
    console.log("아이디 찾기 요청 데이터:", findData);

    // ===== AJAX: 아이디 찾기 요청 =====
    
    fetch("/member/findId", {  // 요청 URL
        
        method: "POST",  // POST: 데이터 전송
        
        // 요청 헤더 설정
        // JSON 형식으로 데이터를 보낸다고 서버에 알림
        headers: {"Content-Type": "application/json"},
        
        // 요청 본문(body)
        // JSON.stringify(): JavaScript 객체를 JSON 문자열로 변환
        // 서버는 JSON 문자열만 이해할 수 있음
        // 예: {memberName: "홍길동"} → '{"memberName":"홍길동"}'
        body: JSON.stringify(findData)
    })
    .then(response => {
        // 서버 응답 받기
        if(response.ok) {  // HTTP 상태코드 200~299 (성공)
            // response.json(): 응답 데이터를 JSON 객체로 변환
            // JSON 문자열 → JavaScript 객체
            return response.json();
        }
        // 에러 발생 시 catch로 이동
        throw new Error("아이디 조회 실패");
    })
    .then(data => {
        // 서버에서 받은 데이터 처리
        // data: JavaScript 객체로 변환된 응답 데이터
        console.log("서버 응답 데이터:", data);
        
        // 예상 응답 형태:
        // 성공 시: 
        //   { 
        //     memberId: "user01", 
        //     enrollDate: "2024년 12월 26일" 
        //   }
        // 실패 시: 
        //   { 
        //     message: "일치하는 회원이 없습니다" 
        //   }
        
        // memberId 속성이 있으면 = 아이디 찾기 성공
       
        if(data.memberId) {
            // 아이디 찾기 성공!
            
            // 찾은 아이디를 화면에 표시
            // innerText: HTML 요소의 텍스트 내용 변경
            resultId.innerText = data.memberId;
            
            // 가입일자를 화면에 표시
            enrollDate.innerText = "가입일: " + data.enrollDate;
            
            // 결과 영역 보이기
            // style.display: CSS display 속성 변경
            // "none" → "block": 숨김 → 보임
            resultArea.style.display = "block";
            
            // 선택사항: 입력 폼 숨기기 (깔끔한 UI를 위해)
            // 필요하면 주석 해제
            // document.querySelector("#findIdForm").style.display = "none";
            
        } else {
            // 일치하는 회원 없음
            // data.message가 있으면 그 메시지 표시, 없으면 기본 메시지
            alert(data.message || "입력하신 정보와 일치하는 회원이 없습니다.");
        }
    })
    .catch(err => {
        // 에러 발생 시 실행
        // 네트워크 오류, 서버 오류 등
        console.error("아이디 찾기 에러:", err);
        alert("아이디 찾기 중 문제가 발생했습니다.\n잠시 후 다시 시도해주세요.");
    });
    }


    // ===== 7. 실시간 유효성 검사 이벤트 리스너 =====
    // 사용자가 입력할 때마다 실시간으로 검사

    /** 이름 입력 시 실시간 검사
     * input 이벤트: 키보드로 입력값이 바뀔 때마다 발생
     * 동작 예시:
     *   사용자가 "홍" 입력 → validateName() 실행 → "한글 2글자 이상" 에러
     *   사용자가 "홍길" 입력 → validateName() 실행 → 통과 
     *   사용자가 "홍길동" 입력 → validateName() 실행 → 통과 
     */
    memberName.addEventListener("input", validateName);

    /** 주민번호 입력 시 실시간 검사
     * 앞자리, 뒷자리 둘 다 같은 검사 함수 사용
     * 둘 중 하나라도 변경되면 validateRrn() 실행
     */
    memberRrn1.addEventListener("input", validateRrn);
    memberRrn2.addEventListener("input", validateRrn);

    /** 이메일 입력 시 실시간 검사
     * 이메일 형식에 맞는지 입력할 때마다 확인
     */
    memberEmail.addEventListener("input", validateEmail);


    // ===== 8. 페이지 이동 버튼 이벤트 =====

    /** 로그인 페이지로 이동 버튼
     * - 아이디를 찾았으니 로그인하러 가기
     * - 클릭 시 /member/login 페이지로 이동
     */
    const goToLoginBtn = document.querySelector("#goToLoginBtn");

    // 버튼이 HTML에 존재하는지 확인
    // null이 아니면 = 버튼이 있으면
    if(goToLoginBtn) {
        goToLoginBtn.addEventListener("click", () => {
            // location.href: 현재 페이지를 다른 페이지로 이동시킨
            // 페이지 전체가 새로고침되면서 이동
            location.href = "/member/login";
        });
    }


    /** 비밀번호 찾기 페이지로 이동 버튼
     * - 아이디는 찾았는데 비밀번호를 모를 때 사용
     * - 찾은 아이디를 쿼리스트링으로 전달 (선택사항)
     * - 예: /member/findPw?memberId=user01
     */
    const goToFindPwBtn = document.querySelector("#goToFindPwBtn");

    if(goToFindPwBtn) {
        goToFindPwBtn.addEventListener("click", () => {
        
        // 찾은 아이디를 가져오기
        const memberId = resultId.innerText;
        
        // 쿼리스트링으로 아이디를 파라미터로 전달
        // 비밀번호 찾기 페이지에서 이 아이디를 미리 입력해줄 수 있음
        location.href = "/member/findPw?memberId=" + memberId;
    });
}