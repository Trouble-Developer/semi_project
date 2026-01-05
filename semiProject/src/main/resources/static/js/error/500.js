const errorBtn = document.querySelector("#error-btn");
const modal = document.querySelector("#modal");
const closeBtn = document.querySelector("#close-btn");
const mainBtn = document.querySelector("#main-btn"); // mainBtn 추가!

errorBtn.addEventListener("click", () => {
  console.log("click");
  modal.classList.remove("hidden");
});

closeBtn.addEventListener("click", () => {
  console.log("close");
  modal.classList.add("hidden");
});

if (mainBtn !== null) {
  mainBtn.addEventListener("click", () => {
    location.href = "/";
  });
}
