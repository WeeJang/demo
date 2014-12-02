package commlab.weejang.demo;

import java.util.HashMap;
import java.util.List;

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
import android.util.Log;

/**
 * 监听3G信号，向主线程投递消息，UI显示
 * @author jangwee
 *
 */


@SuppressLint("NewApi")
public class MeasureUmtsThread implements Runnable{
	
	private static final String TAG = "MeasureUmtsThread";
	
	//成员变量
	private Handler mHandler = null;
	private Context mContext = null;
	private TelephonyManager telephonyManager = null;
	
	//存储测量信息
	private HashMap<String, String> infoHashMap ;
	
	//其他组件
	
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
	
	
	
	
	//测试
	private int mCount = 0 ;
	
	/**
	 * 
	 * @param looper 处理该MSG的looper
	 */
	public MeasureUmtsThread(Context context,Handler handler)
	{
		this.mHandler = handler;
		this.mContext = context;
		this.telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	
	
	
	
	//实现Runnable接口
	public void run()
	{
		
		Message msg = null ;
		/**
		 * data 格式 {时间戳 + 测量数据}
		 */
		Bundle data = new Bundle();
		//设置监听  : 这里有大量信息
		telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_SERVICE_STATE);
		//获取运营商
		getMark();
				
		while(!GlobalVar.isMeasureThreadStop)
		{
			//被暂停后挂起
			if (GlobalVar.isMeasureThreadWait) {
				try {
					Thread.currentThread().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
		//消息标识
		msg = mHandler.obtainMessage(GlobalVar.MSG_FLAG_INT.utms_measure_data.ordinal());
		data.clear();
		data.putLong("timestamp", System.currentTimeMillis());
		data.putSerializable("data", infoHashMap);
		
		mHandler.sendMessage(msg);
		
		try {
			Thread.currentThread().sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
//			if (++mCount % 1000 == 0) {
//				Message msg = mHandler.obtainMessage(0,mCount);
//				mHandler.sendMessage(msg);
//			}


		}
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
	
	
	//监听器负责更新测量值
	//这个地方有点不确定？ ==
	
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
	    	infoHashMap.put("mGsmBitErrorRate",String)
	    	
	    	
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
