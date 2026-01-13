// ========================================
// 회원 탈퇴 JavaScript
// ========================================

// 요소 가져오기
const memberPw = document.getElementById('memberPw');
const memberPwConfirm = document.getElementById('memberPwConfirm');
const pwConfirmMessage = document.getElementById('pwConfirmMessage');
const agreeCheck = document.getElementById('agreeCheck');
const submitBtn = document.getElementById('submitBtn');
const withdrawForm = document.getElementById('withdrawForm');

/**
 * 비밀번호 확인 검사
 */
memberPwConfirm.addEventListener('input', function() {
    // 비밀번호를 먼저 입력했는지 확인
    if(memberPw.value.trim().length === 0) {
        pwConfirmMessage.innerText = '비밀번호를 먼저 입력해주세요.';
        pwConfirmMessage.classList.remove('confirm');
        pwConfirmMessage.classList.add('error');
        checkSubmitEnabled();
        return;
    }
    
    // 비밀번호 일치 여부 확인
    if(memberPw.value === memberPwConfirm.value) {
        pwConfirmMessage.innerText = '비밀번호가 일치합니다.';
        pwConfirmMessage.classList.remove('error');
        pwConfirmMessage.classList.add('confirm');
    } else {
        pwConfirmMessage.innerText = '비밀번호가 일치하지 않습니다.';
        pwConfirmMessage.classList.remove('confirm');
        pwConfirmMessage.classList.add('error');
    }
    
    checkSubmitEnabled();
});

/**
 * 동의 체크박스 변경 시 버튼 활성화 체크
 */
agreeCheck.addEventListener('change', checkSubmitEnabled);

/**
 * 비밀번호 입력 시에도 체크
 */
memberPw.addEventListener('input', function() {
    // 비밀번호 확인란에 값이 있으면 다시 체크
    if(memberPwConfirm.value.length > 0) {
        memberPwConfirm.dispatchEvent(new Event('input'));
    }
    checkSubmitEnabled();
});

/**
 * 제출 버튼 활성화 조건 체크
 */
function checkSubmitEnabled() {
    const isPwMatch = memberPw.value.length > 0 && 
                     memberPw.value === memberPwConfirm.value;
    const isAgreed = agreeCheck.checked;
    
    // 두 조건 모두 만족해야 버튼 활성화
    submitBtn.disabled = !(isPwMatch && isAgreed);
}

/**
 * 폼 제출 시 최종 확인
 */
withdrawForm.addEventListener('submit', function(e) {
    e.preventDefault();
    
    // 1. 동의 체크 확인
    if(!agreeCheck.checked) {
        alert('안내 사항에 동의해주세요.');
        return;
    }
    
    // 2. 비밀번호 입력 확인
    if(memberPw.value.trim().length === 0) {
        alert('비밀번호를 입력해주세요.');
        memberPw.focus();
        return;
    }
    
    // 3. 비밀번호 일치 확인
    if(memberPw.value !== memberPwConfirm.value) {
        alert('비밀번호가 일치하지 않습니다.');
        memberPwConfirm.focus();
        return;
    }
    
    // 4. 최종 확인
    if(confirm('정말로 탈퇴하시겠습니까?\n\n탈퇴 후에는 복구할 수 없습니다.')) {
        this.submit();
    }
});