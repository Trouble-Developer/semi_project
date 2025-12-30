/* 프로필 이미지가 변경되었는지 확인하는 플래그 (선택사항) */
let headerPreview = null; 

/* 초기 주소 상태 저장 (변경 유무 판단용) */
const initPostcode = document.getElementById("postcode").value;
const initAddress = document.getElementById("address").value;
const initDetailAddress = document.getElementById("detailAddress").value;

/* [추가] 유효성 검사 상태 저장 객체 
   - memberPw: 비밀번호는 변경할 때만 검사하니까 기본값 true (변경 안 하면 통과)
   - memberTel: 기존 전화번호는 이미 유효하다고 가정하고 true (변경 시 검사)
*/
const checkObj = {
  "memberPw" : true,
  "pwConfirm" : true,
  "memberTel" : true
};


// 1. 프로필 이미지 미리보기 & 용량 체크
const profileImgInput = document.getElementById("profileImgInput"); 
const profilePreview = document.getElementById("profilePreview");   

if(profileImgInput != null) {
  profileImgInput.addEventListener("change", function(e) {
    const file = e.target.files[0]; 
    if(file != undefined) { 
      const originalImg = profilePreview.getAttribute("src");
      const maxSize = 5 * 1024 * 1024; 
      if(file.size > maxSize){
        alert("이미지 용량은 5MB를 초과할 수 없습니다.");
        profileImgInput.value = ""; 
        return;
      }
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = function(e) {
        profilePreview.setAttribute("src", e.target.result);
      }
    } else { 
      profilePreview.setAttribute("src", originalImg);
    }
  });
}

// 2. 다음 주소 API
const searchAddressBtn = document.getElementById("searchAddressBtn");
if(searchAddressBtn != null){
  searchAddressBtn.addEventListener("click", execDaumPostcode);
}

function execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function(data) {
      var addr = '';
      if (data.userSelectedType === 'R') addr = data.roadAddress;
      else addr = data.jibunAddress;

      document.getElementById('postcode').value = data.zonecode;
      document.getElementById("address").value = addr;
      document.getElementById("detailAddress").value = "";
      document.getElementById("detailAddress").focus();
    }
  }).open();
}

// 3. 유효성 검사
const memberPw = document.getElementsByName("memberPw")[0];
const pwConfirm = document.getElementById("pwConfirm");
const memberTel = document.getElementsByName("memberTel")[0];

// 1) 비밀번호 유효성 검사 (영어,숫자,특수문자 6~20글자)
if(memberPw != null && pwConfirm != null){
  memberPw.addEventListener("input", function(){
    const val = memberPw.value.trim();
    
    // 입력 안 했으면 검사 안 함 (비밀번호 변경 안 하는 경우)
    if(val.length === 0){
      checkObj.memberPw = true;
      checkObj.pwConfirm = true; 
      // 만약 시각적 효과(빨간글씨 등) 넣었으면 여기서 초기화 해줘야 함
      return;
    }

    // 정규표현식: 영어, 숫자, 특수문자(!,@,#,-,_) 포함 6~20글자
    const regExp = /^[a-zA-Z0-9!@#\-_]{6,20}$/;

    if(regExp.test(val)){
      checkObj.memberPw = true;
    } else {
      checkObj.memberPw = false;
    }

    // 비밀번호 확인이랑 일치하는지 체크
    if(pwConfirm.value.trim().length > 0){
        checkObj.pwConfirm = (val === pwConfirm.value.trim());
    }
  });

  // 2) 비밀번호 확인 유효성 검사
  pwConfirm.addEventListener("input", function(){
    const val = pwConfirm.value.trim();
    
    if(checkObj.memberPw){ // 비밀번호가 유효할 때만 확인 진행
      checkObj.pwConfirm = (val === memberPw.value.trim());
    } else {
      checkObj.pwConfirm = false;
    }
  });
}

// 3) 전화번호 유효성 검사 (01012345678 형식)
if(memberTel != null){
  memberTel.addEventListener("input", function(){
    const val = memberTel.value.trim();
    
    // 정규표현식: 01로 시작, 3~4자리 중간, 4자리 끝, 총 10~11자리 숫자만
    const regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;

    if(regExp.test(val)){
      checkObj.memberTel = true;
    } else {
      checkObj.memberTel = false;
    }
  });
}


// 3. 폼 제출 시 유효성 검사 & 변경사항 체크
const profileFrm = document.getElementById("profileFrm");

if(profileFrm != null) {
  profileFrm.addEventListener("submit", function(e) {

    const currentPw = document.getElementById("currentPw"); 
    
    // 1) 프로필 이미지 변경 체크
    const isImgChanged = profileImgInput.files.length > 0;

    // 2) 비밀번호 변경 체크
    const isPwChanged = memberPw.value.trim().length > 0;

    // 3) 주소 변경 체크
    const currentPostcode = document.getElementById("postcode").value;
    const currentAddress = document.getElementById("address").value;
    const currentDetailAddress = document.getElementById("detailAddress").value;

    const isAddressChanged = (initPostcode != currentPostcode) ||
                             (initAddress != currentAddress) || 
                             (initDetailAddress != currentDetailAddress);

    // A. 변경된 게 하나도 없으면 막기
    if( !isImgChanged && !isPwChanged && !isAddressChanged && 
        (memberTel.defaultValue == memberTel.value) ) { // 전화번호도 그대로면
      alert("수정된 정보가 없습니다.");
      e.preventDefault(); 
      return;
    }

    
    // 새 비밀번호를 입력했는데 유효하지 않은 경우
    if( isPwChanged && !checkObj.memberPw ){
        alert("비밀번호 형식이 유효하지 않습니다. (영어,숫자,특수문자 6~20자)");
        memberPw.focus();
        e.preventDefault();
        return;
    }

    // 새 비밀번호랑 확인이랑 다른 경우
    if( isPwChanged && !checkObj.pwConfirm ){
        alert("비밀번호 확인이 일치하지 않습니다.");
        pwConfirm.focus();
        e.preventDefault();
        return;
    }

    // 전화번호 형식이 이상한 경우
    if( !checkObj.memberTel ){
        alert("전화번호 형식이 올바르지 않습니다. (- 없이 숫자만 입력)");
        memberTel.focus();
        e.preventDefault();
        return;
    }


    // B. 현재 비밀번호 입력 필수 체크
    if(currentPw.value.trim().length == 0) {
      alert("정보를 수정하려면 현재 비밀번호를 입력해주세요.");
      currentPw.focus();
      e.preventDefault();
      return;
    }
  });
}