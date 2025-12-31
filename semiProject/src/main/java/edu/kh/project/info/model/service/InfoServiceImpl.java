package edu.kh.project.info.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.kh.project.info.model.dto.InfoBoard;
import edu.kh.project.info.model.mapper.InfoMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private final InfoMapper infoMapper;
    private final InfoOpenApiService openApiService;

    /** OpenAPI → DB 동기화 */
    @Override
    public int syncFrom1365(String keyword) throws Exception {
        List<InfoBoard> apiList = openApiService.request1365(keyword);
        int count = 0;

        for (InfoBoard info : apiList) {
            count += infoMapper.mergeInfoBoard(info);
        }

        return count;
    }

    /** 목록 조회 (검색/필터 적용) */
    @Override
    public List<InfoBoard> selectInfoList(String schSido, String actRm, String progrmNm,
                                          String adultPosblAt, String yngBgsPosblAt, String noticeStatus) {
        return infoMapper.selectInfoListWithFilter(schSido, actRm, progrmNm,
                                                   adultPosblAt, yngBgsPosblAt, noticeStatus);
    }

    /** 상세 조회 */
    @Override
    public InfoBoard selectInfoBoard(int infoBoardNo) {
        return infoMapper.selectInfoBoard(infoBoardNo);
    }
}
