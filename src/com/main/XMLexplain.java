package com.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLexplain extends DefaultHandler {
	
	@Override
	public void startDocument() throws SAXException {
	// TODO Auto-generated method stub
	super.startDocument();
	System.out.println("准备开始读取配置 文件   start");
	}
	private String st;
	private ArrayList<HashMap<String,String>> al=new ArrayList<HashMap<String,String>> ();
	private HashMap<String,String> hm;
	
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		st=localName; 
		if(st=="file"){ hm=new HashMap<String, String>(); System.out.println("new 一个 hashmap"); }
	}
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub 
			if(st=="fileName"||st=="fileSize"||st=="fileDate"){ System.out.println(st+"   "+new String(ch,start,length)); hm.put(st, new String(ch,start,length)); } 
			st="";
		}
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			if(localName=="file"){ al.add(hm); }// System.out.println("放进去 一个 hashmap"+hm.get("fileName")+hm.get("fileSize")+hm.get("fileDate"));
			
		}
		public ArrayList<HashMap<String,String>> getArrayList()
		{
			return al;
		}
}
