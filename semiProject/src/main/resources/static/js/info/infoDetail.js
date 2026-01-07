/**
 * [봉사 상세 페이지 전체 기능]
 * 1. 카카오 지도: 주소/장소명/기관명 다중 검색
 * 2. 신청하기: 외부 링크 연결
 * 3. 스크랩: 로그인 체크 후 비동기 토글 및 UI 업데이트 (아이콘 교체)
 */
document.addEventListener("DOMContentLoaded", function() {
    
    /** [기능 1] 지도 로드 및 최적 위치 탐색 */
    const initMap = () => {
        const mapContainer = document.getElementById('map');    
        const addrElement = document.getElementById('targetAddr'); 
        const sido = document.getElementById('targetSido')?.innerText.trim() || "";
        const sign = document.getElementById('targetSign')?.innerText.trim() || "";
        const orgNm = document.getElementById('targetOrg')?.innerText.trim() || ""; 

        if (typeof kakao === 'undefined' || !mapContainer || !addrElement) return;

        const rawAddr = addrElement.innerText.trim();
        const searchByAddr = `${sido} ${sign} ${rawAddr}`.trim(); 
        const searchByOrg = `${sido} ${sign} ${orgNm}`.trim();   

        kakao.maps.load(() => {
            const map = new kakao.maps.Map(mapContainer, {
                center: new kakao.maps.LatLng(37.5665, 126.9780),
                level: 3
            });
            const geocoder = new kakao.maps.services.Geocoder(); 
            const ps = new kakao.maps.services.Places();         

            const displayLocation = (coords) => {
                new kakao.maps.Marker({ map, position: coords });
                map.setCenter(coords);
                setTimeout(() => map.relayout(), 300);
            };

            geocoder.addressSearch(searchByAddr, (result, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    displayLocation(new kakao.maps.LatLng(result[0].y, result[0].x));
                } else {
                    ps.keywordSearch(searchByAddr, (data, status) => {
                        if (status === kakao.maps.services.Status.OK) {
                            displayLocation(new kakao.maps.LatLng(data[0].y, data[0].x));
                        } else {
                            ps.keywordSearch(searchByOrg, (dataOrg, statusOrg) => {
                                if (statusOrg === kakao.maps.services.Status.OK) {
                                    displayLocation(new kakao.maps.LatLng(dataOrg[0].y, dataOrg[0].x));
                                } else {
                                    ps.keywordSearch(rawAddr, (dataFinal, statusFinal) => {
                                        if (statusFinal === kakao.maps.services.Status.OK) {
                                            displayLocation(new kakao.maps.LatLng(dataFinal[0].y, dataFinal[0].x));
                                        } else {
                                            mapContainer.innerHTML = `<div class="map-error-msg">📍 위치를 찾을 수 없습니다.</div>`;
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        });
    };

    /** [기능 2] 신청하기 및 스크랩 연동 */
    const initActions = () => {
        // 신청하기
        const applyBtn = document.getElementById("applyBtn");
        if(applyBtn) {
            applyBtn.onclick = () => {
                const url = applyBtn.getAttribute("data-url");
                if(url && url !== 'null') window.open(url);
                else alert("신청 링크가 없습니다.");
            };
        }
        
        // 스크랩 (비동기 처리 및 아이콘 토글)
        const scrapBtn = document.getElementById("scrapBtn");

        if(scrapBtn) {
            scrapBtn.onclick = function() {
                if (!loginMemberNo) {
                    alert("로그인 후 이용 가능한 기능입니다.");
                    return;
                }

                const infoBoardNo = this.getAttribute("data-infono");
                const icon = this.querySelector("i");
                const isScrapped = this.classList.contains("scrapped");

                fetch("/info/scrap", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({ "infoBoardNo": infoBoardNo, "isScrapped": isScrapped })
                })
                .then(resp => resp.json())
                .then(result => {
                    if (result > 0) {
                        this.classList.toggle("scrapped");
                        
                        // 아이콘 클래스 교체 (fa-solid <-> fa-regular)
                        if (this.classList.contains("scrapped")) {
                            icon.classList.replace("fa-regular", "fa-solid");
                            alert("관심 봉사로 등록되었습니다.");
                        } else {
                            icon.classList.replace("fa-solid", "fa-regular");
                            alert("관심 봉사 등록이 해제되었습니다.");
                        }
                    }
                })
                .catch(err => console.error("스크랩 통신 에러:", err));
            };
        }
    };

    initMap();
    initActions();
});