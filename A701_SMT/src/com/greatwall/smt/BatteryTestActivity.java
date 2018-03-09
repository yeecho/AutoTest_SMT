package com.greatwall.smt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class BatteryTestActivity extends Activity{
	
//	private TextView tv;
	private ImageView ivBattery;
	private BroadcastReceiver batteryReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_battery);
//		tv = (TextView) findViewById(R.id.control_model_number);
		ivBattery = (ImageView) findViewById(R.id.iv_battery);
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = registerReceiver(null, ifilter);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
				status == BatteryManager.BATTERY_STATUS_FULL;
		if (isCharging) {
			ivBattery.setImageResource(R.drawable.charging);
		}else{
			ivBattery.setImageResource(R.drawable.nocharging);
		}
		
		batteryReceiver = new BatteryBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		registerReceiver(batteryReceiver, filter);
		
//		String control_model_number = Utils.getString("/sys/devices/platform/ct_bid.0/hw_model_number");
//		tv.setText(control_model_number);
	}
	
	public void fail(View v){
		Intent intent = new Intent();
		intent.putExtra(Constant.TEST_ACTION_BATTERY_RESULT, 0);
		setResult(Constant.TEST_BATTERY_RESULT_CODE, intent);
		finish();
	}
	
	public void pass(View v){
		Intent intent = new Intent();
		intent.putExtra(Constant.TEST_ACTION_BATTERY_RESULT, 1);
		setResult(Constant.TEST_BATTERY_RESULT_CODE, intent);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (batteryReceiver != null) {
			unregisterReceiver(batteryReceiver);
		}
	}
	
	class BatteryBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
				ivBattery.setImageResource(R.drawable.charging);
			}else{
				ivBattery.setImageResource(R.drawable.nocharging);
			}
		}
		
	}
	
}
