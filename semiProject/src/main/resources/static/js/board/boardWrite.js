/**
 * 문자열의 바이트 길이를 계산 (UTF-8 기준)
 */
function getByteLength(s) {
  if (s == null || s.length === 0) return 0;
  return new TextEncoder().encode(s).length;
}

$(document).ready(function () {
  const MAX_BYTE = 4000;
  const profileImg = document.getElementById("profileImg");
  const imageInput = document.getElementById("imageInput");
  const deleteImage = document.getElementById("deleteImage");
  const defaultImageUrl = `/images/footer-logo.png`;

  // --- 1. 섬머노트(Summernote) 초기화 설정 ---
  $("#summernote").summernote({
    width: 1130,
    height: 500,
    lang: "ko-KR",
    placeholder: "내용을 입력해주세요.",

    // 사용 가능한 폰트 목록
    fontNames: [
      "Arial",
      "Arial Black",
      "Comic Sans MS",
      "Courier New",
      "맑은 고딕",
      "궁서",
      "굴림체",
      "돋움체",
      "바탕체",
    ],
    // 시스템에 해당 폰트가 있는지 확인하지 않고 바로 목록에 노출
    fontNamesIgnoreCheck: ["맑은 고딕", "궁서", "굴림체", "돋움체", "바탕체"],

    // 툴바 구성 정의
    toolbar: [
      ["fontname", ["fontname"]], // 글꼴 설정
      ["fontsize", ["fontsize"]], // 글자 크기
      ["style", ["bold", "italic", "underline", "strikethrough", "clear"]], // 서식
      ["color", ["forecolor", "color"]], // 글자색/배경색
      ["para", ["ul", "ol", "paragraph"]], // 정렬/목록
      ["height", ["height"]], // 줄간격
      ["insert", ["picture", "link"]], // 이미지/링크/동영상
      ["view", ["fullscreen", "help"]], // 도구
    ],

    callbacks: {
      // 내용이 변경될 때마다 바이트 수 체크
      onChange: function (contents) {
        const currentByte = getByteLength(contents);
        const $byteCounter = $("#current-byte");
        if ($byteCounter.length > 0) {
          $byteCounter.text(currentByte);
          $byteCounter.css("color", currentByte > MAX_BYTE ? "red" : "black");
        }
      },
      // 이미지 업로드 시 실행
      onImageUpload: function (files) {
        for (let i = 0; i < files.length; i++) {
          uploadImage(files[i]);
        }
      },
    },
  });

  // --- 2. 썸네일 이미지 삭제 버튼 초기 상태 제어 ---
  if (profileImg && deleteImage) {
    if (profileImg.getAttribute("src") !== defaultImageUrl) {
      deleteImage.style.display = "flex";
    }
  }

  // --- 3. 폼 제출(Submit) 유효성 검사 ---
  const form = document.querySelector("#summernote-write");
  if (form) {
    form.addEventListener("submit", (e) => {
      // 제목 검사
      const boardTitle = document.querySelector("#board-title");
      if (boardTitle.value.trim() === "") {
        e.preventDefault();
        alert("제목을 입력해주세요!");
        boardTitle.focus();
        return false;
      }

      // 비밀글 체크 시 비밀번호 검사
      const secretCheck = document.querySelector("#checkbox");
      const boardPw = document.querySelector("#board-pw");
      if (secretCheck && secretCheck.checked) {
        if (boardPw.value.trim() === "") {
          e.preventDefault();
          alert("비밀번호를 입력해주세요!");
          boardPw.focus();
          return false;
        }
      }

      // 봉사 기간 유효성 검사 (시작일/종료일 세트 체크)
      const sdate = document.querySelector("#sdate");
      const edate = document.querySelector("#edate");
      if (sdate && edate) {
        const startVal = sdate.value.trim();
        const endVal = edate.value.trim();
        if (
          (startVal !== "" && endVal === "") ||
          (startVal === "" && endVal !== "")
        ) {
          e.preventDefault();
          alert(
            "봉사 기간을 설정하시려면 시작 날짜와 종료 날짜를 모두 입력하셔야 합니다."
          );
          if (startVal === "") sdate.focus();
          else edate.focus();
          return false;
        }
        if (startVal !== "" && endVal !== "" && startVal > endVal) {
          e.preventDefault();
          alert("시작 날짜는 종료 날짜보다 빠르거나 같아야 합니다.");
          sdate.focus();
          return false;
        }
      }

      // 섬머노트 내용 유효성 및 용량 검사
      const contents = $("#summernote").summernote("code");
      const currentByte = getByteLength(contents);
      if ($("#summernote").summernote("isEmpty")) {
        e.preventDefault();
        alert("내용을 입력해주세요!");
        return false;
      }
      if (currentByte > MAX_BYTE) {
        e.preventDefault();
        alert(
          `용량이 초과되었습니다! (현재: ${currentByte} / 최대: ${MAX_BYTE} bytes)\n내용을 조금 줄여주세요.`
        );
        return false;
      }
    });
  }

  // --- 4. 게시판 유형에 따른 비밀글 영역 노출 제어 ---
  const secretWrapper = document.querySelector("#secret-wrapper");
  function toggleSecret() {
    if (secretWrapper) {
      // boardCode가 5(예: 문의 게시판)인 경우에만 비밀글 활성화
      if (typeof boardCode !== "undefined" && boardCode == 5) {
        secretWrapper.style.display = "block";
      } else {
        secretWrapper.style.display = "none";
        const checkbox = document.querySelector("#checkbox");
        if (checkbox) checkbox.checked = false;
      }
    }
  }
  toggleSecret();

  // --- 5. 썸네일 이미지 업로드 및 미리보기 로직 ---
  if (imageInput) {
    let previousImage = profileImg ? profileImg.src : defaultImageUrl;
    let previousFile = null;

    imageInput.addEventListener("change", () => {
      const file = imageInput.files[0];
      if (file) {
        // 용량 제한 (5MB)
        if (file.size <= 1024 * 1024 * 5) {
          const newImageUrl = URL.createObjectURL(file);
          profileImg.src = newImageUrl;
          previousImage = newImageUrl;
          previousFile = file;
          if (deleteImage) deleteImage.style.display = "flex";
        } else {
          alert("5MB 이하의 이미지를 선택해주세요!");
          imageInput.value = "";
          profileImg.src = previousImage;
          if (previousFile) {
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(previousFile);
            imageInput.files = dataTransfer.files;
          }
        }
      } else {
        profileImg.src = previousImage;
        if (previousFile) {
          const dataTransfer = new DataTransfer();
          dataTransfer.items.add(previousFile);
          imageInput.files = dataTransfer.files;
        }
      }
    });

    // 썸네일 삭제 버튼 클릭 이벤트
    if (deleteImage) {
      deleteImage.addEventListener("click", () => {
        imageInput.value = "";
        profileImg.src = defaultImageUrl;
        previousFile = null;
        previousImage = defaultImageUrl;
        deleteImage.style.display = "none";
      });
    }
  }
});

/**
 * 에디터 내 본문 이미지 업로드 (서버 전송)
 */
function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);
  $.ajax({
    url: "/editBoard/image/upload",
    type: "POST",
    data: formData,
    contentType: false,
    processData: false,
    success: function (res) {
      // 서버에서 반환한 이미지 URL을 에디터에 삽입
      $("#summernote").summernote("insertImage", res.url);
    },
    error: function () {
      alert("이미지 업로드 중 오류가 발생했습니다.");
    },
  });
}
