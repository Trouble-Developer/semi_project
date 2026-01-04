package edu.kh.project.common.scheduling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.kh.project.board.model.service.BoardService;
import lombok.extern.slf4j.Slf4j;


@EnableScheduling
@Component
@PropertySource("classpath:/" + "config.properties")
@Slf4j
public class ImageDeleteScheduling {
	@Autowired
	private BoardService service;

	@Value("${profile.image.folder-path}")
	private String profileImageFolderPath;

	@Value("${board.image.folder-path}")
	private String boardImageFolderPath;

	// @Scheduled(cron = "0 * * * * *") -> 1분마다 -> 테스트용

	@Scheduled(cron = "0 0 0 1 * *")	// 매일 자정
	public void scheduling() {
		log.info("더미 파일 삭제 스케줄러 시작");
		File boardFolder = new File(boardImageFolderPath);
		File memberFolder = new File(profileImageFolderPath);

		File[] boardArr = boardFolder.listFiles();
		File[] memberArr = memberFolder.listFiles();

		File[] imageArr = new File[boardArr.length + memberArr.length];

		System.arraycopy(memberArr, 0, imageArr, 0, memberArr.length);
		System.arraycopy(boardArr, 0, imageArr, memberArr.length, boardArr.length);

		List<File> serverImageList = Arrays.asList(imageArr);

		List<String> dbImageList = service.selectDbImageList();
		if (!serverImageList.isEmpty()) {
			for (File serverImage : serverImageList) {
				if (dbImageList.indexOf(serverImage.getName()) == -1) {
					serverImage.delete();
					log.info(serverImage.getName() + " 삭제");
				}
			}
		}
	}
}
