/**
 * 문자열의 바이트 길이를 계산 (UTF-8 기준)
 */
function getByteLength(s) {
  if (s == null || s.length === 0) return 0;
  return new TextEncoder().encode(s).length;
}

// document.ready 대신 DOMContentLoaded 사용
document.addEventListener("DOMContentLoaded", function () {
  const MAX_BYTE = 4000;
  const profileImg = document.getElementById("profileImg");
  const imageInput = document.getElementById("imageInput");
  const deleteImage = document.getElementById("deleteImage");
  const defaultImageUrl = `/images/footer-logo.png`;
  const summernoteElement = document.getElementById("summernote");

  // 1. Summernote 초기화 (라이브러리 특성상 jQuery 객체 필요)
  $(summernoteElement).summernote({
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
        const byteCounter = document.getElementById("current-byte");
        if (byteCounter) {
          byteCounter.innerText = currentByte;
          byteCounter.style.color = currentByte > MAX_BYTE ? "red" : "black";
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

  // 2. 폼 제출 유효성 검사
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

      // 봉사 기간 유효성 검사
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
          startVal === "" ? sdate.focus() : edate.focus();
          return false;
        }

        if (startVal !== "" && endVal !== "" && startVal > endVal) {
          e.preventDefault();
          alert("시작 날짜는 종료 날짜보다 빠르거나 같아야 합니다.");
          sdate.focus();
          return false;
        }
      }

      // 내용 검사 (Summernote API 호출은 jQuery 방식 유지)
      const contents = $(summernoteElement).summernote("code");
      const pureText = contents
        .replace(/<[^>]*>?/g, "")
        .replace(/&nbsp;/g, "")
        .trim();

      const hasImage = contents.includes("<img");

      if (pureText.length === 0 && !hasImage) {
        e.preventDefault();
        alert("내용 또는 사진을 입력해주세요!");
        $(summernoteElement).summernote("focus");
        return false;
      }

      // 용량 검사
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

  // 비밀글 체크박스 제어
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

  // 썸네일 업로드 로직 (이벤트 바인딩 변경)
  if (imageInput) {
    let previousImage = profileImg ? profileImg.src : defaultImageUrl;

    imageInput.addEventListener("change", () => {
      const file = imageInput.files[0];
      if (file) {
        if (file.size <= 1024 * 1024 * 5) {
          const newImageUrl = URL.createObjectURL(file);
          profileImg.src = newImageUrl;
          previousImage = newImageUrl;
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
        previousImage = defaultImageUrl;
        deleteImage.style.display = "none";
      });
    }
  }
});

/**
 * 3. 에디터 내 본문 이미지 업로드 ($.ajax를 fetch API로 변경)
 */
async function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  try {
    const response = await fetch("/editBoard/image/upload", {
      method: "POST",
      body: formData,
    });

    if (response.ok) {
      const res = await response.json();
      // Summernote 내부 메서드 실행은 jQuery 객체 필요
      $("#summernote").summernote("insertImage", res.url);
    } else {
      throw new Error("서버 응답 오류");
    }
  } catch (error) {
    console.error(error);
    alert("이미지 업로드 중 오류가 발생했습니다.");
  }
}
