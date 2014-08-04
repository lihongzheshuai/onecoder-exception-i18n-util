package com.neusoft.pc.exception.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author zhangzhzh
 * @date 2012-12-31 上午11:29:35
 */
public class ExceptionI18nCompareUtil {

	private static final String I18N_DIR_PATH = System.getProperty("user.dir")
			+ File.separator + "i18n" + File.separator;

	private static final String EN_FILE = I18N_DIR_PATH
			+ "aclome-messages_en_US.properties";

	private static final String CN_FILE = I18N_DIR_PATH
			+ "aclome-messages_zh_CN.properties";

	private static final String EN_FILE_OLD = I18N_DIR_PATH + "old"
			+ File.separator + "aclome-messages_en_US.properties";

	private static final String CN_FILE_OLD = I18N_DIR_PATH + "old"
			+ File.separator + "aclome-messages_zh_CN.properties";

	private static final String EN_FILE_NEW = I18N_DIR_PATH + "new"
			+ File.separator + "aclome-messages_en_US.properties";

	private static final String CN_FILE_NEW = I18N_DIR_PATH + "new"
			+ File.separator + "aclome-messages_zh_CN.properties";

	/**
	 * @param args
	 * @author zhangzhzh
	 * @date 2012-12-31 上午11:29:35
	 */
	public static void main(String[] args) {
		rewriteI18n(EN_FILE_OLD, EN_FILE_NEW, EN_FILE);
		rewriteI18n(CN_FILE_OLD, CN_FILE_NEW, CN_FILE);

		System.out.println("re write i18n file finished.");

	}

	private static void rewriteI18n(String oldFile, String newFile, String path) {
		try {
			Map<Integer, String> oldCN = getMessages(oldFile);
			Map<Integer, String> newCN = getMessages(newFile);
			updateNew(oldCN, newCN);
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			doWriteFile(file, newCN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateNew(Map<Integer, String> old,
			Map<Integer, String> msgs) {
		if (old.isEmpty()) {
			return;
		}
		for (Entry<Integer, String> msg : old.entrySet()) {
			if (!msgs.containsKey(msg.getKey())) {
				msgs.put(msg.getKey(), msg.getValue());
			}
		}
	}

	private static Map<Integer, String> getMessages(String path)
			throws IOException {
		Map<Integer, String> msgs = new HashMap<Integer, String>();
		File file = new File(path);
		if (!file.exists()) {
			return msgs;
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String msg = null;
		while ((msg = reader.readLine()) != null) {
			if (!msg.isEmpty()) {
				String code = msg.substring(0, msg.indexOf("="));
				msgs.put(Integer.valueOf(code), msg);
			}
		}
		reader.close();
		return msgs;
	}

	/**
	 * 开始写入文件信息
	 * 
	 * @param file
	 * @author lihzh
	 * @param map
	 * @date 2012-3-31 下午5:20:16
	 */
	private static void doWriteFile(File file, Map<Integer, String> map) {
		try {
			List<String> msgs = sortByCode(map);
			FileWriter fw = new FileWriter(file);
			for (String msg : msgs) {
				fw.append(msg).append("\r\n");
			}
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> sortByCode(Map<Integer, String> map) {
		List<String> list = new ArrayList<String>();
		Iterator<Entry<Integer, String>> entryIt = map.entrySet().iterator();
		while (entryIt.hasNext()) {
			Entry<Integer, String> entry = entryIt.next();
			String msg = entry.getValue();
			list.add(msg);
		}
		Collections.sort(list, new Comparator<String>() {

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
		return list;
	}

}
