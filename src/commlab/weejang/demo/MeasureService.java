package commlab.weejang.demo;

/**
 * 后台记录服务，后期将所有DataStream定向到这里，然后记录到本地，最终定时上传到服务器，服务器汇总
 * 其实还有一个思路，就是本地不断在线学习，这个得到后期模型建立出来，进行参数优化的时候做
 */

/**
 * 这里利用SQLite数据库存储本地数据，然后导出
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;


import commlab.weejang.demo.db.DBManager;
import commlab.weejang.demo.db.MeasureData;
import commlab.weejang.demo.utils.FileUtils;
import commlab.weejang.demo.utils.GlobalVar;
import android.R.integer;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MeasureService extends Service 
{

	private static final String TAG = "NoteService";
	
	//数据库管理类
	private DBManager dbManager;
	//测量管理子线程类
	private MeasureWoker mMeasureWoker;
		

	//测量数据处理类
	private  MeasureHandler measureHandler;
	
	

	// 测量UMTS
	private  MeasureUmts mMeasureUmts  ;
	// 测量WiFi
	private  MeasureWiFi mMeasureWiFi  ;
	//测量Traffic
	private  MeasureTraffic mMeasureTraffic;
	
	
	//测量信息
	private List<MeasureData> measureDatas ;
	
	
	public MeasureService()
	{
		super();
		
		mMeasureWoker = new MeasureWoker("MeasureAll");
		measureHandler = new MeasureHandler(mMeasureWoker.getLooper());

		mMeasureUmts = new MeasureUmts(MeasureService.this, measureHandler);
		mMeasureWiFi = new MeasureWiFi(MeasureService.this, measureHandler);
		mMeasureTraffic = new MeasureTraffic(measureHandler);
		
		measureDatas = new ArrayList<MeasureData>(1024);
		
	}
		
	@Override
	public void onCreate()
	{
		super.onCreate();
		dbManager = new DBManager(this);
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
				startMeasure();
			break;
		case GlobalVar.SERVICE_OPERATOR_FLAG_PAUSE:
				pauseMeasure();
				Log.i(TAG,"receive OPF:  pause" );
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
		dbManager.closeDB();
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
				
				if (measureDatas == null)
					return ;
				switch (msg.what)
				{
				// 处理UMTS
				case GlobalVar.MSG_HANDLER_MEASURE_UMTS:
					measureDatas.add(new MeasureData(timeStamp, "umts", mData.toString()));
					
					Log.i(TAG,"receive umts data: " + mData.toString());
					
					break;
				// 处理WiFi
				case GlobalVar.MSG_HANDLER_MEASURE_WIFI:
					measureDatas.add(new MeasureData(timeStamp, "wifi", mData.toString()));
					
					Log.i(TAG,"receive wifi data: " + mData.toString());
					
					break;
				case GlobalVar.MSG_HANDLER_MEASURE_TRAFFIC:
					measureDatas.add(new MeasureData(timeStamp, "traffic", mData.toString()));
					
					Log.i(TAG,"receive traffic data: " + mData.toString());
					
					break;
				default:
					break;
				}
			}
		}
		
		
		//开始测量
		private void startMeasure()
		{
			// TODO Auto-generated method stub
			// 开启UMTS测量
			mMeasureUmts.initMeasureProc();
			//开启WiFi测量
			mMeasureWiFi.initMeasureProc();
			//开启Traffic测量
			mMeasureTraffic.initMeasureProc();
		}
		
		//暂停测量
		private void pauseMeasure()
		{
			// TODO Auto-generated method stub			
			
			//从列表写入数据库
			if (measureDatas.size() > 0)
			{
				dbManager.add(measureDatas);
				measureDatas.clear();
			}
		}
		
		
		//终止测量
		private void stopMeasure()
		{
			
			//写入数据库
			if (measureDatas.size() > 0)
			{
				dbManager.add(measureDatas);
				measureDatas.clear();
			}
		}

		@Override
		public IBinder onBind(Intent intent)
		{
			// TODO Auto-generated method stub
			return null;
		}

}


