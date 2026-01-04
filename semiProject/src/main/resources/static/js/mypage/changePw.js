const checkObj = {
    "newPw"        : false,
    "newPwConfirm" : false
};

const newPw = document.getElementById("newPw");
const newPwConfirm = document.getElementById("newPwConfirm");
const pwMessage = document.getElementById("pwMessage");
const pwConfirmMessage = document.getElementById("pwConfirmMessage");

// 새 비밀번호 유효성 검사
newPw.addEventListener("input", () => {
    if(newPw.value.trim().length == 0){
        pwMessage.innerText = "영어,숫자,특수문자(!@#$%^&*) 6~20글자 사이";
        pwMessage.classList.remove("confirm", "error");
        checkObj.newPw = false;
        return;
    }

    // 강력한 정규식
    const regExp = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{6,20}$/;

    if(regExp.test(newPw.value)){
        pwMessage.innerText = "유효한 비밀번호 형식입니다.";
        pwMessage.classList.add("confirm");
        pwMessage.classList.remove("error");
        checkObj.newPw = true;
    } else {
        pwMessage.innerText = "비밀번호 형식이 유효하지 않습니다.";
        pwMessage.classList.add("error");
        pwMessage.classList.remove("confirm");
        checkObj.newPw = false;
    }
    
    // 비밀번호 확인란이 작성되어 있다면 일치 검사
    if(newPwConfirm.value.trim().length > 0){
        checkPw();
    }
});

// 비밀번호 확인 일치 검사 함수
const checkPw = () => {
    if(newPw.value === newPwConfirm.value){
        pwConfirmMessage.innerText = "비밀번호가 일치합니다.";
        pwConfirmMessage.classList.add("confirm");
        pwConfirmMessage.classList.remove("error");
        checkObj.newPwConfirm = true;
    } else{
        pwConfirmMessage.innerText = "비밀번호가 일치하지 않습니다.";
        pwConfirmMessage.classList.add("error");
        pwConfirmMessage.classList.remove("confirm");
        checkObj.newPwConfirm = false;
    }
};

newPwConfirm.addEventListener("input", checkPw);

// 폼 제출 시 유효성 검사
document.getElementById("changePwForm").addEventListener("submit", e => {
    // 현재 비밀번호 입력했는지 확인
    const currentPw = document.getElementById("currentPw");
    if(currentPw.value.trim().length == 0){
        alert("현재 비밀번호를 입력해주세요.");
        currentPw.focus();
        e.preventDefault();
        return;
    }

    // 새 비밀번호 유효성 검사 확인
    if(!checkObj.newPw){
        alert("새 비밀번호가 유효하지 않습니다.");
        newPw.focus();
        e.preventDefault();
        return;
    }
    
    // 비번 확인 일치 확인
    if(!checkObj.newPwConfirm){
        alert("새 비밀번호 확인이 일치하지 않습니다.");
        newPwConfirm.focus();
        e.preventDefault();
        return;
    }
});