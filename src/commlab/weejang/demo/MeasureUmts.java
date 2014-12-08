package commlab.weejang.demo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import commlab.weejang.demo.interfaces.Measurable;
import commlab.weejang.demo.utils.GlobalVar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 *
 * 
 * @author jangwee
 *
 *      
 *
 */

@SuppressLint("NewApi")
public class MeasureUmts implements Measurable
{

	private static final String TAG = "MeasureUmtsThread";

	private Context mContext = null;
	// 管理器
	TelephonyManager telephonyManager = null;
	// 存储测量信息
	private static HashMap<String, String> infoHashMap = new HashMap<String, String>();
	

	// 跟LTE相关的API都被@hide了，怎么获得? :) 反射！
	static Class<?> SS;

	// //是获取Method(成员函数),不用这么麻烦，直接获取Field(成员变量)

	static Field lteSignalStrengthField;
	static Field lteRsrpField;
	static Field lteRsrqField;
	static Field lteRssnrField;
	static Field lteCqiField;

	// 加载反射相关，动态加载
	static
	{
		try
		{
			SS = Class.forName("android.telephony.SignalStrength");

			lteSignalStrengthField = SS.getDeclaredField("mLteSignalStrength");
			lteRsrpField = SS.getDeclaredField("mLteRsrp");
			lteRsrqField = SS.getDeclaredField("mLteRsrq");
			lteRssnrField = SS.getDeclaredField("mLteRssnr");
			lteCqiField = SS.getDeclaredField("mLteCqi");

			// 提升权限
			lteSignalStrengthField.setAccessible(true);
			lteRsrpField.setAccessible(true);
			lteRsrqField.setAccessible(true);
			lteRssnrField.setAccessible(true);
			lteCqiField.setAccessible(true);

		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param looper
	 *            处理该MSG的looper
	 */
	public MeasureUmts(Context context)
	{
		this.mContext = context;
	}


	@Override
	public int IdentifyMeasurable()
	{
		return GlobalVar.MSG_HANDLER_MEASURE_UMTS;
	}

	@Override
	public HashMap<String, String> MeasureParameters()
	{
		return infoHashMap;
	}

	
	
	@Override
	public void initDevice()
	{
		// 设置监听 : 这里有大量信息
		telephonyManager = (TelephonyManager)mContext 
				.getSystemService(Context.TELEPHONY_SERVICE);

		telephonyManager.listen(new MyPhoneStateListener(),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_SERVICE_STATE);
	}
	
	// 监听器负责更新（观察者模式）
	private class MyPhoneStateListener extends PhoneStateListener
	{

		@Override
		public void onServiceStateChanged(ServiceState serviceState)
		{
			super.onServiceStateChanged(serviceState);
		}

		@Override
		public void onCellLocationChanged(CellLocation location)
		{
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
		public void onDataConnectionStateChanged(int state)
		{
			super.onDataConnectionStateChanged(state);
		}

		@Override
		/**
		 * same as above, but with the network type.  Both called.
		 */
		public void onDataConnectionStateChanged(int state, int networkType)
		{
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
		public void onDataActivity(int direction)
		{
			super.onDataActivity(direction);
		}

		// 暂时覆盖这里
		/**
		 * Callback invoked when network signal strengths changes.
		 *
		 * @see ServiceState#STATE_EMERGENCY_ONLY
		 * @see ServiceState#STATE_IN_SERVICE
		 * @see ServiceState#STATE_OUT_OF_SERVICE
		 * @see ServiceState#STATE_POWER_OFF
		 */
		public void onSignalStrengthsChanged(SignalStrength signalStrength)
		{
			
			Log.i(TAG, "Error~~~~~~~~~~~~~~~~~~~~~~~");
			super.onSignalStrengthsChanged(signalStrength);

			// 更新测量值
			/**
			 * See SignalStrength.class 查询API : private int mGsmSignalStrength;
			 * // Valid values are (0-31, 99) as defined in TS 27.007 8.5
			 * private int mGsmBitErrorRate; // bit error rate (0-7, 99) as
			 * defined in TS 27.007 8.5 private int mCdmaDbm; // This value is
			 * the RSSI value private int mCdmaEcio; // This value is the Ec/Io
			 * private int mEvdoDbm; // This value is the EVDO RSSI value
			 * private int mEvdoEcio; // This value is the EVDO Ec/Io private
			 * int mEvdoSnr; // Valid values are 0-8. 8 is the highest signal to
			 * noise ratio private int mLteSignalStrength; private int mLteRsrp;
			 * private int mLteRsrq; private int mLteRssnr; private int mLteCqi;
			 * 
			 * private boolean isGsm; // This value is set by the
			 * ServiceStateTracker onSignalStrengthResult
			 */
			/**
			 * toString format : "SignalStrength:" + " " + mGsmSignalStrength +
			 * " " + mGsmBitErrorRate + " " + mCdmaDbm + " " + mCdmaEcio + " " +
			 * mEvdoDbm + " " + mEvdoEcio + " " + mEvdoSnr + " " +
			 * mLteSignalStrength + " " + mLteRsrp + " " + mLteRsrq + " " +
			 * mLteRssnr + " " + mLteCqi + " " + (isGsm ? "gsm|lte" : "cdma"
			 */
			Log.i(TAG, "Error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			
			synchronized (this)
			{
				infoHashMap.clear();
				infoHashMap.put("mGsmSignalStrength",
						String.valueOf(signalStrength.getGsmSignalStrength()));
				infoHashMap.put("mGsmBitErrorRate",
						String.valueOf(signalStrength.getGsmBitErrorRate()));
				infoHashMap.put("mCdmaDbm",
						String.valueOf(signalStrength.getCdmaDbm()));
				infoHashMap.put("mCdmaEcio",
						String.valueOf(signalStrength.getCdmaEcio()));
				infoHashMap.put("mEvdoDbm",
						String.valueOf(signalStrength.getEvdoDbm()));
				infoHashMap.put("mEvdoEcio",
						String.valueOf(signalStrength.getEvdoEcio()));
				infoHashMap.put("mEvdoSnr",
						String.valueOf(signalStrength.getEvdoSnr()));
				infoHashMap.put("isGSM", String.valueOf(signalStrength.isGsm()));

				// infoHashMap.put("mLteSignalStrength",
				// String.valueOf(signalStrength.getLteSignalStrength()));
				// 反射获取

				try
				{
					infoHashMap.put("mLteSignalStrength", String
							.valueOf(lteSignalStrengthField.get(signalStrength)));
					infoHashMap.put("mLteRsrp",
							String.valueOf(lteRsrpField.get(signalStrength)));
					infoHashMap.put("mLteRsrq",
							String.valueOf(lteRsrqField.get(signalStrength)));
					infoHashMap.put("mLteCqi",
							String.valueOf(lteCqiField.get(signalStrength)));
				} catch (IllegalAccessException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		@Override
		/**
		 * Callback invoked when a observed cell info has changed,
		 * or new cells have been added or removed.
		 * @param cellInfo is the list of currently visible cells.
		 */
		public void onCellInfoChanged(List<CellInfo> cellInfo)
		{
			super.onCellInfoChanged(cellInfo);
		}

	}





}
