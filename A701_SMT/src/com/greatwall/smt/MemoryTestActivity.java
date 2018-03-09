package com.greatwall.smt;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi") public class MemoryTestActivity extends Activity{
	
	private LinearLayout ddr,emmc,sd;
	private TextView size_ddr,size_emmc,size_sd;
	private int ddr_result = 0;
	private int emmc_result = 0;
	private int sd_result = 0;
	
	private long kb = 1024;
	private long mb = 1024 * kb;
	private long gb = 1024 * mb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory);
		ddr = (LinearLayout) findViewById(R.id.ddr);
		emmc = (LinearLayout) findViewById(R.id.emmc);
		sd = (LinearLayout) findViewById(R.id.sd);
		size_ddr = (TextView) findViewById(R.id.size_ddr);
		size_emmc = (TextView) findViewById(R.id.size_emmc);
		size_sd = (TextView) findViewById(R.id.size_sd);
		
		new Thread(){
			public void run() {
				long timeStart = System.currentTimeMillis();
				long timeCurrune = System.currentTimeMillis();
				while(true){
					if(false//(ddr_result != 0 && emmc_result != 0 && sd_result != 0)
							||timeCurrune-timeStart > 2000){
						Intent intent = new Intent();
						intent.putExtra(Constant.TEST_ACTION_DDR_RESULT, ddr_result);
						intent.putExtra(Constant.TEST_ACTION_EMMC_RESULT, emmc_result);
						intent.putExtra(Constant.TEST_ACTION_SD_RESULT, sd_result);
						setResult(Constant.TEST_MEMORY_RESULT_CODE,intent);
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

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getDDR();
		getEMMC();
		getSD();
	}
	
	private void getDDR() {
		// TODO Auto-generated method stub
		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		am.getMemoryInfo(memInfo);
//		String availMen = String.valueOf((memInfo.availMem/(1024*1024)))+"M";
		if (memInfo.totalMem>0) {
			ddr_result = 1;
		}
		String totalMen = String.format("%.2f MB", (float) memInfo.totalMem / mb); ;
		Message msg = Message.obtain();
		msg.what = 0;
		msg.obj = totalMen;
		handler.sendMessage(msg);
	}
	
	private void getEMMC() {
		// TODO Auto-generated method stub
        File path = Environment.getExternalStorageDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSizeLong();  
        long totalBlocks = stat.getBlockCountLong();  
        Long totalSize = totalBlocks * blockSize;
        if (totalSize > 0) {
			emmc_result = 1;
		}
        String totalMen = String.format("%.2f GB", (float) totalSize / gb); 
		Message msg = Message.obtain();
		msg.what = 1;
		msg.obj = totalMen;
		handler.sendMessage(msg);
	}

	private void getSD() {
		// TODO Auto-generated method stub
        long sdcardSize = 0;
        String totalMen = "0M";
        File sdcardDir = new File("/mnt/external_sd");
        String state = Environment.getStorageState(sdcardDir);  
        if (Environment.MEDIA_MOUNTED.equals(state)) {  
//            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());  
            long bSize = sf.getBlockSizeLong();  
            long bCount = sf.getBlockCountLong();  
//            long availBlocks = sf.getAvailableBlocksLong();  
            sdcardSize = bSize * bCount;//�ܴ�С  
            if (sdcardSize > 0) {
				sd_result = 1;
			}
            totalMen = String.format("%.2f GB", (float) sdcardSize / gb); 
        }else{
        	sd_result = -1;
        	totalMen = "SDδ����";
        }
        Message msg = Message.obtain();
		msg.what = 2;
		msg.obj = totalMen;
		handler.sendMessage(msg);
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String size = msg.obj.toString();
			switch (msg.what) {
			case 0:
				size_ddr.setText(size);
				ddr.setBackgroundColor(Color.GREEN);
				break;
			case 1:
				size_emmc.setText(size);
				emmc.setBackgroundColor(Color.GREEN);
				break;
			case 2:
				size_sd.setText(size);
				if (size.equals("SDδ����")) {
					sd.setBackgroundColor(Color.RED);
				}else{
					sd.setBackgroundColor(Color.GREEN);
				}
				break;

			default:
				break;
			}
		}
		
	};
}
