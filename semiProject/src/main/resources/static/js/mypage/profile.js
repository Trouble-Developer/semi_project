/* 프로필 이미지가 변경되었는지 확인하는 플래그 (선택사항) */
let headerPreview = null; 

/* 초기 주소 상태 저장 (변경 유무 판단용) */
const initPostcode = document.getElementById("postcode").value;
const initAddress = document.getElementById("address").value;
const initDetailAddress = document.getElementById("detailAddress").value;


// 1. 프로필 이미지 미리보기 & 용량 체크
const profileImgInput = document.getElementById("profileImgInput"); // input type="file"
const profilePreview = document.getElementById("profilePreview");   // img 태그

if(profileImgInput != null) {
  profileImgInput.addEventListener("change", function(e) {
    
    const file = e.target.files[0]; // 선택된 파일

    if(file != undefined) { // 파일이 선택되었을 때

			const originalImage = profilePreview.getAttribute("src");
      
      // [추가] 이미지 용량 제한 (5MB) - 서버 터지기 싫으면 이거 필수다
      const maxSize = 5 * 1024 * 1024; // 5MB
      if(file.size > maxSize){
        alert("이미지 용량은 5MB를 초과할 수 없습니다.");
        profileImgInput.value = ""; // 선택 초기화
        return;
      }

      const reader = new FileReader();
      reader.readAsDataURL(file);

      reader.onload = function(e) {
        profilePreview.setAttribute("src", e.target.result);
      }
      
    } else { 
			// 파일 선택 취소 시 원래 이미지로 복원
      profilePreview.setAttribute("src", originalImage);
    }
  });
}


// 2. 다음 주소 API (변경 버튼 클릭 시 동작)
const searchAddressBtn = document.getElementById("searchAddressBtn");

if(searchAddressBtn != null){
  searchAddressBtn.addEventListener("click", execDaumPostcode);
}

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
      
      document.getElementById("detailAddress").value = "";
      document.getElementById("detailAddress").focus();
    }
  }).open();
}


// 3. 폼 제출 시 유효성 검사 & 변경사항 체크 (핵심 로직)
const profileFrm = document.getElementById("profileFrm");

if(profileFrm != null) {
  profileFrm.addEventListener("submit", function(e) {

    const currentPw = document.getElementById("currentPw"); // [추가] 현재 비밀번호
    const memberPw = document.getElementsByName("memberPw")[0]; // 새 비밀번호
    const pwConfirm = document.getElementById("pwConfirm");     // 새 비밀번호 확인
    
    
    // --- 변경사항 존재 여부 확인 ---
    
    // 1) 프로필 이미지 변경: 파일이 업로드 되었는가?
    const isImageChanged = profileImgInput.files.length > 0;

    // 2) 비밀번호 변경: 새 비밀번호 값을 입력 했는가?
    const isPwChanged = memberPw.value.trim().length > 0;

    // 3) 주소 변경: 초기값이랑 현재 값이 다른가?
    const currentPostcode = document.getElementById("postcode").value;
    const currentAddress = document.getElementById("address").value;
    const currentDetailAddress = document.getElementById("detailAddress").value;

    const isAddressChanged = (initPostcode != currentPostcode) ||
                             (initAddress != currentAddress) || 
                             (initDetailAddress != currentDetailAddress);


    // A. 셋 중 하나라도 변경된 게 없다면 제출 막기
    if( !isImageChanged && !isPwChanged && !isAddressChanged ) {
      alert("수정된 정보가 없습니다.");
      e.preventDefault(); 
      return;
    }

    // B. [추가] 정보 수정 시 현재 비밀번호 입력 필수 체크
    if(currentPw.value.trim().length == 0) {
      alert("정보를 수정하려면 현재 비밀번호를 입력해주세요.");
      currentPw.focus();
      e.preventDefault();
      return;
    }

    // C. 새 비밀번호 유효성 검사 (입력했을 경우에만)
    if(isPwChanged) {
      if(memberPw.value != pwConfirm.value) {
        alert("새 비밀번호가 일치하지 않습니다.");
        memberPw.focus();
        e.preventDefault();
        return;
      }
    }

    // 여기까지 문제 없으면 form submit 진행됨
  });
}