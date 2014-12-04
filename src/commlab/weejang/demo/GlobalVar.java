package commlab.weejang.demo;

public class GlobalVar
{

	/**
	 * 消息处理标识
	 */
	public static final int MSG_HANDLER_MEASURE_UMTS = 0x01; // UMTS测量
	public static final int MSG_HANDLER_MEASURE_WIFI = 0x02; // WIFI测量
	public static final int MSG_HANDLER_MEASURE_TRAFFIC = 0x03; //Traffic测量

	/**
	 * 控制UTMS测量线程
	 * 
	 */
	public static boolean isMeasureThreadStop = false;
	public static boolean isMeasureThreadWait = false;

}
