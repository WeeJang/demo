package commlab.weejang.demo;

import java.util.HashMap;

import commlab.weejang.demo.interfaces.Measurable;
import commlab.weejang.demo.utils.GlobalVar;
import android.net.TrafficStats;
/**
 * 流量测量
 *
 * @author jangwee
 *
 */
public class TrafficInfo implements Measurable
{
	
	private static HashMap<String, String> infoMap = new HashMap<String,String>(6);
	

	@Override
	public int IdentifyMeasurable()
	{
		return GlobalVar.MSG_HANDLER_MEASURE_TRAFFIC;
	}


	@Override
	public HashMap<String, String> MeasureParameters()
	{
		
		long recevieBytes = TrafficStats.getMobileRxBytes();
		long receviePackets = TrafficStats.getMobileRxPackets();
		long totalBytes = TrafficStats.getTotalRxBytes();
		long totalPackets = TrafficStats.getTotalRxPackets();
		
		infoMap.clear();
		infoMap.put("measuretimestamp", String.valueOf(System.currentTimeMillis()));
		infoMap.put("receivebytes", String.valueOf(recevieBytes));
		infoMap.put("receivepackets", String.valueOf(receviePackets));
		infoMap.put("totalbytes",String.valueOf(totalBytes));
		infoMap.put("totalPackets",String.valueOf(totalPackets));
	
		return infoMap;
	}

	@Override
	public void initDevice()
	{
		
	}


}
