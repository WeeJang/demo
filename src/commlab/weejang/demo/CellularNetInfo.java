package commlab.weejang.demo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import commlab.weejang.demo.interfaces.IMeasurable;
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
 * Cellular NetWork测量
 * 
 * @author jangwee
 */

@SuppressLint("NewApi")
public class CellularNetInfo implements IMeasurable
{
	// log tag
	private static final String TAG = "MeasureUmtsThread";
	// 上下文
	private Context mContext = null;
	// 管理器
	private TelephonyManager mTelephonyManager = null;
	// 存储测量信息
	private static HashMap<String, String> mInfoMap = new HashMap<String, String>();

	// 跟LTE相关的API在这一版本被@hide，通过reflect获取
	static Class<?> SS;
	static Field lteSignalStrengthField;
	static Field lteRsrpField;
	static Field lteRsrqField;
	static Field lteRssnrField;
	static Field lteCqiField;

	static
	{
		try
		{
			// 获取class对象
			SS = Class.forName("android.telephony.SignalStrength");

			// 获取Field(成员变量)
			lteSignalStrengthField = SS.getDeclaredField("mLteSignalStrength");
			lteRsrpField = SS.getDeclaredField("mLteRsrp");
			lteRsrqField = SS.getDeclaredField("mLteRsrq");
			lteRssnrField = SS.getDeclaredField("mLteRssnr");
			lteCqiField = SS.getDeclaredField("mLteCqi");

			// 提升访问权限
			lteSignalStrengthField.setAccessible(true);
			lteRsrpField.setAccessible(true);
			lteRsrqField.setAccessible(true);
			lteRssnrField.setAccessible(true);
			lteCqiField.setAccessible(true);
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            context 上下文
	 */
	public CellularNetInfo(Context context)
	{
		this.mContext = context;
	}

	@Override
	public int IdentifyMeasurable()
	{
		return GlobalVar.MSG_HANDLER_MEASURE_LTE;
	}

	@Override
	public HashMap<String, String> MeasureParameters()
	{
		return mInfoMap;
	}

	@Override
	public void initDevice()
	{
		// 获取电信服务管理器
		mTelephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 添加监听器
		mTelephonyManager.listen(new MyPhoneStateListener(),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_SERVICE_STATE);
	}

	// 自定义监听器：负责更新（观察者模式）
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
			synchronized (this)
			{

				mInfoMap.clear();
				mInfoMap.put("isGSM", String.valueOf(signalStrength.isGsm()));
				mInfoMap.put("mGsmSignalStrength",
						String.valueOf(signalStrength.getGsmSignalStrength()));
				mInfoMap.put("mGsmBitErrorRate",
						String.valueOf(signalStrength.getGsmBitErrorRate()));

				// //暂时不使用
				// mInfoMap.put("mCdmaDbm",
				// String.valueOf(signalStrength.getCdmaDbm()));
				// mInfoMap.put("mCdmaEcio",
				// String.valueOf(signalStrength.getCdmaEcio()));
				// mInfoMap.put("mEvdoDbm",
				// String.valueOf(signalStrength.getEvdoDbm()));
				// mInfoMap.put("mEvdoEcio",
				// String.valueOf(signalStrength.getEvdoEcio()));
				// mInfoMap.put("mEvdoSnr",
				// String.valueOf(signalStrength.getEvdoSnr()));

				// mInfoMap.put("mLteSignalStrength",
				// String.valueOf(signalStrength.getLteSignalStrength()));
				// 反射获取

				// try
				// {
				// mInfoMap.put("mLteSignalStrength", String
				// .valueOf(lteSignalStrengthField.get(signalStrength)));
				// mInfoMap.put("mLteRsrp",
				// String.valueOf(lteRsrpField.get(signalStrength)));
				// mInfoMap.put("mLteRsrq",
				// String.valueOf(lteRsrqField.get(signalStrength)));
				// mInfoMap.put("mLteCqi",
				// String.valueOf(lteCqiField.get(signalStrength)));
				// } catch (IllegalAccessException e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (IllegalArgumentException e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
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
