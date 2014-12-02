package commlab.weejang.demo;

public class GlobalVar {
	
	
	
	/**
	 *消息处理标识 
	 */
	
	public static enum MSG_FLAG_INT
	{
		utms_measure_data /*获取的UTMS测量结果*/
	}
	
	/**
	 * 控制UTMS测量线程
	 * 
	 */
	public static boolean isMeasureThreadStop = false;
	public static boolean isMeasureThreadWait = false;
	

}
