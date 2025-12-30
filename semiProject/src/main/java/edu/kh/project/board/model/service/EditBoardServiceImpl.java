package edu.kh.project.board.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class EditBoardServiceImpl implements EditBoardService {
	@Autowired
	private EditBoardMapper mapper;
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public int boardInsert(Map<String, Object> map) {

//		img_path = /upload/board/10231203.jpg
//				img_original_name=안됌
//				img_rename=10230213.png

		if (map.get("boardLock").equals("Y")) {
			map.put("encPw", bcrypt.encode((CharSequence) map.get("boardPw")));
		} else {
			map.put("encPw", null);
		}
		int result = mapper.boardInsert(map);

		if (result == 0)
			return 0;

		String content = (String) map.get("content");

		Pattern pattern = Pattern.compile("<img[^>]+src=\"(/upload/board/[^\"\\s>]+)\"");
		Matcher matcher = pattern.matcher(content);

		List<BoardImg> imgList = new ArrayList<>();
		int order = 0;

		while (matcher.find()) {
			String fullSrc = matcher.group(1); // /upload/board/xxx.png
			String fileName = fullSrc.replace("/upload/board/", "");

			BoardImg img = new BoardImg();
			img.setBoardNo((int) map.get("boardNo"));
			img.setImgPath("/upload/board/");
			img.setImgRename(fileName);
			img.setImgOriginal(fileName);
			img.setImgOrder(order++);

			imgList.add(img);
		}

		if (!imgList.isEmpty()) {
			mapper.insertUploadList(imgList);
		}

		return result;
	}

	@Override
	public int boardDelete(int boardNo) {
		mapper.deleteBoardImg(boardNo);
		return mapper.boardDelete(boardNo);
	}

	@Override
	public int boardUpdate(Map<String, Object> paramMap) {
		return mapper.boardUpdate(paramMap);
	}

}
