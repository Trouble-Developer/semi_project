package edu.kh.project.info.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.kh.project.info.model.dto.AreaCode;
import edu.kh.project.info.model.dto.InfoBoard;
import edu.kh.project.info.model.dto.InfoPagination;
import edu.kh.project.info.model.mapper.InfoMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class InfoServiceImpl implements InfoService {

    @Autowired
    private InfoMapper mapper;

    @Autowired
    private InfoOpenApiService apiService;

    /**
     * [기능: 서버 시작 시 자동 동기화 가동]
     */
    @PostConstruct
    public void init() {
        try { 
            syncFrom1365(); 
        } catch (Exception e) { 
            log.error("초기 동기화 에러: {}", e.getMessage()); 
        }
    }

    /**
     * [기능: 1365 데이터 동기화]
     */
    @Override
    public int syncFrom1365() throws Exception {
        
        // 1. 마감된 데이터 선행 삭제
        int deletedCount = mapper.deleteExpiredInfo();
        if(deletedCount > 0) {
            log.info(">>> 마감된 봉사 정보 {}건이 정리되었습니다.", deletedCount);
        }

        // 2. API를 통해 데이터 가져오기
        List<InfoBoard> list = apiService.requestBatch();
        int result = 0;
        
        if (list != null && !list.isEmpty()) {
            for (InfoBoard info : list) {
                try { 
                    // [데이터 정제 로직 추가]
                    // DB 제약조건(예: CHAR(1)) 위반 방지를 위해 Y/N 값 정제
                    
                    // 성인 가능 여부 정제 (Y가 아니면 무조건 N)
                    String adultAt = info.getAdultPosblAt();
                    info.setAdultPosblAt("Y".equalsIgnoreCase(adultAt) ? "Y" : "N");
                    
                    // 청소년 가능 여부 정제 (Y가 아니면 무조건 N, UNK 방지)
                    String teenAt = info.getYngBgsPosblAt(); // DTO 필드명 확인 필요 (YNG_BGS_POSBL_AT 기준)
                    info.setYngBgsPosblAt("Y".equalsIgnoreCase(teenAt) ? "Y" : "N");

                    // 3. Upsert 실행
                    result += mapper.mergeInfoBoard(info); 
                } 
                catch (Exception e) { 
                    // 에러 발생 시 상세 원인 로그 출력 (트러블슈팅용)
                    log.error("데이터 저장 실패 - URL: {}, 원인: {}", info.getUrl(), e.getMessage());
                }
            }
        }
        log.info(">>> 총 {}건의 봉사 정보가 동기화(삽입/수정)되었습니다.", result);
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> selectInfoList(int cp, Map<String, Object> paramMap) {
        int listCount = mapper.getListCount(paramMap);
        InfoPagination pagination = new InfoPagination(cp, listCount);
        
        int limit = 10; 
        int offset = (cp - 1) * limit;
        paramMap.put("offset", offset);
        paramMap.put("limit", limit);
        
        List<InfoBoard> infoList = mapper.selectInfoList(paramMap);
        
        Map<String, Object> map = new HashMap<>();
        map.put("pagination", pagination);
        map.put("infoList", infoList);
        
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public InfoBoard selectInfoBoard(int infoBoardNo) {
        return mapper.selectInfoBoard(infoBoardNo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaCode> getSidoList() {
        return mapper.getSidoList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaCode> getSignList(String sidoCd) {
        return mapper.getSignList(sidoCd);
    }

    @Override
    @Transactional(readOnly = true)
    public InfoBoard selectInfoDetail(int infoBoardNo, int memberNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("infoBoardNo", infoBoardNo);
        paramMap.put("memberNo", memberNo);
        return mapper.selectInfoDetail(paramMap);
    }

    @Override
    public int updateScrap(Map<String, Object> paramMap) {
        int result = 0;
        boolean isScrapped = (boolean)paramMap.get("isScrapped");

        if(isScrapped) {
            result = mapper.deleteScrap(paramMap);
        } else {
            result = mapper.insertScrap(paramMap);
        }
        return result;
    }
}