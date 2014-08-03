package com.main;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import tool.FileTool;
import tool.XMLRead;

import com.example.xdownload.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler; 
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast; 
public class Main extends ListActivity implements OnTouchListener {
	 
	
	Button downLoad;
	SimpleAdapter sa;
	TextView address;
	Button reflesh;
	static String fileName=null;//确定框下载传递回来是否需要下载，需要下载则有文件名,否则为null
	ArrayList<HashMap<String,String>> al=new ArrayList<HashMap<String,String>>();
 
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	 
		System.out.println("被生成"+savedInstanceState);
		setContentView(R.layout.main);
		
		super.onCreate(savedInstanceState); 
		
		Button reflesh =(Button)findViewById(R.id.reflesh);		//刷新按钮：刷新就是下载最新配置文件并读取里面的信息
		reflesh.setOnTouchListener(this);
		OnClickListener refleshOnclick=new OnClickListener() { 
			public void onClick(View v) {
				Thread thread=new Thread(new downThread("fileList.xml"));
				thread.start();
			}
		};
		reflesh.setOnClickListener(refleshOnclick);
		   
		ListView listview=(ListView)findViewById(android.R.id.list);
		listview.setOnTouchListener(this);
		

		
		//读取旧的配置文件
		File file=new File(Environment.getExternalStorageDirectory()+"/download/fileList.xml");
		if(file.exists())
		{XMLRead read=new XMLRead(); 
		read.read(Environment.getExternalStorageDirectory()+"/download/"+"fileList.xml");
		al=read.getArrayList();
		
		}
		else {Toast.makeText(Main.this, "第一次使用，准备下载配置文件更新", 1).show();
		
		al=new ArrayList<HashMap<String,String>>();
		
		refleshOnclick.onClick(null);}  
		 sa=new SimpleAdapter(this, al, R.layout.file_list, new String[]{"fileName","fileSize","fileDate"}, new int[]{R.id.list1,R.id.list2,R.id.list3});
		
		setListAdapter(sa);
		
 
		
		pb=(ProgressBar)findViewById(R.id.pbr);//进度条设置
		
	}
  
	protected void onResume() {	//查看是否在对话框中选择下载
		// TODO Auto-generated method stub
		super.onResume(); 
		System.out.println("有没有传值给intent.getStringExtra(fileName)= !!!!!!!!!"+fileName);
		if(fileName==null) return ; 
		Thread thead=new Thread(new downThread(fileName));
		thead.start();
		fileName=null;
		
	}
	boolean ischanging=false;
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(ischanging) return;
		HashMap<String, String> item = (HashMap<String, String>)l.getItemAtPosition(position);
		System.out.println(item.get("fileName"));
		Intent intent=new Intent();
		intent.putExtra("fileName", item.get("fileName"));
		
		 intent.setClass(Main.this, downSure.class); //测试而改变了
		Main.this.startActivity(intent);
  
		  
	}
	
	class downThread implements Runnable {
		private String name;
		public downThread(String a)
		{ 
			name=a;
		}
		public void run() {  
			downHelper dh=new downHelper();
			 System.out.println("http://192.168.252.1:8088/PhotoDownload"+"   "+name);
			  
			 System.out.println(Environment.getExternalStorageDirectory()+"/download");
			 name=dh.down("http://192.168.252.1:8088/PhotoDownload", Environment.getExternalStorageDirectory()+"/download", name,handler); 
			 
		}
	}
	ProgressBar pb;
	Handler handler=new Handler(){		//进度条设置
		public void handleMessage(android.os.Message msg) {
			
			if(msg.arg1==-2) //mes.arg1=-2 证明有错误信息返回
			{
				Toast.makeText(getApplicationContext(), msg.obj.toString(), 1).show();
			}
			
			pb.setProgress(msg.arg1);
			pb.setSecondaryProgress(msg.arg1+2 );
			if(pb.getVisibility()==View.GONE)
			{ 
			pb.setVisibility(View.VISIBLE);
			}
			
			if(msg.arg1==20)
			{	pb.setVisibility(View.GONE);
				if(msg.arg2==-1) //-1代表下载了配置文件;
				{	msg.arg2=0;
					System.out.println("列表下载完毕 ");
					XMLRead read=new XMLRead(); 
					read.read(Environment.getExternalStorageDirectory()+"/download/"+"fileList.xml");
				 
					//System.out.println(al.size()+" ]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
					//ACTIVITYList适配器 
				 
					al.clear();			//必须改成这样子才行，切记切记
					al.addAll(read.getArrayList()); 
					sa.notifyDataSetChanged();
					
					System.out.println(al.size()+" ]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
					Toast.makeText(getApplicationContext(), "刷新成功", 1).show();
					 
				}else	
				 Toast.makeText(getApplicationContext(), "下载成功", 1).show();
			 
			msg.arg1=0; 
			}
			System.out.println("收到！！=  "+msg.arg1);
		};
	};
 
	 
		float ex;
		float ey;;
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			
			switch(event.getAction())
			{
			case MotionEvent.ACTION_DOWN:ey=event.getY(); ex=event.getX(); break;
			case MotionEvent.ACTION_UP:
			{if( ( event.getY()-ey==0&&(ex-event.getX())>0)||(ex-event.getX())/Math.abs(ey-event.getY())>1) {Intent intent=new Intent(); intent.setClass(Main.this, ShowFile.class); Main.this.startActivity(intent);}}
			
			}
			if((ex-event.getX())*(ex-event.getX())+(ey-event.getY())*(ey-event.getY())<30000) ischanging=false;
			else ischanging=true;
		 	return false;
		}
	}



