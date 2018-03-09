package com.greatwall.smt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.greatwall.smt.view.MyPaintPanel;

public class TouchTestActivity extends Activity {
	
	private MyPaintPanel touchView;
	private int count = 0;

	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			count = 0;
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/**getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();  
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  
		              | View.SYSTEM_UI_FLAG_FULLSCREEN;  
		decorView.setSystemUiVisibility(uiOptions);*/
		setContentView(R.layout.activity_touch);
		
		touchView = (MyPaintPanel) findViewById(R.id.touchView);
		
		touchView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				count++;
				if (count==1) {
					mHandler.sendEmptyMessageDelayed(0, 500);
				}else if (count == 3) {
					setResult(Constant.TEST_TOUCH_RESULT_CODE);
                    finish();
				}
			}
		});
		
		new Thread(){
			public void run() {
				long timeStart = System.currentTimeMillis();
				long timeCurrune = System.currentTimeMillis();
				while(true){
					if(timeCurrune-timeStart > Constant.TIME_COST){
						Intent intent = new Intent();
						intent.putExtra("name", "TP");
						setResult(Constant.TEST_TIMEOUT_RESULT_CODE,intent);
						finish();
						break;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeCurrune = System.currentTimeMillis();
				}
			};
		}.start();
		
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
