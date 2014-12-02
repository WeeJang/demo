package commlab.weejang.demo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract.Contacts.Data;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.StaticLayout;
import android.util.Log;

/**
 * 监听3G信号，向主线程投递消息，UI显示
 * @author jangwee
 *
 *之前开启一个线程，投递消息。后来发现与监听器（观察者模式）冲突，就把线程去掉了。
 *
 */


@SuppressLint("NewApi")
public class MeasureUmts{
	
	private static final String TAG = "MeasureUmtsThread";
	
	//成员变量
	private Handler mHandler = null;
	private Context mContext = null;
	private TelephonyManager telephonyManager = null;
	
	//存储测量信息
	private HashMap<String, String> infoHashMap ;
	
	
	//运营商标识
	/**
	 *  中国移动的是 46000
	 *   中国联通的是 46001
	 *   中国电信的是 46003
	 *   460 是 中国
	 */
	private String  	networkOperator[] = {"46000","46001","46003"};
	//标记本卡运营商归属
	private int netOperatorMark = -1 ;

	//消息
	private Message msg = null ;
	// 数据 格式 {时间戳 + 测量数据}
	private Bundle data = new Bundle();

	//跟LTE相关的API都被@hide了，怎么获得?  :) 反射！
	static  Class SS ;
	static Constructor<SignalStrength> constructor ;
	static SignalStrength instanceSignalStrength ;
//	static Method methodGetLteSignalStrength ;
//	static Method methodGetLteRsrp ;
//	static Method methodGetLteRsrq ;
//	static Method methodGetLteRssnr ;
//	static Method methodGetLteCqi;
//	//是获取Method(成员函数),不用这么麻烦，直接获取Field(成员变量)

	static Field lteSignalStrengthField;
	static Field lteRsrpField;
	static Field lteRsrqField;
	static Field lteRssnrField;
	static Field lteCqiField;
	
	
	
	//加载反射相关，动态加载
	static{

			try {
				SS = Class.forName("android.telephony.SignalStrength");
				constructor = SS.getConstructor(null);
				instanceSignalStrength = constructor.newInstance(null);
				
				lteSignalStrengthField = SS.getField("mLteSignalStrength");
				lteRsrpField = SS.getField("mLteRsrp");
				lteRsrqField = SS.getField("mLteRsrq");
				lteRssnrField = SS.getField("mLteRssnr");
				lteCqiField = SS.getField("mLteCqi");
//				methodGetLteSignalStrength = SS.getMethod("getLteSignalStrength", null);
//				methodGetLteRsrp = SS.getMethod("getLteRsrp", null);
//				methodGetLteRsrq = SS.getMethod("getLteRsrq", null);
//				methodGetLteRssnr = SS.getMethod("getLteRssnr", null);
//				methodGetLteCqi = SS.getMethod("getLteCqi", null);
		
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
  
	}
	
	/**
	 * 构造函数 
	 * @param looper 处理该MSG的looper
	 */
	public MeasureUmts(Context context,Handler handler)
	{
		this.mHandler = handler;
		this.mContext = context;
		this.telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	
	
	//启动测量，
	public void initMeasure()
	{
		//设置监听  : 这里有大量信息
		telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_SERVICE_STATE);
		//获取运营商
		getMark();
			
	}

	//确定接入运营商
	private void getMark()
	{
		String networkOperatorString  = telephonyManager.getNetworkOperator();
		for (int i = 0; i < 3; i++) {
			netOperatorMark = -1 ;
			if(networkOperatorString.equals(networkOperator[i]))
			{
				netOperatorMark = i ;
				Log.v(TAG, "networkMark + " + netOperatorMark);
				break;
			}
		}
	}
	
	//投递消息给处理
	private void sendInfo()
	{
		//消息标识
		msg = mHandler.obtainMessage(GlobalVar.MSG_HANDLER_MEASURE_UTMS);
		data.clear();
		data.putLong("timestamp", System.currentTimeMillis());
		data.putSerializable("data", infoHashMap);
		
		mHandler.sendMessage(msg);
	}
	
	
	
	
	//监听器负责更新（观察者模式）
	
	private class MyPhoneStateListener extends PhoneStateListener{
		
		@Override
		public void onServiceStateChanged(ServiceState serviceState){
			super.onServiceStateChanged(serviceState);
		}
		
		@Override
		public void onCellLocationChanged(CellLocation location)	{
			super.onCellLocationChanged(location);
		}
		
		 /**
	     * Callback invoked when connection state changes.
	     *
	     * @see TelephonyManager#DATA_DISCONNECTED
	     * @see TelephonyManager#DATA_CONNECTING
	     * @see TelephonyManager#DATA_CONNECTED
	     * @see TelephonyManager#DATA_SUSPENDED
	     */
		@Override
		 public void onDataConnectionStateChanged(int state) {
		        super.onDataConnectionStateChanged(state);
		    }
		
		@Override
	    /**
	     * same as above, but with the network type.  Both called.
	     */
	    public void onDataConnectionStateChanged(int state, int networkType) {
			super.onDataConnectionStateChanged(state, networkType);
		}
		
		@Override
	    /**
	     * Callback invoked when data activity state changes.
	     *
	     * @see TelephonyManager#DATA_ACTIVITY_NONE
	     * @see TelephonyManager#DATA_ACTIVITY_IN
	     * @see TelephonyManager#DATA_ACTIVITY_OUT
	     * @see TelephonyManager#DATA_ACTIVITY_INOUT
	     * @see TelephonyManager#DATA_ACTIVITY_DORMANT
	     */
	    public void onDataActivity(int direction) {
			super.onDataActivity(direction);
	    }
	
		
		//暂时覆盖这里
	    /**
	     * Callback invoked when network signal strengths changes.
	     *
	     * @see ServiceState#STATE_EMERGENCY_ONLY
	     * @see ServiceState#STATE_IN_SERVICE
	     * @see ServiceState#STATE_OUT_OF_SERVICE
	     * @see ServiceState#STATE_POWER_OFF
	     */
	    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
	    	super.onSignalStrengthsChanged(signalStrength);
	    	
	    	//更新测量值
	    	/**See SignalStrength.class
	    	 * 查询API : 
	    	        private int mGsmSignalStrength; // Valid values are (0-31, 99) as defined in TS 27.007 8.5
    		    	private int mGsmBitErrorRate;   // bit error rate (0-7, 99) as defined in TS 27.007 8.5
    				private int mCdmaDbm;   // This value is the RSSI value
    				private int mCdmaEcio;  // This value is the Ec/Io
    				private int mEvdoDbm;   // This value is the EVDO RSSI value
    				private int mEvdoEcio;  // This value is the EVDO Ec/Io
    				private int mEvdoSnr;   // Valid values are 0-8.  8 is the highest signal to noise ratio
    				private int mLteSignalStrength;
    				private int mLteRsrp;
    				private int mLteRsrq;
    				private int mLteRssnr;
    				private int mLteCqi;

    				private boolean isGsm; // This value is set by the ServiceStateTracker onSignalStrengthResult
	    	 */
	    	/**
	    	 * toString format :
	    	 * "SignalStrength:"
                + " " + mGsmSignalStrength
                + " " + mGsmBitErrorRate
                + " " + mCdmaDbm
                + " " + mCdmaEcio
                + " " + mEvdoDbm
                + " " + mEvdoEcio
                + " " + mEvdoSnr
                + " " + mLteSignalStrength
                + " " + mLteRsrp
                + " " + mLteRsrq
                + " " + mLteRssnr
                + " " + mLteCqi
                + " " + (isGsm ? "gsm|lte" : "cdma"
	    	 */
	    	
	    	infoHashMap.clear();
	    	infoHashMap.put("mGsmSignalStrength", String.valueOf(signalStrength.getGsmSignalStrength()));
	    	infoHashMap.put("mGsmBitErrorRate",String.valueOf(signalStrength.getGsmBitErrorRate()));
	    	infoHashMap.put("mCdmaDbm", String.valueOf(signalStrength.getCdmaDbm()));
	    	infoHashMap.put("mCdmaEcio", String.valueOf(signalStrength.getCdmaEcio()));
	    	infoHashMap.put("mEvdoDbm", String.valueOf(signalStrength.getEvdoDbm()));
	    	infoHashMap.put("mEvdoEcio", String.valueOf(signalStrength.getEvdoEcio()));
	    	infoHashMap.put("mEvdoSnr",String.valueOf(signalStrength.getEvdoSnr()));
	    	infoHashMap.put("isGSM", String.valueOf(signalStrength.isGsm()));

	    	
	    	//infoHashMap.put("mLteSignalStrength", String.valueOf(signalStrength.getLteSignalStrength()));
	    	//反射获取
	    	
	    	try {
				infoHashMap.put("mLteSignalStrength", String.valueOf(lteSignalStrengthField.get(signalStrength)));
				infoHashMap.put("mLteRsrp", String.valueOf(lteRsrpField.get(signalStrength)));
				infoHashMap.put("mLteRsrq", String.valueOf(lteRsrqField.get(signalStrength)));
				infoHashMap.put("mLteCqi", String.valueOf(lteCqiField.get(signalStrength)));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	//刷新后投递消息
	    	sendInfo();
	    	
	    }
	    
	    @Override
	    /**
	     * Callback invoked when a observed cell info has changed,
	     * or new cells have been added or removed.
	     * @param cellInfo is the list of currently visible cells.
	     */
	    public void onCellInfoChanged(List<CellInfo> cellInfo) {
	    	super.onCellInfoChanged(cellInfo);
	    }


	}
	
	
	
	
	
}
