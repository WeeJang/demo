package commlab.weejang.demo;

/**
 * 测量主服务 全局 单独进程
 * 后台记录服务，后期将所有DataStream定向到这里，然后记录到本地，最终定时上传到服务器，服务器汇总
 * 其实还有一个思路，就是本地不断在线学习，这个得到后期模型建立出来，进行参数优化的时候做
 * @author jangwee 
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import commlab.weejang.demo.aidl.ClientMessage;
import commlab.weejang.demo.aidl.IMeasureService;
import commlab.weejang.demo.db.DBManager;
import commlab.weejang.demo.db.MeasureData;
import commlab.weejang.demo.utils.EventHandler;
import commlab.weejang.demo.utils.GlobalVar;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MeasureService extends Service
{
	// log tag
	private static final String TAG = "NoteService";
	// 数据库管理器
	private DBManager mDbManager;
	// 测量管理LoopfulWorker
	private LoopfulWorker mMeasureWoker;
	// 测量数据Handler
	private MeasureHandler mMeasureHandler;

	// 测量LTE worker
	private MeasureWorker mMeasureLTEWorker;
	// 测量WiFi worker
	private MeasureWorker mMeasureWiFiWorker;
	// 测量Traffic worker
	private MeasureWorker mMeasureTrafficWorker;
	// 测量TCP worker
	private MeasureWorker mMeasureTCPWorker;
	// 测量worker Group
	private List<MeasureWorker> mMeasureWorkerGroup = new LinkedList<MeasureWorker>();

	// 实现Remote接口,为IPC预留
	private final IMeasureService.Stub mBinder = new IMeasureService.Stub()
	{
		@Override
		public void collectClientMessage(int pid, ClientMessage clientMessage)
				throws RemoteException
		{
			Log.i("IPC MSG", clientMessage.dataInfo);
			// 写入数据库
			mDbManager.add(clientMessage);
		}
	};

	// 获取数据库管理器
	public DBManager getmDbManager()
	{
		return mDbManager;
	}

	// 构造函数
	public MeasureService()
	{
		super();
		// 实例化LoopfulWorker,MeasureHandler
		mMeasureWoker = new LoopfulWorker("MeasureAll");
		mMeasureHandler = new MeasureHandler(mMeasureWoker.getLooper());
		// 实例化MeasureWorker
		mMeasureLTEWorker = new MeasureWorker(new CellularNetInfo(
				MeasureService.this), mMeasureHandler);
		mMeasureWiFiWorker = new MeasureWorker(
				new WiFiInfo(MeasureService.this), mMeasureHandler);
		mMeasureTrafficWorker = new MeasureWorker(new TrafficInfo(),
				mMeasureHandler);
		mMeasureTCPWorker = new MeasureWorker(new TCPInfo(), mMeasureHandler);
		// 添加进组
		mMeasureWorkerGroup.add(mMeasureLTEWorker);
		mMeasureWorkerGroup.add(mMeasureWiFiWorker);
		mMeasureWorkerGroup.add(mMeasureTrafficWorker);
		mMeasureWorkerGroup.add(mMeasureTCPWorker);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		// 获取电信服务管理器
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 创建数据库表名
		GlobalVar.dbTableName = "commlab_" + tm.getDeviceId() + "_"
				+ new SimpleDateFormat("yyyyMMdd").format(new Date());
		// 实例化数据库
		mDbManager = new DBManager(this);
		// 清除表已存在
		mDbManager.initDBManager();
	}

	/**
	 * 这个地方得改进
	 */

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Bundle data = intent.getExtras();
		// 消息类型匹配
		switch (data.getInt("OPF"))
		{
		// 启动服务
		case GlobalVar.SERVICE_OPERATOR_FLAG_START:
			Log.i(TAG, "receive OPF:  start");
			initMeasure();
			startMeasure();
			break;
		// 暂停服务
		case GlobalVar.SERVICE_OPERATOR_FLAG_PAUSE:
			pauseMeasure();
			Log.i(TAG, "receive OPF:  pause");
			break;
		// 继续服务
		case GlobalVar.SERVICE_OPERATOR_FLAG_CONTIUE:
			continueMeasure();
			Log.i(TAG, "receive OPF:  continue");
			break;
		// 暂停服务
		case GlobalVar.SERVICE_OPERATOR_FLAG_STOP:
			stopMeasure();
			this.stopSelf();
			Log.i(TAG, "receive OPF:  stop");
			break;
		// 上传服务
		case GlobalVar.SERVICE_OPERATOR_FLAG_UPLOAD:
			uploadMeasure();
			Log.i(TAG, "receive OPF: upload");
			break;
		// 刷新服务
		case GlobalVar.SERVICE_OPERATOR_FLAG_REFRESHINFO:
			refreshInfo();
			Log.i("TAG", "receive OPF: refreshInfo");
			break;
		default:
			break;
		}

		return START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mDbManager.closeDB();
	}

	// 初始化测量
	private void initMeasure()
	{
		for (MeasureWorker measureWorker : mMeasureWorkerGroup)
		{
			measureWorker.initMeasureProc();
		}
	}

	// 开始测量
	private void startMeasure()
	{
		for (MeasureWorker measureWorker : mMeasureWorkerGroup)
		{
			measureWorker.startMeasureProc();
		}
	}

	// 暂停测量
	private void pauseMeasure()
	{
		for (MeasureWorker measureWorker : mMeasureWorkerGroup)
		{
			measureWorker.pauseMeasureProc();
		}
	}

	// 重启测量
	private void continueMeasure()
	{
		for (MeasureWorker measureWorker : mMeasureWorkerGroup)
		{
			measureWorker.continueMeasureProc();
		}
	}

	// 终止测量
	private void stopMeasure()
	{
		for (MeasureWorker measureWorker : mMeasureWorkerGroup)
		{
			measureWorker.stopMeasureProc();
		}
	}

	// 刷新当前数据库信息
	private void refreshInfo()
	{
		Bundle eventData = new Bundle();
		eventData.putInt("eventType", GlobalVar.MAIN_EVENT_FLAG_REFRESH_INFO);
		eventData.putString("dataInfo", mDbManager.getDBInfo());
		// 通过事件总线发送消息
		EventHandler.getInstance().postEvent(eventData);
	}

	// 上传测量数据
	private void uploadMeasure()
	{
		// 暂停测量
		pauseMeasure();
		// 开启上传线程
		UploadDataWorker uploadDataWorker = new UploadDataWorker(
				this.getmDbManager());
		new Thread(uploadDataWorker).start();
	}

	// MeasureHandler，负责处理收到的测量数据消息，暂时不接入事件总线，后期优化再汇入
	private class MeasureHandler extends Handler
	{
		public MeasureHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			// data 格式： timestamp | flag | datainfo
			Bundle data = msg.getData();
			if (data == null)
				return;

			// 无时间戳，无效
			Long timeStamp = data.getLong("timestamp");
			if (timeStamp == null)
				return;

			HashMap<String, String> mData = (HashMap<String, String>) data
					.getSerializable("data");
			// 消息类型匹配
			switch (msg.what)
			{
			// 处理LTE相关数据
			case GlobalVar.MSG_HANDLER_MEASURE_LTE:
				mDbManager.add(new MeasureData(timeStamp, "lte", mData
						.toString()));
				Log.i(TAG, "receive lte data: " + mData.toString());
				break;
			// 处理WiFi相关数据
			case GlobalVar.MSG_HANDLER_MEASURE_WIFI:
				mDbManager.add(new MeasureData(timeStamp, "wifi", mData
						.toString()));
				Log.i(TAG, "receive wifi data: " + mData.toString());
				break;
			// 处理traffic相关数据
			case GlobalVar.MSG_HANDLER_MEASURE_TRAFFIC:
				mDbManager.add(new MeasureData(timeStamp, "traffic", mData
						.toString()));
				Log.i(TAG, "receive traffic data: " + mData.toString());
				break;
			// 处理TCP相关数据
			case GlobalVar.MSG_HANDLER_MEASURE_TCP:
				mDbManager.add(new MeasureData(timeStamp, "tcp", mData
						.toString()));
				Log.i(TAG, "receive tcp data: " + mData.toString());
				break;
			default:
				break;
			}
		}
	}
}
