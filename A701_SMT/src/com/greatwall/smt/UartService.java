package com.greatwall.smt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.greatwall.smt.listener.OnDataReceiveListener;
import com.greatwall.smt.serial.SerialPort;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class UartService extends Service implements OnDataReceiveListener{
	
	private SerialPort mSerialPort;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;
	private ReadThread mReadThread;
	private boolean isStop;
	private OnDataReceiveListener dataReceiveListener = null;
	private String data = "";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//串口初始化
		boolean result = initSerialPort();
		if (result) {
//			Toast.makeText(this, "串口服务开启", 0).show();
			setOnDataReceiveListener(this);
			mReadThread = new ReadThread();
			mReadThread.start();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		isStop = true;
//		Toast.makeText(this, "串口服务关闭", 0).show();
		super.onDestroy();
	}
	
	private boolean initSerialPort() {
		File file = new File(Constant.UART_NAME);
		mSerialPort = null;
		try {
			mSerialPort = new SerialPort(file, Constant.BAUD_RATE);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mSerialPort != null) {
			mInputStream = (FileInputStream) mSerialPort.getInputStream();
			mOutputStream = (FileOutputStream) mSerialPort.getOutputStream();
			
			Log.d("yuanye", "init success");
			return true;
		}
		return false;
	}
	
	private class ReadThread extends Thread {  
		  
        @Override  
        public void run() {  
            super.run();  
            while (!isStop && !isInterrupted()) {  
                int size;  
                try {  
                    if (mInputStream == null)  
                        return;  
                    byte[] buffer = new byte[512];  
                    size = mInputStream.read(buffer);  
                    if (size > 0) {  
                         
                        if (null != dataReceiveListener) {  
                            dataReceiveListener.onDataReceive(buffer, size);  
                        }  
                    }  
                    Thread.sleep(100);  
                } catch (Exception e) {  
                    e.printStackTrace();  
                    return;  
                }  
            }  
        }  
    }
	

	private void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
		this.dataReceiveListener = dataReceiveListener;
	}

	@Override
	public void onDataReceive(byte[] buffer, int size) {
		Log.d("yuanye", "data receive");
		byte[] tempBuf = new byte[size];
		for (int i = 0; i < size; i++) {
			tempBuf[i] = buffer[i];
		}
		data += new String(buffer, 0, size);
//		Toast.makeText(UartService.this, "收到串口数据："+data+"并返回", 0).show();
		if (data.contains("#")) {
			if (mOutputStream != null) {
				try {
					mOutputStream.write(data.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			data = "";
		}
	}
	
	
}
