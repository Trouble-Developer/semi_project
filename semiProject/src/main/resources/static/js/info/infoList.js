/**
 * [역할: 정보게시판 동적 UI 제어]
 */
document.addEventListener("DOMContentLoaded", () => {
    const sidoSelect = document.getElementById("sidoCode");
    const gugunSelect = document.getElementById("gugunCode");

    // 1. 시도 목록 가져오기
    fetch("/info/getSidoList")
        .then(resp => resp.json())
        .then(data => {
            console.log("수신 데이터 확인:", data); // 브라우저 콘솔에서 확인 가능

            if (!data || data.length === 0) return;

            data.forEach(item => {
                const opt = document.createElement("option");
                // XML 별칭과 동일한 소문자 필드명 사용
                opt.value = item.areaCd;  
                opt.textContent = item.areaNm; 
                sidoSelect.appendChild(opt);
            });
        })
        .catch(err => console.error("시도 로드 실패:", err));

    // 2. 시도 변경 시 시군구 목록 로드
    sidoSelect?.addEventListener("change", (e) => {
        const sidoCd = e.target.value;
        gugunSelect.innerHTML = '<option value="">전체(시군구)</option>';
        
        if (!sidoCd) return;

        fetch(`/info/getSignList?sidoCd=${sidoCd}`)
            .then(resp => resp.json())
            .then(data => {
                data.forEach(item => {
                    const opt = document.createElement("option");
                    opt.value = item.areaCd;
                    opt.textContent = item.areaNm;
                    gugunSelect.appendChild(opt);
                });
            })
            .catch(err => console.error("시군구 로드 실패:", err));
    });

    // 3. 모집 인원 0명 처리 로직 (자동 실행)
    const processRcritNmpr = () => {
        const nmprElements = document.querySelectorAll(".rcrit-nmpr");
        nmprElements.forEach(el => {
            const val = parseInt(el.textContent.trim());
            if (isNaN(val) || val <= 0) {
                el.textContent = "기관 문의 또는 상세페이지 참조";
                el.style.color = "#ff6b6b";
                el.style.fontSize = "0.85em";
            } else {
                el.textContent = val + "명";
            }
        });
    };
    processRcritNmpr();
});

// 체크박스 단일 선택
function checkOnlyOne(element) {
    const checkboxes = document.getElementsByName(element.name);
    checkboxes.forEach((cb) => { cb.checked = false; });
    element.checked = true;  
}