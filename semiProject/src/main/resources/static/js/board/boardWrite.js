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

  // Summernote 초기화
  $("#summernote").summernote({
    width: 1130,
    height: 500,
    lang: "ko-KR",
    placeholder: "내용을 입력해주세요.",
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
    fontNamesIgnoreCheck: ["맑은 고딕", "궁서", "굴림체", "돋움체", "바탕체"],
    toolbar: [
      ["fontname", ["fontname"]],
      ["fontsize", ["fontsize"]],
      ["style", ["bold", "italic", "underline", "strikethrough", "clear"]],
      ["color", ["forecolor", "color"]],
      ["para", ["ul", "ol", "paragraph"]],
      ["height", ["height"]],
      ["insert", ["picture", "link"]],
      ["view", ["fullscreen", "help"]],
    ],
    callbacks: {
      onChange: function (contents) {
        const currentByte = getByteLength(contents);
        const $byteCounter = $("#current-byte");
        if ($byteCounter.length > 0) {
          $byteCounter.text(currentByte);
          $byteCounter.css("color", currentByte > MAX_BYTE ? "red" : "black");
        }
      },
      onImageUpload: function (files) {
        for (let i = 0; i < files.length; i++) {
          uploadImage(files[i]);
        }
      },
    },
  });

  // 썸네일 삭제 버튼 노출 여부
  if (profileImg && deleteImage) {
    if (profileImg.getAttribute("src") !== defaultImageUrl) {
      deleteImage.style.display = "flex";
    }
  }

  // -------------------------------------------------------------------------
  // 폼 제출 시 유효성 검사 (비밀번호 로직 제거 버전)
  // -------------------------------------------------------------------------
  const form = document.querySelector("#summernote-write");
  if (form) {
    form.addEventListener("submit", (e) => {
      // 1. 제목 검사
      const boardTitle = document.querySelector("#board-title");
      if (boardTitle.value.trim() === "") {
        e.preventDefault();
        alert("제목을 입력해주세요!");
        boardTitle.focus();
        return false;
      }

      // [삭제됨] 비밀글 체크 시 비밀번호 검사 로직은 이제 필요 없음 (단순 Y/N 처리)

      // 2. 봉사 기간 유효성 검사 (시작일/종료일 세트 체크)
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

      // 3. 내용 검사 (텍스트가 없어도 이미지가 있으면 허용)
      const contents = $("#summernote").summernote("code");
      const pureText = contents
        .replace(/<[^>]*>?/g, "")
        .replace(/&nbsp;/g, "")
        .trim();

      const hasImage = contents.includes("<img"); // 에디터 내 이미지 포함 여부

      if (pureText.length === 0 && !hasImage) {
        e.preventDefault();
        alert("내용 또는 사진을 입력해주세요!");
        $("#summernote").summernote("focus");
        return false;
      }

      // 4. 용량(바이트) 검사
      const currentByte = getByteLength(contents);
      if (currentByte > MAX_BYTE) {
        e.preventDefault();
        alert(
          `용량이 초과되었습니다! (현재: ${currentByte} / 최대: ${MAX_BYTE} bytes)\n내용을 조금 줄여주세요.`
        );
        return false;
      }
    });
  }

  // 비밀글 체크박스 노출 제어 (문의게시판 등)
  const secretWrapper = document.querySelector("#secret-wrapper");
  function toggleSecret() {
    if (secretWrapper) {
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

  // 썸네일 이미지 업로드 미리보기 로직
  if (imageInput) {
    let previousImage = profileImg ? profileImg.src : defaultImageUrl;
    let previousFile = null;

    imageInput.addEventListener("change", () => {
      const file = imageInput.files[0];
      if (file) {
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
        }
      }
    });

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
 * 에디터 내 본문 이미지 업로드 (AJAX)
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
      $("#summernote").summernote("insertImage", res.url);
    },
    error: function () {
      alert("이미지 업로드 중 오류가 발생했습니다.");
    },
  });
}
