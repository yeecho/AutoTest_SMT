package com.greatwall.smt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.greatwall.smt.serial.SerialPort;

public class UartTestActivity extends Activity{

	private String tag = "com.greatwall.autotest.UartTestActivity";
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;
	
	private TextView tv_uart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uart);
		
		tv_uart = (TextView) findViewById(R.id.tv_uart);
		
		initSerial();//初始化
//		new ReadThread().start();
		final byte[] msg = hexStringToBytes("ABCDEF0123456");
		
		new Thread(){
			public void run() {
				long timeStart = System.currentTimeMillis();
				long timeCurrune = System.currentTimeMillis();
				while(true){
					if(timeCurrune-timeStart > 2000){
						Intent intent = new Intent();
						setResult(Constant.TEST_UART_RESULT_CODE, intent);
						finish();
						break;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						if (mOutputStream != null) {
							mHandler.sendEmptyMessage(0);
						}
						mOutputStream.write(msg);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeCurrune = System.currentTimeMillis();
				}
			};
		}.start();
		
	}

	private void initSerial() {
		File file = new File("/dev/ttyS2");
		SerialPort serial = null;
		try {
			serial = new SerialPort(file, 921600);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (serial != null) {
			mInputStream = (FileInputStream) serial.getInputStream();
			mOutputStream = (FileOutputStream) serial.getOutputStream();
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			tv_uart.setText("串口初始化成功！");
		}
		
	};
	
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[128];
					if (mInputStream != null) {
						size = mInputStream.read(buffer);
						if (size > 0) {
							onDataReceived(buffer, size);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	void onDataReceived(final byte[] buffer, final int size) {
		byte[] tempBuf = new byte[size];
		for (int i = 0; i < size; i++) {
			tempBuf[i] = buffer[i];
		}
		String result = bytesToHexString(tempBuf);
		Log.d(tag, result);
	}
	
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.replaceAll(" ", "");
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	
	private static byte charToByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
}
