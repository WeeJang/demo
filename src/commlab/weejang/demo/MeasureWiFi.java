package commlab.weejang.demo;

import java.util.HashMap;

import commlab.weejang.demo.interfaces.Measurable;
import commlab.weejang.demo.utils.GlobalVar;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


/**
 * 测量WiFi,没有监听器，开一个线程，定时投放
 * 
 * @author jangwee
 *
 */
public class MeasureWiFi implements Measurable
{

	private Context mContext;
	// 管理器
	WifiManager wifiManager;
	// 相关信息类
	WifiInfo wifiInfo;

	// 数据存储
	HashMap<String, String> infoHashMap = new HashMap<String, String>();

	public MeasureWiFi(Context context)
	{
		this.mContext = context;
	}
	
	@Override
	public void initDevice()
	{
	wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
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
		if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
		{
			wifiInfo = wifiManager.getConnectionInfo();

			infoHashMap.clear();
			infoHashMap.put("SSID", wifiInfo.getSSID());
			infoHashMap.put("BSSID", wifiInfo.getBSSID());
			infoHashMap.put("MAC", wifiInfo.getMacAddress());
			infoHashMap.put("HiddenSSID",
					String.valueOf(wifiInfo.getHiddenSSID()));
			infoHashMap.put("IP",
					String.valueOf(wifiInfo.getIpAddress()));
			infoHashMap.put("LinkSpeed",
					String.valueOf(wifiInfo.getLinkSpeed()));
			infoHashMap.put("RSSI", String.valueOf(wifiInfo.getRssi()));
			infoHashMap.put("NetWorkID",
					String.valueOf(wifiInfo.getNetworkId()));
		}
		return infoHashMap;
	}

  
}
