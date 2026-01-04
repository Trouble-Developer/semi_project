// 다음 주소 API
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = ''; 
            if (data.userSelectedType === 'R') { 
                addr = data.roadAddress;
            } else { 
                addr = data.jibunAddress;
            }
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById("address").value = addr;
            document.getElementById("detailAddress").focus();
        }
    }).open();
}
// 주소 검색 버튼 이벤트
document.querySelector("#searchAddress").addEventListener("click", execDaumPostcode);


// -------------------------------------------------------------
// **** 회원 가입 유효성 검사 객체 ****
const checkObj = {
    "memberName"      : false,  // 이름
    "memberId"        : false,  // 아이디
    "memberNickname"  : false,  // 닉네임
    "memberEmail"     : false,  // 이메일
    "authKey"         : false,  // 이메일 인증번호
    "memberPw"        : false,  // 비밀번호
    "memberPwConfirm" : false,  // 비밀번호 확인
    "memberTel"       : false  // 전화번호
};


// -------------------------------------------------------------
/* 1. 이름 유효성 검사 */
const memberName = document.querySelector("#memberName");
const nameMessage = document.querySelector("#nameMessage");

memberName.addEventListener("input", e => {  // input 이벤트: 입력될 때마다
    const inputName = e.target.value;       // 현재 입력창에 작성된 값이
    if(inputName.trim().length === 0){      // 공백만 작성한 경우
        nameMessage.innerText = "이름을 입력해주세요.";
        nameMessage.classList.remove("confirm", "error");   // 둘 다 제거
        checkObj.memberName = false;    // 유효성 검사 결과 저장 객체의 상태 false 로 변경
        return;
    }
    
    const regExp = /^[가-힣]{2,10}$/; // 한글 2~10글자
    if(!regExp.test(inputName)){    // 가 아닐 경우
        nameMessage.innerText = "이름은 한글 2~10글자만 가능합니다.";
        nameMessage.classList.add("error"); // 에러 스타일 적용
        nameMessage.classList.remove("confirm");    // 확인 스타일 삭제 
        checkObj.memberName = false;        // 유효성 검사 결과 저장 객체 상태 false
        return;
    }

    // 모두 통과 시
    nameMessage.innerText = "유효한 이름입니다.";   
    nameMessage.classList.add("confirm");
    nameMessage.classList.remove("error");  // 검사 성공 처리
    checkObj.memberName = true; // 유효성 검사 결과 저장 객체 상태 true
});


// -------------------------------------------------------------
/* 2. 아이디 유효성 검사 (AJAX 포함) */
const memberId = document.querySelector("#memberId");   // 아이디 입력창
const idMessage = document.querySelector("#idMessage");  // 결과 메세지 span

memberId.addEventListener("input", e => {
    const inputId = e.target.value; // 현재 입력한 값

    if(inputId.trim().length === 0){    // 빈칸일 경우
        idMessage.innerText = "영문, 숫자로 6~20글자"; 
        idMessage.classList.remove("confirm", "error");
        checkObj.memberId = false;  // 유효성 검사 결과 false
        return;
    }

    const regExp = /^[a-zA-Z0-9]{6,20}$/; // 영문, 숫자 6~20자 제한
    if(!regExp.test(inputId)){  // 형식이 유효하지 않으면
        idMessage.innerText = "아이디 형식이 유효하지 않습니다.";
        idMessage.classList.add("error");
        idMessage.classList.remove("confirm");  // 에러 스타일 적용
        checkObj.memberId = false;
        return;
    }

    // 아이디 중복 검사 (비동기)
    // Spring Controller의 /member/checkId?memberId=입력값
    fetch("/member/checkId?memberId=" + inputId)
    .then(resp => resp.text())  // 서버에서 돌려준 응답(0 / 1)을 텍스트로 변환
    .then(count => {
        // count가 1이면 중복, 0이면 사용 가능
        if(count == 1){ // 중복
            idMessage.innerText = "이미 사용중인 아이디입니다.";
            idMessage.classList.add("error");
            idMessage.classList.remove("confirm");
            checkObj.memberId = false;
        } else { // 사용 가능
            idMessage.innerText = "사용 가능한 아이디입니다.";
            idMessage.classList.add("confirm");
            idMessage.classList.remove("error");
            checkObj.memberId = true;
        }
    })
    .catch(err => console.log(err));    // 오류 처리
});


// -------------------------------------------------------------
/* 3. 닉네임 유효성 검사 (AJAX 포함) */
const memberNickname = document.querySelector("#memberNickname");
const nickMessage = document.querySelector("#nickMessage");

// 빈칸검사
memberNickname.addEventListener("input", e => { 
    const inputNick = e.target.value;
    if(inputNick.trim().length === 0){  // 공백이면
        nickMessage.innerText = "한글,영어,숫자로만 2~10글자";
        nickMessage.classList.remove("confirm", "error");
        checkObj.memberNickname = false;
        return;
    }

    const regExp = /^[가-힣\w\d]{2,10}$/;  // 한글, 영어, 숫자 2~10자 제한
    // \w : 영문, 숫자, 언더바(_) / \d : 숫자 / 가-힣 : 한글
    if(!regExp.test(inputNick)){    // 형식이 유효하지 않다면
        nickMessage.innerText = "유효하지 않은 닉네임 형식입니다.";
        nickMessage.classList.add("error");
        nickMessage.classList.remove("confirm");    // 에러 스타일
        checkObj.memberNickname = false;
        return;
    }

    // 닉네임 중복 검사
    // Spring Controller의 /member/checkNickname?memberNickname=입력값
    fetch("/member/checkNickname?memberNickname=" + inputNick)
    .then(resp => resp.text())
    .then(count => {
        if(count == 1){ // 중복
            nickMessage.innerText = "이미 사용중인 닉네임입니다.";
            nickMessage.classList.add("error");
            nickMessage.classList.remove("confirm");    // 에러 스타일
            checkObj.memberNickname = false;
        } else {  // 사용 가능
            nickMessage.innerText = "사용 가능한 닉네임입니다.";
            nickMessage.classList.add("confirm");
            nickMessage.classList.remove("error");
            checkObj.memberNickname = true;
        }
    })
    .catch(err => console.log(err));
});


// -------------------------------------------------------------
/* 4. 이메일 인증 및 유효성 검사 */
const memberEmail = document.querySelector("#memberEmail");
const emailMessage = document.querySelector("#emailMessage");
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");
const authKey = document.querySelector("#authKey");
const authKeyMessage = document.querySelector("#authKeyMessage");
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn");

let authTimer;
const initMin = 4; 
const initSec = 59;
const initTime = "05:00";
let min = initMin;
let sec = initSec;  // 5분 타이머 설정

// 이메일 입력 시 유효성 검사
memberEmail.addEventListener("input", e => {
    // 이메일이 변경되면 인증 상태 초기화
    checkObj.authKey = false;
    document.querySelector("#authKeyMessage").innerText = "";
    clearInterval(authTimer);   // 타이머 정지

    const inputEmail = e.target.value;
    // 빈칸 검사
    if(inputEmail.trim().length === 0){
        emailMessage.innerText = "메일을 받을 수 있는 이메일을 입력해주세요.";
        emailMessage.classList.remove("confirm", "error");  // 둘 다 제거
        checkObj.memberEmail = false;   
        return;
    }
    // 이메일 형식 검사
    const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if(!regExp.test(inputEmail)){   // 형식이 유효하지 않다면
        emailMessage.innerText = "알맞은 이메일 형식으로 작성해주세요.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");   // 에러 스타일 적용
        checkObj.memberEmail = false;
        return;
    }

    // 이메일 중복 검사
    // Spring Controller의 /member/checkEmail?memberEmail=입력값
    fetch("/member/checkEmail?memberEmail=" + inputEmail)
    .then(resp => resp.text())  // 응답을 텍스트로 변환
    .then(count => {    // count가 1이면 중복, 0이면 사용 가능
        if(count == 1){ // 중복
            emailMessage.innerText = "이미 사용중인 이메일입니다.";
            emailMessage.classList.add("error");
            emailMessage.classList.remove("confirm");
            checkObj.memberEmail = false;
        } else {   // 사용 가능
            emailMessage.innerText = "사용 가능한 이메일입니다.";
            emailMessage.classList.add("confirm");
            emailMessage.classList.remove("error");
            checkObj.memberEmail = true;
        }
    })
    .catch(err => console.log(err));
});

// 인증번호 발송 버튼 클릭 시
sendAuthKeyBtn.addEventListener("click", () => {
    if(!checkObj.memberEmail){  // 이메일이 유효하지 않다면
        alert("유효한 이메일 작성 후 클릭해 주세요");
        return;
    }

    min = initMin;
    sec = initSec;
    clearInterval(authTimer);   // 타이머 초기화

    // Spring 서버로 이메일 발송 요청(POST)
    fetch("/email/signup", {
    method: "POST",
    headers: {"Content-Type" : "text/plain; charset=UTF-8"},    // 일반 텍스트(문자열) 전달
    body: memberEmail.value   // 입력한 이메일 주소 보냄
})
.then(resp => resp.text())  // 서버에서 인증번호 발송 후 결과(1 or 0) 받음
.then(result => {   // 1: 성공, 0: 실패
    if(result == 1) console.log("인증번호 발송 성공");
    else console.log("인증번호 발송 실패");
});

    authKeyMessage.innerText = initTime;    // 타이머 초기값 표시
    authKeyMessage.classList.remove("confirm", "error");
    alert("인증번호가 발송되었습니다.");    // 발송 알림

    // 타이머 시작 (5분)
    authTimer = setInterval(() => {
        authKeyMessage.innerText = `${addZero(min)}:${addZero(sec)}`;
        if(min == 0 && sec == 0){   // 시간이 다 된 경우
            checkObj.authKey = false;
            clearInterval(authTimer);   // 타이머 정지
            authKeyMessage.classList.add('error');
            authKeyMessage.classList.remove('confirm'); // 스타일 적용
            authKeyMessage.innerText = "인증번호 입력 제한시간을 초과하였습니다.";
            return;
        }
        if(sec == 0){   // 초가 0이 된 경우
            sec = 60;
            min--;  // 분 감소
        }
        sec--;
    }, 1000);   // 1초마다 실행
});

function addZero(number){
    return number < 10 ? "0" + number : number;
}   // 숫자가 한 자리 수일 때 앞에 0 붙이기

// 인증번호 확인 버튼 클릭 시
checkAuthKeyBtn.addEventListener("click", () => {
    if(min === 0 && sec === 0){ // 시간 초과
        alert("인증번호 입력 제한시간을 초과하였습니다.");
        return;
    }
    if(authKey.value.length < 6){ // 인증번호 자리수 미달
        alert("인증번호를 정확히 입력해 주세요.");
        return;
    }

    // 서버로 보낼 데이터를 객체로 만들기(JSON 형태)
    const obj = {
        "email" : memberEmail.value,
        "authKey" : authKey.value
    };

    // 인증번호 검사 요청(POST)
    fetch("/email/checkAuthKey", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(obj)   // JS 객체를 JSON 문자열로 변환
    })
    .then(resp => resp.text())  // 응답을 텍스트로 변환
    .then(result => {
        if(result == 0){    // 인증번호 불일치
            alert("인증번호가 일치하지 않습니다!");
            checkObj.authKey = false;
            return;
        }
        // 인증번호 일치
        authKeyMessage.innerText = "인증 되었습니다.";
        authKeyMessage.classList.remove("error");
        authKeyMessage.classList.add("confirm");
        checkObj.authKey = true;
        clearInterval(authTimer);   // 타이머 정지
    });
});


// -------------------------------------------------------------
/* 5. 비밀번호 유효성 검사 */
const memberPw = document.querySelector("#memberPw");
const memberPwConfirm = document.querySelector("#memberPwConfirm");
const pwMessage = document.querySelector("#pwMessage");

// 비밀번호와 확인 값이 일치하는가?
const checkPw = () => { // 화살표 함수
    if(memberPw.value === memberPwConfirm.value){
        // 두 입력창의 값이 같으면
        pwMessage.innerText = "비밀번호가 일치합니다";
        pwMessage.classList.add("confirm");
        pwMessage.classList.remove("error");
        checkObj.memberPwConfirm = true;
        return;
    }
    // 다르면
    pwMessage.innerText = "비밀번호가 일치하지 않습니다";
    pwMessage.classList.add("error");
    pwMessage.classList.remove("confirm");
    checkObj.memberPwConfirm = false;
};

// 비밀번호 입력 시 유효성 검사
memberPw.addEventListener("input", e => {   // input 이벤트: 입력될 때마다
    const inputPw = e.target.value;
    if(inputPw.trim().length === 0){    // 빈칸일 경우
        pwMessage.innerText = "영어,숫자,특수문자(!,@,#,-,_) 6~20글자";
        pwMessage.classList.remove("confirm", "error");
        checkObj.memberPw = false;  // 유효성 검사 결과 false
        return;
    }

    // 영문, 숫자, 특수문자(!@#$%^&*)가 모두 최소 1개 이상 포함된 6~20자
    const regExp = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{6,20}$/;
    if(!regExp.test(inputPw)){  // 형식이 유효하지 않으면
        pwMessage.innerText = "비밀번호가 유효하지 않습니다";
        pwMessage.classList.add("error");
        pwMessage.classList.remove("confirm");
        checkObj.memberPw = false;  // 유효성 검사 결과 false
        return;
    }
    // 모두 통과 시
    pwMessage.innerText = "유효한 비밀번호 형식입니다"; // 검사 성공 처리
    pwMessage.classList.add("confirm"); 
    pwMessage.classList.remove("error");
    checkObj.memberPw = true;

    if(memberPwConfirm.value.length > 0) checkPw(); // 비밀번호 확인이 작성된 상태라면 일치 여부 검사
});

memberPwConfirm.addEventListener("input", () => {   // 비밀번호 확인 입력 시
    if(checkObj.memberPw) checkPw();    // 비밀번호가 유효한 상태라면 일치 여부 검사
    else checkObj.memberPwConfirm = false;  // 비밀번호가 유효하지 않으면 확인도 false
});


// -------------------------------------------------------------
/* 6. 전화번호 유효성 검사 */
const memberTel = document.querySelector("#memberTel");
const telMessage = document.querySelector("#telMessage");

memberTel.addEventListener("input", e => {
    const inputTel = e.target.value;
    if(inputTel.trim().length === 0){   // 빈칸 검사
        telMessage.innerText = "전화번호를 입력해주세요.(- 제외)";
        telMessage.classList.remove("confirm", "error");
        checkObj.memberTel = false;
        return;
    }

    // 2) 정규표현식 검사
    // ^01 : 01로 시작함
    // [0-9]{1} : 그다음 숫자가 1개 (보통 0, 1, 6, 7, 9 등)
    // [0-9]{3,4} : 중간 번호 숫자 3글자 또는 4글자
    // [0-9]{4}$ : 끝 번호 숫자 4글자로 끝남
    const regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
    if(!regExp.test(inputTel)){ 
        // 형식이 유효하지 않다면
        telMessage.innerText = "유효하지 않은 전화번호 형식입니다.";
        telMessage.classList.add("error");
        telMessage.classList.remove("confirm");
        checkObj.memberTel = false;
        return;
    }

    // 모두 통과 시
    telMessage.innerText = "유효한 전화번호 형식입니다.";
    telMessage.classList.add("confirm");
    telMessage.classList.remove("error");
    checkObj.memberTel = true;
});

// -------------------------------------------------------------
/* 최종 회원가입 버튼 클릭 시 검사 */
const signUpForm = document.querySelector("#signUpForm");

signUpForm.addEventListener("submit", e => {

    // 1. checkObj에 있는 항목들 먼저 검사
    for(let key in checkObj){   // 객체용 향상된 for문
        if(!checkObj[key]){ // 각 key에 대한 value가 false인 경우
            let str;    // 경고창에 출력할 메세지 변수
            switch(key){
                case "memberName" : str = "이름이 유효하지 않습니다"; break;
                case "memberId"   : str = "아이디가 유효하지 않습니다"; break;
                case "memberNickname" : str = "닉네임이 유효하지 않습니다"; break;
                case "memberEmail" : str = "이메일이 유효하지 않습니다"; break;
                case "authKey" : str = "이메일 인증이 완료되지 않았습니다"; break;
                case "memberPw" : str = "비밀번호가 유효하지 않습니다"; break;
                case "memberPwConfirm" : str = "비밀번호 확인이 일치하지 않습니다"; break;
                case "memberTel" : str = "전화번호가 유효하지 않습니다"; break;
            }
            alert(str); 
            document.getElementById(key).focus(); // 해당 입력창으로 포커스 이동
            e.preventDefault(); 
            return; 
        }
    }

    // 2. 주소 유효성 검사 (추가된 부분)
    // 우편번호와 주소 중 하나라도 비어있으면 막음
    const postcode = document.getElementById("postcode");
    const address = document.getElementById("address");

    if(postcode.value.trim().length == 0 || address.value.trim().length == 0){
        alert("주소를 입력해주세요.");
        e.preventDefault();
        return;
    }
});