package edu.kh.project.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Value("${board.image.web-path}")
	private String boardImageWebPath;

	@Value("${board.image.folder-path}")
	private String boardImageFolderPath;

	@Override
	public int boardInsert(Map<String, Object> map, MultipartFile thumbnail) throws Exception {
		if ("Y".equals(map.get("boardLock"))) {
			map.put("encPw", bcrypt.encode((CharSequence) map.get("boardPw")));
		} else {
			map.put("encPw", null);
		}

		int result = mapper.boardInsert(map);
		if (result == 0)
			return 0;

		int boardNo = Integer.parseInt(map.get("boardNo").toString());
		List<BoardImg> imgList = new ArrayList<>();

		if (thumbnail != null && !thumbnail.isEmpty()) {
			String rename = Utility.fileRename(thumbnail.getOriginalFilename());

			thumbnail.transferTo(new java.io.File(boardImageFolderPath + rename));

			BoardImg img = new BoardImg();
			img.setBoardNo(boardNo);
			img.setImgPath(boardImageWebPath);
			img.setImgRename(rename);
			img.setImgOriginal(thumbnail.getOriginalFilename());
			img.setImgOrder(0); // 썸네일은 무조건 0번
			imgList.add(img);
		}

		String content = (String) map.get("content");
		Pattern pattern = Pattern.compile("<img[^>]+src=\"(/upload/board/[^\"\\s>]+)\"");
		Matcher matcher = pattern.matcher(content);

		int order = 1;
		while (matcher.find()) {
			String fullSrc = matcher.group(1);
			log.debug("fullSrc = " + fullSrc);
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

		int result = mapper.boardUpdate(paramMap);

		if (result > 0) { // 수정 성공 시 이미지 처리
			int boardNo = Integer.parseInt(paramMap.get("boardNo").toString());

			List<BoardImg> imgList = new ArrayList<>();

			if (thumbnail != null && !thumbnail.isEmpty()) {
				String rename = Utility.fileRename(thumbnail.getOriginalFilename());
				thumbnail.transferTo(new java.io.File(boardImageFolderPath + rename));

				BoardImg img = new BoardImg();
				img.setBoardNo(boardNo);
				img.setImgPath(boardImageWebPath);
				img.setImgRename(rename);
				img.setImgOriginal(thumbnail.getOriginalFilename());
				img.setImgOrder(0); // 썸네일은 0번
				imgList.add(img);

				Map<String, Object> delMap = new HashMap<>();
				delMap.put("boardNo", boardNo);
				delMap.put("imgOrder", 0);
			}

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

			mapper.deleteBoardImg(boardNo);

			if (!imgList.isEmpty()) {
				result = mapper.insertUploadList(imgList);
			}
		}

		return result;
	}

}
