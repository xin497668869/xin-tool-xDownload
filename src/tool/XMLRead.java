package tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
 


import com.main.XMLexplain;
 

public class XMLRead {
	
	private ArrayList<HashMap<String,String>> al;
	
	public ArrayList<HashMap<String,String>> getArrayList()
	{
		return al;
	}
	
	public void read(String address){
	
	try {
		System.out.println(address);
		File f=new File(address);
		SAXParserFactory SAF=SAXParserFactory.newInstance();
		XMLReader parser=SAF.newSAXParser().getXMLReader();
		XMLexplain XML=new XMLexplain();
		parser.setContentHandler(XML);
		
		System.out.println("准备读取配置文件 数据");
		parser.parse(new InputSource(new FileInputStream(address)));
		al=XML.getArrayList();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
	System.out.println("dasfasdfasdfasdfasdf");
	
		
	}
}
