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
        try { syncFrom1365(); } catch (Exception e) { log.error("초기 동기화 에러: {}", e.getMessage()); }
    }

    /**
     * [기능: 1365 데이터 동기화]
     * 역할: API를 통해 데이터를 가져와 DB에 MERGE 수행
     */
    @Override
    public int syncFrom1365() throws Exception {
        List<InfoBoard> list = apiService.requestBatch();
        if (list == null || list.isEmpty()) return 0;

        int result = 0;
        for (InfoBoard info : list) {
            try { result += mapper.mergeInfoBoard(info); } 
            catch (Exception e) { log.debug("데이터 스킵: {}", info.getUrl()); }
        }
        return result;
    }

    /**
     * [기능: 페이징 처리된 봉사 목록 조회]
     */
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

    /**
     * [기능: 시도 목록 조회 (반환 타입 통일)]
     */
    @Override
    @Transactional(readOnly = true)
    public List<AreaCode> getSidoList() {
        return mapper.getSidoList();
    }

    /**
     * [기능: 시군구 목록 조회 (반환 타입 통일)]
     */
    @Override
    @Transactional(readOnly = true)
    public List<AreaCode> getSignList(String sidoCd) {
        return mapper.getSignList(sidoCd);
    }
}