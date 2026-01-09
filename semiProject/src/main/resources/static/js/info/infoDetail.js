document.addEventListener("DOMContentLoaded", function() {
    
    /** [기능 1] 지도 초기화 */
    const initMap = () => {
        const mapContainer = document.getElementById('map');    
        const addrElement = document.getElementById('targetAddr'); 
        const sido = document.getElementById('targetSido')?.innerText.trim() || "";
        const sign = document.getElementById('targetSign')?.innerText.trim() || "";
        const orgNm = document.getElementById('targetOrg')?.innerText.trim() || ""; 

        // kakao 객체가 로드되었는지 확인
        if (typeof kakao === 'undefined' || !mapContainer || !addrElement) {
            console.error("카카오 맵 라이브러리가 로드되지 않았거나 map 영역이 없습니다.");
            return;
        }

        const rawAddr = addrElement.innerText.trim();
        const searchByAddr = `${sido} ${sign} ${rawAddr}`.trim(); 
        const searchByOrg = `${sido} ${sign} ${orgNm}`.trim();   

        // autoload=false를 제거했으므로 kakao.maps.load 없이 바로 실행 가능합니다.
        const map = new kakao.maps.Map(mapContainer, {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 3
        });
        
        const geocoder = new kakao.maps.services.Geocoder(); 
        const ps = new kakao.maps.services.Places();         

        const displayLocation = (coords) => {
            new kakao.maps.Marker({ map, position: coords });
            map.setCenter(coords);
            
            // IP 주소 접속 및 브라우저 크기 차이에 따른 지도 깨짐 방지
            setTimeout(() => {
                map.relayout();
                map.setCenter(coords);
            }, 300);
        };

        // 검색 로직은 기존과 동일하게 유지
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
    };

    /** [기능 2] 신청 및 스크랩 액션 */
    const initActions = () => {
        const applyBtn = document.getElementById("applyBtn");
        if(applyBtn) {
            applyBtn.onclick = () => {
                let url = applyBtn.getAttribute("data-url");
                if(url && url !== 'null') {
                    const regExp = /[?&]progrmRegistNo=([^&]+)/;
                    const match = url.match(regExp);
                    if(match && match[1]) {
                        const progrmNo = match[1];
                        const validUrl = `https://www.1365.go.kr/vols/1572247904127/partcptn/timeCptn.do?type=show&progrmRegistNo=${progrmNo}`;
                        window.open(validUrl);
                    } else {
                        window.open(url);
                    }
                } else {
                    alert("신청 링크가 없습니다.");
                }
            };
        }
        
        const scrapBtn = document.getElementById("scrapBtn");
        if(scrapBtn) {
            scrapBtn.onclick = function() {
                if (typeof loginMemberNo === 'undefined' || loginMemberNo === null) {
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
                        if (this.classList.contains("scrapped")) {
                            icon.classList.replace("fa-regular", "fa-solid");
                            alert("관심 봉사로 등록되었습니다.");
                        } else {
                            icon.classList.replace("fa-solid", "fa-regular");
                            alert("관심 봉사 등록이 해제되었습니다.");
                        }
                    }
                });
            };
        }
    };

    /** [기능 3] 모집인원 표시 보정 */
    const handleNmpr = () => {
        const nmprEl = document.getElementById("nmprDisplay");
        if(nmprEl) {
            const rawText = nmprEl.innerText.replace(/[^0-9-]/g, "");
            const count = parseInt(rawText);
            if(isNaN(count) || count <= 0) {
                nmprEl.innerText = "기관 문의 (상세페이지 참조)";
            } else {
                nmprEl.innerText = count + " 명";
            }
        }
    };

    /** [기능 4] 참여 여부 */
    const handleTargetAt = () => {
        const updateUI = (el) => {
            if(!el) return;
            const val = el.innerText.trim();
            if(val === 'Y') {
                el.innerText = "가능";
                el.style.color = "#2ecc71"; el.style.fontWeight = "bold";
            } else if(val === 'N') {
                el.innerText = "불가능";
                el.style.color = "#e74c3c"; el.style.fontWeight = "bold";
            } else {
                el.innerText = "기관 문의";
                el.style.color = "#95a5a6";
            }
        };
        updateUI(document.getElementById("adultAt"));
        updateUI(document.getElementById("yngAt"));
    };

    // [통합 실행 루틴]
    initMap(); 
    initActions(); 
    handleNmpr(); 
    handleTargetAt(); 
});