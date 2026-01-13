package edu.kh.project.info.model.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kh.project.info.model.dto.AreaCode;
import edu.kh.project.info.model.dto.InfoBoard;
import lombok.extern.slf4j.Slf4j;

/**
 * [역할] 공공데이터(1365 API)로부터 봉사 정보 및 지역 코드를 수집하고, DTO 객체로 보강/변환하는 서비스
 */
@Slf4j
@Service
public class InfoOpenApiService {

    @Value("${public.api.serviceKey}")
    private String serviceKey;

    private final String LIST_URL = "http://openapi.1365.go.kr/openapi/service/rest/VolunteerPartcptnService/getVltrSearchWordList";
    private final String AREA_URL = "http://openapi.1365.go.kr/openapi/service/rest/VolunteerPartcptnService/getAreaList";

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
            log.info(">>> 총 {}건 봉사 정보 수집 완료", totalList.size());
        } catch (Exception e) {
            log.error(">>> [봉사 API 수집 에러] : {}", e.getMessage());
        }
        return totalList;
    }

    /**
     * [기능추가] 특정 시도 코드에 해당하는 시군구 목록을 API에서 수집
     */
    public List<AreaCode> requestSigunGuList(String sidoCd) {
        List<AreaCode> list = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String url = AREA_URL + "?serviceKey=" + serviceKey + "&schSido=" + sidoCd + "&_type=json";
            String response = restTemplate.getForObject(new URI(url), String.class);
            JsonNode items = mapper.readTree(response).path("response").path("body").path("items").path("item");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    if (!item.path("gugunCd").isMissingNode()) {
                        AreaCode area = new AreaCode();
                        area.setAreaCd(item.path("gugunCd").asText());
                        area.setAreaNm(item.path("gugunNm").asText());
                        area.setParentCd(sidoCd); 
                        list.add(area);
                    }
                }
            }
        } catch (Exception e) {
            log.error(">>> [지역 API 수집 에러] 시도코드 {}: {}", sidoCd, e.getMessage());
        }
        return list;
    }

    /**
     * [기능] JSON 데이터를 DTO로 변환
     */
    private InfoBoard convertToDto(JsonNode node) {
        String registNo = node.path("progrmRegistNo").asText("");
        String detailPageUrl = "https://www.1365.go.kr/vols/P9210/partcptn/volsDetail.do?type=show&progrmRegistNo=" + registNo;
        String title = node.path("progrmSj").asText("제목없음");

        // 모집인원 데이터 추출
        int rcritNmpr = node.path("rcritNmpr").asInt(0);
        if (rcritNmpr == 0) rcritNmpr = node.path("rcritNmprCo").asInt(0);
        if (rcritNmpr == 0) rcritNmpr = extractCountFromTitle(title);
        if (rcritNmpr <= 0) rcritNmpr = -1;

        // 상세내용
        String content = node.path("progrmCn").asText("").trim();
        if (content.isEmpty() || content.equals("null")) {
            content = "상세 활동 내용은 '신청하기' 버튼을 클릭하여 1365 포털에서 확인하실 수 있습니다.";
        }

        String adultPosblAt = node.path("adultPosblAt").asText("UNK").trim();
        String yngBgsPosblAt = node.path("yngBgsPosblAt").asText("UNK").trim();

        return InfoBoard.builder()
                .url(detailPageUrl)
                .progrmNm(title)
                .progrmCn(content)
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
                .adultPosblAt(adultPosblAt) 
                .yngBgsPosblAt(yngBgsPosblAt)
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
        return 0;
    }
    

}