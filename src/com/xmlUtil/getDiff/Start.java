package com.xmlUtil.getDiff;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.xmlUtils.Tools;

public class Start {
	
	public static void main(String[] args) throws Exception {
		XmlTool tool = new XmlTool(Paths.standardForderPath, Paths.selfForderPath);
		
 		List<File> standaredList = tool.standaredList;
		List<File> selfsList = tool.selfsList;
		
		int time = standaredList.size();
		for(int a= 0; a < time; a++){
			File[] xmls = tool.provideXML(standaredList, selfsList);
			if(xmls != null && xmls.length > 0){
				System.out.println("提取的标准文件：" + xmls[0].getName());
				System.out.println("提取的我方文件：" + xmls[1].getName());
				try {
					tool.compareAndCreate(xmls[0], xmls[1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		String newSelfPath = createFolder(Paths.selfForderPath);
		String newStandardPath = createFolder(Paths.standardForderPath);
		Tools.writeToFile(tool.standerdSb.toString(), newSelfPath + tool.getStandardForderName());
		Tools.writeToFile(tool.selfSb.toString(), newStandardPath + tool.getSelfForderName());
	}
	
	/**
	 * 创建两个新的文件夹
	 * 创建规则：在同一路径下获取文件夹名前边加 ：new_
	 */
	public static String createFolder(String path){
		
		String[] temp = path.split("\\\\");
		String newPath = "";
		
		if(temp != null && temp.length > 0){
			String tempPath = "";
			for(int i = 0; i < temp.length - 1; i++){
				tempPath += temp[i] + "\\";
			}
//			newPath = tempPath + "new_" + temp[temp.length - 1];
			newPath = tempPath;
		}
		Tools.createFolder(newPath);
		return newPath;
	}
}
