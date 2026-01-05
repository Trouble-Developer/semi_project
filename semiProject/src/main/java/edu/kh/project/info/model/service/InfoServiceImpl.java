package edu.kh.project.info.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.kh.project.info.model.dto.InfoBoard;
import edu.kh.project.info.model.dto.InfoPagination;
import edu.kh.project.info.model.mapper.InfoMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 역할: 데이터 동기화 관리 및 UI용 목록/상세 조회 서비스
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class InfoServiceImpl implements InfoService {

    @Autowired
    private InfoMapper mapper;

    @Autowired
    private InfoOpenApiService apiService;

    /**
     * [서버 기동 시 자동 실행] 
     * 애플리케이션이 켜지자마자 API에서 최신 데이터를 받아와 DB를 업데이트함
     */
    @PostConstruct
    public void init() {
        log.info("===== [시스템 시작] 1365 데이터 자동 동기화 가동 =====");
        try {
            syncFrom1365();
        } catch (Exception e) {
            log.error(">>> [초기화 실패] 자동 동기화 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * [동기화 핵심 로직] 
     * API 서버에서 가져온 리스트를 DB에 한 건씩 MERGE(중복 체크 후 삽입)함
     */
    @Override
    public int syncFrom1365() throws Exception {
        // 1. Open API 서비스를 통해 리스트 수집
        List<InfoBoard> list = apiService.requestBatch();
        if (list == null || list.isEmpty()) return 0;

        int result = 0;
        for (InfoBoard info : list) {
            try {
                // 2. Mapper를 통해 DB에 MERGE 수행 (URL 컬럼 기준 중복 체크)
                result += mapper.mergeInfoBoard(info);
            } catch (Exception e) {
                log.debug("중복 데이터 스킵: {}", info.getUrl());
            }
        }
        log.info(">>> [동기화 완료] 신규 반영 건수: {}건", result);
        return result;
    }

    /**
     * [목록 조회] 
     * 사용자가 보는 게시판 화면에 필요한 페이징 계산 및 목록 검색
     */
    @Override
    @Transactional(readOnly = true) // 단순 조회는 읽기 전용으로 성능 최적화
    public Map<String, Object> selectInfoList(int cp, Map<String, Object> paramMap) {
        
        // 1. 조건에 맞는 전체 게시글 수 조회 (페이징 계산의 기본)
        int listCount = mapper.getListCount(paramMap);
        
        // 2. 페이지네이션 객체 생성 (현재 페이지, 전체 건수 전달)
        InfoPagination pagination = new InfoPagination(cp, listCount);
        
        // 3. Oracle의 BETWEEN 연산 처리를 위한 offset 계산
        // (현재페이지 - 1) * 10
        int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
        paramMap.put("offset", offset);
        paramMap.put("limit", pagination.getLimit());
        
        // 4. DB에서 실제 출력할 10개의 데이터만 조회
        List<InfoBoard> infoList = mapper.selectInfoList(paramMap);
        
        // 5. 컨트롤러로 보낼 결과 Map 구성
        Map<String, Object> map = new HashMap<>();
        map.put("pagination", pagination);
        map.put("infoList", infoList);
        
        return map;
    }

    /**
     * [상세 조회]
     */
    @Override
    @Transactional(readOnly = true)
    public InfoBoard selectInfoBoard(int infoBoardNo) {
        return mapper.selectInfoBoard(infoBoardNo);
    }
}