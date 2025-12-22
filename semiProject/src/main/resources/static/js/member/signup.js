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
    "memberName"      : false,
    "memberId"        : false,
    "memberNickname"  : false,
    "memberEmail"     : false,
    "authKey"         : false,
    "memberPw"        : false,
    "memberPwConfirm" : false,
    "memberTel"       : false,
    "memberRrn"       : false // 주민번호 (앞/뒤 다 입력했는지)
};


// -------------------------------------------------------------
/* 1. 이름 유효성 검사 */
const memberName = document.querySelector("#memberName");
const nameMessage = document.querySelector("#nameMessage");

memberName.addEventListener("input", e => {
    const inputName = e.target.value;
    if(inputName.trim().length === 0){
        nameMessage.innerText = "이름을 입력해주세요.";
        nameMessage.classList.remove("confirm", "error");
        checkObj.memberName = false;
        return;
    }
    
    const regExp = /^[가-힣]{2,10}$/; // 한글 2~10글자
    if(!regExp.test(inputName)){
        nameMessage.innerText = "이름은 한글 2~10글자만 가능합니다.";
        nameMessage.classList.add("error");
        nameMessage.classList.remove("confirm");
        checkObj.memberName = false;
        return;
    }

    nameMessage.innerText = "유효한 이름입니다.";
    nameMessage.classList.add("confirm");
    nameMessage.classList.remove("error");
    checkObj.memberName = true;
});


// -------------------------------------------------------------
/* 2. 아이디 유효성 검사 (AJAX 포함) */
const memberId = document.querySelector("#memberId");
const idMessage = document.querySelector("#idMessage");

memberId.addEventListener("input", e => {
    const inputId = e.target.value;

    if(inputId.trim().length === 0){
        idMessage.innerText = "영문, 숫자로 6~20글자";
        idMessage.classList.remove("confirm", "error");
        checkObj.memberId = false;
        return;
    }

    const regExp = /^[a-zA-Z0-9]{6,20}$/; // 영문, 숫자 6~20자
    if(!regExp.test(inputId)){
        idMessage.innerText = "아이디 형식이 유효하지 않습니다.";
        idMessage.classList.add("error");
        idMessage.classList.remove("confirm");
        checkObj.memberId = false;
        return;
    }

    // 아이디 중복 검사 (비동기)
    fetch("/member/checkId?memberId=" + inputId)
    .then(resp => resp.text())
    .then(count => {
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
    .catch(err => console.log(err));
});


// -------------------------------------------------------------
/* 3. 닉네임 유효성 검사 (AJAX 포함) */
const memberNickname = document.querySelector("#memberNickname");
const nickMessage = document.querySelector("#nickMessage");

memberNickname.addEventListener("input", e => {
    const inputNick = e.target.value;
    if(inputNick.trim().length === 0){
        nickMessage.innerText = "한글,영어,숫자로만 2~10글자";
        nickMessage.classList.remove("confirm", "error");
        checkObj.memberNickname = false;
        return;
    }

    const regExp = /^[가-힣\w\d]{2,10}$/;
    if(!regExp.test(inputNick)){
        nickMessage.innerText = "유효하지 않은 닉네임 형식입니다.";
        nickMessage.classList.add("error");
        nickMessage.classList.remove("confirm");
        checkObj.memberNickname = false;
        return;
    }

    // 닉네임 중복 검사
    fetch("/member/checkNickname?memberNickname=" + inputNick)
    .then(resp => resp.text())
    .then(count => {
        if(count == 1){
            nickMessage.innerText = "이미 사용중인 닉네임입니다.";
            nickMessage.classList.add("error");
            nickMessage.classList.remove("confirm");
            checkObj.memberNickname = false;
        } else {
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
let sec = initSec;

// 이메일 입력 시 유효성 검사
memberEmail.addEventListener("input", e => {
    checkObj.authKey = false;
    document.querySelector("#authKeyMessage").innerText = "";
    clearInterval(authTimer);

    const inputEmail = e.target.value;
    if(inputEmail.trim().length === 0){
        emailMessage.innerText = "메일을 받을 수 있는 이메일을 입력해주세요.";
        emailMessage.classList.remove("confirm", "error");
        checkObj.memberEmail = false;
        return;
    }

    const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if(!regExp.test(inputEmail)){
        emailMessage.innerText = "알맞은 이메일 형식으로 작성해주세요.";
        emailMessage.classList.add("error");
        emailMessage.classList.remove("confirm");
        checkObj.memberEmail = false;
        return;
    }

    // 이메일 중복 검사
    fetch("/member/checkEmail?memberEmail=" + inputEmail)
    .then(resp => resp.text())
    .then(count => {
        if(count == 1){
            emailMessage.innerText = "이미 사용중인 이메일입니다.";
            emailMessage.classList.add("error");
            emailMessage.classList.remove("confirm");
            checkObj.memberEmail = false;
        } else {
            emailMessage.innerText = "사용 가능한 이메일입니다.";
            emailMessage.classList.add("confirm");
            emailMessage.classList.remove("error");
            checkObj.memberEmail = true;
        }
    })
    .catch(err => console.log(err));
});

// 인증번호 발송
sendAuthKeyBtn.addEventListener("click", () => {
    if(!checkObj.memberEmail){
        alert("유효한 이메일 작성 후 클릭해 주세요");
        return;
    }

    min = initMin;
    sec = initSec;
    clearInterval(authTimer);

    fetch("/email/signup", {
    method: "POST",
    headers: {"Content-Type" : "text/plain; charset=UTF-8"},
    body: memberEmail.value
})
.then(resp => resp.text())
.then(result => {
    if(result == 1) console.log("인증번호 발송 성공");
    else console.log("인증번호 발송 실패");
});

    authKeyMessage.innerText = initTime;
    authKeyMessage.classList.remove("confirm", "error");
    alert("인증번호가 발송되었습니다.");

    authTimer = setInterval(() => {
        authKeyMessage.innerText = `${addZero(min)}:${addZero(sec)}`;
        if(min == 0 && sec == 0){
            checkObj.authKey = false;
            clearInterval(authTimer);
            authKeyMessage.classList.add('error');
            authKeyMessage.classList.remove('confirm');
            return;
        }
        if(sec == 0){
            sec = 60;
            min--;
        }
        sec--;
    }, 1000);
});

function addZero(number){
    return number < 10 ? "0" + number : number;
}

// 인증번호 확인
checkAuthKeyBtn.addEventListener("click", () => {
    if(min === 0 && sec === 0){
        alert("인증번호 입력 제한시간을 초과하였습니다.");
        return;
    }
    if(authKey.value.length < 6){
        alert("인증번호를 정확히 입력해 주세요.");
        return;
    }

    const obj = {
        "email" : memberEmail.value,
        "authKey" : authKey.value
    };

    fetch("/email/checkAuthKey", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(obj)
    })
    .then(resp => resp.text())
    .then(result => {
        if(result == 0){
            alert("인증번호가 일치하지 않습니다!");
            checkObj.authKey = false;
            return;
        }
        authKeyMessage.innerText = "인증 되었습니다.";
        authKeyMessage.classList.remove("error");
        authKeyMessage.classList.add("confirm");
        checkObj.authKey = true;
        clearInterval(authTimer);
    });
});


// -------------------------------------------------------------
/* 5. 비밀번호 유효성 검사 */
const memberPw = document.querySelector("#memberPw");
const memberPwConfirm = document.querySelector("#memberPwConfirm");
const pwMessage = document.querySelector("#pwMessage");

const checkPw = () => {
    if(memberPw.value === memberPwConfirm.value){
        pwMessage.innerText = "비밀번호가 일치합니다";
        pwMessage.classList.add("confirm");
        pwMessage.classList.remove("error");
        checkObj.memberPwConfirm = true;
        return;
    }
    pwMessage.innerText = "비밀번호가 일치하지 않습니다";
    pwMessage.classList.add("error");
    pwMessage.classList.remove("confirm");
    checkObj.memberPwConfirm = false;
};

memberPw.addEventListener("input", e => {
    const inputPw = e.target.value;
    if(inputPw.trim().length === 0){
        pwMessage.innerText = "영어,숫자,특수문자(!,@,#,-,_) 6~20글자";
        pwMessage.classList.remove("confirm", "error");
        checkObj.memberPw = false;
        return;
    }

    const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;
    if(!regExp.test(inputPw)){
        pwMessage.innerText = "비밀번호가 유효하지 않습니다";
        pwMessage.classList.add("error");
        pwMessage.classList.remove("confirm");
        checkObj.memberPw = false;
        return;
    }

    pwMessage.innerText = "유효한 비밀번호 형식입니다";
    pwMessage.classList.add("confirm"); // 오타 수정: confrim -> confirm
    pwMessage.classList.remove("error");
    checkObj.memberPw = true;

    if(memberPwConfirm.value.length > 0) checkPw();
});

memberPwConfirm.addEventListener("input", () => {
    if(checkObj.memberPw) checkPw();
    else checkObj.memberPwConfirm = false;
});


// -------------------------------------------------------------
/* 6. 전화번호 유효성 검사 */
const memberTel = document.querySelector("#memberTel");
const telMessage = document.querySelector("#telMessage");

memberTel.addEventListener("input", e => {
    const inputTel = e.target.value;
    if(inputTel.trim().length === 0){
        telMessage.innerText = "전화번호를 입력해주세요.(- 제외)";
        telMessage.classList.remove("confirm", "error");
        checkObj.memberTel = false;
        return;
    }

    const regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
    if(!regExp.test(inputTel)){
        telMessage.innerText = "유효하지 않은 전화번호 형식입니다.";
        telMessage.classList.add("error");
        telMessage.classList.remove("confirm");
        checkObj.memberTel = false;
        return;
    }

    telMessage.innerText = "유효한 전화번호 형식입니다.";
    telMessage.classList.add("confirm");
    telMessage.classList.remove("error");
    checkObj.memberTel = true;
});


// -------------------------------------------------------------
/* 7. 주민등록번호 유효성 검사 (길이 체크 및 포커스 이동) */
const memberRrn1 = document.querySelector("#memberRrn1");
const memberRrn2 = document.querySelector("#memberRrn2");
const rrnMessage = document.querySelector("#rrnMessage");

// 주민번호 앞자리
memberRrn1.addEventListener("input", (e) => {
    // 숫자만 입력받게
    e.target.value = e.target.value.replace(/[^0-9]/g, '');

    if(memberRrn1.value.length === 6){
        if(memberRrn2.value.length === 7){
            checkObj.memberRrn = true;
            rrnMessage.innerText = "유효한 형식입니다.";
            rrnMessage.classList.add("confirm");
            rrnMessage.classList.remove("error");
        } else {
            checkObj.memberRrn = false;
             memberRrn2.focus(); // 앞자리 다 치면 뒷자리로 이동
        }
    } else {
        checkObj.memberRrn = false;
    }
});

// 주민번호 뒷자리
memberRrn2.addEventListener("input", (e) => {
    // 숫자만 입력받게
    e.target.value = e.target.value.replace(/[^0-9]/g, ''); // 보안상 type=password지만 숫자가 아닌 값 방지
    
    if(memberRrn1.value.length === 6 && memberRrn2.value.length === 7){
        rrnMessage.innerText = "유효한 형식입니다.";
        rrnMessage.classList.add("confirm");
        rrnMessage.classList.remove("error");
        checkObj.memberRrn = true;
    } else {
        rrnMessage.innerText = "주민등록번호를 모두 입력해주세요.";
        rrnMessage.classList.remove("confirm");
        rrnMessage.classList.add("error");
        checkObj.memberRrn = false;
    }
});


// -------------------------------------------------------------
/* 최종 회원가입 버튼 클릭 시 검사 */
const signUpForm = document.querySelector("#signUpForm");

signUpForm.addEventListener("submit", e => {
    
    for(let key in checkObj){
        if(!checkObj[key]){
            let str;
            switch(key){
                case "memberName" : str = "이름이 유효하지 않습니다"; break;
                case "memberId"   : str = "아이디가 유효하지 않습니다"; break;
                case "memberNickname" : str = "닉네임이 유효하지 않습니다"; break;
                case "memberEmail" : str = "이메일이 유효하지 않습니다"; break;
                case "authKey" : str = "이메일 인증이 완료되지 않았습니다"; break;
                case "memberPw" : str = "비밀번호가 유효하지 않습니다"; break;
                case "memberPwConfirm" : str = "비밀번호 확인이 일치하지 않습니다"; break;
                case "memberTel" : str = "전화번호가 유효하지 않습니다"; break;
                case "memberRrn" : str = "주민등록번호를 모두 입력해주세요"; break;
            }
            alert(str);
            
            // 해당 요소로 포커스 이동 (주민번호는 예외 처리)
            if(key === "memberRrn") document.getElementById("memberRrn1").focus();
            else document.getElementById(key).focus();
            
            e.preventDefault();
            return;
        }
    }
});