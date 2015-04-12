package commlab.weejang.demo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import commlab.weejang.demo.db.DBManager;
import commlab.weejang.demo.utils.EventHandler;
import commlab.weejang.demo.utils.GlobalVar;
import commlab.weejang.demo.utils.HttpUtils;

public class UploadDataWorker implements Runnable
{
	// log tag
	private static final String TAG = "UploadDataWorker";
	// 数据库管理器
	private DBManager dbManager;
	// 上传完成标识
	private boolean isFinished;
	// 上传线程控制标识
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
		// 获取最大下标
		long maxId = dbManager.getMaxId();
		// 获取上一次上传的最后数据索引
		int startIndex = GlobalVar.uploadedLastIndex;
		Log.i(TAG, "maxId : " + maxId + "startIndex : " + startIndex);
		JSONObject data = null;

		// 启动计时线程，超时退出
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					// 设置超时时间
					Thread.sleep(GlobalVar.uploadTimeOut);
					Log.i(TAG, "timer is awake!");
					// 判断上传是否结束
					if (!isFinished)
					{
						Log.i(TAG, "Error!");
						Bundle eventData = new Bundle();
						eventData.putInt("eventType",
								GlobalVar.MAIN_EVENT_FLAG_UPLOAD_ERROR);
						// 通过事件总线发送上传失败消息
						EventHandler.getInstance().postEvent(eventData);
						// 终止上传线程
						isUploadDataWorkerAlive = false;
					}
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();

		// 上传数据
		while (isUploadDataWorkerAlive)
		{
			try
			{
				// 打包成JSON数据
				data = dbManager.packedData2JSON(startIndex, startIndex
						+ GlobalVar.uploadSize);
				// 判断是否为空
				if (data == null)
				{
					// 数据库中没有需要上传数据
					isFinished = true;
					Log.i(TAG, "data is null");
					// 通过事件总线发送消息
					Bundle eventData = new Bundle();
					eventData.putInt("eventType",
							GlobalVar.MAIN_EVENT_FLAG_UPLOAD_FINISHED);
					EventHandler.getInstance().postEvent(eventData);
					return;
				}
				// 通过HTTP Post 数据
				if (HttpUtils.postJSONData(data))
				{
					startIndex += data.getInt("dataNum");
					// 更新标记
					GlobalVar.uploadedLastIndex = startIndex;
					Log.i(TAG, "POST FINISH");
				}
				// sleep for a while
				Thread.sleep(500);
			} catch (JSONException e1)
			{
				e1.printStackTrace();
			} catch (InterruptedException e2)
			{
				e2.printStackTrace();
			} catch (Exception e3)
			{
				e3.printStackTrace();
			}
		}
	}

}
