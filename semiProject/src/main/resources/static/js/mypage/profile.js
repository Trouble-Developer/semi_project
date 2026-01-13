/* 프로필 이미지가 변경되었는지 확인하는 플래그 */
let headerPreview = null; 

/* 초기값 저장 (변경 유무 판단용) */
// 주소
const initPostcode = document.getElementById("postcode").value;
const initAddress = document.getElementById("address").value;
const initDetailAddress = document.getElementById("detailAddress").value;

// 전화번호 (HTML에 value 속성으로 초기값이 있다고 가정)
const memberTel = document.getElementById("memberTel");
const initTel = memberTel.value;


/* [수정] 유효성 검사 상태 저장 객체 
  - 닉네임, 비밀번호 다 빠지고 전화번호 하나만 남음
*/
const checkObj = {
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


// 3. 유효성 검사 - 전화번호
if(memberTel != null){
  memberTel.addEventListener("input", function(){
    
    // 1) 입력되자마자 숫자 아닌 건 싹 지워버림 (한글, 영어, 특수문자 차단)
    this.value = this.value.replace(/[^0-9]/g, "");
    
    // 2) 그 상태에서 유효성 검사 진행
    const val = this.value.trim();
    
    // 정규표현식: 01로 시작, 3~4자리 중간, 4자리 끝 (총 10~11자리)
    const regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;

    if(regExp.test(val)){
      checkObj.memberTel = true;
    } else {
      checkObj.memberTel = false;
    }
  });
}


// 4. 폼 제출 시 유효성 검사 & 변경사항 체크
const profileFrm = document.getElementById("profileFrm");

if(profileFrm != null) {
  profileFrm.addEventListener("submit", function(e) {

    // 1) 변경 여부 체크
    // 이미지 변경 체크
    const isImgChanged = profileImgInput.files.length > 0;
    
    // 주소 변경 체크
    const currentPostcode = document.getElementById("postcode").value;
    const currentAddress = document.getElementById("address").value;
    const currentDetailAddress = document.getElementById("detailAddress").value;

    const isAddressChanged = (initPostcode != currentPostcode) ||
                             (initAddress != currentAddress) || 
                             (initDetailAddress != currentDetailAddress);

    // 전화번호 변경 체크
    const isTelChanged = (initTel != memberTel.value);


    // 변경된 게 하나도 없으면 막기
    if( !isImgChanged && !isAddressChanged && !isTelChanged ) { 
      alert("수정된 정보가 없습니다.");
      e.preventDefault(); 
      return;
    }

    // 2) 유효성 검사 실패 시 막기
    
    // 전화번호 형식이 이상한 경우
    if( !checkObj.memberTel ){
        alert("전화번호 형식이 올바르지 않습니다. (- 없이 숫자만 입력)");
        memberTel.focus();
        e.preventDefault();
        return;
    }

    // 주소 상세정보 체크 (우편번호는 있는데 상세주소가 비어있을 때)
    const postcodeVal = document.getElementById("postcode").value.trim();
    const detailAddressVal = document.getElementById("detailAddress").value.trim();

    if(postcodeVal.length > 0 && detailAddressVal.length == 0) {
        alert("상세 주소를 입력해주세요.");
        document.getElementById("detailAddress").focus();
        e.preventDefault();
        return;
    }

  });
}