package edu.kh.project.info.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.kh.project.info.model.dto.InfoBoard;
import edu.kh.project.info.model.dto.InfoPagination; // DTO 임포트
import edu.kh.project.info.model.mapper.InfoMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class InfoServiceImpl implements InfoService {

    @Autowired
    private InfoMapper mapper;

    @Autowired
    private InfoOpenApiService apiService;

    @PostConstruct
    public void init() {
        log.info("===== [시스템 시작] 봉사 데이터 자동 동기화 가동 =====");
        try {
            syncFrom1365();
        } catch (Exception e) {
            log.error("자동 동기화 에러: {}", e.getMessage());
        }
    }

    @Override
    public int syncFrom1365() throws Exception {
        List<InfoBoard> list = apiService.requestBatch();
        if (list == null || list.isEmpty()) return 0;

        int result = 0;
        for (InfoBoard info : list) {
            try {
                result += mapper.mergeInfoBoard(info);
            } catch (Exception e) {
                log.debug("중복 데이터 스킵");
            }
        }
        log.info(">>> 동기화 결과: 신규 반영 {}건", result);
        return result;
    }

    /** 페이지네이션 처리를 포함한 목록 조회 */
    @Override
    public Map<String, Object> selectInfoList(int cp, Map<String, Object> paramMap) {
        
        // 1. 전체 게시글 수 조회
        int listCount = mapper.getListCount(paramMap);
        
        // 2. InfoPagination 객체 생성
        InfoPagination pagination = new InfoPagination(cp, listCount);
        
        // 3. Oracle BETWEEN 쿼리용 offset과 limit 설정
        int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
        paramMap.put("offset", offset);
        paramMap.put("limit", pagination.getLimit());
        
        // 4. 목록 조회
        List<InfoBoard> infoList = mapper.selectInfoList(paramMap);
        
        // 5. 결과 반환용 Map 구성
        Map<String, Object> map = new HashMap<>();
        map.put("pagination", pagination);
        map.put("infoList", infoList);
        
        return map;
    }

    @Override
    public InfoBoard selectInfoBoard(int infoBoardNo) {
        return mapper.selectInfoBoard(infoBoardNo);
    }
}