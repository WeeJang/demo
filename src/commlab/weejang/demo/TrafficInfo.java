package commlab.weejang.demo;

import java.util.HashMap;

import commlab.weejang.demo.interfaces.IMeasurable;
import commlab.weejang.demo.utils.GlobalVar;
import android.net.TrafficStats;

/**
 * 流量测量
 *
 * @author jangwee
 *
 */
public class TrafficInfo implements IMeasurable
{
	private static HashMap<String, String> infoMap = new HashMap<String, String>(
			6);

	@Override
	public int IdentifyMeasurable()
	{
		return GlobalVar.MSG_HANDLER_MEASURE_TRAFFIC;
	}

	@Override
	public HashMap<String, String> MeasureParameters()
	{
		infoMap.clear();
		infoMap.put("measuretimestamp",
				String.valueOf(System.currentTimeMillis()));
		// UE 全部流量(WiFi + MN)
		infoMap.put("totalRxbytes",
				String.valueOf(TrafficStats.getTotalRxBytes()));
		infoMap.put("totalRxPackets",
				String.valueOf(TrafficStats.getTotalRxPackets()));
		return infoMap;
	}

	@Override
	public void initDevice()
	{
	}
}
