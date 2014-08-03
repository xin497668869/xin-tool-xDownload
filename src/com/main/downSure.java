package com.main;

import com.example.xdownload.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class downSure extends Activity {
	
	
	Button yes,no;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downsure);
		yes=(Button)findViewById(R.id.sure);
		no=(Button)findViewById(R.id.notsure);
		
		no.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 finish();
			}
		});
		
		
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				Intent intent=getIntent();
				System.out.println(intent.getStringExtra("fileName"));
				Main.fileName=intent.getStringExtra("fileName");
				finish();
			}
		});
	}
	
	
}
