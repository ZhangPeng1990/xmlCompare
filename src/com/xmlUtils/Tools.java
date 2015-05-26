package com.xmlUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;

public class Tools {

	public static boolean createFolder(String path){
		boolean success = false;
		File file = new File(path);
		if(!file.exists()){
			success = file.mkdirs();
		}
		return success;
	}
	
	/**
	 * 创建一个空的dom4j的Document对象
	 * @return
	 */
	public static Document createDocument(){
		Document doc = DocumentHelper.createDocument();
		return doc;
	}
	
	public static void writeToFile(String conent, String path){
		File file = new File(path);
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			fos.write(conent.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 把File对象转换成dom4j的Document对象
	 * @param path
	 * @return
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public static Document getDocumentByFile(File file) throws FileNotFoundException, DocumentException{
		SAXReader reader = new SAXReader();
        Document document = null;
		try {
			reader.setEncoding("UTF-8");
			document = reader.read(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			System.out.println("以UTF-16编码重新读取");
			reader.setEncoding("UTF-16");
			document = reader.read(new FileInputStream(file));
		}
		return document;
	}
}
