package edu.kh.project.info.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import edu.kh.project.info.model.dto.InfoBoard;
import edu.kh.project.info.model.service.InfoService;
import lombok.RequiredArgsConstructor;

@RestController  // JSON 반환 전용 컨트롤러
@RequiredArgsConstructor
@RequestMapping("/info")
public class InfoController {

    private final InfoService infoService;

    /**
     * OpenAPI → DB 동기화
     * 
     * @param keyword 검색 키워드 (기본: "급식")
     * @return 동기화 완료 건수
     */
    @GetMapping("/sync")
    public String syncFrom1365(
            @RequestParam(name = "keyword", defaultValue = "급식") String keyword
    ) throws Exception {
        int count = infoService.syncFrom1365(keyword);
        return count + "건 동기화 완료";
    }

    /**
     * 정보 게시판 목록 조회 (검색/필터 적용 가능)
     * 
     * JSON API 반환
     *
     * @param schSido 지역 필터
     * @param actRm 활동분야 필터
     * @param progrmNm 봉사명 검색
     * @param adultPosblAt 성인 참여 가능 여부
     * @param yngBgsPosblAt 청소년 참여 가능 여부
     * @param noticeStatus 모집 상태 ("모집중"/"마감")
     * @return 필터 적용된 InfoBoard 리스트(JSON)
     */
    @GetMapping("/list")
    public List<InfoBoard> infoList(
            @RequestParam(value="schSido", required=false) String schSido,
            @RequestParam(value="actRm", required=false) String actRm,
            @RequestParam(value="progrmNm", required=false) String progrmNm,
            @RequestParam(value="adultPosblAt", required=false) String adultPosblAt,
            @RequestParam(value="yngBgsPosblAt", required=false) String yngBgsPosblAt,
            @RequestParam(value="noticeStatus", required=false) String noticeStatus
    ) {
        // Service에서 필터 적용 후 데이터 반환
        return infoService.selectInfoList(schSido, actRm, progrmNm,
                                          adultPosblAt, yngBgsPosblAt, noticeStatus);
    }

    /**
     * 단일 게시글 상세 조회
     * 
     * @param infoBoardNo 게시글 번호
     * @return InfoBoard 상세 정보(JSON)
     */
    @GetMapping("/detail/{infoBoardNo}")
    public InfoBoard infoDetail(@PathVariable int infoBoardNo) {
        return infoService.selectInfoBoard(infoBoardNo);
    }
}
