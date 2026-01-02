package edu.kh.project.info.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kh.project.info.model.dto.InfoBoard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource("classpath:/config.properties")
public class InfoOpenApiService {

    @Value("${public.api.serviceKey}")
    private String serviceKey;

    private final String API_URL = "http://openapi.1365.go.kr/openapi/service/rest/VolunteerPartcptnService/getVltrPartcptnItem";

    /**
     * [기능] 1365 API를 호출하여 최신 봉사 데이터를 수집합니다.
     */
    public List<InfoBoard> requestBatch() {
        log.info(">>> [API] 최신 데이터 수집 시도 (1~5페이지)...");
        
        for (int i = 1; i <= 5; i++) {
            try {
                String fullUrl = API_URL + "?serviceKey=" + serviceKey 
                               + "&numOfRows=100" 
                               + "&pageNo=" + i 
                               + "&_type=json";

                RestTemplate restTemplate = new RestTemplate();
                String rawResponse = restTemplate.getForObject(fullUrl, String.class);
                
                if (rawResponse == null || rawResponse.contains("<response>")) continue;

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseMap = mapper.readValue(rawResponse, Map.class);
                Map<String, Object> responseNode = (Map<String, Object>) responseMap.get("response");
                Map<String, Object> bodyNode = (Map<String, Object>) responseNode.get("body");
                
                Object itemsObj = bodyNode.get("items");
                if (itemsObj == null || !(itemsObj instanceof Map)) {
                    log.warn(">>> [알림] {}페이지에 데이터가 없습니다.", i);
                    continue;
                }

                Map<String, Object> itemsMap = (Map<String, Object>) itemsObj;
                Object itemObj = itemsMap.get("item");
                if (itemObj == null) continue;

                List<InfoBoard> boardList = new ArrayList<>();

                if (itemObj instanceof List) {
                    for (Map<String, Object> map : (List<Map<String, Object>>) itemObj) {
                        boardList.add(mapToDto(map));
                    }
                } else if (itemObj instanceof Map) {
                    boardList.add(mapToDto((Map<String, Object>) itemObj));
                }
                
                log.info(">>> [성공] {}페이지에서 {}건 확보!", i, boardList.size());
                return boardList;

            } catch (Exception e) {
                log.error(">>> [에러] {}페이지 수집 중 문제: {}", i, e.getMessage());
            }
        }
        return null;
    }

    /** * [기능] API 응답 데이터를 새 DTO 구조에 맞춰 매핑 
     * DB 컬럼과 일치하도록 모든 필드를 매칭했습니다.
     */
    private InfoBoard mapToDto(Map<String, Object> map) {
        return InfoBoard.builder()
                .progrmNm(String.valueOf(map.getOrDefault("progrmSj", "제목없음")))
                .progrmCn(String.valueOf(map.getOrDefault("progrmCn", "")))
                .nanmmByNm(String.valueOf(map.getOrDefault("nanmmbyNm", "")))
                .progrmBgnde(String.valueOf(map.getOrDefault("progrmBgnde", "")))
                .progrmEndde(String.valueOf(map.getOrDefault("progrmEndde", "")))
                .noticeBgnDe(String.valueOf(map.getOrDefault("noticeBgnde", ""))) // 추가
                .noticeEndDe(String.valueOf(map.getOrDefault("noticeEndde", "")))
                .actBeginTm(String.valueOf(map.getOrDefault("actBeginTm", "0")))  // 추가
                .actEndTm(String.valueOf(map.getOrDefault("actEndTm", "0")))      // 추가
                .actPlace(String.valueOf(map.getOrDefault("actPlace", "")))
                .actRm(String.valueOf(map.getOrDefault("srvcClCode", "")))       // 추가 (분류코드)
                .schSido(String.valueOf(map.getOrDefault("schSido", "")))        // 추가
                .schSign(String.valueOf(map.getOrDefault("schSign", "")))        // 추가
                .adultPosblAt(String.valueOf(map.getOrDefault("adultPosblAt", "N"))) // 추가
                .yngBgsPosblAt(String.valueOf(map.getOrDefault("yngBgsPosblAt", "N"))) // 추가
                .url("https://www.1365.go.kr/vols/P9210/partcptn/volsDetail.do?progrmRegistNo=" + map.get("progrmRegistNo"))
                .build();
    }
}