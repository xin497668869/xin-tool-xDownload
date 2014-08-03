package com.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 



import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tool.findLrc;

import com.example.xdownload.R; 
import com.main.MusicService.myBinder.lrcRun;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.PatternMatcher;
import android.os.RemoteException;

public class MusicService extends Service {		//相.mp3一样，后缀只能是3位的音乐文件
	MediaPlayer mp=new MediaPlayer();
	Handler mainHandler,SeekBarHandler;
	Object k=new Object();

	
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Notification notification= new Notification(R.drawable.qq4,"小程序来的",System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, Main.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "titleeeeee",
		        "messageeee", pendingIntent);
		startForeground(1,notification);
		return new myBinder();
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
	  
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	lrcRun lrcRun;
	Thread lrcThread;
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		lrcRun.endPlaying(true);
		try {
			lrcThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.release();
		
	System.out.println("MusicService被破坏了");
	mp.release();
		super.onDestroy();
	}
	
	
	class Lrc implements Comparable<Lrc>{
		Lrc(int time,String content){ this.time=time; this.content=content;}
		int time;
		String content;
		public int compareTo(Lrc arg0) {
			// TODO Auto-generated method stub
			Lrc ll=(Lrc)arg0;
			if(this.time<ll.time) return -1;
			return 1;
		}
	}
	
	
	class myBinder extends Binder{
		public void setHandler1(Handler handler)
		{
			mainHandler=handler;		//把主线程的handler放进去
		}
		public void setHandler2(Handler handler)
		{
			SeekBarHandler=handler;		//把SeekBar线程的handler放进去
		}
		public String getS()
		{
			return "这是我的东东b";
		}
		public myBinder() {
			// TODO Auto-generated constructor stub
		}
		
		boolean stop=false;
		
		class lrcRun implements Runnable{
			
			Boolean flag=true,end=false;
			int time=0;
			public void setFlag(boolean flag,String fileName){
				this.flag=flag;
				if(flag)
				{this.setlrcRun(fileName, flag);
				Message msg=mainHandler.obtainMessage();msg.arg1=-2;
				msg.obj="歌词下载成功,加载中";
				msg.sendToTarget();
				System.out.println("重新设置歌词   文件"+fileName+this.flag);
				}
				 
			}
			public void stopPlaying(boolean stopp)
			{
				stop=stopp;
			}
			public void endPlaying(boolean endd)
			{
				end=endd;
			}			
		 
			 int []timeLrc=new int[10];
			 ArrayList<Lrc> lrc;
			 
			public void setlrcRun(String fileName,boolean flag){
			
				this.flag=flag;
				end=false;
				//System.out.println("到底有没有set");
				 if(!flag) { System.out.println("被return了");return ;}
				 lrc=new ArrayList<Lrc>() ;
			BufferedReader br;
			try {
				lrc.clear();
				System.out.println("歌词文件在 "+fileName);
				 br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"GB2312"));		
				 String llrc;int time = 0;
			 
				 Pattern pattern=Pattern.compile("\\[\\d{2}:\\d{2}\\.\\d{2}\\]+");
				 int tt=120;
				 int end = 0,j;
				 while((llrc=br.readLine())!=null)		//正则表达式读取歌词文件
				 {	
					 Matcher matcher=pattern.matcher(llrc);
					  if(tt--<=0) break;//测试所用，歌曲长度不能超过200
					  
					 System.out.println("匹配     结果  "+llrc.matches("^\\[\\d{2}:\\d{2}\\.\\d{2}\\].*")+ llrc);
					 
					  j=0;
					 while(matcher.find())
					 {
						 System.out.println(llrc.substring(matcher.start(),matcher.end())); 
						 System.out.println(llrc.substring(matcher.end()));
						  end=matcher.end();
						  time=((llrc.charAt(matcher.start()+1)-'0')*10+(llrc.charAt(matcher.start()+2)-'0'))*60*1000+((llrc.charAt(matcher.start()+4)-'0')*10+(llrc.charAt(matcher.start()+5)-'0'))*1000+(llrc.charAt(matcher.start()+7)-'0')*10+(llrc.charAt(matcher.start()+8)-'0');
						  timeLrc[j]=time; 
						  System.out.println("Timeeeeee   "+time+"    "+timeLrc[j]); 
						  j++;
					 }
					 while(j-->0){
						 System.out.println(j+"放进去数据 "+timeLrc[j]+"      "+llrc.substring(end));
						 lrc.add(new Lrc(timeLrc[j],llrc.substring(end))); 
					 }
					 
			// System.out.println(llrc+"   ! "+time);
				 }
				 Collections.sort(lrc);
				 for(Lrc l:lrc)
				 {
					 System.out.println("已排序  "+l.time+"  "+l.content);
				 }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		 
			}
			public int modifyLrc(int now) //更新歌词时间片
			{	 System.out.println("开始更新时间片");
				for(int i=0;i<lrc.size();i++)
				{//	System.out.println(st[i]+"   "+i+"   "+now);
					if(lrc.get(i).time>now) return i;
				}
				return lrc.size();
			}
			public void run() {
				// TODO Auto-generated method stub
//				if(stop)   { stop=true; Thread.interrupted(); }
//				if(pause){ stop=false; Thread.interrupted();   }
//					
				
				int i=0;
				
				while(true)
				{  
					//System.out.println("线程还执行吗"+flag);
					if(end) return ;
					if(stop){ System.out.println("准备wait"+"   "+Thread.interrupted()); 
						
						synchronized (k) {
							
						 try {
							k.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						}System.out.println("退出 wait");
						stop=false;
					 	}
					try {       
					Thread.sleep(20);      
				} catch (InterruptedException e) {
					// TODO Auto-generated  catch block
					e.printStackTrace();
				}finally{
			 
				}
					
					if(playPosition!=mp.getCurrentPosition()/1000)
					{Message msg=SeekBarHandler.obtainMessage();
					msg.arg2=mp.getDuration()/1000;
					msg.arg1=mp.getCurrentPosition()/1000;
					//System.out.println("在service 中发送歌曲播放位置");
					msg.sendToTarget();
					playPosition=mp.getCurrentPosition()/1000;
					}
					
					if(flag){	//如果 没有歌词文件就不执行
					//	System.out.println("可以执行歌词文件"+lrc.size());
					//	System.out.println(i+"   "+lrc.size()+"   "+lrc.get(i).time+"  "+mp.getCurrentPosition());
						if(i>=lrc.size()) continue;
					if(mp.getCurrentPosition()<lrc.get(i).time+30&&mp.getCurrentPosition()>lrc.get(i).time-30)
					{System.out.println(lrc.get(i).content+"    "+i); Message msg=mainHandler.obtainMessage();msg.obj=lrc.get(i).content;msg.sendToTarget();
					 System.out.println(mp.getCurrentPosition()+"   "+lrc.get(i).content+"   "+stop);
					 i++; 
					}
					else if(mp.getCurrentPosition()>lrc.get(i).time+30 ||(mp.getCurrentPosition()>playPositionmm+60||mp.getCurrentPosition()<playPositionmm-30) ) {   i=modifyLrc(mp.getCurrentPosition()); if(i>0){ Message msg=mainHandler.obtainMessage();msg.obj=lrc.get(i-1).content;msg.sendToTarget();} }
					playPositionmm=mp.getCurrentPosition();
					}	
					
					
				
				
				} 

				}
			
		}
		int playPosition;
		int playPositionmm;//判断是否有被人调整过歌曲
		class downLrcRun implements  Runnable  { 
			  	String address,fileName;
			  	public downLrcRun(String address,String fileName) { 
			  		this.address=address;
			  		this.fileName=fileName;
			  	}
					public void run() {
						findLrc findLrc=new findLrc();
						if(!findLrc.downloadLrc(address,fileName)) //无法下载新歌词
						{Message msg=mainHandler.obtainMessage(); msg.arg1=-2 ;msg.obj="网络不通,无法下载歌词"; msg.sendToTarget();} 
						else lrcRun.setFlag(true,fileName);
						System.out.println("asdfkd  okokokkokokokok");

					}   
			}
		
		
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			// TODO Auto-generated method stub
			if(flags==1)//用户移动歌曲进度条
			{	data.setDataPosition(0);
				int msec=data.readInt();
				
				mp.seekTo(msec*1000);
				System.out.println("修改"+msec*1000);
				return true;
			}
			
			data.setDataPosition(0);
			String st=data.readString();
			System.out.println(st+"    "+mp.isPlaying());
			if(st.equals("播放"))
			{	if(lrcRun!=null)
				lrcRun.endPlaying(true);
			mp.reset();					//重新设置播放器，好多catch
			String address=data.readString();
			String fileName=data.readString();	//单纯歌曲名
			address=address+fileName;			//歌词全路径，绝对路径
			
			//lrcThread. 
			File file=new File(address.substring(0, address.length()-3)+"lrc");
			if(file.exists())
			{lrcThread=new Thread(lrcRun=new lrcRun());
			lrcRun.setlrcRun(file.toString(),true);
			lrcThread.start();
			}else {
				lrcThread=new Thread(lrcRun=new lrcRun());
				lrcRun.setlrcRun(file.toString(),false);
				lrcThread.start();
			System.out.println("木有音乐文件");
			Thread downLrcThread=new Thread(new downLrcRun(address, file.toString()));
			downLrcThread.start();
			
			}
			
			//准备发送长度信息给SeekBar

			System.out.println("进入播放");
			try {
				mp.setDataSource(address);
				System.out.println("资源配置完成");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

				try {
					mp.prepare();			//prepare播放器
					System.out.println("准备完毕");
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("开始播放");
			mp.start();	//开始播放歌曲
			Message msg=SeekBarHandler.obtainMessage(); 
			msg.arg1=mp.getDuration()/1000;
			msg.arg2=1;
			System.out.println("歌曲时长"+msg.arg1+"     ");
			msg.sendToTarget();
			}
			else if(st.equals("继续播放")){
				mp.start();
				synchronized (k) {
					System.out.println("有木有唤醒线程");
					System.out.println(stop);
					stop=false;
					k.notifyAll();
					System.out.println("有木有唤醒线程");
				}
				
			}
			else if(st.equals("暂停")){
				mp.pause();System.out.println("暂停播放-------- ----");
				 stop=true;
				 
			}
			else if(st.equals("停止"))
			{	lrcThread.interrupt();
				mp.stop();System.out.println("停止播放！！！！！！！        !"+st);
				Message msg=SeekBarHandler.obtainMessage(); 
				 
				msg.arg2=-1;
				System.out.println("歌曲时长"+msg.arg1+"     ");
				msg.sendToTarget();
				
			}
			data.setDataPosition(0);
			return super.onTransact(code, data, reply, flags);
		}
		
	}
	

}