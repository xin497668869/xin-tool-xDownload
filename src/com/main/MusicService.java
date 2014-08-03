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

public class MusicService extends Service {		//��.mp3һ������׺ֻ����3λ�������ļ�
	MediaPlayer mp=new MediaPlayer();
	Handler mainHandler,SeekBarHandler;
	Object k=new Object();

	
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Notification notification= new Notification(R.drawable.qq4,"С��������",System.currentTimeMillis());
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
		
	System.out.println("MusicService���ƻ���");
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
			mainHandler=handler;		//�����̵߳�handler�Ž�ȥ
		}
		public void setHandler2(Handler handler)
		{
			SeekBarHandler=handler;		//��SeekBar�̵߳�handler�Ž�ȥ
		}
		public String getS()
		{
			return "�����ҵĶ���b";
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
				msg.obj="������سɹ�,������";
				msg.sendToTarget();
				System.out.println("�������ø��   �ļ�"+fileName+this.flag);
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
				//System.out.println("������û��set");
				 if(!flag) { System.out.println("��return��");return ;}
				 lrc=new ArrayList<Lrc>() ;
			BufferedReader br;
			try {
				lrc.clear();
				System.out.println("����ļ��� "+fileName);
				 br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"GB2312"));		
				 String llrc;int time = 0;
			 
				 Pattern pattern=Pattern.compile("\\[\\d{2}:\\d{2}\\.\\d{2}\\]+");
				 int tt=120;
				 int end = 0,j;
				 while((llrc=br.readLine())!=null)		//������ʽ��ȡ����ļ�
				 {	
					 Matcher matcher=pattern.matcher(llrc);
					  if(tt--<=0) break;//�������ã��������Ȳ��ܳ���200
					  
					 System.out.println("ƥ��     ���  "+llrc.matches("^\\[\\d{2}:\\d{2}\\.\\d{2}\\].*")+ llrc);
					 
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
						 System.out.println(j+"�Ž�ȥ���� "+timeLrc[j]+"      "+llrc.substring(end));
						 lrc.add(new Lrc(timeLrc[j],llrc.substring(end))); 
					 }
					 
			// System.out.println(llrc+"   ! "+time);
				 }
				 Collections.sort(lrc);
				 for(Lrc l:lrc)
				 {
					 System.out.println("������  "+l.time+"  "+l.content);
				 }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		 
			}
			public int modifyLrc(int now) //���¸��ʱ��Ƭ
			{	 System.out.println("��ʼ����ʱ��Ƭ");
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
					//System.out.println("�̻߳�ִ����"+flag);
					if(end) return ;
					if(stop){ System.out.println("׼��wait"+"   "+Thread.interrupted()); 
						
						synchronized (k) {
							
						 try {
							k.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						}System.out.println("�˳� wait");
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
					//System.out.println("��service �з��͸�������λ��");
					msg.sendToTarget();
					playPosition=mp.getCurrentPosition()/1000;
					}
					
					if(flag){	//��� û�и���ļ��Ͳ�ִ��
					//	System.out.println("����ִ�и���ļ�"+lrc.size());
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
		int playPositionmm;//�ж��Ƿ��б��˵���������
		class downLrcRun implements  Runnable  { 
			  	String address,fileName;
			  	public downLrcRun(String address,String fileName) { 
			  		this.address=address;
			  		this.fileName=fileName;
			  	}
					public void run() {
						findLrc findLrc=new findLrc();
						if(!findLrc.downloadLrc(address,fileName)) //�޷������¸��
						{Message msg=mainHandler.obtainMessage(); msg.arg1=-2 ;msg.obj="���粻ͨ,�޷����ظ��"; msg.sendToTarget();} 
						else lrcRun.setFlag(true,fileName);
						System.out.println("asdfkd  okokokkokokokok");

					}   
			}
		
		
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			// TODO Auto-generated method stub
			if(flags==1)//�û��ƶ�����������
			{	data.setDataPosition(0);
				int msec=data.readInt();
				
				mp.seekTo(msec*1000);
				System.out.println("�޸�"+msec*1000);
				return true;
			}
			
			data.setDataPosition(0);
			String st=data.readString();
			System.out.println(st+"    "+mp.isPlaying());
			if(st.equals("����"))
			{	if(lrcRun!=null)
				lrcRun.endPlaying(true);
			mp.reset();					//�������ò��������ö�catch
			String address=data.readString();
			String fileName=data.readString();	//����������
			address=address+fileName;			//���ȫ·��������·��
			
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
			System.out.println("ľ�������ļ�");
			Thread downLrcThread=new Thread(new downLrcRun(address, file.toString()));
			downLrcThread.start();
			
			}
			
			//׼�����ͳ�����Ϣ��SeekBar

			System.out.println("���벥��");
			try {
				mp.setDataSource(address);
				System.out.println("��Դ�������");
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
					mp.prepare();			//prepare������
					System.out.println("׼�����");
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("��ʼ����");
			mp.start();	//��ʼ���Ÿ���
			Message msg=SeekBarHandler.obtainMessage(); 
			msg.arg1=mp.getDuration()/1000;
			msg.arg2=1;
			System.out.println("����ʱ��"+msg.arg1+"     ");
			msg.sendToTarget();
			}
			else if(st.equals("��������")){
				mp.start();
				synchronized (k) {
					System.out.println("��ľ�л����߳�");
					System.out.println(stop);
					stop=false;
					k.notifyAll();
					System.out.println("��ľ�л����߳�");
				}
				
			}
			else if(st.equals("��ͣ")){
				mp.pause();System.out.println("��ͣ����-------- ----");
				 stop=true;
				 
			}
			else if(st.equals("ֹͣ"))
			{	lrcThread.interrupt();
				mp.stop();System.out.println("ֹͣ���ţ�������������        !"+st);
				Message msg=SeekBarHandler.obtainMessage(); 
				 
				msg.arg2=-1;
				System.out.println("����ʱ��"+msg.arg1+"     ");
				msg.sendToTarget();
				
			}
			data.setDataPosition(0);
			return super.onTransact(code, data, reply, flags);
		}
		
	}
	

}