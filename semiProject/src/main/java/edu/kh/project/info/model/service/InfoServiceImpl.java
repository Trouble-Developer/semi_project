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
     * 역할: API를 통해 봉사 정보 및 시군구 지역 코드를 가져와 DB에 MERGE 수행
     */
    @Override
    public int syncFrom1365() throws Exception {
        List<InfoBoard> list = apiService.requestBatch();
        int result = 0;
        if (list != null && !list.isEmpty()) {
            for (InfoBoard info : list) {
                try { result += mapper.mergeInfoBoard(info); } 
                catch (Exception e) { log.debug("데이터 스킵: {}", info.getUrl()); }
            }
        }
        log.info(">>> 현재 DB 내 시도/시군구 데이터를 기반으로 서비스를 운영합니다.");
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
	public InfoBoard selectInfoDetail(int infoBoardNo, int memberNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("infoBoardNo", infoBoardNo);
        paramMap.put("memberNo", memberNo);
		return mapper.selectInfoDetail(paramMap);
	}

    /** [기능추가: 관심 봉사 스크랩 업데이트] */
    @Override
    public int updateScrap(Map<String, Object> paramMap) {
        int result = 0;
        // isScrapped가 true면 이미 스크랩 된 상태 -> 삭제(delete)
        // isScrapped가 false면 스크랩 안 된 상태 -> 삽입(insert)
        boolean isScrapped = (boolean)paramMap.get("isScrapped");

        if(isScrapped) {
            result = mapper.deleteScrap(paramMap);
        } else {
            result = mapper.insertScrap(paramMap);
        }
        return result;
    }
}