package org.openremote.beehive.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openremote.beehive.file.EnumCharset;

/**
 * Utility class for File
 * 
 * @author Dan 2009-2-16
 * 
 */
public class FileUtil {

	private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());

    private FileUtil(){} 

	/**
	 * Reads a <code>FileInputStream</code> from a file with a specified file system path
	 * 
	 * @param path
	 *            a specified file system path,e.g C:\remotes\3m\MP8640
	 * @return <code>FileInputStream</code>
	 */
	public static FileInputStream readStream(String path){
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return fis;
	}

	/**
	 * Gets a list of content text from a file with a specified file system path
	 * 
	 * @param path
	 *            a specified file system path,e.g C:\remotes\3m\MP8640
	 * @return list of String
	 */
	public static List<String> getContentList(String path) {
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			return null;
		}
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					path)));
			String line = null;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("Close BufferedReader error");
			}
		}
		return list;
	}
	/**
	 * Gets a list of content text from a file with a specified file system path
	 * <code>FileInputStream</code>
	 * 
	 * @param fis a specified <code>FileInputStream</code>
	 * @return list of String
	 */
	public static List<String> getContentList(FileInputStream fis) {

		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try {
			br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("Close BufferedReader error");
			}
		}
		return list;
	}
	/**
	 * Gets a <code>PrintWriter</code> from a file with a specified file system
	 * path and charset, the defaut charet is UTF-8.
	 * 
	 * @param filePath
	 *            a specified file system path,e.g C:\remotes\3m\MP8640
	 * @param charset
	 *            a charset enumeration:<code>EnumCharset</code>
	 * @return a <code>PrintWriter</code>
	 */
	public static PrintWriter getPrintWriterFromFile(String filePath,
			EnumCharset... charset) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdir();
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		PrintWriter pw = null;
		try {
			if (charset != null) {
				if (charset.length != 0) {
					pw = new PrintWriter(new OutputStreamWriter(
							new FileOutputStream(filePath), charset[0].getValue()));
				} else {
					pw = new PrintWriter(new OutputStreamWriter(
							new FileOutputStream(filePath)));
				}
			} else {
				pw = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(filePath)));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			LOGGER.error("Get PrintWriter from " + filePath
					+ " occurs UnsupportedEncodingException");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LOGGER.error("Get PrintWriter from " + filePath
					+ " occurs FileNotFoundException");
		}
		return pw;
	}
	/**
	 * Writes a file on a file system with a specified file system path and
	 * charset, the defaut charet is UTF-8.
	 * 
	 * @param filePath
	 *            a specified file system path,e.g C:\remotes\3m\MP8640
	 * @param content
	 *            a content text to write
	 * @param charset
	 *            a charset enumeration:<code>EnumCharset</code>
	 */
	public static void writeFile(String filePath, String content, EnumCharset... charset) {
		File dir = new File(filePath).getParentFile();
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		PrintWriter pw = null;
		if (charset.length != 0) {
			pw = getPrintWriterFromFile(filePath, charset);
		} else {
			pw = getPrintWriterFromFile(filePath);
		}
		pw.println(content);
		pw.close();
	}
	/**
	 * Writes a String list to a file with a specified file system path and
	 * charset, the defaut charet is UTF-8.
	 * 
	 * @param filePath
	 *            a specified file system path,e.g C:\remotes\3m\MP8640
	 * @param contentList
	 *            content list
	 * @param charset
	 *            a charset enumeration:<code>EnumCharset</code>
	 */
	public void writeListToFile(String filePath, List<String> contentList,
			EnumCharset... charset) {
		File dir = new File(filePath).getParentFile();
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		PrintWriter pw = null;
		if (charset.length != 0) {
			pw = getPrintWriterFromFile(filePath, charset);
		} else {
			pw = getPrintWriterFromFile(filePath);
		}
		if (contentList != null) {
			int i = 1;
			for (String line : contentList) {
				i++;
				if (line != null && line.indexOf("\\xEF") < 0) {
					pw.println(line.toString());
				}
			}
		}
		pw.close();
	}
	/**
	 * Checks if the file is a image.
	 * 
	 * @param file
	 *            a file to check
	 * @return true if it is a image,false otherwise.
	 */
	public static boolean isImage(File file){
		String path = file.getAbsolutePath();
		String extension = path.substring(path.lastIndexOf('.') + 1);
		return extension.toLowerCase().matches("gif|png|jpg|bmp");
	}
	/**
	 * Checks if a file is a HTML
	 * 
	 * @param file
	 *            a file to check
	 * @return true if it is a HTML,false otherwise.
	 */
	public static boolean isHTML(File file){
		String path = file.getAbsolutePath();
		String extension = path.substring(path.lastIndexOf('.') + 1);
		return extension.toLowerCase().matches("html|htm");
	}
	/**
	 * Checks if it is ignored. lircrc,HTML,image,lircmd.conf will be ignored.
	 * 
	 * @param file
	 *            a file to check
	 * @return true if it is a ignored,false otherwise.
	 */
	public static boolean isIgnored(File file){
		String path = file.getAbsolutePath().toLowerCase();
		boolean isImage = isImage(file);
		boolean isLircrc = path.endsWith(".lircrc");
		boolean isLircmd = path.indexOf("lircmd.conf") != -1;
		boolean isHtml = isHTML(file);
		return isImage || isLircrc || isLircmd || isHtml;
	}
	/**
	 * Splits a file path by path separator
	 * 
	 * @param file
	 *            a file to split
	 * @return array of String including directory name and file name split by
	 *         path separator
	 */
	public static String[] splitPath(File file){
		String path = file.getAbsolutePath();
		if (path.startsWith("/")) {
			return path.split("/");
		} else {
			return path.split("\\\\");
		}
	}

}
