package commlab.weejang.demo.utils;

/*
 * 定义全局变量
 * 包括 
 *   消息协议，标识
 */

public class GlobalVar
{

	/**
	 * 消息处理标识
	 */
	public static final int MSG_HANDLER_MEASURE_LTE = 0x01; // UMTS测量
	public static final int MSG_HANDLER_MEASURE_WIFI = 0x02; // WIFI测量
	public static final int MSG_HANDLER_MEASURE_TCP = 0x03; // TCP测量
	public static final int MSG_HANDLER_MEASURE_TRAFFIC = 0x04; // Traffic测量

	/**
	 * 控制UMTS测量线程
	 * 
	 */
	public static boolean isMeasureUMTSThreadRun = false;
	public static boolean isMeasureUMTSThreadWait = false;

	/**
	 * 控制WiFi测量线程
	 * 
	 */
	public static boolean isMeasureWiFiThreadRun = false;
	public static boolean isMeasureWiFiThreadWait = false;

	/**
	 * 控制WiFi测量线程
	 * 
	 */
	public static boolean isMeasureTrafficThreadRun = false;
	public static boolean isMeasureTrafficThreadWait = false;

	/**
	 * 日期变量,这个每天都得更新，为数据库的表命名
	 */
	public static String dbTableName = "measure_data";

	/**
	 * 设备ID
	 */
	public static String deviceId = "android";

	/**
	 * 跟测量服务相关的标识
	 * 
	 */
	public static final int SERVICE_OPERATOR_FLAG_INVALID = -1;// 无效的
	public static final int SERVICE_OPERATOR_FLAG_START = 1; // 开始测量
	public static final int SERVICE_OPERATOR_FLAG_PAUSE = 2;// 暂停测量
	public static final int SERVICE_OPERATOR_FLAG_CONTIUE = 3;// 暂停之后继续
	public static final int SERVICE_OPERATOR_FLAG_STOP = 4; // 终止测量

	public static final int SERVICE_OPERATOR_FLAG_UPLOAD = 5;// 上传数据
	public static final int SERVICE_OPERATOR_FLAG_REFRESHINFO = 6;// 刷新数据库信息

	/**
	 * MainActivity 处理事件
	 */
	public static final int MAIN_EVENT_FLAG_UPLOAD_FINISHED = 0x10;// 上传完成
	public static final int MAIN_EVENT_FLAG_UPLOAD_ERROR = 0x11;// 上传失败
	public static final int MAIN_EVENT_FLAG_REFRESH_INFO = 0x12;// 数据库信息更新

	/**
	 * 数据上传
	 */
	public static final String uploadDataUrl = "http://192.168.1.137:8081/insertSignalMeasureData"; // 上传的数据路径
	// public static final String uploadDataUrl =
	// "http://192.168.1.137:8081/insertDashMeasureData"; // 上传的数据路径
	public static final int uploadSize = 100; // 一次性打包上传的数据量
	public static int uploadedLastIndex = 1; // 上传成功最后数据的下一个标号
	public static int uploadTimeOut = 30000; // 上传超时

}
