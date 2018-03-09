package com.greatwall.smt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.greatwall.smt.adapter.TestAdapter;
import com.greatwall.smt.bean.TestResult;


public class MainActivity extends Activity implements OnItemClickListener{
	
	public static String tag = "yuanye";
	public SharedPreferences sp;
	private String logcat_path;
	private ListView lv_main;
	private TestAdapter testAdapter;
	private int count = 0;
	private TestResult mTestResult;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultCheck();
        //启动Uart监听服务
        Intent intent = new Intent(this, UartService.class);
        startService(intent);
        
        init();
        boolean flag = sp.getBoolean(Constant.isTestOk, false);
		if (flag) {
			showTestConfirmDialog();
		}else{
	        handler.sendEmptyMessage(count);
		}
    }
    
	private void resultCheck() {
		// TODO Auto-generated method stub
		File resultFile = new File(Environment.getExternalStorageDirectory(),"testresult.xml");
		if (resultFile.exists()) {
			resultFile.delete();
//			executeRunTimeCommand(Constant.RESULT_DELETE);
		}
	}
    
	private void init() {
		initView();
        initData();
        initListener();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent(this, UartService.class);
        stopService(intent);
		super.onDestroy();
	}
	
	private void initListener() {
		lv_main.setOnItemClickListener(this);
	}

	private void initData() {
		sp = getSharedPreferences(Constant.SharePref, MODE_PRIVATE);
		testAdapter = new TestAdapter(this);
		lv_main.setAdapter(testAdapter);
		mTestResult = new TestResult(this);
	}


	private void initView() {
		setContentView(R.layout.activity_main);
        lv_main = (ListView) findViewById(R.id.lv_main);
	}

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				testControlNumber();
				break;
			case 1:
				testBattery();
				break;
			case 2:
				testTp();
				break;
			case 3:
				testSensor();
				break;
			case 4:
				testMemory();
				break;
			case 5:
				submit();
				break;
			case 6:
				break;

			default:
				break;
			}
		}
		
	};

	private void testLcd() {
		executeRunTimeCommand(Constant.HIDE_NAV_BAR);
		Intent intent = new Intent();
		intent.setClass(this, LcdTestActivity.class);
		startActivityForResult(intent, 0);
	}
	
	protected void testControlNumber() {
		Intent intent = new Intent();
		intent.setClass(this, ControlNumberTestActivity.class);
		startActivityForResult(intent, 0);
	}

	protected void testBattery() {
		Intent intent = new Intent();
		intent.setClass(this, BatteryTestActivity.class);
		startActivityForResult(intent, 0);
	}

	protected void testMemory() {
    	Intent intent = new Intent();
		intent.setClass(this, MemoryTestActivity.class);
		startActivityForResult(intent, 0);
	}

	protected void testTp() {
		executeRunTimeCommand(Constant.HIDE_NAV_BAR);
    	Intent intent = new Intent();
		intent.setClass(this, TouchTestActivity.class);
		startActivityForResult(intent, 0);
	}
    
    private void testSensor() {
		Intent intent = new Intent();
		intent.setClass(this, SensorTestActivity.class);
		startActivityForResult(intent, 0);
	}
    
    private void testUart(){
    	Intent intent = new Intent();
		intent.setClass(this, UartTestActivity.class);
		startActivityForResult(intent, 0);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Constant.TEST_TIMEOUT_RESULT_CODE:
			executeRunTimeCommand(Constant.SHOW_NAV_BAR);
			if (data.getStringExtra("name").equals("TP")) {
				setStatu(Constant.TP_pos, false);
				mTestResult.setResult(Constant.TP_pos, "Failed", "", "");
			}
			handler.sendEmptyMessage(++count);
			break;
		case Constant.TEST_CM_NUMBER_RESULT_CODE:
			int result = data.getIntExtra(Constant.TEST_ACTION_CM_NUMBER_RESULT, 0);
			if (result == 0) {
				setStatu(Constant.Cm_Number_pos, false);
				mTestResult.setResult(Constant.Cm_Number_pos, "Failed", "", "");
			}else{
				setStatu(Constant.Cm_Number_pos, true);
				mTestResult.setResult(Constant.Cm_Number_pos, "Pass", "", "");
			}
			handler.sendEmptyMessage(++count);
			break;
		case Constant.TEST_BATTERY_RESULT_CODE:
			int result2 = data.getIntExtra(Constant.TEST_ACTION_BATTERY_RESULT, 0);
			if (result2 == 0) {
				setStatu(Constant.Battery_pos, false);
				mTestResult.setResult(Constant.Battery_pos, "Failed", "", "");
			}else{
				setStatu(Constant.Battery_pos, true);
				mTestResult.setResult(Constant.Battery_pos, "Pass", "", "");
			}
			handler.sendEmptyMessage(++count);
			break;
		case Constant.TEST_TOUCH_RESULT_CODE:
			executeRunTimeCommand(Constant.SHOW_NAV_BAR);
			setStatu(Constant.TP_pos, true);
			mTestResult.setResult(Constant.TP_pos, "Pass", "", "");
			handler.sendEmptyMessage(++count);
			break;
		case Constant.TEST_SENSOR_RESULT_CODE:
			int lResult = data.getIntExtra(Constant.TEST_ACTION_L_SENSOR_RESULT, 0);
			int pResult = data.getIntExtra(Constant.TEST_ACTION_P_SENSOR_RESULT, 0);
			int tResult = data.getIntExtra(Constant.TEST_ACTION_T_SENSOR_RESULT, 0);
			Log.e(tag, ""+lResult+" "+pResult+" "+tResult);
			if(lResult > 0){
				setStatu(Constant.Light_sensor_pos, true);
				mTestResult.setResult(Constant.Light_sensor_pos, "Pass", "", "");
			}else{
				setStatu(Constant.Light_sensor_pos, false);
				mTestResult.setResult(Constant.Light_sensor_pos, "Failed", "", "");
			}
			if(pResult > 0){
				setStatu(Constant.Proximity_sensor_pos, true);
				mTestResult.setResult(Constant.Proximity_sensor_pos, "Pass", "", "");
			}else{
				setStatu(Constant.Proximity_sensor_pos, false);
				mTestResult.setResult(Constant.Proximity_sensor_pos, "Failed", "", "");
			}
			if(tResult > 0){
				setStatu(Constant.Temperature_sensor_pos, true);
				mTestResult.setResult(Constant.Temperature_sensor_pos, "Pass", "", "");
			}else{
				setStatu(Constant.Temperature_sensor_pos, false);
				mTestResult.setResult(Constant.Temperature_sensor_pos, "Failed", "", "");
			}
			handler.sendEmptyMessage(++count);
			break;
		case Constant.TEST_MEMORY_RESULT_CODE:
			int ddrResult = data.getIntExtra(Constant.TEST_ACTION_DDR_RESULT, 0);
			int emmcResult = data.getIntExtra(Constant.TEST_ACTION_EMMC_RESULT, 0);
			int sdResult = data.getIntExtra(Constant.TEST_ACTION_SD_RESULT, 0);
			if (ddrResult > 0) {
				setStatu(Constant.DDR_pos, true);
				mTestResult.setResult(Constant.DDR_pos, "Pass", "", "");
			}else{
				setStatu(Constant.DDR_pos, false);
				mTestResult.setResult(Constant.DDR_pos, "Failed", "", "");
			}
			if (emmcResult > 0) {
				setStatu(Constant.EMMC_pos, true);
				mTestResult.setResult(Constant.EMMC_pos, "Pass", "", "");
			}else{
				setStatu(Constant.EMMC_pos, false);
				mTestResult.setResult(Constant.EMMC_pos, "Failed", "", "");
			}
			if (sdResult > 0) {
				setStatu(Constant.SD_pos, true);
				mTestResult.setResult(Constant.SD_pos, "Pass", "", "");
			}else{
				setStatu(Constant.SD_pos, false);
				mTestResult.setResult(Constant.SD_pos, "Failed", "", "");
			}
			handler.sendEmptyMessage(++count);
			break;
		case Constant.TEST_UART_RESULT_CODE:
			setStatu(8,true);
			handler.sendEmptyMessage(++count);
			break;

		default:
			break;
		}
	}

    protected void showTestDialog(String str, final int resultCode) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.test_dialog_title, str));
		builder.setNegativeButton(R.string.fail, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(resultCode){
				case Constant.TEST_LCD_RESULT_CODE:
					setStatu(0, false);
					handler.sendEmptyMessage(++count);
					break;
				case Constant.TEST_TOUCH_RESULT_CODE:
					setStatu(0, false);
					handler.sendEmptyMessage(++count);
					break;
				}
			}
		});
		builder.setNeutralButton(R.string.pass, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(resultCode){
				case Constant.TEST_LCD_RESULT_CODE:
					setStatu(0, true);
					handler.sendEmptyMessage(++count);
					break;
				case Constant.TEST_TOUCH_RESULT_CODE:
					setStatu(0, true);
					handler.sendEmptyMessage(++count);
					break;	
				}
			}
		});

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}
    

	private void showTestConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.test_confirm_dialog_title));
		builder.setNegativeButton(R.string.confirm_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setPositiveButton(R.string.confirm_continue, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor edit = sp.edit();
				edit.putBoolean(Constant.isTestOk, false);
				edit.commit();
				handler.sendEmptyMessage(count);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}
    
	public void setStatu(int count, boolean b) {
		testAdapter.update(count, b);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	submit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		count = 99;
		switch (position) {
		case 0:
			testControlNumber();
			break;
		case 1:
			testBattery();
			break;
		case 2:
			testTp();
			break;
		case 3:
		case 4:
		case 5:
			testSensor();
			break;
		case 6:
		case 7:
		case 8:
			testMemory();
			break;
		case 9:
			testUart();
			break;

		default:
			break;
		}
	}
	
    public static void logcat(String fileName, String content) {  
        try {  
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
            FileWriter writer = new FileWriter(fileName, true);  
            writer.write(content+"\n");  
            writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
    
	public String getSDPath() {  
        File sdDir = null;  
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在  
        if (sdCardExist) {  
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录  
        }  
        return sdDir.toString();  
    }  
	
	
    protected void submit() {
		commitReport(testAdapter);
		if (allPassed()) {
			Editor edit = sp.edit();
			edit.putBoolean(Constant.isTestOk, true);
			edit.commit();
		}
	}

	private boolean allPassed() {
		for (String statu : testAdapter.getStatus()) {
			if (!statu.equals("PASS")) {
				return false;
			}
		}
		return true;
	}
	
	public void commitReport(TestAdapter adapter) {
		try {
			// 1.创建一个xml文件的序列化器
			XmlSerializer serializer = Xml.newSerializer();
			// 2.设置文件的输出和编码方式
			FileOutputStream os = new FileOutputStream(
					new File(Environment.getExternalStorageDirectory(),
							"testresult.xml"));
			// 对于目标文件的一个输出流
			serializer.setOutput(os, "utf-8");
			// 3.写xml文件的头
			serializer.startDocument("utf-8", true);
			// 4.写info开始节点
			serializer.startTag(null, "root");
			String statu;
			for (int i = 0; i < mTestResult.getTitles().size(); i++) {
				serializer.startTag(null, mTestResult.getTitles().get(i));
				
				serializer.startTag(null, "result");
				serializer.text(mTestResult.getResults().get(i));
				serializer.endTag(null, "result");
				serializer.startTag(null, "reason");
				serializer.text(mTestResult.getReasons().get(i));
				serializer.endTag(null, "reason");
				serializer.startTag(null, "value");
				serializer.text(mTestResult.getValues().get(i));
				serializer.endTag(null, "value");
				
				serializer.endTag(null, mTestResult.getTitles().get(i));
			}
			serializer.endTag(null, "root");
			serializer.endDocument();// 写文件的末尾
			os.close();
			Toast.makeText(this, "报告提交成功", 0).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "报告提交失败", 0).show();
		}
	}

	public static void executeRunTimeCommand(final String arCommand){
		Process loProcess = null;
		try{
		    loProcess = Runtime.getRuntime().exec(arCommand);
		    System.out.println("executeRunTimeCommand " + arCommand);
		}catch (IOException e){
		    Log.e(tag, e.getMessage());
		    return;
		}
	}

}
