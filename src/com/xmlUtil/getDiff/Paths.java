package com.xmlUtil.getDiff;

public class Paths {

	static final String  selfForderPath = "D:\\ECO\\ECO-XML-iqenergy-20131127";
	static final String   standardForderPath= "D:\\ECO\\IQenergy2";
	
	static final String[] namespaceForPrefix = {"xsi","HIP","SAP","SAP09","CS"};
	
	static final String standard = "STANDARD";
	static final String self = "SELF";
	
	/**
	 * TODO 
	 * 配置不会读取的节点，但是只要节点名匹配，
	 * 如在不同级下的同名节点不能单一用节点名判断，需考虑用xpath
	 */
	static final String[] unReadElement = {"Report-Date","UPRN-10","Calculation-Software-Name",
											"Calculation-Software-Version","Report-Author","Measure-Reference","Property-Type","Improved-Heating-Heats-All-Rooms","Heating-System-Cod"};
	
	/**误差允许节点**/
	//static final String[] lgnoreDeviationElement={"Annual-Cost-Saving","Lifetime-Cost-Saving","Annual-CO2-Saving","Lifetime-CO2-Saving"};
	static final String[][] lgnoreDeviationElement={
		{"0.0099","Annual-Cost-Saving"},
		{"0.9999","Lifetime-Cost-Saving"},
		
	};
}
