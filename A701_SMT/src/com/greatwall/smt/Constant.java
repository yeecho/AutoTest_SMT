package com.greatwall.smt;

public class Constant {
	
	public static final String SharePref = "com.greatwall.smt.sharePref";
	public static final String isTestOk = "isTestOk";
	
	public static final int TIME_COST = 10000;
	
	public static final int Cm_Number_pos = 0;
	public static final int Battery_pos = 1;
	public static final int TP_pos = 2;
	public static final int Light_sensor_pos = 3;
	public static final int Proximity_sensor_pos = 4;
	public static final int Temperature_sensor_pos = 5;
	public static final int DDR_pos = 6;
	public static final int EMMC_pos = 7;
	public static final int SD_pos = 8;
	
	public static final int TEST_TIMEOUT_RESULT_CODE = 800;
	public static final int TEST_LCD_RESULT_CODE = 801;
	public static final int TEST_TOUCH_RESULT_CODE = 802;
	public static final int TEST_SENSOR_RESULT_CODE = 803;
	public static final int TEST_MEMORY_RESULT_CODE = 804;
	public static final int TEST_UART_RESULT_CODE = 805;
	public static final int TEST_BATTERY_RESULT_CODE = 806;
	public static final int TEST_CM_NUMBER_RESULT_CODE = 807;
	
	public static final String TEST_ACTION_CM_NUMBER_RESULT = "com.greatwall.smt.Action.cm_number.Result";
	public static final String TEST_ACTION_BATTERY_RESULT = "com.greatwall.smt.Action.battery.Result";
	public static final String TEST_ACTION_L_SENSOR_RESULT = "com.greatwall.smt.Action.l.sensor.Result";
	public static final String TEST_ACTION_P_SENSOR_RESULT = "com.greatwall.smt.Action.p.sensor.Result";
	public static final String TEST_ACTION_T_SENSOR_RESULT = "com.greatwall.smt.Action.t.sensor.Result";
	public static final String TEST_ACTION_DDR_RESULT = "com.greatwall.smt.Action.ddr.Result";
	public static final String TEST_ACTION_EMMC_RESULT = "com.greatwall.smt.Action.emmc.Result";
	public static final String TEST_ACTION_SD_RESULT = "com.greatwall.smt.Action.sd.Result";
	
	
	public static final String SHOW_NAV_BAR = "am startservice -n com.android.systemui/.SystemUIService";
	public static final String HIDE_NAV_BAR = "service call activity 42 s16 com.android.systemui";
	
	public static final String UART_NAME = "/dev/ttyS2";
	public static final int BAUD_RATE = 115200;
}
