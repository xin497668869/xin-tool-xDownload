package com.main;
 
import java.io.BufferedInputStream; 
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection; 
import java.net.URL;
import java.net.URLEncoder;

import android.os.Handler;
import android.os.Message;
import android.util.Xml.Encoding;
 

public class downHelper {
	
	public File findAdress(String t) throws IOException
	{
		File file=new File(t);
		 if(!file.isDirectory())
			file.mkdir();
		return file;
	}
	 
 
	public String down(String w,String t,String fileName,Handler handler)  
	{
		
		HttpURLConnection hconn=null;
		InputStream bs=null;
		try {
			
	//  fileName=URLEncoder.encode(fileName,"iso-8859-1");
			 File file=findAdress(t);
			file=new File(file+"/"+fileName);
	 fileName = URLEncoder.encode(fileName,"UTF-8").replace("+", "%20") ;  
	 
	 
     String sss=w+"/"+fileName;System.out.println("准备下载   "+sss);
		 
			URL url=new URL(sss);
			
			hconn=(HttpURLConnection) url.openConnection();
			
			try{
				System.out.println("aaa");
			hconn.connect();System.out.println("bbbb");}
			catch(ConnectException e)
			{
				Message msg=handler.obtainMessage();msg.arg1=-2;
				msg.obj="无法连接服务器，请先连wifi";
				msg.sendToTarget();
			}
			
			 System.out.println("----------  "+file+"/"+fileName);
			 
			 if(file.exists()&&file.toString().equals("fileList.xml")) return "已经存在该文件";
			 file.createNewFile();
			 try{ 
			 bs=hconn.getInputStream();}
			 catch(java.io.EOFException e){
					Message msg=handler.obtainMessage();msg.arg1=-2;
					msg.obj="通信不畅，无法下载，请重启服务器";
					msg.sendToTarget();
			 }
			 if(fileName.substring(fileName.length()-3, fileName.length())=="txt"){
				 BufferedWriter bw=new BufferedWriter(new FileWriter(file));
				 BufferedReader br=new BufferedReader(new InputStreamReader(bs));
				 String kk;
			
				 while((kk=br.readLine())!=null)
				 {
					 bw.write(kk);
					 System.out.println("正在下载2"+kk.length());
				 } 
				 bw.flush();
				 bw.close(); br.close(); 
			 }else{ 
				 BufferedInputStream bis=new BufferedInputStream(bs);
				 BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
				 byte[] byt=new byte[1024*5];
				 int len=0;
				  
				 int sum = 0;
				 int slen=hconn.getContentLength();
				 System.out.println("lennnnnnnn +  "+slen);
				 int k=-1;
				 while((len=bis.read(byt))>-1)
				 {
					 bos.write(byt,0,len); 
					 sum+=len;
					 
					 if(k!=sum*20/slen)	//没20分之1更新一次
					 {
						 k=sum*20/slen;
						 if(k==20) break;
						 Message msg=handler.obtainMessage();
						 msg.arg2=1;
						 msg.arg1=k;
						System.out.println(k+"   "+sum+"       " +slen+"    !!  "+sum*20/slen);
						 msg.sendToTarget(); 
					 }
				 }			 
				
				 bos.flush();
				 bis.close(); bos.close();
			 }
			 System.out.println("ddasdfasdfasdfddfasdfasdf asdf    "+fileName.equals("fileList.xml"));
			 bs.close();
			 Message msg=handler.obtainMessage();msg.arg1=20;
			 if(fileName.equals("fileList.xml")) {	System.out.println("准备发送"); msg.arg2=-1;	 }
			 msg.sendToTarget();
			 
			 return "下载成功";
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("asdfasdfasdf");
			e.printStackTrace();
		} 
		return  "下载失败";
	}

}
