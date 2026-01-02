package edu.kh.project.info.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.info.model.service.InfoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("info")
public class InfoBoardController {

    @Autowired
    private InfoService service;

    /**
     * 봉사 목록 조회 (페이지네이션 적용)
     */
    @GetMapping("listPage")
    public String selectInfoList(
            @RequestParam(value="cp", required=false, defaultValue="1") int cp, // 현재 페이지 번호 추가
            @RequestParam Map<String, Object> paramMap, // 모든 검색 파라미터를 한 번에 받음
            Model model) {
        
        // 서비스 호출 시 현재 페이지(cp)와 검색 조건(paramMap)을 같이 전달
        // 서비스에서 비즈니스 로직 처리 후 "pagination"과 "infoList"를 Map에 담아 반환함
        Map<String, Object> map = service.selectInfoList(cp, paramMap);
        
        // 마이바티스 실행 결과 로그 확인
        log.info("조회결과: {}", map.get("infoList"));
        
        // [핵심] HTML에서 사용할 수 있도록 모델에 담기
        model.addAttribute("pagination", map.get("pagination")); // 페이지네이션 객체
        model.addAttribute("infoList", map.get("infoList"));     // 게시글 목록
        
        return "info/infoList";
    }

    /**
     * 1365 데이터 수집 (동기화)
     */
    @GetMapping("sync")
    public String syncFrom1365(RedirectAttributes ra) throws Exception {
        int result = service.syncFrom1365();
        String message = "총 " + result + "건의 봉사 데이터가 동기화되었습니다.";
        ra.addFlashAttribute("message", message);
        return "redirect:/info/listPage";
    }
}