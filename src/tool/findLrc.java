package tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;

public class findLrc {
	
	public boolean downloadLrc(String address,String fileName) 
	{
		address=address.substring(0,address.length()-4)+".lrc";	//歌词放置位置
		fileName=fileName.substring(0,fileName.length()-4);	//没有后缀的歌名
		Pattern patternnn=Pattern.compile(".* - (.*)");
		Matcher matcherrr=patternnn.matcher(fileName);
		if(matcherrr.find())
		System.out.println(fileName=matcherrr.group(1));
		
		String UrlName = null;
		try {
			UrlName = URLEncoder.encode(fileName,"GBK");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("名字名是："+fileName+"  编码后变成"+UrlName);
		try {//http://lrc.aspxp.net/	服务器下载
			URL url=new URL("http://lrc.aspxp.net/?k="+UrlName+"&x=0&y=0&st=all");
			System.out.println("http://lrc.aspxp.net/?k="+UrlName+"&x=0&y=0&st=all");
			HttpURLConnection conn= (HttpURLConnection) url.openConnection();
			conn.connect();
			BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"GBK"));
			
			Pattern patternn=Pattern.compile("&");
			Matcher matcherr=patternn.matcher("&");
			System.out.println(matcherr.matches());
			 
			String st="(lrc.asp\\?id=[0-9]+&id1=[0-9]+&t=lrc&ac=dl)";
			 
			Pattern pattern=Pattern.compile(st);
			String stt,filename = null;
			boolean ok=false;
			System.out.println("下载网站地址源代码分析：");
			while((stt=br.readLine())!=null)
			{//http://lrc.aspxp.net/lrc.asp?id=122657&id1=49&t=lrc&ac=dl
			System.out.println(stt);
			Matcher matcher=pattern.matcher(stt);
			
			if(matcher.find())
			{System.out.println(filename=stt.substring(matcher.start(), matcher.end())); ok=true; break;} 
			
			}
			br.close();
			conn.disconnect();
			System.out.println("不知道找不找的到"+ok+"  ");
			
			if(!ok) return false;
			
			File file=new File(address);
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			  url=new URL("http://lrc.aspxp.net/"+filename);
			  System.out.println("http://lrc.aspxp.net/"+filename);
			  conn= (HttpURLConnection) url.openConnection();
			conn.connect();
			
			InputStreamReader isr=new InputStreamReader(conn.getInputStream(),"GBK");
			BufferedReader brr=new BufferedReader(isr);
			
			OutputStreamWriter ow= new OutputStreamWriter(new FileOutputStream(file),"GB2312");
			BufferedWriter bww=new BufferedWriter(ow);
			 
			while((stt=brr.readLine())!=null)
			{ 
			  bww.write(stt);
			  bww.newLine();
			 System.out.println("下载的歌词文件     ： "+stt);  
			} 
			 bww.close();
			brr.close();
			ow.close();
			isr.close();
			conn.disconnect();
			System.out.println("全部关闭");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
			//网络不通出错
			return false; 
		}
		return true;
			
		
	}
}
