package edu.kh.project.info.model.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.kh.project.info.model.dto.InfoBoard;

@Service
@PropertySource("classpath:/config.properties")
public class InfoOpenApiService {

    @Value("${public.api.serviceKey}")
    private String serviceKey; // OpenAPI 인증키

    /**
     * 1365 공공데이터 API 호출 후 XML 파싱 → InfoBoard 리스트 반환
     */
    public List<InfoBoard> request1365(String keyword) throws Exception {

        StringBuilder urlBuilder = new StringBuilder(
            "http://openapi.1365.go.kr/openapi/service/rest/VolunteerPartcptnService/getVltrSearchWordList"
        );
        urlBuilder.append("?serviceKey=").append(URLEncoder.encode(serviceKey, "UTF-8"));
        urlBuilder.append("&SchCateGu=prgrmSj"); // 봉사분야 기준
        urlBuilder.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
        urlBuilder.append("&numOfRows=20");
        urlBuilder.append("&pageNo=1");

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(conn.getInputStream());

        NodeList itemList = doc.getElementsByTagName("item");
        List<InfoBoard> list = new ArrayList<>();

        for (int i = 0; i < itemList.getLength(); i++) {
            Element item = (Element) itemList.item(i);
            InfoBoard board = new InfoBoard();

            // OpenAPI 태그 ↔ DTO 필드 매핑
            board.setSchSido(getText(item, "sidoCd"));
            board.setSchSign(getText(item, "gugunCd"));
            board.setProgrmBgnde(getText(item, "progrmBgnde"));
            board.setProgrmEndde(getText(item, "progrmEndde"));
            board.setAdultPosblAt(getText(item, "adultPosblAt"));
            board.setYngBgsPosblAt(getText(item, "yngbgsPosblAt"));
            board.setActBeginTm(parseInt(getText(item, "actBeginTm")));
            board.setActEndTm(parseInt(getText(item, "actEndTm")));
            board.setNoticeBgnDe(getText(item, "noticeBgnde"));
            board.setNoticeEndDe(getText(item, "noticeEndde"));
            board.setActPlace(getText(item, "actPlace"));
            board.setNanmmByNm(getText(item, "nanmmbyNm"));
            board.setUrl(getText(item, "url"));
            board.setProgrmNm(getText(item, "progrmNm")); 
            board.setActRm(getText(item, "actRm")); 

            list.add(board);
        }

        return list;
    }

    private String getText(Element e, String tag) {
        NodeList nl = e.getElementsByTagName(tag);
        if (nl.getLength() == 0) return null;
        return nl.item(0).getTextContent();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
