package commlab.weejang.demo;

import java.util.HashMap;

import android.util.Log;
import commlab.weejang.demo.interfaces.IMeasurable;
import commlab.weejang.demo.utils.GlobalVar;

public class TCPInfo implements IMeasurable
{
	public static final String TAG = "TCPInfo";

	public int rtt; // 平滑rtt(us)
	public int rttvar; // 四分之一mdev(us)
	public int rcv_rtt; // 作为接收端，测出的RTT(us)

	public int last_data_sent;// 最近一次发送数据包是在多久之前(ms)
	public int last_data_recv;// 最近一次接收数据包是在多久之前(ms)
	public int last_data_size;// 本次读取的数据量(Byte)

	HashMap<String, String> infoHashMap = new HashMap<String, String>();

	@Override
	public String toString()
	{
		return ":" + rtt + ":" + rttvar + ":" + rcv_rtt + ":" + last_data_sent
				+ ":" + last_data_recv + ":" + last_data_size;
	}
	//native 方法
	public static native TCPInfo[] native_socket_getStructArray();

	@Override
	public int IdentifyMeasurable()
	{
		return GlobalVar.MSG_HANDLER_MEASURE_TCP;
	}

	@Override
	public void initDevice()
	{
		//载入动态库
		try
		{
			System.loadLibrary("NativeSocket");
		} catch (UnsatisfiedLinkError e)
		{
			Log.i(TAG, "load native socket dynamic lib error...");
			e.printStackTrace();
		}
	}

	@Override
	public HashMap<String, String> MeasureParameters()
	{
		TCPInfo[] tcpInfos = TCPInfo.native_socket_getStructArray();
		int len = 0;
		for (TCPInfo tcpInfo : tcpInfos)
		{
			infoHashMap.put(String.valueOf(len++), tcpInfo.toString());
		}
		return infoHashMap;
	}
}
