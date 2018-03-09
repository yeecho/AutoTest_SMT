package com.greatwall.smt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SensorTestActivity extends Activity {

	private SensorManager sm;
    private Sensor ligthSensor; 
    private Sensor proxiSensor;
    private Sensor tempSensor;
    
    private TextView lightTextView;
    private TextView proxiTextView;
    private TextView tempTextView;
    
    private View lightView;
    private View proxiView;
    private View tempView;
    
    private int light1 = 0;
    private int light2 = 0;
    
    private int proxi1 = 0;
    private int proxi2 = 0;
    
    private int lTestResult = 0;
    private int pTestResult = 0;
    private int tTestResult = 0;
	private MySensorEventListener listener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		
		lightTextView = (TextView) findViewById(R.id.lightTV);
		proxiTextView = (TextView) findViewById(R.id.proxiTV);
		tempTextView = (TextView) findViewById(R.id.tempTV);
		
		lightView = findViewById(R.id.lightView);
		proxiView = findViewById(R.id.proxiView);
		tempView = findViewById(R.id.tempView);
		
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		ligthSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
		proxiSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		tempSensor = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		listener = new MySensorEventListener();
		if(ligthSensor == null){
			lTestResult = -1;
			lightView.setBackgroundColor(Color.RED);
		}else{
			sm.registerListener(listener, ligthSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		if(proxiSensor == null){
			pTestResult = -1;
			proxiView.setBackgroundColor(Color.RED);
		}else{
			sm.registerListener(listener, proxiSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		if(tempSensor == null){
			tTestResult = -1;
			tempView.setBackgroundColor(Color.RED);
		}else{
			sm.registerListener(listener, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		new Thread(){
			public void run() {
				long timeStart = System.currentTimeMillis();
				long timeCurrune = System.currentTimeMillis();
				while(true){
					if((lTestResult != 0 && pTestResult != 0 && tTestResult != 0)
							|| (timeCurrune-timeStart > Constant.TIME_COST)){
						Intent intent = new Intent();
						intent.putExtra(Constant.TEST_ACTION_L_SENSOR_RESULT, lTestResult);
						intent.putExtra(Constant.TEST_ACTION_P_SENSOR_RESULT, pTestResult);
						intent.putExtra(Constant.TEST_ACTION_T_SENSOR_RESULT, tTestResult);
						setResult(Constant.TEST_SENSOR_RESULT_CODE, intent);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		sm.unregisterListener(listener);
		super.onDestroy();
	}

	public class MySensorEventListener implements SensorEventListener {  
		  
        public void onAccuracyChanged(Sensor sensor, int accuracy) {  
              
        }  
        public void onSensorChanged(SensorEvent event) {
        	int type = event.sensor.getType();
        	if(type == Sensor.TYPE_LIGHT){
	            float lux = event.values[0];   
	            lightTextView.setText(""+lux);
	            if(light1 < 5 && lux >= 160){
	            	light1++;
	            }else if(light2 < 5 && (int)lux == 10){
	            	light2++;
	            }
	            if(light1 >=2 && light2 >=1){
	            	lTestResult = 1;
	            	lightView.setBackgroundColor(Color.GREEN);
	            }
        	}else if(type == Sensor.TYPE_PROXIMITY){
	            float proxi = event.values[0];   
	            proxiTextView.setText(""+proxi);
	            /*
	            if(proxi1 < 5 && (int)prixi == 9){
	            	proxi1++;
	            }else if(proxi2 < 5 && (int)prixi == 0){
	            	proxi2++;
	            }
	            if(proxi1 >=3 && proxi2 >=2){
	            	pTestResult = 1;
	            	proxiView.setBackgroundColor(Color.GREEN);
	            }*/
	            if (proxi == 0) {
	            	pTestResult = 1;
	            	proxiView.setBackgroundColor(Color.GREEN);
				}else{
					pTestResult = 0;
					proxiView.setBackgroundColor(Color.RED);
					
				}
        	}else if(type == Sensor.TYPE_AMBIENT_TEMPERATURE){
	            float temp = event.values[0];
	            Log.d("yuanye", ""+temp);
	            tempTextView.setText(""+temp);
	            
	            if(temp > 8 && temp < 12){
	            	tTestResult = 1;
	            	tempView.setBackgroundColor(Color.GREEN);
	            }
        	}
        }
          
    }  
}
