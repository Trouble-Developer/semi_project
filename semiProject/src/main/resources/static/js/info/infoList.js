document.addEventListener("DOMContentLoaded", () => {
    const schSido = document.querySelector("select[name='schSido']");
    const schSign = document.querySelector("select[name='schSign']");

    // 1. 시군구 데이터 (더미 데이터용)
    const areaData = {
        "6110000": ["서울 전체", "종로구", "강남구", "서초구", "송파구"],
        "6410000": ["경기 전체", "수원시", "성남시", "용인시", "고양시"],
        "6260000": ["부산 전체", "해운대구", "사하구", "수영구", "연제구"]
    };

    // 2. 시도 변경 이벤트 리스너
    schSido.addEventListener("change", (e) => {
        const selectedSido = e.target.value;

        // 기존 시군구 옵션 초기화 (첫 번째 '전체' 제외)
        schSign.innerHTML = '<option value="">전체(시군구)</option>';

        if (selectedSido && areaData[selectedSido]) {
            areaData[selectedSido].forEach(city => {
                const option = document.createElement("option");
                option.value = city; // 나중에 실제 코드로 대체 가능
                option.textContent = city;
                schSign.appendChild(option);
            });
        }
    });

    // 3. 초기화 버튼 동작 보완
    document.getElementById("searchForm").addEventListener("reset", () => {
        setTimeout(() => {
            schSign.innerHTML = '<option value="">전체(시군구)</option>';
        }, 10);
    });
});