package tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter; 
import java.text.DateFormat; 
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FileTool {
	
	public ArrayList<HashMap<String,String>>readSDFile(String address){
		File file=new File(address);
		if(!file.isDirectory())return null;
		File[] list=file.listFiles();
		
		ArrayList<HashMap<String,String>> al=new ArrayList<HashMap<String,String>>();
		
		for(int i=0;i<list.length;i++)
		{System.out.println("正在读取SD数据"+i);
			HashMap<String,String> hm=new HashMap<String,String>();
			String kk=NumberFormat.getInstance().format(list[i].length()/1024+1);
			hm.put("fileSize", kk);
		    long modifiedTime = list[i].lastModified(); 
		    Date d = new Date(modifiedTime);
		    
			  hm.put("fileDate", DateFormat.getDateInstance().format(d));
			  hm.put("fileName", list[i].getName());
			  al.add(hm);
		}return al;
		
	}
	 
	 
}
