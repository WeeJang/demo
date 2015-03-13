package commlab.weejang.demo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import commlab.weejang.demo.db.DBManager;
import commlab.weejang.demo.utils.EventHandler;
import commlab.weejang.demo.utils.GlobalVar;
import commlab.weejang.demo.utils.HttpUtils;

public class UploadDataWorker implements Runnable
{
	private static final String TAG = "UploadDataWorker";
	private DBManager dbManager;
	private boolean isFinished;
	private boolean isUploadDataWorkerAlive;
	
	public UploadDataWorker(DBManager dbManager)
	{
		this.dbManager = dbManager;
		this.isFinished = false;
		this.isUploadDataWorkerAlive = true;
	}
	
	public boolean isFinished()
	{
		return isFinished;
	} 
	
	@Override
	public void run()
	{
		//获取最大下标
		long maxId = dbManager.getMaxId();
		//获取上一次上传的最后数据索引
		int startIndex = GlobalVar.uploadedLastIndex;
		Log.i(TAG,"maxId : " + maxId +"startIndex : " + startIndex);
		JSONObject data = null;
		
		//	启动计时线程，超时退出
		new Thread(new Runnable()
		{		
			@Override
			public void run()
			{
				try
				{
					Thread.currentThread().sleep(GlobalVar.uploadTimeOut);
					Log.i(TAG,"timer is awake!");
					if(!isFinished){
						Log.i(TAG,"Error!");
						Bundle eventData = new Bundle();
						eventData.putInt("eventType", GlobalVar.MAIN_EVENT_FLAG_UPLOAD_ERROR);
						EventHandler.getInstance().postEvent(eventData);
						isUploadDataWorkerAlive = false;
					}
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
		
		// 上传
		while(isUploadDataWorkerAlive){			
			//上传
			try
			{
				data = dbManager.packedData2JSON(startIndex, startIndex+GlobalVar.uploadSize);
				//检验是否有数据
//				Log.i(TAG,data.toString());
				if(data == null){
					isFinished = true;
					//发送消息
					Log.i(TAG,"data is null");
					Bundle eventData = new Bundle();
					eventData.putInt("eventType", GlobalVar.MAIN_EVENT_FLAG_UPLOAD_FINISHED);
					EventHandler.getInstance().postEvent(eventData);
					return;
				}
				if(HttpUtils.postJSONData(data)){
					startIndex += data.getInt("dataNum");
					//更新标记
					GlobalVar.uploadedLastIndex = startIndex;
					Log.i(TAG,"POST FINISH");
				}
				//sleep for a while
				Thread.sleep(500);
			}catch(JSONException e1){
				e1.printStackTrace();
			}catch(InterruptedException e2){
				e2.printStackTrace();
			}catch (Exception e3)
			{
				e3.printStackTrace();
			}
		
		}
	}
	
}
