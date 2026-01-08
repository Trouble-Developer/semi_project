package edu.kh.project.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.util.Utility;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class EditBoardServiceImpl implements EditBoardService {
	@Autowired
	private EditBoardMapper mapper;

	@Value("${board.image.web-path}")
	private String boardImageWebPath;

	@Value("${board.image.folder-path}")
	private String boardImageFolderPath;

	@Override
	public int boardInsert(Map<String, Object> map, MultipartFile thumbnail) throws Exception {
		// 비밀번호를 사용하지 않으므로 null 세팅 (IllegalArgumentException 방지)
		map.put("encPw", null);

		int result = mapper.boardInsert(map);

		if (result == 0)
			return 0;

		int boardNo = Integer.parseInt(map.get("boardNo").toString());
		List<BoardImg> imgList = new ArrayList<>();

		// 썸네일 처리
		if (thumbnail != null && !thumbnail.isEmpty()) {
			String rename = Utility.fileRename(thumbnail.getOriginalFilename());
			thumbnail.transferTo(new java.io.File(boardImageFolderPath + rename));

			BoardImg img = new BoardImg();
			img.setBoardNo(boardNo);
			img.setImgPath(boardImageWebPath);
			img.setImgRename(rename);
			img.setImgOriginal(thumbnail.getOriginalFilename());
			img.setImgOrder(0);
			imgList.add(img);
		}

		// 본문 이미지 추출
		String content = (String) map.get("content");
		Pattern pattern = Pattern.compile("<img[^>]+src=\"(/upload/board/[^\"\\s>]+)\"");
		Matcher matcher = pattern.matcher(content);

		int order = 1;
		while (matcher.find()) {
			String fullSrc = matcher.group(1);
			String fileName = fullSrc.replace("/upload/board/", "");

			BoardImg img = new BoardImg();
			img.setBoardNo(boardNo);
			img.setImgPath("/upload/board/");
			img.setImgRename(fileName);
			img.setImgOriginal(fileName);
			img.setImgOrder(order++);
			imgList.add(img);
		}

		if (!imgList.isEmpty()) {
			mapper.insertUploadList(imgList);
		}

		return boardNo;
	}

	@Override
	public int boardDelete(int boardNo) {
		mapper.deleteBoardImg(boardNo);
		return mapper.boardDelete(boardNo);
	}

	@Override
	public int boardUpdate(Map<String, Object> paramMap, MultipartFile thumbnail) throws Exception {

		// 1. 게시글 기본 정보 및 봉사 정보 업데이트
		int result = mapper.boardUpdate(paramMap);

		if (result > 0) {
			int boardNo = Integer.parseInt(paramMap.get("boardNo").toString());
			List<BoardImg> imgList = new ArrayList<>();

			// 2. 썸네일 업데이트
			if (thumbnail != null && !thumbnail.isEmpty()) {
				String rename = Utility.fileRename(thumbnail.getOriginalFilename());
				thumbnail.transferTo(new java.io.File(boardImageFolderPath + rename));

				BoardImg img = new BoardImg();
				img.setBoardNo(boardNo);
				img.setImgPath(boardImageWebPath);
				img.setImgRename(rename);
				img.setImgOriginal(thumbnail.getOriginalFilename());
				img.setImgOrder(0);
				imgList.add(img);
			}

			// 3. 본문 이미지 추출
			String content = (String) paramMap.get("content");
			Pattern pattern = Pattern.compile("<img[^>]+src=\"(/upload/board/[^\"\\s>]+)\"");
			Matcher matcher = pattern.matcher(content);

			int order = 1;
			while (matcher.find()) {
				String fullSrc = matcher.group(1);
				String fileName = fullSrc.replace("/upload/board/", "");

				BoardImg img = new BoardImg();
				img.setBoardNo(boardNo);
				img.setImgPath("/upload/board/");
				img.setImgRename(fileName);
				img.setImgOriginal(fileName);
				img.setImgOrder(order++);
				imgList.add(img);
			}

			// 4. 이미지 기록 갱신 (중복 변수 선언 result 삭제함)
			mapper.deleteBoardImg(boardNo);
			if (!imgList.isEmpty()) {
				mapper.insertUploadList(imgList);
			}
		}
		return result;
	}
}