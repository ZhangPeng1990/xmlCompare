package com.xmlUtil.getDiff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class XmlTool {
	
	public List<File> standaredList = new ArrayList<File>();
	public List<File> selfsList = new ArrayList<File>();
	
	Element e;
	XPath xpathSelector;
	Document newDoc;
	Element fatherElement;
	int elementNum = 0;
	Map<String, String> nameSpaceMap = new HashMap<String, String>();
	
	StringBuilder selfSb = new StringBuilder();
	StringBuilder standerdSb = new StringBuilder();
	StringBuilder tempSelfSb = new StringBuilder();
	StringBuilder tempStanderdSb = new StringBuilder();
	String standardForderName;
	String selfForderName;
	
	public XmlTool(){
		
	}
	
	/**
	 * 初始化时把两个文件夹中的xml文件分别放到两个集合中
	 * @param standardForderPath
	 * @param selfForderPath
	 */
	public XmlTool(String standardForderPath, String selfForderPath){
		
		File standardForder = new File(standardForderPath);
		standardForderName=standardForder.getName();
		File[] standareds = standardForder.listFiles();
		if(standareds != null && standareds.length > 0){
			for(File file : standareds){
				if(getFileType(file).equals("xml")){
					standaredList.add(file);
				}
			}
		}
		
		File selfForder = new File(selfForderPath);
		selfForderName=selfForder.getName();
		File[] selfs = selfForder.listFiles();
		if(selfs != null && selfs.length > 0){
			for(File file : selfs){
				if(getFileType(file).equals("xml")){
					selfsList.add(file);
				}
			}
		}
	}
	
	/**
	 * 每次提供两份xml文件用于对比
	 * xmls[0]为标准文件夹中取的xml，
	 * xmls[1]为待对比的xml文件。
	 */
	public File[] provideXML(List<File> standaredList,List<File> selfsList){
		File[] xmls = new File[2];
		
		for(File standaredXML : standaredList){
			String tempName = standaredXML.getName();
			int findNum = 0;
			for(int i = 0; i < selfsList.size(); i++){
				if(tempName.equals(selfsList.get(i).getName())){
					File selfXML = selfsList.get(i);
					xmls[0] = standaredXML;
					xmls[1] = selfXML;
					standaredList.remove(standaredXML);
					selfsList.remove(selfXML);
					findNum++;
					return xmls;
				}
			}
			if(findNum == 0){
				standaredList.remove(standaredXML);
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 获取文件类型并返回
	 * @param file
	 * @return
	 */
	public String getFileType(File file){
		String type = "";
		if(file.isFile()){
			String[] temp = file.getName().split("\\.");
			if(temp != null && temp.length > 0){
				type = temp[temp.length - 1];
			}
		}
		return type;
	}
	
	
	/**
	 * selfXml以standaredXml为标准进行对比，并调整节点顺序进行重组生成新的xml文件
	 * @param standaredXml
	 * @param selfXml
	 * @throws Exception 
	 */
	public void compareAndCreate(File standaredXml, File selfXml) throws Exception{
		
		Document standardDoc = this.getDocumentByFile(standaredXml);
		Document selefDoc = this.getDocumentByFile(selfXml);
		HashMap<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, standardDoc.getRootElement());
		map.put(2, selefDoc);
		Map<Integer, Object> returnMap = loopAndCreateXml(map);
		Document returnedDoc = (Document)returnMap.get(3);
		
		tempSelfSb = (StringBuilder) returnMap.get(4);
		tempStanderdSb = (StringBuilder) returnMap.get(5);
		
		
		selfSb.append("\n#################################################################")
		.append("\n"+selfXml.getName())
		.append("\n#################################################################\n");
		
		selfSb.append(tempSelfSb);
		
		standerdSb.append("\n#################################################################")
		.append("\n"+standaredXml.getName())
		.append("\n#################################################################\n");
		
		standerdSb.append(tempStanderdSb);
		tempSelfSb.replace(0, tempSelfSb.length(), "");
		tempStanderdSb.replace(0, tempStanderdSb.length(), "");
		
		File parentFile = standaredXml.getParentFile();
		String newPath = parentFile.getParent();
		if(!newPath.endsWith(File.separator)){
			newPath = newPath + File.separator;
		}
		newPath = newPath + "newXML" + File.separator;
		this.createFolder(newPath);
		
		writeDocToXMLFile(returnedDoc, newPath + selfXml.getName(), true);
		fatherElement = null;
        newDoc = null;
	}
	
	public void createFolder(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
	/**
	 * 根据地址把xml文件转换成dom4j的Document对象
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Document getDocumentByURI(String path) throws Exception{
		SAXReader reader = new SAXReader();
        Document document = reader.read(new FileInputStream(path));
		return document;
	}
	
	/**
	 * 把File对象转换成dom4j的Document对象
	 * @param path
	 * @return
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public Document getDocumentByFile(File file) throws FileNotFoundException, DocumentException{
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
	
	/**
	 * 创建一个空的dom4j的Document对象
	 * @return
	 */
	public Document createDocument(){
		Document doc = DocumentHelper.createDocument();
		return doc;
	}
	
	/**
	 * 把Document对象写进系统文件的xml文件中
	 * @param document
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public File writeDocToXMLFile(Document document, String uri, boolean isFormat) throws IOException{
		XMLWriter writer;
		
		if(isFormat){
			
			//通过 org.dom4j.io.OutputFormat 来设置XML文档输出格式
			OutputFormat format = OutputFormat.createPrettyPrint(); //设置XML文档输出格式
//			format.setEncoding("UTF-8"); //设置XML文档的编码类型
//			format.setSuppressDeclaration(true);
//			format.setIndent(true); //设置是否缩进
//			format.setIndent(" "); //以空格方式实现缩进
//			format.setNewlines(true); //设置是否换行
			writer = new XMLWriter(new FileWriter(uri),format);
		}else{
			writer = new XMLWriter(new FileWriter(uri));
		}
		
		// lets write to a file
        writer.write(document);
        writer.close();
        
        File file = new File(uri);
		return file;
	}
	
	/**
	 * 把Document对象转换成字符串
	 * @param doc
	 * @return
	 */
	public String documentToString(Document doc){
		if(doc != null){
			return doc.asXML();
		}
		return null;
	}
	
	/**
	 * 把String类型的xml转换成Document对象
	 * @param xml
	 * @return
	 * @throws DocumentException 
	 */
	public Document stringToDocument(String xml) throws DocumentException{
		Document document = DocumentHelper.parseText(xml);
		return document;
	}
	
	public Element loopXml(Element element, Document doc){
		for(Iterator<?> iterator = element.elementIterator(); iterator.hasNext();){
			e = (Element) iterator.next();
			loopXml(e, doc);
		}
		return e;
	}
	
	/**
	 * 根据节点名判断是否输出
	 * @param elementName
	 * @return
	 */
	public boolean isRead(String elementName){
		String[] names = Paths.unReadElement;
		for(String name : names){
			if(name.equals(elementName)){
				return false;
			}
		}
		return true;
	}
	
	
	public Double hasLgnore(String elementName){
		Double lgnore=null;
		for(String[] strs : Paths.lgnoreDeviationElement){
			for(String name : strs){
				if(name.equals(elementName)){
					return Double.valueOf(strs[0]);
				}
			}
		}
		return lgnore;
	}
	
	public HashMap<Integer, Object> loopAndCreateXml(HashMap<Integer, Object> map){
		
		
		if(newDoc == null){
			newDoc = this.createDocument();
		}
		Element standerdXmlElement = (Element) map.get(1);
		xpathSelector = DocumentHelper.createXPath(standerdXmlElement.getUniquePath());
		Element e = (Element)xpathSelector.selectSingleNode(((Document) map.get(2)));
		//new
		if(standerdXmlElement.getTextTrim() != null && isRead(standerdXmlElement.getName())){
			if(standerdXmlElement.getTextTrim() != null && e != null){
				if(!standerdXmlElement.getTextTrim().equals(e.getTextTrim())){
					 boolean printl=true;
					//误差处理
					try{
						Double value=Double.valueOf(e.getText());
						Double standerdValue=Double.valueOf(standerdXmlElement.getText());
						double difference=value - standerdValue;
						Double lgnore=hasLgnore(e.getName());
						if(lgnore !=null && Math.abs(difference) <= lgnore.doubleValue()){
							printl=false;
						}
					}catch(Exception exception){
					}
					if(printl){
						tempSelfSb.append(e.getName() + "：" + e.getText() + "\n");
						tempStanderdSb.append(standerdXmlElement.getName() + "：" + standerdXmlElement.getText() + "\n");
					}
				}
			}else if(standerdXmlElement.getTextTrim() != null && e == null){
				tempSelfSb.append("\n");
				tempStanderdSb.append(standerdXmlElement.getName() + "：" + standerdXmlElement.getText() + "\n");
			}
		}
		//new
		if(e != null){
			System.out.println("提取的节点名称：" + e.getQualifiedName());
			
			if(e.getParent() != null){
				xpathSelector = DocumentHelper.createXPath(e.getParent().getUniquePath());
				fatherElement = (Element)xpathSelector.selectSingleNode(newDoc);
			}
			
			if(fatherElement == null){
				fatherElement = newDoc.addElement(e.getQualifiedName(), e.getNamespaceURI());
				fatherElement.setAttributes(e.attributes());
				
				String spaceUri = "";
				Element rootElt = ((Document) map.get(2)).getRootElement();//获取根节点
				
				String defNamespace = rootElt.getNamespaceURI();
				//处理名称空间
				if(defNamespace != null)
				{
					nameSpaceMap.put("defu", defNamespace);
				}
				for(String nameSpace : Paths.namespaceForPrefix){
					spaceUri = rootElt.getNamespaceForPrefix(nameSpace) != null ? rootElt.getNamespaceForPrefix(nameSpace).getURI() : null;
					if(spaceUri != null){
						nameSpaceMap.put(nameSpace, spaceUri);
						fatherElement.addNamespace(nameSpace, spaceUri);
					}
				}
			    
			}else{
				fatherElement = fatherElement.addElement(e.getQualifiedName(), e.getNamespaceURI());
				fatherElement.setText(e.getText());
				fatherElement.setAttributes(e.attributes());
			}
		}
		
		HashMap<Integer, Object> newMap = new HashMap<Integer, Object>();
		newMap.put(2, (Document) map.get(2));
		
		for(Iterator<?> iterator = standerdXmlElement.elementIterator(); iterator.hasNext();){
			standerdXmlElement = (Element) iterator.next();
			
			newMap.put(1, standerdXmlElement);
			newMap.put(3, newDoc);
			
			loopAndCreateXml(newMap);
		}
		newMap.put(3, newDoc);
		newMap.put(4, tempSelfSb);
		newMap.put(5, tempStanderdSb);
		return newMap;
	}

	public String getStandardForderName() {
		return standardForderName+".txt";
	}

	public String getSelfForderName() {
		return selfForderName+".txt";
	}
}
