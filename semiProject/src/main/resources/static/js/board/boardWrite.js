const form = document.querySelector("#summernote-write");
const secretWrapper = document.querySelector("#secret-wrapper");

form.addEventListener("submit", (e) => {
  const boardTitle = document.querySelector("#board-title");
  const secretCheck = document.querySelector("#checkbox");
  const boardPw = document.querySelector("#board-pw");
  const sdate = document.querySelector("#sdate");
  const edate = document.querySelector("#edate");

  // 날짜 비교 (요소가 존재할 때만)
  if (sdate && edate && sdate.value !== "" && edate.value !== "") {
    if (sdate.value > edate.value) {
      e.preventDefault();
      alert("시작날짜는 종료날짜보다 빠르거나 같아야 합니다.");
      sdate.focus();
      return;
    }
  }

  if (secretCheck && secretCheck.checked) {
    if (boardPw.value.trim() === "") {
      e.preventDefault();
      alert("비밀번호를 입력해주세요!");
      return;
    }
  }

  if (boardTitle.value.trim() === "") {
    e.preventDefault();
    alert("제목을 입력해주세요!");
    boardTitle.focus();
    return;
  }

  if ($("#summernote").summernote("isEmpty")) {
    e.preventDefault();
    alert("내용을 입력해주세요!");
    return;
  }
});

function toggleSecret() {
  if (secretWrapper) {
    if (boardCode == 5) {
      secretWrapper.style.display = "block";
    } else {
      secretWrapper.style.display = "none";
      const checkbox = document.querySelector("#checkbox");
      if (checkbox) checkbox.checked = false;
    }
  }
}
toggleSecret();

// --- 썸네일 처리 영역 ---
const profileImg = document.getElementById("profileImg");
const imageInput = document.getElementById("imageInput");
const deleteImage = document.getElementById("deleteImage");
const MAX_SIZE = 1024 * 1024 * 5;

const defaultImageUrl = `/images/logo.jpg`;
let statusCheck = -1;
let previousImage = profileImg ? profileImg.src : defaultImageUrl;
let previousFile = null;

if (imageInput !== null) {
  imageInput.addEventListener("change", () => {
    const file = imageInput.files[0];

    if (file) {
      if (file.size <= MAX_SIZE) {
        const newImageUrl = URL.createObjectURL(file);
        profileImg.src = newImageUrl;
        statusCheck = 1;
        previousImage = newImageUrl;
        previousFile = file;
        deleteImage.style.display = "inline";
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

  deleteImage.addEventListener("click", () => {
    if (profileImg.src !== defaultImageUrl) {
      imageInput.value = "";
      profileImg.src = defaultImageUrl;
      statusCheck = 0;
      previousFile = null;
      previousImage = defaultImageUrl;
      deleteImage.style.display = "none";
    }
  });
}
