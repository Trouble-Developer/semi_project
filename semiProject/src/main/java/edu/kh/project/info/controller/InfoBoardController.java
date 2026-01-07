package edu.kh.project.info.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.info.model.dto.AreaCode;
import edu.kh.project.info.model.dto.InfoBoard;
import edu.kh.project.info.model.service.InfoService;
import edu.kh.project.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("info")
public class InfoBoardController {

    @Autowired
    private InfoService service;

    /**
     * [기능: 봉사 목록 조회 및 페이지네이션]
     * @param cp : 현재 페이지 (기본값 1)
     * @param paramMap : 검색 조건 (지역, 분야, 제목 등)
     * @return : info/infoList.html 뷰
     */
    @GetMapping("listPage")
    public String selectInfoList(
            @RequestParam(value="cp", required=false, defaultValue="1") int cp,
            @RequestParam Map<String, Object> paramMap, 
            Model model) {
        
        Map<String, Object> map = service.selectInfoList(cp, paramMap);
        model.addAttribute("pagination", map.get("pagination"));
        model.addAttribute("infoList", map.get("infoList"));
        
        return "info/infoList";
    }

    /**
     * [기능: 비동기 시도 목록 조회]
     * @return : DB에서 조회한 시도 리스트 (JSON)
     */
    @ResponseBody
    @GetMapping("getSidoList")
    public List<AreaCode> getSidoList() {
        return service.getSidoList(); // List<AreaCode> 반환으로 타입 일치
    }

    /**
     * [기능: 비동기 시군구 목록 조회]
     * @param sidoCd : 선택된 부모 시도 코드
     * @return : 해당 시도에 속한 시군구 리스트 (JSON)
     */
    @ResponseBody
    @GetMapping("getSignList")
    public List<AreaCode> getSignList(@RequestParam("sidoCd") String sidoCd) {
        return service.getSignList(sidoCd);
    }

    /**
     * [기능: 1365 데이터 동기화]
     * @return : 목록 페이지로 리다이렉트
     */
    @GetMapping("sync")
    public String syncFrom1365(RedirectAttributes ra) throws Exception {
        int result = service.syncFrom1365();
        ra.addFlashAttribute("message", "총 " + result + "건의 데이터가 동기화되었습니다.");
        return "redirect:/info/listPage";
    }
    
    /**
     * [기능: 봉사 상세 조회]
     * @param infoBoardNo : 게시글 번호
     * @param model : 데이터 전달 객체
     */
    @GetMapping("detail/{infoBoardNo}")
    public String infoDetail(
            @PathVariable("infoBoardNo") int infoBoardNo,
            @SessionAttribute(value="loginMember", required=false) Member loginMember,
            Model model) {

        // 로그인한 회원이 있을 경우 회원 번호를 같이 넘겨 스크랩 여부 확인
        int memberNo = (loginMember != null) ? loginMember.getMemberNo() : 0;
        
        // 서비스 호출 시 memberNo를 함께 전달하여 scrapCheck를 조회하도록 수정
        InfoBoard info = service.selectInfoDetail(infoBoardNo, memberNo);

        if (info != null) {
            model.addAttribute("info", info);
            return "info/infoDetail";
        } else {
            return "redirect:/info/listPage";
        }
    }

    /**
     * [기능추가: 봉사 정보 스크랩/취소]
     * @param paramMap : infoBoardNo, isScrapped 데이터를 담은 맵
     * @param loginMember : 세션에서 가져온 로그인 회원 정보
     * @return : 성공 시 1, 실패 시 0 (JSON)
     */
    @ResponseBody
    @PostMapping("scrap")
    public int infoScrap(@RequestBody Map<String, Object> paramMap,
                        @SessionAttribute("loginMember") Member loginMember) {
        
        // 로그인한 유저 정보 추가
        paramMap.put("memberNo", loginMember.getMemberNo());
        
        // 서비스 호출 (상태에 따라 INSERT 또는 DELETE 수행)
        return service.updateScrap(paramMap);
    }
    
    /**
     * [기능: 1365 데이터 동기화 (JS 비동기 통신용)]
     * 지난번과 동일하게 비동기(Fetch) 방식으로 호출되어 
     * 서비스의 syncFrom1365를 실행하고 결과 건수를 반환합니다.
     */
    @ResponseBody
    @GetMapping("api/refresh")
    public int refreshApi() throws Exception {
        // 이미 구현된 서비스 로직을 호출하여 API 데이터를 DB에 MERGE 합니다.
        return service.syncFrom1365(); 
    }
}