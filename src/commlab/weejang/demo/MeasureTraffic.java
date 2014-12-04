package commlab.weejang.demo;

import java.util.HashMap;

import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.net.TrafficStatsCompat;

/**
 * 统计流量，监控某一进程，获取流量信息。
 * 思路 ： 先扫描获取手机中的浏览器，获得对应的启动进程的PID，然后进行流量检测，可以达到测网速的目的
 *
 * @author jangwee
 *
 */
public class MeasureTraffic
{
	
	//Handler
	private Handler mHandler;
	
	//存储测量信息,这里面要有测量点的时间戳（measuretimestamp），跟发送的时间戳(sendtimestamp)不一样，为了计算速率准确。
	private static HashMap<String, String> infoMap = new HashMap<String,String>();
	
	//控制测量线程
	private static boolean isMeasureRun = false;
	
	private Bundle data = new Bundle();
	
	
	public MeasureTraffic(Handler handler)
	{
		this.mHandler = handler;
	}
	
	//开启测量
	public void initMeasureProc()
	{
		isMeasureRun = true;
		//开启线程
		new Thread(new measureThread()).start();
		
	}
	
	//重置
	
	//关闭测量进程
	public void stopMeasureProc()
	{
		isMeasureRun = false;
	}
	
	//发送测量信息
	public void sendMeasureData()
	{
		Message msg = mHandler.obtainMessage(GlobalVar.MSG_HANDLER_MEASURE_TRAFFIC);
		data.clear();
		data.putLong("timestamp", System.currentTimeMillis());
		data.putSerializable("data", infoMap);
		msg.setData(data);
		
		mHandler.sendMessage(msg);
		
	}

	//测量线程
	//TrafficStates类有大量信息，
	private class measureThread implements Runnable
	{
		public void run()
		{
			while (isMeasureRun)
			{
				//更新信息
				//long lastMearsureTimeStamp = Long.valueOf(infoMap.get("measuretimestamp"));
				
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
				
				sendMeasureData();
				
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
