package edu.kh.project.info.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.kh.project.info.model.dto.AreaCode;
import edu.kh.project.info.model.service.InfoService;
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
}