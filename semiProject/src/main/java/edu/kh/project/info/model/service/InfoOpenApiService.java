package edu.kh.project.info.model.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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

    private final String API_URL = "http://apis.data.go.kr/1383000/YouthActivInfoVolSrvc/getVolProgrmList";

    public List<InfoBoard> requestBatch() {
        log.info(">>> [시스템] 데이터 수집 시작 (재시도 및 타임아웃 강화)");

        List<InfoBoard> totalBoardList = new ArrayList<>();
        
        // 타임아웃 설정 (연결 30초, 읽기 60초로 더 강화)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); 
        factory.setReadTimeout(60000);    
        RestTemplate restTemplate = new RestTemplate(factory);
        ObjectMapper mapper = new ObjectMapper();

        try {
            // 1. 전체 개수 파악 (최대 3번 재시도)
            int totalCount = 0;
            String checkUrl = API_URL + "?serviceKey=" + serviceKey + "&numOfRows=1&pageNo=1&_type=json";
            
            for(int attempt = 1; attempt <= 3; attempt++) {
                try {
                    log.info(">>> [연결 시도 {}회차] 서버 응답 대기 중...", attempt);
                    String checkResponse = restTemplate.getForObject(new URI(checkUrl), String.class);
                    
                    if (checkResponse != null && checkResponse.contains("response")) {
                        Map<String, Object> checkMap = mapper.readValue(checkResponse, Map.class);
                        Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) checkMap.get("response")).get("body");
                        totalCount = Integer.parseInt(String.valueOf(body.get("totalCount")));
                        break; // 성공 시 반복문 탈출
                    }
                } catch (Exception e) {
                    log.warn(">>> [경고] {}회차 시도 실패: {}", attempt, e.getMessage());
                    if(attempt == 3) throw e; // 3번 다 실패하면 에러 던짐
                    Thread.sleep(2000); // 2초 쉬고 다시 시도
                }
            }

            log.info(">>> [연결 성공] 총 데이터 {}건 수집을 시작합니다.", totalCount);

            // 2. 데이터 수집 (한 번에 50개씩 끊어서 안정적으로 가져옴)
            int numOfRows = 50; 
            int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

            for (int i = 1; i <= totalPages; i++) {
                try {
                    String currentUrl = API_URL + "?serviceKey=" + serviceKey + "&numOfRows=" + numOfRows + "&pageNo=" + i + "&_type=json";
                    String rawResponse = restTemplate.getForObject(new URI(currentUrl), String.class);
                    
                    Map<String, Object> responseMap = mapper.readValue(rawResponse, Map.class);
                    Map<String, Object> itemsMap = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) responseMap.get("response")).get("body")).get("items");
                    
                    Object itemObj = itemsMap.get("item");
                    if (itemObj instanceof List) {
                        for (Map<String, Object> map : (List<Map<String, Object>>) itemObj) {
                            totalBoardList.add(mapToDto(map));
                        }
                    } else if (itemObj instanceof Map) {
                        totalBoardList.add(mapToDto((Map<String, Object>) itemObj));
                    }
                    
                    log.info(">>> [수집 완료] {} / {} 페이지", i, totalPages);
                    Thread.sleep(300); // 서버 부하 방지
                } catch (Exception e) {
                    log.error(">>> [부분 에러] {}페이지 실패 (건너뜀)", i);
                }
            }

        } catch (Exception e) {
            log.error(">>> [치명적 에러] 서버 상태가 불안정하여 수집을 중단합니다: {}", e.getMessage());
        }

        return totalBoardList;
    }

    private InfoBoard mapToDto(Map<String, Object> map) {
        String sdate = String.valueOf(map.getOrDefault("sdate", ""));
        String onlyDate = sdate.contains(" ") ? sdate.split(" ")[0] : sdate;
        return InfoBoard.builder()
                .url(String.valueOf(map.get("key1")))
                .progrmNm(String.valueOf(map.get("pgmNm")))
                .nanmmByNm(String.valueOf(map.get("organNm")))
                .actPlace(String.valueOf(map.getOrDefault("place", "장소 미정")))
                .progrmCn(String.valueOf(map.getOrDefault("info1", "상세내용 참고")))
                .actRm(String.valueOf(map.get("target")))
                .progrmBgnde(onlyDate)
                .progrmEndde(onlyDate)
                .adultPosblAt("Y")
                .yngBgsPosblAt("Y")
                .build();
    }
}