package commlab.weejang.demo;

import java.util.HashMap;

import commlab.weejang.demo.interfaces.Measurable;
import commlab.weejang.demo.utils.GlobalVar;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


/**
 * WiFi测量
 * 
 * @author jangwee
 *
 */
public class WiFiInfo implements Measurable
{

	private Context mContext;
	// 管理器
	private WifiManager mWifiManager;
	// 相关信息类
	private WifiInfo mWifiInfo;

	// 数据存储
	HashMap<String, String> infoHashMap = new HashMap<String, String>();

	public WiFiInfo(Context context)
	{
		this.mContext = context;
	}
	
	@Override
	public void initDevice()
	{
	mWifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	@Override
	public int IdentifyMeasurable()
	{
		// TODO Auto-generated method stub
		return GlobalVar.MSG_HANDLER_MEASURE_WIFI;
	}

	@Override
	public HashMap<String, String> MeasureParameters()
	{
		if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
		{
			mWifiInfo = mWifiManager.getConnectionInfo();

			infoHashMap.clear();
			infoHashMap.put("SSID", mWifiInfo.getSSID());
			infoHashMap.put("BSSID", mWifiInfo.getBSSID());
			infoHashMap.put("MAC", mWifiInfo.getMacAddress());
			infoHashMap.put("HiddenSSID",
					String.valueOf(mWifiInfo.getHiddenSSID()));
			infoHashMap.put("IP",
					String.valueOf(mWifiInfo.getIpAddress()));
			infoHashMap.put("LinkSpeed",
					String.valueOf(mWifiInfo.getLinkSpeed()));
			infoHashMap.put("RSSI", String.valueOf(mWifiInfo.getRssi()));
			infoHashMap.put("NetWorkID",
					String.valueOf(mWifiInfo.getNetworkId()));
		}
		return infoHashMap;
	}

  
}
