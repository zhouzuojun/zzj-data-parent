package com.zzj.data.util;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileArtrive {
	private static Log LOGGER = LogFactory.getLog(FileArtrive.class);
	/**
	 * 解压jar包中的驱动包，仅供关系型数据源使用
	 * @param path driverlib所在jar的路径
	 * @return 解压后的目录
	 */
	@SuppressWarnings("resource")
	public static String archive(String path) {
		String newPath = path.substring(0, path.lastIndexOf("lib/"));
		try {
			JarFile jarFile = new JarFile(path);
			Enumeration<JarEntry> jarEntrys = jarFile.entries();
			while (jarEntrys.hasMoreElements()) {
				JarEntry jarEntry = jarEntrys.nextElement();
				if (jarEntry.getName().contains("driverlib")) {
					File file = new File(newPath + "/" + jarEntry.getName());
					if (jarEntry.isDirectory()) {
						if (!file.exists()) {
							file.mkdirs();
						}
					} else {
						writeFile(jarFile.getInputStream(jarEntry) , file);
					}

				} else {
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newPath + "driverlib/";
	}

	/**
	 * 拷贝文件
	 * @param ips jar文件的输入流
	 * @param outputFile 输出文件
	 */
	private static void writeFile(InputStream ips, File outputFile){
		OutputStream ops = null;
		try {
			ops = new BufferedOutputStream((new FileOutputStream(outputFile)));
			byte[] buffer = new byte[1024];
			int nBytes = 0;
			while ((nBytes = ips.read(buffer)) > 0) {
				ops.write(buffer, 0, nBytes);
			}
			ops.flush();
		} catch (Exception e) {
			LOGGER.error("拷贝压缩文件失败", e);
		} finally {
			try {
				if (null != ops) {
					ops.close();
				}
				if (null != ips) {
					ips.close();
				}
			} catch (IOException e) {
				LOGGER.error("关闭流异常");
			}
		}
	}
}
