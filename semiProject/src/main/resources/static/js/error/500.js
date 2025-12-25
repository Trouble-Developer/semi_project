const errorBtn = document.querySelector("#error-btn");
const modal = document.querySelector("#modal");
const closeBtn = document.querySelector("#close-btn");

errorBtn.addEventListener("click", () => {
	console.log("click");
	modal.classList.remove("hidden");
});

closeBtn.addEventListener("click", () => {
	console.log("close");
	modal.classList.add("hidden");
});

mainBtn.addEventListener("click", () => {
	location.href = "/";
});
