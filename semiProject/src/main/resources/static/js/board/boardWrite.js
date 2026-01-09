function getByteLength(s) {
  if (s == null || s.length === 0) return 0;
  return new TextEncoder().encode(s).length;
}

document.addEventListener("DOMContentLoaded", function () {
  const MAX_BYTE = 4000;
  const profileImg = document.getElementById("profileImg");
  const imageInput = document.getElementById("imageInput");
  const deleteImage = document.getElementById("deleteImage");
  const defaultImageUrl = `/images/footer-logo.png`;
  const summernoteElement = document.getElementById("summernote");

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

  if (profileImg && deleteImage) {
    if (profileImg.getAttribute("src") !== defaultImageUrl) {
      deleteImage.style.display = "flex";
    }
  }

  const form = document.querySelector("#summernote-write");
  if (form) {
    form.addEventListener("submit", (e) => {
      const boardTitle = document.querySelector("#board-title");
      if (boardTitle.value.trim() === "") {
        e.preventDefault();
        alert("제목을 입력해주세요!");
        boardTitle.focus();
        return false;
      }

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

  if (imageInput) {
    let previousImage = profileImg ? profileImg.src : defaultImageUrl;

    imageInput.addEventListener("change", () => {
      const file = imageInput.files[0];
      if (file) {
        const maxSize = 1024 * 1024 * 10;
        if (file.size <= maxSize) {
          const newImageUrl = URL.createObjectURL(file);
          profileImg.src = newImageUrl;
          previousImage = newImageUrl;
          if (deleteImage) deleteImage.style.display = "flex";
        } else {
          alert("10MB 이하의 이미지를 선택해주세요!");
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
      $("#summernote").summernote("insertImage", res.url);
    } else {
      if (response.status === 413) {
        alert("이미지 용량이 너무 큽니다.");
      } else {
        throw new Error("서버 응답 오류");
      }
    }
  } catch (error) {
    console.error(error);
    alert("이미지 업로드 중 오류가 발생했습니다.");
  }
}
