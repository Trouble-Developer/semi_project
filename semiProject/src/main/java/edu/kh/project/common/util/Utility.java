package edu.kh.project.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
	private static int seqNum = 1;

	public static String fileRename(String originalFileName) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = sdf.format(new Date());

		String number = String.format("%05d", seqNum++);
		if (seqNum == 100000)
			seqNum = 1;

		String ext = originalFileName.substring(originalFileName.lastIndexOf("."));

		return date + "_" + number + ext;
	}
}
