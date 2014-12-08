package commlab.weejang.demo;

import java.util.HashMap;

import commlab.weejang.demo.interfaces.Measurable;
import commlab.weejang.demo.utils.GlobalVar;
import android.net.TrafficStats;
/**
 * 统计流量，监控某一进程，获取流量信息。
 * 思路 ： 先扫描获取手机中的浏览器，获得对应的启动进程的PID，然后进行流量检测，可以达到测网速的目的
 *
 * @author jangwee
 *
 */
public class MeasureTraffic implements Measurable
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
