package com.neusoft.pc.exception.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author zhangzhzh
 * @date 2012-12-31 上午10:58:46
 */
public class ExceptionI18nSortUtil {

	private static final String I18N_DIR_PATH = System.getProperty("user.dir") + File.separator + "i18n"
			+ File.separator;

	private static final String EN_FILE = I18N_DIR_PATH + "aclome-messages_en_US.properties";

	private static final String CN_FILE = I18N_DIR_PATH + "aclome-messages_zh_CN.properties";

	/**
	 * @param args
	 * @author zhangzhzh
	 * @throws IOException 
	 * @date 2012-12-31 上午10:58:46
	 */
	public static void main(String[] args) throws IOException {
		sortAndRewrite(EN_FILE);
		sortAndRewrite(CN_FILE);

	}

	private static void sortAndRewrite(String path) throws IOException {
		List<String> msgs = new ArrayList<String>();
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String msg = null;
		while ((msg = reader.readLine()) != null) {
			if (!msg.isEmpty()) {
				msgs.add(msg);
			}
		}
		reader.close();
		
		Collections.sort(msgs, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				String code1 = o1.substring(0, o1.indexOf("="));
				String code2 = o2.substring(0, o2.indexOf("="));
				int num1 = Integer.valueOf(code1);
				int num2 = Integer.valueOf(code2);
				if (num1 > num2) {
					return 1;
				}
				if (num1 < num2) {
					return -1;
				}
				return 0;
			}
			
		});
		
		FileWriter writer = new FileWriter(file);
		for (String str : msgs) {
			writer.append(str).append("\r\n");
		}
		writer.close();
	}
}
