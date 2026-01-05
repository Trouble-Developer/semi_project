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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kh.project.info.model.dto.InfoBoard;
import lombok.extern.slf4j.Slf4j;

/**
 * 외부 공공데이터 API(여성가족부 청소년 봉사활동 정보)를 호출하여
 * 데이터를 수집하고 DTO 리스트로 변환하는 서비스 클래스
 */
@Slf4j
@Service
@PropertySource("classpath:/config.properties")
public class InfoOpenApiService {

    // 1. 설정 파일(properties)로부터 공공데이터 인증키 주입
    @Value("${public.api.serviceKey}")
    private String serviceKey;

    // 2. 외부 API 접속을 위한 기본 End Point 주소 (이미지 기준 업데이트)
    private final String API_URL = "https://apis.data.go.kr/1383000/yhis/VolunteerActiveLinkService";

    /**
     * 외부 API로부터 데이터를 일괄 수집(Batch)하는 메인 메서드
     * @return 수집된 봉사활동 정보 리스트 (List<InfoBoard>)
     */
    public List<InfoBoard> requestBatch() {
        log.info(">>> [시스템] 데이터 수집 시작 (인증키 보호 및 타임아웃 강화)");

        List<InfoBoard> totalBoardList = new ArrayList<>();
        
        // 3. HTTP 통신 설정 (RestTemplate): 서버 응답 지연에 대비한 타임아웃 설정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 연결 대기 시간 30초
        factory.setReadTimeout(60000);    // 데이터 읽기 대기 시간 60초
        RestTemplate restTemplate = new RestTemplate(factory);
        ObjectMapper mapper = new ObjectMapper(); // JSON 파싱을 위한 Jackson 라이브러리

        try {
            // 4. [단계 1] 전체 데이터 개수 파악
            // 전체 페이지 수를 계산하기 위해 numOfRows=1로 설정하여 메타데이터만 먼저 호출
            int totalCount = 0;
            
            // UriComponentsBuilder: 인증키의 특수문자(==)가 인코딩 과정에서 깨지지 않도록 URI 생성
            URI checkUri = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("numOfRows", 1)
                    .queryParam("pageNo", 1)
                    .queryParam("_type", "json")
                    .build(true) // 인코딩된 키를 그대로 유지
                    .toUri();

            // 5. [재시도 로직] 공공데이터 서버 불안정에 대비하여 최대 3회 연결 시도
            for(int attempt = 1; attempt <= 3; attempt++) {
                try {
                    log.info(">>> [연결 시도 {}회차] 서버 응답 대기 중...", attempt);
                    String checkResponse = restTemplate.getForObject(checkUri, String.class);
                    
                    if (checkResponse != null) {
                        // 인증 실패(Unauthorized) 메시지 포함 여부 확인
                        if(checkResponse.contains("Unauthorized")) {
                            log.error(">>> [인증 에러] 서비스 키 권한이 없습니다.");
                            return totalBoardList;
                        }
                        
                        // JSON 응답에서 totalCount(전체 게시글 수) 추출
                        Map<String, Object> checkMap = mapper.readValue(checkResponse, Map.class);
                        Map<String, Object> responseNode = (Map<String, Object>) checkMap.get("response");
                        Map<String, Object> body = (Map<String, Object>) responseNode.get("body");
                        totalCount = Integer.parseInt(String.valueOf(body.get("totalCount")));
                        break; 
                    }
                } catch (Exception e) {
                    log.warn(">>> [경고] {}회차 실패: {}", attempt, e.getMessage());
                    if(attempt == 3) throw e;
                    Thread.sleep(2000); // 실패 시 2초 대기 후 재시도
                }
            }

            log.info(">>> [인증 성공] 총 데이터 {}건 수집을 시작합니다.", totalCount);

            // 6. [단계 2] 실데이터 반복 수집
            int numOfRows = 50; // 한 번의 호출당 가져올 데이터 개수
            int totalPages = (int) Math.ceil((double) totalCount / numOfRows); // 전체 페이지 수 계산

            for (int i = 1; i <= totalPages; i++) {
                try {
                    // 각 페이지 번호(pageNo)를 변경하며 API 호출 URI 생성
                    URI currentUri = UriComponentsBuilder.fromHttpUrl(API_URL)
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("pageNo", i)
                            .queryParam("_type", "json")
                            .build(true)
                            .toUri();

                    String rawResponse = restTemplate.getForObject(currentUri, String.class);
                    Map<String, Object> responseMap = mapper.readValue(rawResponse, Map.class);
                    
                    // JSON 계층 구조(response > body > items > item)를 따라가며 데이터 추출
                    Map<String, Object> itemsMap = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) responseMap.get("response")).get("body")).get("items");
                    
                    Object itemObj = itemsMap.get("item");
                    
                    // 7. [데이터 매핑] API 응답 객체(Map)를 시스템 DTO(InfoBoard)로 변환하여 리스트에 추가
                    if (itemObj instanceof List) {
                        for (Map<String, Object> map : (List<Map<String, Object>>) itemObj) {
                            totalBoardList.add(mapToDto(map));
                        }
                    } else if (itemObj instanceof Map) { // 데이터가 1개인 경우 처리
                        totalBoardList.add(mapToDto((Map<String, Object>) itemObj));
                    }
                    
                    log.info(">>> [수집 완료] {} / {} 페이지", i, totalPages);
                    Thread.sleep(200); // 서버 과부하 방지를 위한 미세 대기
                } catch (Exception e) {
                    log.error(">>> [부분 에러] {}페이지 데이터 처리 실패 (건너뜀)", i);
                }
            }

        } catch (Exception e) {
            log.error(">>> [치명적 에러] 데이터 수집 프로세스 중단: {}", e.getMessage());
        }

        return totalBoardList;
    }

    /**
     * API 응답 결과인 Map 객체를 InfoBoard DTO로 변환하는 헬퍼 메서드
     * @param map API로부터 전달받은 개별 데이터 항목
     * @return 변환된 InfoBoard 객체
     */
    private InfoBoard mapToDto(Map<String, Object> map) {
        // 날짜 형식 정리 (예: "2024-01-01 00:00:00" -> "2024-01-01")
        String sdate = String.valueOf(map.getOrDefault("sdate", ""));
        String onlyDate = sdate.contains(" ") ? sdate.split(" ")[0] : sdate;

        return InfoBoard.builder()
                .url(String.valueOf(map.get("key1")))        // 상세조회 키 혹은 URL
                .progrmNm(String.valueOf(map.get("pgmNm")))  // 프로그램명
                .nanmmByNm(String.valueOf(map.get("organNm"))) // 기관명
                .actPlace(String.valueOf(map.getOrDefault("place", "장소 미정"))) // 활동장소
                .progrmCn(String.valueOf(map.getOrDefault("info1", "상세내용 참고"))) // 프로그램내용
                .actRm(String.valueOf(map.get("target")))     // 모집대상
                .progrmBgnde(onlyDate)                       // 시작일
                .progrmEndde(onlyDate)                       // 종료일
                .adultPosblAt("Y")                           // 성인 가능 여부 기본값
                .yngBgsPosblAt("Y")                          // 청소년 가능 여부 기본값
                .build();
    }
}