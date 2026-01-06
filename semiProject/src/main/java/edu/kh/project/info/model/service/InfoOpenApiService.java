package edu.kh.project.info.model.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kh.project.info.model.dto.InfoBoard;
import lombok.extern.slf4j.Slf4j;

/**
 * [역할] 공공데이터(1365 API)로부터 봉사 정보를 수집하고, DTO 객체로 보강/변환하는 서비스
 */
@Slf4j
@Service
public class InfoOpenApiService {

    @Value("${public.api.serviceKey}")
    private String serviceKey;

    private final String LIST_URL = "http://openapi.1365.go.kr/openapi/service/rest/VolunteerPartcptnService/getVltrSearchWordList";

    /**
     * [기능] 대량의 봉사 데이터를 한 번에 요청하여 리스트로 반환
     */
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
            log.info(">>> 총 {}건 수집 완료", totalList.size());
        } catch (Exception e) {
            log.error(">>> [API 수집 에러] : {}", e.getMessage());
        }
        return totalList;
    }

    /**
     * [기능] JSON 데이터를 DTO로 변환 (인원수 파싱 로직 강화)
     */
    private InfoBoard convertToDto(JsonNode node) {
        String registNo = node.path("progrmRegistNo").asText("");
        String detailPageUrl = "https://www.1365.go.kr/vols/P9210/partcptn/volsDetail.do?type=show&progrmRegistNo=" + registNo;
        String title = node.path("progrmSj").asText("제목없음");

        // [핵심 수정] API가 0을 주므로 제목에서 "(10명)" 같은 패턴을 찾아 숫자를 추출합니다.
        int rcritNmpr = node.path("rcritNmpr").asInt(0);
        
        if (rcritNmpr == 0) {
            rcritNmpr = extractCountFromTitle(title);
        }

        return InfoBoard.builder()
                .url(detailPageUrl)
                .progrmNm(title)
                .progrmCn(node.path("progrmCn").asText("상세내용 참조"))
                .nanmmByNm(node.path("nanmmbyNm").asText(""))
                .rcritNmpr(rcritNmpr) 
                .progrmBgnde(node.path("progrmBgnde").asText(""))
                .progrmEndde(node.path("progrmEndde").asText(""))
                .noticeBgnDe(node.path("noticeBgnde").asText(""))
                .noticeEndDe(node.path("noticeEndde").asText(""))
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

    /**
     * [보조 기능] 제목에서 "00명" 패턴을 찾아 숫자로 변환
     */
    private int extractCountFromTitle(String title) {
        try {
            Pattern pattern = Pattern.compile("(\\d+)명");
            Matcher matcher = pattern.matcher(title);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            return 0;
        }
        return 0; // 못 찾으면 0
    }
}