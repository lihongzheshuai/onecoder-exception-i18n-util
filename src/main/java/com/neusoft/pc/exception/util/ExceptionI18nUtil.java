package com.neusoft.pc.exception.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.neusoft.acap.pc.exception.PcExceptionCode;
import com.neusoft.acap.pc.exception.annotation.ExceptionInfoCN;
import com.neusoft.acap.pc.exception.annotation.ExceptionInfoUS;

/**
 * @author lihzh
 * @date 2012-3-31 下午3:40:06
 */
public class ExceptionI18nUtil {

	private static Class<?>[] exceptinClassArray = new Class[] { PcExceptionCode.class };

	private static Map<String, String> cnMap = new HashMap<String, String>(1000);
	private static Map<String, String> enMap = new HashMap<String, String>(1000);
	private static final String I18N_DIR_PATH = System.getProperty("user.dir")
			+ File.separator + "i18n" + File.separator + "new" + File.separator;
	private static final String I18N_EXP_FILE_NAME_PREFIX = "LocalStrings_";
	private static final String I18N_EXP_FILE_NAME_POSTFIX = ".properties";
	private static final String CODE_PRIFIX = "ACAP_PC_";

	// 是否追加
	private static final boolean IS_APPEND = false;

	// 是否重新生成
	private static final boolean IS_CREATE_NEW_ONE = true;

	/**
	 * @param args
	 * @author lihzh
	 * @date 2012-3-31 下午3:40:06
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		System.out.println("Begin to parse exception code at time: "
				+ new Date());
		for (Class clz : exceptinClassArray) {
			System.out.println("Begin to parse exception class: ["
					+ clz.getName() + "].");
			parseExceptionInfo(clz);
		}
		writeExpInfo();
		System.out.println("Exception i18n properties write finished at time: "
				+ new Date());
	}

	/**
	 * 向国际化文件写入异常信息
	 * 
	 * @author lihzh
	 * @date 2012-3-31 下午4:50:29
	 */
	private static void writeExpInfo() {
		validatePath();
		writeCN();
		writeUS();
	}

	/**
	 * 写入英文信息
	 * 
	 * @author lihzh
	 * @date 2012-3-31 下午6:22:01
	 */
	private static void writeUS() {
		File usFile = new File(createCNFilePath(Locale.US));
		prepareFile(usFile);
		doWriteFile(usFile, enMap, false);
	}

	/**
	 * 校验路径是否合法
	 * 
	 * @author lihzh
	 * @date 2012-3-31 下午5:08:23
	 */
	private static void validatePath() {
		File file = new File(I18N_DIR_PATH);
		if (!file.exists()) {
			throw new IllegalArgumentException("The path + [" + I18N_DIR_PATH
					+ "] is not exist");
		}
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("The path + [" + I18N_DIR_PATH
					+ "] is not a directory.");
		}
	}

	/**
	 * 写入中文国际化信息
	 * 
	 * @param file
	 * @author lihzh
	 * @date 2012-3-31 下午5:05:12
	 */
	private static void writeCN() {
		File cnFile = new File(createCNFilePath(Locale.CHINA));
		prepareFile(cnFile);
		doWriteFile(cnFile, cnMap, true);
	}

	/**
	 * 准备要写入的文件
	 * 
	 * @param file
	 * @author lihzh
	 * @date 2012-3-31 下午6:22:56
	 */
	private static void prepareFile(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (IS_CREATE_NEW_ONE) {
				if (file.delete()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					throw new ExceptionUtilException("File: [" + file.getName()
							+ "] can not be deleted.");
				}
			}
		}
		if (!file.canWrite()) {
			throw new ExceptionUtilException("File: [" + file.getName()
					+ "] can not be written.");
		}
	}

	/**
	 * 开始写入文件信息
	 * 
	 * @param file
	 * @author lihzh
	 * @param map
	 * @date 2012-3-31 下午5:20:16
	 */
	private static void doWriteFile(File file, Map<String, String> map,
			boolean isNeedTrans) {
		try {
			List<String> msgs = sortByCode(map, isNeedTrans);
			FileWriter fw = new FileWriter(file, IS_APPEND);
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

	private static List<String> sortByCode(Map<String, String> map,
			boolean isNeedTrans) {
		List<String> list = new ArrayList<String>();
		Iterator<Entry<String, String>> entryIt = map.entrySet().iterator();
		while (entryIt.hasNext()) {
			Entry<String, String> entry = entryIt.next();
			String code = entry.getKey();
			String codeStr = String.valueOf(code);
			String msg = entry.getValue();
			list.add(codeStr
					+ "="
					+ (isNeedTrans ? CharacterSetToolkit.toUnicode(msg, true)
							: msg));
		}
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				String code1 = o1.substring(0, o1.indexOf("="));
				String code2 = o2.substring(0, o2.indexOf("="));
				return code1.compareTo(code2);
			}

		});
		return list;
	}

	/**
	 * 生成国际化文件全路径
	 * 
	 * @param locale
	 * @return
	 * @author lihzh
	 * @date 2012-3-31 下午5:09:53
	 */
	private static String createCNFilePath(Locale locale) {
		return I18N_DIR_PATH + I18N_EXP_FILE_NAME_PREFIX + locale.toString()
				+ I18N_EXP_FILE_NAME_POSTFIX;
	}

	/**
	 * 解析异常注解
	 * 
	 * @param clz
	 * @author lihzh
	 * @date 2012-3-31 下午4:59:14
	 */
	private static void parseExceptionInfo(Class<?> clz) {
		Field[] fields = clz.getFields();
		for (Field field : fields) {
			parseFieldExpCN(field, clz);
			parseFieldExpUS(field, clz);
		}
	}

	/**
	 * 解析英文注解
	 * 
	 * @param field
	 * @param clz
	 * @author lihzh
	 * @date 2012-3-31 下午4:28:36
	 */
	private static void parseFieldExpUS(Field field, Class<?> clz) {
		ExceptionInfoUS anno = field.getAnnotation(ExceptionInfoUS.class);
		if (anno != null) {
			cacheUS(getCode(field, clz), anno);
		}
	}

	/**
	 * 缓存英文注解信息
	 * 
	 * @param anno
	 * @param clz
	 * @author lihzh
	 * @date 2012-3-31 下午4:41:09
	 */
	private static void cacheUS(String code, ExceptionInfoUS anno) {
		enMap.put(code, anno.value());
	}

	/**
	 * 解析中文注解，会优先从{@code ExceptionInfoCN}注解中读取，如果没有从
	 * {@code ExceptionDescription} 中读取
	 * 
	 * @param field
	 * @param clz
	 * @author lihzh
	 * @date 2012-3-31 下午4:28:49
	 */
	private static void parseFieldExpCN(Field field, Class<?> clz) {
		Annotation anno = field.getAnnotation(ExceptionInfoCN.class);
		if (anno != null) {
			cacheCN(getCode(field, clz), anno);
		}
	}

	/**
	 * 读取异常码信息，如果该异常码已经存在则抛出异常
	 * 
	 * @param field
	 * @return
	 * @author lihzh
	 * @date 2012-3-31 下午4:42:30
	 */
	private static String getCode(Field field, Class<?> clz) {
		try {
			String code = CODE_PRIFIX + field.getName();
			if (cnMap.containsKey(code) && enMap.containsKey(code)) {
				throw new IllegalArgumentException("The code: [" + code
						+ "] is exist. Current class is: [" + clz.getName()
						+ "], current field is: [" + field.getName() + "].");
			}
			return code;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 缓存中文异常信息
	 * 
	 * @param code
	 * @param field
	 * @param anno
	 * @author lihzh
	 * @date 2012-3-31 下午4:37:13
	 */
	private static void cacheCN(String code, Annotation anno) {
		if (anno instanceof ExceptionInfoCN) {
			ExceptionInfoCN anCN = (ExceptionInfoCN) anno;
			cnMap.put(code, anCN.value());
		}
	}

}
