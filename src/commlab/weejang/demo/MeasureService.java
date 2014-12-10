package commlab.weejang.demo;

/**
 * 测量主服务 全局 单独进程
 * 后台记录服务，后期将所有DataStream定向到这里，然后记录到本地，最终定时上传到服务器，服务器汇总
 * 其实还有一个思路，就是本地不断在线学习，这个得到后期模型建立出来，进行参数优化的时候做
 * @author jangwee 
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import commlab.weejang.demo.aidl.ClientMessage;
import commlab.weejang.demo.aidl.IMeasureService;
import commlab.weejang.demo.db.DBManager;
import commlab.weejang.demo.db.MeasureData;
import commlab.weejang.demo.utils.GlobalVar;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MeasureService extends Service 
{

	private static final String TAG = "NoteService";
	

	
	//数据库管理类
	private DBManager mDbManager;
	//测量管理子线程类
	private MeasureDataWorker mMeasureWoker;

	//测量数据处理类
	private  MeasureHandler mMeasureHandler;
	
	

	// 测量UMTS
	private MeasureWorker mMeasureUMTSWorker  ;
	// 测量WiFi
	private MeasureWorker mMeasureWiFiWorker  ;
	//测量Traffic
	private  MeasureWorker mMeasureTrafficWorker;
	
	
	//测量信息
	private List<MeasureData> mMeasureDataList ;
	
	//日期格式化
	private static SimpleDateFormat mFormater = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	
	//实现Remote 接口
	private final IMeasureService.Stub mBinder = new IMeasureService.Stub()
	{
		
		@Override
		public void collectClientMessage(int pid, ClientMessage clientMessage)
				throws RemoteException
		{
			Log.i("IPC MSG", clientMessage.dataInfo);
			mMeasureDataList.add(clientMessage);
		}
	};
	
	
	
	public MeasureService()
	{
		super();
		
		mMeasureWoker = new MeasureDataWorker("MeasureAll");
		mMeasureHandler = new MeasureHandler(mMeasureWoker.getLooper());
		
		mMeasureUMTSWorker = new MeasureWorker(new UmtsInfo(MeasureService.this), mMeasureHandler);
		mMeasureWiFiWorker =new MeasureWorker(new WiFiInfo(MeasureService.this), mMeasureHandler);
		mMeasureTrafficWorker = new MeasureWorker(new TrafficInfo(), mMeasureHandler);
		
		mMeasureDataList = new ArrayList<MeasureData>(1024);		
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
		
		//更新表
		GlobalVar.dbTableName = mFormater.format(new Date());
		mDbManager = new DBManager(this);
		//清除表已存在
		mDbManager.initDBManager();
	}
	
	/**
	 * 这个地方得改进
	 */
	@Override
	 public int onStartCommand(Intent intent, int flags, int startId)
	 {
		
		Bundle data = intent.getExtras();
		switch (data.getInt("OPF"))
		{
		case GlobalVar.SERVICE_OPERATOR_FLAG_START:
				Log.i(TAG,"receive OPF:  start" );
				initMeasure();
				startMeasure();
			break;
		case GlobalVar.SERVICE_OPERATOR_FLAG_PAUSE:
				pauseMeasure();
				Log.i(TAG,"receive OPF:  pause" );
			break;
		
		case GlobalVar.SERVICE_OPERATOR_FLAG_CONTIUE:
				continueMeasure();
				Log.i(TAG,"receive OPF:  continue" );
				break;
		
		case GlobalVar.SERVICE_OPERATOR_FLAG_STOP:
				stopMeasure();
				this.stopSelf();
				Log.i(TAG,"receive OPF:  stop" );
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
			
		//初始化测量
		private void initMeasure()
		{
			mMeasureUMTSWorker.initMeasureProc();
			mMeasureWiFiWorker.initMeasureProc();
			mMeasureTrafficWorker.initMeasureProc();
		}
		
		//开始测量
		private void startMeasure()
		{
			mMeasureUMTSWorker.startMeasureProc();
			mMeasureWiFiWorker.startMeasureProc();
			mMeasureTrafficWorker.startMeasureProc();
		}
		
		//暂停测量
		private void pauseMeasure()
		{
			
			mMeasureUMTSWorker.pauseMeasureProc();
			mMeasureWiFiWorker.pauseMeasureProc();	
			mMeasureTrafficWorker.pauseMeasureProc();
			
			//从列表写入数据库
			if (mMeasureDataList.size() > 0)
			{
				mDbManager.add(mMeasureDataList);
				mMeasureDataList.clear();
			}
		}
		
		//重启测量
		private void continueMeasure()
		{
			mMeasureUMTSWorker.continueMeasureProc();
			mMeasureWiFiWorker.continueMeasureProc();
			mMeasureTrafficWorker.continueMeasureProc();
		}
		
		//终止测量
		private void stopMeasure()
		{
			mMeasureUMTSWorker.stopMeasureProc();
			mMeasureWiFiWorker.stopMeasureProc();
			mMeasureTrafficWorker.stopMeasureProc();
		
			//写入数据库
			if (mMeasureDataList.size() > 0)
			{
				mDbManager.add(mMeasureDataList);
				mMeasureDataList.clear();
			}			
		}


		
		//MeasureHandler 负责处理收到的测量数据消息
		private class MeasureHandler  extends Handler
		{
			public MeasureHandler(Looper looper)
			{
				super(looper);
			}
			
			@Override
			public void handleMessage(Message msg)
			{
				// data 格式 统一 ： timestamp + flag(omit) + datadetai
				Bundle data = msg.getData();
					
				if (data == null)
					return;
					// 无时间戳，无效
				Long timeStamp = data.getLong("timestamp");
				if (timeStamp == null)
					return ;
				
				HashMap<String, String> mData = (HashMap<String, String>) data
						.getSerializable("data");
				
				if (mMeasureDataList == null)
					return ;
				switch (msg.what)
				{
				// 处理UMTS
				case GlobalVar.MSG_HANDLER_MEASURE_UMTS:
					mMeasureDataList.add(new MeasureData(timeStamp, "umts", mData.toString()));
					
					Log.i(TAG,"receive umts data: " + mData.toString());
					
					break;
				// 处理WiFi
				case GlobalVar.MSG_HANDLER_MEASURE_WIFI:
					mMeasureDataList.add(new MeasureData(timeStamp, "wifi", mData.toString()));
					
					Log.i(TAG,"receive wifi data: " + mData.toString());
					
					break;
				case GlobalVar.MSG_HANDLER_MEASURE_TRAFFIC:
					mMeasureDataList.add(new MeasureData(timeStamp, "traffic", mData.toString()));
					
					Log.i(TAG,"receive traffic data: " + mData.toString());
					
					break;
				default:
					break;
				}
			}
		}
}


