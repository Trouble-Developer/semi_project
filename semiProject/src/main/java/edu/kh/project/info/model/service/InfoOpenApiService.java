package edu.kh.project.info.model.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kh.project.info.model.dto.InfoBoard;
import lombok.extern.slf4j.Slf4j;

/**
 * 역할: 데이터 수집 시 누락된 필드 보강 및 실제 상세페이지 URL 생성
 */
@Slf4j
@Service
public class InfoOpenApiService {

    @Value("${public.api.serviceKey}")
    private String serviceKey;

    private final String LIST_URL = "http://openapi.1365.go.kr/openapi/service/rest/VolunteerPartcptnService/getVltrSearchWordList";

    public List<InfoBoard> requestBatch() {
        List<InfoBoard> totalList = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            int targetCount = 500;
            String url = LIST_URL + "?serviceKey=" + serviceKey + "&numOfRows=" + targetCount + "&_type=json";
            
            String response = restTemplate.getForObject(new URI(url), String.class);
            JsonNode items = mapper.readTree(response).path("response").path("body").path("items").path("item");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    totalList.add(convertToDto(item));
                }
            }
            log.info(">>> 총 {}건 수집 및 URL 생성 완료", totalList.size());
        } catch (Exception e) {
            log.error("수집 중 오류: {}", e.getMessage());
        }
        return totalList;
    }

    private InfoBoard convertToDto(JsonNode node) {
        // 1365 등록번호 추출
        String registNo = node.path("progrmRegistNo").asText("");
        
        // [핵심] 실제 봉사 상세페이지 주소 생성
        String detailPageUrl = "https://www.1365.go.kr/vols/P9210/partcptn/volsDetail.do?type=show&progrmRegistNo=" + registNo;

        return InfoBoard.builder()
                .url(detailPageUrl) // 이제 고유번호 대신 실제 클릭 가능한 주소가 저장됨
                .progrmNm(node.path("progrmSj").asText("제목없음"))
                .progrmCn(node.path("progrmCn").asText("내용은 상세페이지를 참조하세요."))
                .nanmmByNm(node.path("nanmmbyNm").asText(""))
                
                // [수정] 가이드 v1.8 필드명 재매핑
                .progrmBgnde(node.path("progrmBgnde").asText("")) // 봉사 시작일
                .progrmEndde(node.path("progrmEndde").asText("")) // 봉사 종료일
                .noticeBgnDe(node.path("noticeBgnde").asText("")) // 모집 시작일
                .noticeEndDe(node.path("noticeEndde").asText("")) // 모집 종료일
                
                // [수정] 활동분야(srvcClCode 또는 분류명)
                .actRm(node.path("srvcClCode").asText("기타")) 
                
                .actPlace(node.path("actPlace").asText(""))
                .schSido(node.path("sidoCd").asText(""))
                .schSign(node.path("gugunCd").asText(""))
                .actBeginTm(node.path("actBeginTm").asInt(0))
                .actEndTm(node.path("actEndTm").asInt(0))
                .adultPosblAt(node.path("adultPosblAt").asText("Y"))
                .yngBgsPosblAt(node.path("yngBgsPosblAt").asText("Y"))
                .build();
    }
}