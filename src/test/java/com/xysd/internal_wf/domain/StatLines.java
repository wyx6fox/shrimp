/*
 * Copyright 2002-2007 tayoo company.
 */
package com.xysd.internal_wf.domain;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class StatLines {

	/** 
	* wuhongsheng_2005@sina.com 
	* 此程序用于计算代码行数 
	*/

	static long codeLines = 0;

	static long commentLines = 0;

	static long spaceLines = 0;

	static long classnum = 0;

	static long jspnum = 0;

	static long jsplines = 0;

	static long num = 0;

	public static void main(String[] args) {

		File f = new File("E:/bjdc_workspace/thwreport/src/main/java"); // 这里的目录要输入你要统计代码的文件夹目录
		if (f.exists()) {
			judge(f);
		}

		System.out.println("实际代码行数为：" + codeLines);
		System.out.println("注释代码行数为：" + commentLines);
		System.out.println("空行行数为：" + spaceLines);

		System.out.println("jsp的行数为：" + jsplines);
		num = codeLines + commentLines + spaceLines + jsplines;
		System.out.println("总的行数为：" + num);
		System.out.println("类的数目为：" + classnum);
		System.out.println("jsp的数目为：" + jspnum);

	}

	private static void judge(File f) {
		if (!f.isFile()) {
			File[] fs = f.listFiles();
			for (File child : fs) {
				judge(child);
			}
		}
		if (f.exists() && f.isFile()) {
			counter(f);
		}
	}

	private static void counter(File child) {

		FileReader fr;
		try {
			fr = new FileReader(child);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			boolean flag = false;
			boolean jsp = false;
			if (child.getName().endsWith(".java")) {
				classnum++;
			}
			if (child.getName().toLowerCase().endsWith(".jsp")) {
				jspnum++;
				jsp = true;

			}
			if (child.getName().endsWith(".java") || child.getName().toLowerCase().endsWith(".jsp")) {
				try {
					while ((line = br.readLine()) != null) {
						String l = line.trim();
						if (jsp)
							jsplines++;
						else {
							if (l.matches("^[[\\s]&&[^\\n]]*")) {
								spaceLines++;
							} else if (l.startsWith("/*") && l.endsWith("*/")) {
								commentLines++;
								flag = false;
							} else if (l.startsWith("/*")) {
								commentLines++;
								flag = true;
							} else if (l.endsWith("*/")) {
								commentLines++;
								flag = false;
							} else if (true == flag) {
								commentLines++;
							} else if (l.startsWith("//")) {
								commentLines++;
							} else {
								codeLines++;
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
