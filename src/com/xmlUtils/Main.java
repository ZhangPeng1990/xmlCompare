package com.xmlUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.junit.Test;

public class Main {

	StringBuilder selfSb = new StringBuilder();
	StringBuilder standerdSb = new StringBuilder();
	
	
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
	
	@Test
	public void start(){
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
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:Sequence");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecoSequence = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecoSequence != null){
					selectValue = ecoSequence.getText();
					
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + ecoSequence.getText() + "\']/defu:Sequence");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breSequence = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breSequence != null){
						selfSb.append(ecoSequence.getName() + "：" + ecoSequence.getText() + "\n");
						standerdSb.append(ecoSequence.getName() + "：" + breSequence.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:measureType");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecomeasureType = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecomeasureType != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Ofgem-Measure-Name");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breOfgem_Measure_Name = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breOfgem_Measure_Name != null){
						selfSb.append(ecomeasureType.getName() + "：" + ecomeasureType.getText() + "\n");
						standerdSb.append(ecomeasureType.getName() + "：" + breOfgem_Measure_Name.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:lifetimeOfMeasure");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecolifetimeOfMeasure = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecolifetimeOfMeasure != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Lifetime");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breLifetime = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breLifetime != null){
						selfSb.append(ecolifetimeOfMeasure.getName() + "：" + ecolifetimeOfMeasure.getText() + "\n");
						standerdSb.append(ecolifetimeOfMeasure.getName() + "：" + breLifetime.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:heatingFuel");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecoheatingFuel = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecoheatingFuel != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Heating-Measure/defu:Fuel");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breFuel = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breFuel != null){
						selfSb.append(ecoheatingFuel.getName() + "：" + ecoheatingFuel.getText() + "\n");
						standerdSb.append(ecoheatingFuel.getName() + "：" + breFuel.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:boilerIndexNo");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecoboilerIndexNo = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecoboilerIndexNo != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Heating-Measure/defu:Heating-System-PCDF-Index");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breHeating_System_PCDF_Index = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breHeating_System_PCDF_Index != null){
						selfSb.append(ecoboilerIndexNo.getName() + "：" + ecoboilerIndexNo.getText() + "\n");
						standerdSb.append(ecoboilerIndexNo.getName() + "：" + breHeating_System_PCDF_Index.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:efficiencySource");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecoefficiencySource = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecoefficiencySource != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Heating-Measure/defu:Main-Heating-Category");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breMain_Heating_Category = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breMain_Heating_Category != null){
						selfSb.append(ecoefficiencySource.getName() + "：" + ecoefficiencySource.getText() + "\n");
						standerdSb.append(ecoefficiencySource.getName() + "：" + breMain_Heating_Category.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:roofInputU");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecoroofInputU = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecoroofInputU != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Park-Home-Insulation/defu:Roof-U-Value");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breRoof_U_Value = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breRoof_U_Value != null){
						selfSb.append(ecoroofInputU.getName() + "：" + ecoroofInputU.getText() + "\n");
						standerdSb.append(ecoroofInputU.getName() + "：" + breRoof_U_Value.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:wallsInputU");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecowallsInputU = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecowallsInputU != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Park-Home-Insulation/defu:Wall-U-Value");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breWall_U_Value = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breWall_U_Value != null){
						selfSb.append(ecowallsInputU.getName() + "：" + ecowallsInputU.getText() + "\n");
						standerdSb.append(ecowallsInputU.getName() + "：" + breWall_U_Value.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:percentageMain");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecopercentageMain = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecopercentageMain != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Insulation-Measure/defu:Percentage-Main");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element brePercentage_Main = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(brePercentage_Main != null){
						selfSb.append(ecopercentageMain.getName() + "：" + ecopercentageMain.getText() + "\n");
						standerdSb.append(ecopercentageMain.getName() + "：" + brePercentage_Main.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:gValue");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecogValue = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecogValue != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Windows-Doors-Measure/defu:Window-g-Value");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breWindow_g_Value = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breWindow_g_Value != null){
						selfSb.append(ecogValue.getName() + "：" + ecogValue.getText() + "\n");
						standerdSb.append(ecogValue.getName() + "：" + breWindow_g_Value.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:uValue");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecouValue = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecouValue != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Windows-Doors-Measure/defu:Window-U-Value");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breWindow_U_Value = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breWindow_U_Value != null){
						selfSb.append(ecouValue.getName() + "：" + ecouValue.getText() + "\n");
						standerdSb.append(ecouValue.getName() + "：" + breWindow_U_Value.getText() + "\n");
					}
				}
				
				xpathSelector = DocumentHelper.createXPath("//defu:ECO/defu:EcoDataItem[" + orderNum + "]/defu:draughtProofed");
				xpathSelector.setNamespaceURIs(selfNameSpaceMap);
				Element ecodraughtProofed = (Element) xpathSelector.selectSingleNode(selfDoc);
				if(ecodraughtProofed != null){
					xpathSelector = DocumentHelper.createXPath("//defu:Improvement[defu:Sequence=\'" + selectValue + "\']/defu:Windows-Doors-Measure/defu:Draught-Proofing");
					xpathSelector.setNamespaceURIs(standardDocNameSpaceMap);
					Element breDraught_Proofing = (Element) xpathSelector.selectSingleNode(standardDoc);
					if(breDraught_Proofing != null){
						selfSb.append(ecodraughtProofed.getName() + "：" + ecodraughtProofed.getText() + "\n");
						standerdSb.append(ecodraughtProofed.getName() + "：" + breDraught_Proofing.getText() + "\n");
					}
				}
				
				selfSb.append("\n");
				standerdSb.append("\n");
			}
		}
		
		
	}
	
}
