package com.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import tool.FileTool;

import com.example.xdownload.R;
import com.main.MusicService.myBinder;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter; 
import android.widget.TextView;
import android.widget.Toast;


public class ShowFile extends ListActivity implements OnTouchListener{
	
	myBinder binder;
	Parcel data=Parcel.obtain();
	Parcel reply=Parcel.obtain();
	TextView lrcTextView,timeShow;
	SeekBar sb;
	
	Handler main=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
		//	 updateLrc();
			if(msg.arg1==-2){ Toast.makeText(getApplicationContext(), msg.obj.toString(), 1).show(); return ;}
			 System.out.println(msg.arg1+"  "+msg.obj);
			String words=(String) msg.obj;
			 
			lrcTextView.setText(words);

		}
	};
	Handler main2=new Handler(){
		
		public void handleMessage(Message msg) {
			//主线程得到更新SeekBar的信息
			
			 
		 	 
		 	if(msg.arg2==-1){sb.setProgress(0); timeShow.setText(""); lrcTextView.setText("歌词显示区");return ;} //按了停止键停止 
			sb.setProgress(msg.arg1);
			sb.setMax(msg.arg2);
			sb.setSecondaryProgress(msg.arg2);
		};
	};
	
	class conn implements ServiceConnection{
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			  binder=(myBinder) service; 
			  binder.setHandler1(main);
			  binder.setHandler2(main2);
			  System.out.println("asdfkalskdfaklsd  fj;lajksdf;ljkasdf");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			 
		}
		
	}
	boolean ischanging=false;
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(ischanging) return ;


	 
		HashMap<String,String> hm=(HashMap<String, String>)l.getItemAtPosition(position);
		String st=hm.get("fileName");
		if(!st.substring(st.length()-3,st.length()).equals("mp3")) return ;
	try {
			System.out.println(Environment.getExternalStorageDirectory()+"/download/"+hm.get("fileName"));
			File file=new File(Environment.getExternalStorageDirectory()+"/download/"+hm.get("fileName"));
			System.out.println(file.exists());
			
			data.setDataPosition(0);
			data.writeString("播放");
			data.writeString(Environment.getExternalStorageDirectory()+"/download/");
			data.writeString(hm.get("fileName"));
			try {
				binder.transact(3, data, reply, 0);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 Button play,pause,stop;
	 conn con=null;
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(con);
	}
	 
	 @Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putInt("maxProgress", sb.getMax());
	}
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showfile);
		 
	 
		 con=new conn();
			Intent intent=new Intent();			//一开始就绑定Music Service服务器
		intent.setClass(getApplicationContext(), MusicService.class);
		 
		bindService(intent, con, BIND_AUTO_CREATE);
		 
		
		
		ArrayList<HashMap<String,String>> al=new ArrayList<HashMap<String,String>>();
		
		ListView listview=(ListView)findViewById(android.R.id.list);
		listview.setOnTouchListener(this);
		
		FileTool ft=new FileTool();
		al=ft.readSDFile(Environment.getExternalStorageDirectory()+"/download");
		SimpleAdapter  sa=new SimpleAdapter(this, al, R.layout.showfile_file_list, new String[]{"fileName","fileSize","fileDate"}, new int[]{R.id.list1,R.id.list2,R.id.list3});
		 
		setListAdapter(sa);
		play=(Button)findViewById(R.id.play);
		play.setOnClickListener(new playListener());
		
		pause=(Button)findViewById(R.id.pause);
		pause.setOnClickListener(new pauseListener());
		
		stop=(Button)findViewById(R.id.stop);
		stop.setOnClickListener(new stopListener());
		
		lrcTextView=(TextView)findViewById(R.id.lrc);
	
		sb=(SeekBar)findViewById(R.id.sb);
		sb.setOnSeekBarChangeListener(new seekBarListener());
	 
		timeShow=(TextView)findViewById(R.id.timeShow);
	}
	
	
	
	class seekBarListener implements  OnSeekBarChangeListener{
		 int progress;
		 DecimalFormat df=new DecimalFormat("00");
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
	 
			
			//System.out.println("主线程得到更新SeekBar的信息"+progress);
			timeShow.setText(df.format((progress*1.0)/60)+":"+df.format((progress*1.0)%60));
			// TODO Auto-generated method stub
			this.progress=progress;
 
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		 
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			data.setDataPosition(0);
			data.writeInt(progress);
			lrcTextView.setText("");
			System.out.println("修改数据了要"+progress);
			try {
				binder.transact(1, data, reply, 1);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	
	
	
	
	float ex,ey;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//System.out.println(event.getX());
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:ey=event.getY();  ex=event.getX(); break;
		case MotionEvent.ACTION_UP:
			{if( ( ey-event.getY()==0&&(event.getX()-ex)>0)||(event.getX()-ex)/Math.abs(ey-event.getY())>1) {Intent intent=new Intent(); intent.setClass(ShowFile.this, Main.class); ShowFile.this.startActivity(intent);}}
		
		}
		if((ex-event.getX())*(ex-event.getX())+(ey-event.getY())*(ey-event.getY())<30000) ischanging=false;
		else ischanging=true;
		return false;
	};
	class playListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			data.setDataPosition(0);
			data.writeString("继续播放");
			try { 
				
				binder.transact(3, data, reply, 0);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 
	} 
class stopListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		data.setDataPosition(0);
		data.writeString("停止");
		try {
			binder.transact(3, data, reply, 0);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
} 
	class pauseListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			data.setDataPosition(0);
			data.writeString("暂停");
			try {
				binder.transact(3, data, reply, 0);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}



//class runn implements Runnable {
//String st;
//runn(String stt){st=stt;}
//public void run() {
//	try {
//		System.out.println("准备上传"+Environment.getExternalStorageDirectory()+"/download/"+st);
//		Socket sk=new Socket("192.168.252.1",8888);
//		BufferedInputStream bis=new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory()+"/download/"+st));
//		BufferedOutputStream bos=new BufferedOutputStream(sk.getOutputStream());
//		
//		int len=0;
//		byte[] pack=new byte[1024*5];
//		while((len=bis.read(pack))>=0)
//		{System.out.println("正在上传"+len);
//		  bos.write(pack);
//		}bos.flush();
//		bos.close();
//		bis.close();
//		sk.close(); 
//		
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} 
//}
//}
//super.onListItemClick(l, v, position, id);
//HashMap<String,String> hm=(HashMap<String,String>) l.getItemAtPosition(position);
//Thread thread=new Thread(new runn(hm.get("fileName")));
//thread.start();
