/**
 * [역할: 정보게시판 동적 UI 제어 및 필터 데이터 삽입]
 */
document.addEventListener("DOMContentLoaded", () => {

    // 1. [요소 획득]
    const sidoSelect = document.getElementById("sidoCode");
    const gugunSelect = document.getElementById("gugunCode");
    const catSelect = document.getElementById("category"); // 봉사분야
    const actSelect = document.getElementById("actType");  // 활동구분
    const tarSelect = document.getElementById("target");   // 봉사대상
    const progrmNmInput = document.getElementsByName("progrmNm")[0];
    const infoSearchForm = document.getElementById("infoSearchForm");

    const urlParams = new URLSearchParams(window.location.search);

    // 2. [고정 데이터 정의]
    const filterData = {
        category: ["보건ㆍ의료", "생활편의", "교육", "환경보호", "행정지원", "문화ㆍ체육", "안전ㆍ방범", "인권ㆍ권익", "재난ㆍ재해", "기타"],
        actType: ["온라인", "오프라인"],
        target: ["아동/청소년", "노인", "장애인", "다문화", "기타"]
    };

    /**
     * [공통 함수: Option 추가]
     */
    const addOptions = (el, data, paramKey) => {
        if (!el) return;
        data.forEach(text => {
            const opt = document.createElement("option");
            opt.value = text;
            opt.textContent = text;
            el.appendChild(opt);
        });

        if (urlParams.get(paramKey)) {
            el.value = urlParams.get(paramKey);
        }
    };

    // 3. [고정 필터 목록 채우기]
    addOptions(catSelect, filterData.category, "actRm");
    addOptions(actSelect, filterData.actType, "actType");
    addOptions(tarSelect, filterData.target, "target");

    if (progrmNmInput && urlParams.get("progrmNm")) {
        progrmNmInput.value = urlParams.get("progrmNm");
    }

    // 4. [비동기 지역 데이터 로드] - 시도
    fetch("/info/getSidoList")
        .then(resp => resp.json())
        .then(data => {
            if (!data || data.length === 0) return;
            data.forEach(item => {
                const opt = new Option(item.areaNm, item.areaCd);
                sidoSelect?.appendChild(opt);
            });

            const schSido = urlParams.get("schSido");
            if (schSido && sidoSelect) {
                sidoSelect.value = schSido;
                sidoSelect.dispatchEvent(new Event('change'));
            }
        })
        .catch(err => console.error("시도 로드 실패:", err));

    // 5. [지역 연동] 시도 변경 시 시군구 로드
    sidoSelect?.addEventListener("change", (e) => {
        const sidoCd = e.target.value;
        if (gugunSelect) gugunSelect.innerHTML = '<option value="">전체(시군구)</option>';

        if (!sidoCd) return;

        fetch(`/info/getSignList?sidoCd=${sidoCd}`)
            .then(resp => resp.json())
            .then(data => {
                data.forEach(item => {
                    const opt = new Option(item.areaNm, item.areaCd);
                    gugunSelect?.appendChild(opt);
                });

                const schSign = urlParams.get("schSign");
                if (schSign && gugunSelect) {
                    gugunSelect.value = schSign;
                }
            })
            .catch(err => console.error("시군구 로드 실패:", err));
    });

    // 6. [UI 가공] 모집 인원 0명 처리
    const processRcritNmpr = () => {
        document.querySelectorAll(".rcrit-nmpr").forEach(el => {
            const val = parseInt(el.textContent.trim());
            if (isNaN(val) || val <= 0) {
                el.textContent = "기관 문의";
                el.style.color = "#ff6b6b";
                el.style.fontWeight = "bold";
            } else {
                el.textContent = val + "명";
            }
        });
    };
    processRcritNmpr();

    // 7. [검색 시 페이지 초기화] 검색 버튼 누르면 무조건 1페이지로
    infoSearchForm?.addEventListener("submit", () => {
        const cpHidden = document.createElement("input");
        cpHidden.type = "hidden";
        cpHidden.name = "cp";
        cpHidden.value = "1";
        infoSearchForm.appendChild(cpHidden);
    });

    /**
     * [기능 8] 관리자 전용 공공데이터 동기화
     */
    const initAdminSync = () => {
        
        const syncBtn = document.getElementById("refreshOpenApi");
        
        if (syncBtn) {
            syncBtn.onclick = () => {
                if (!confirm("1365 공공데이터와 동기화를 진행하시겠습니까?")) return;
                
                syncBtn.disabled = true;
                const originalText = syncBtn.innerHTML;
                syncBtn.innerHTML = '<i class="fa-solid fa-sync fa-spin"></i> 동기화 중...';

                fetch("/info/api/refresh")
                    .then(resp => {
                        if(!resp.ok) throw new Error("동기화 실패");
                        return resp.json(); 
                    })
                    .then(result => {
                        if (result > 0) {
                            alert(`동기화 완료! 총 ${result}건의 데이터가 업데이트되었습니다.`);
                        } else {
                            alert("동기화 완료! 최신 데이터가 이미 반영되어 있습니다.");
                        }
                        location.reload();
                    })
                    .catch((err) => {
                        console.error(err);
                        alert("데이터 수집 중 오류가 발생했습니다.");
                    })
                    .finally(() => {
                        syncBtn.disabled = false;
                        syncBtn.innerHTML = originalText;
                    });
            };
        }
    };

    /**
     * [기능 9] 검색 결과 알림창 표시 
     */
    const checkSearchAlert = () => {
        const countInput = document.getElementById("searchListCount");
        const urlParams = new URLSearchParams(window.location.search);

        const hasSearchCondition = Array.from(urlParams.keys()).some(key =>
            key !== 'cp' && urlParams.get(key) !== ""
        );

        if (hasSearchCondition && countInput) {
            const count = countInput.value;
            alert(`총 ${count}건의 봉사 정보가 검색되었습니다.`);
        }
    };

    initAdminSync();   
    checkSearchAlert();
});

/**
 * [체크박스 단일 선택]
 */
function checkOnlyOne(element) {
    const checkboxes = document.getElementsByName(element.name);
    checkboxes.forEach((cb) => {
        if (cb !== element) cb.checked = false;
    });
}