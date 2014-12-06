package commlab.weejang.demo;

import java.util.HashMap;

import commlab.weejang.demo.utils.GlobalVar;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 测量WiFi,没有监听器，开一个线程，定时投放
 * 
 * @author jangwee
 *
 */
public class MeasureWiFi
{

	private Context mContext;
	private Handler mHandler;

	// 管理器
	WifiManager wifiManager;
	// 相关信息类
	WifiInfo wifiInfo;

	// 数据存储
	HashMap<String, String> infoHashMap = new HashMap<String, String>();
	// 消息
	Message msg;
	Bundle data = new Bundle();
	// 管理测量线程
	private boolean isMeasureThreadRun = false;

	public MeasureWiFi(Context context, Handler handler)
	{
		this.mContext = context;
		this.mHandler = handler;
	}

	// 开启测量
	// 将来统一成一个接口，还得重构
	public void initMeasureProc(){
		wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		isMeasureThreadRun = true;
		new Thread(new measureThread()).start();;
		
	}

	public void sendMeasureData()
	{
		msg = mHandler.obtainMessage(GlobalVar.MSG_HANDLER_MEASURE_WIFI);
		data.clear();
		data.putLong("timestamp", System.currentTimeMillis());
		data.putSerializable("data", infoHashMap);
		msg.setData(data);

		mHandler.sendMessage(msg);

	}

	public void stopMeasureProc()
	{
		isMeasureThreadRun = false;
	}

	private class measureThread implements Runnable
	{
		public void run()
		{

			while (isMeasureThreadRun)
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

					sendMeasureData();
					
					
					// 5000ms测试一次
					try
					{
						Thread.currentThread().sleep(5000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}
	}

}
