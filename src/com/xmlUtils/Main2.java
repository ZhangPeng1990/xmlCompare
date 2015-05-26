package com.xmlUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.junit.Test;

/**
 * 通过读取配置文件来加载xml节点
 * @author zp
 *
 */
public class Main2 {

	StringBuilder selfSb = new StringBuilder();
	StringBuilder standerdSb = new StringBuilder();
	Map<String, String[]> xPathMap = new HashMap<String, String[]>();
	final String alignValue = "ALIGNVALUE";
	
	/**
	 * 创建两个新的文件夹
	 * 创建规则：在同一路径下获取文件夹名前边加 ：new_
	 */
	public String createFolder(String path){
		
		String[] temp = path.split("\\\\");
		String newPath = "";
		
		if(temp != null && temp.length > 0){
			String tempPath = "";
			for(int i = 0; i < temp.length - 1; i++){
				tempPath += temp[i] + "\\";
			}
			newPath = tempPath + "new_" + temp[temp.length - 1];
		}
		Tools.createFolder(newPath);
		return newPath;
	}
	
	/**
	 * 初始化，把配置文件xml的数据放到xPathMap中
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public void init() throws FileNotFoundException, DocumentException{
		URL url = this.getClass().getResource("config.xml");
		File configFile = new File(url.getFile());
		Document configDoc = Tools.getDocumentByFile(configFile);
		
		XPath xpathSelector;
		
		String key = "";
		String[] values;
		
		xpathSelector = DocumentHelper.createXPath("//valuePath");
		Element alignValueElement = (Element) xpathSelector.selectSingleNode(configDoc);
		values = new String[1];
		values[0] = (alignValueElement != null ? alignValueElement.getTextTrim() : "");
		key = this.alignValue;
		xPathMap.put(key, values);
		
		
		xpathSelector = DocumentHelper.createXPath("//resource");
		List<Element> resourceElement = xpathSelector.selectNodes(configDoc);
		if(resourceElement != null && resourceElement.size() > 0){
			for(int i = 0; i < resourceElement.size(); i++){
				int orderNum = i + 1;
				xpathSelector = DocumentHelper.createXPath("//resource[" + orderNum + "]/key");
				Element keyElement = (Element) xpathSelector.selectSingleNode(configDoc);
				key = keyElement.getText();
				
				xpathSelector = DocumentHelper.createXPath("//resource[" + orderNum + "]/values/value");
				List<Element> valuesElement = xpathSelector.selectNodes(configDoc);
				if(valuesElement != null && valuesElement.size() > 0){
					values = new String[valuesElement.size()];
					for(int a = 0; a < valuesElement.size(); a++){
						int orderNum2 = a + 1;
						xpathSelector = DocumentHelper.createXPath("//resource[" + orderNum + "]/values/value[" + orderNum2 + "]");
						Element valueElement = (Element) xpathSelector.selectSingleNode(configDoc);
						values[a] = valueElement.getText();
					}
					xPathMap.put(key, values);
				}
			}
		}
	}
	
	@Test
	public void start(){
		try {
			init();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		String newSelfPath = this.createFolder(Paths.selfForderPath);
		String newStandardPath = this.createFolder(Paths.standardForderPath);
		
		XmlTool tool = new XmlTool(Paths.standardForderPath, Paths.selfForderPath);
 		List<File> standaredList = tool.standaredList;
		List<File> selfsList = tool.selfsList;
		
		int time = standaredList.size();
		for(int a= 0; a < time; a++){
			File[] file = tool.provideXML(standaredList, selfsList);
			if(file != null && file.length == 2){
				//对比创建
				try {
					selfSb.append("\n#################################################################")
					.append("\n"+file[1].getName())
					.append("\n#################################################################\n");
					
					standerdSb.append("\n#################################################################")
					.append("\n"+file[0].getName())
					.append("\n#################################################################\n");
					
					analysis(tool.getDocumentByFile(file[1]), tool.getDocumentByFile(file[0]));
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//对比创建
			}
			
		}
		
		Tools.writeToFile(selfSb.toString(), newSelfPath + "\\eco.txt");
		Tools.writeToFile(standerdSb.toString(), newStandardPath + "\\bre.txt");
	}
	
	@SuppressWarnings("unchecked")
	public void analysis(Document selfDoc, Document standardDoc){
		
		Element selfDocrootElt = selfDoc.getRootElement();//获取根节点
		String selfDocdefNamespace = selfDocrootElt.getNamespaceURI();
		String selfDocxsiNamespace = selfDocrootElt.getNamespaceForPrefix("xsi") != null ? selfDocrootElt.getNamespaceForPrefix("xsi").getURI() : null;
		
		XPath xpathSelector;
		Map<String, String> selfNameSpaceMap = new HashMap<String, String>();
		
		//处理名称空间
		if(selfDocdefNamespace != null)
		{
			selfNameSpaceMap.put("defu", selfDocdefNamespace);
		}
	    if(selfDocxsiNamespace != null)
	    {
	    	selfNameSpaceMap.put("xsi", selfDocxsiNamespace);
	    }
		//处理名称空间
	    
	    Element standardDocrootElt = standardDoc.getRootElement();//获取根节点
		String standardDocdefNamespace = standardDocrootElt.getNamespaceURI();
		String standardDocxsiNamespace = standardDocrootElt.getNamespaceForPrefix("xsi") != null ? standardDocrootElt.getNamespaceForPrefix("xsi").getURI() : null;
		
		Map<String, String> standardDocNameSpaceMap = new HashMap<String, String>();
		
		//处理名称空间
		if(standardDocdefNamespace != null)
		{
			standardDocNameSpaceMap.put("defu", standardDocdefNamespace);
		}
	    if(standardDocxsiNamespace != null)
	    {
	    	standardDocNameSpaceMap.put("xsi", standardDocxsiNamespace);
	    }
		//处理名称空间
		
	    
		xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem");
		xpathSelector.setNamespaceURIs(selfNameSpaceMap);
		List<Element> listElement = xpathSelector.selectNodes(selfDoc);
		if(listElement != null){
			for(int i = 0; i < listElement.size(); i++){
				
				String selectValue = "";
				int orderNum = i + 1;
				
				Set<String> tempKeys = xPathMap.keySet();
				List<String> keys = new ArrayList<String>();
				keys.addAll(tempKeys);
				
				String selectValueXpath = xPathMap.get(this.alignValue)[0];
				selectValueXpath = selectValueXpath.replaceAll("###orderNum###", String.valueOf(orderNum));
				
				xpathSelector = DocumentHelper.createXPath(selectValueXpath);
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecosequenceElement = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecosequenceElement != null){
					selectValue = ecosequenceElement.getText();
				}
				
				for(int v = 0; v < keys.size(); v++){
					
					String key = keys.get(v);
					key = key.replace("###orderNum###", String.valueOf(orderNum));
					xpathSelector = DocumentHelper.createXPath(key);
					xpathSelector.setNamespaceURIs(selfNameSpaceMap);
					Element ecoElement = (Element) xpathSelector.selectSingleNode(selfDoc);
					
					if(ecoElement != null){
						String[] values = xPathMap.get(keys.get(v));
						for(String value : values){
							value = value.replace("###sequence###", String.valueOf(selectValue));
							xpathSelector = DocumentHelper.createXPath(value);
							xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
							Element breElement = (Element) xpathSelector.selectSingleNode(standardDoc);
							if(breElement != null){
								selfSb.append(ecoElement.getName() + "：" + ecoElement.getText() + "\n");
								standerdSb.append(ecoElement.getName() + "：" + breElement.getText() + "\n");
							}
						}
					}
				}
			
				selfSb.append("\n");
				standerdSb.append("\n");
			}
		}
		
		
	}
	
}
