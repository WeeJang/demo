package commlab.weejang.demo;

/**
 * 测量线程类，驱动测量
 * @author jangwee
 * 
 */

import java.util.HashMap;

import commlab.weejang.demo.interfaces.IMeasurable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MeasureWorker implements Runnable
{
	// 可测量对象
	private IMeasurable mMeasurable;
	// 测量消息Handler
	private Handler mHandler;
	// 控制线程退出
	private boolean isProcExist = false;
	// 控制线程暂停
	private boolean isProcPause = false;
	// 消息载体
	private Bundle data = new Bundle();

	public MeasureWorker(IMeasurable mMeasurable, Handler mHandler)
	{
		this.mMeasurable = mMeasurable;
		this.mHandler = mHandler;
	}

	public IMeasurable getMeasurable()
	{
		return this.mMeasurable;
	}

	public Handler getHandler()
	{
		return this.mHandler;
	}

	public void setMeasurable(IMeasurable measurable)
	{
		this.mMeasurable = measurable;
	}

	public void setHandler(Handler handler)
	{
		this.mHandler = handler;
	}

	// 初始化测量
	public void initMeasureProc()
	{
		isProcExist = true;
		this.mMeasurable.initDevice();

	}

	// 开始测量
	public void startMeasureProc()
	{
		new Thread(this).start();
	}

	// 暂停测量
	public void pauseMeasureProc()
	{
		isProcPause = true;
	}

	// 继续测量
	public void continueMeasureProc()
	{
		isProcPause = false;
	}

	// 停止测量
	public void stopMeasureProc()
	{
		isProcExist = false;
	}

	@Override
	public void run()
	{
		// 是否退出
		while (isProcExist)
		{
			// 是否暂停
			if (!isProcPause)
			{
				// 获取测量数据
				HashMap<String, String> getMeasureData = mMeasurable
						.MeasureParameters();
				// 获取测量类型
				Message msg = mHandler.obtainMessage(mMeasurable
						.IdentifyMeasurable());
				// 组装消息：timestamp | data |
				data.clear();
				data.putLong("timestamp", System.currentTimeMillis());
				data.putSerializable("data", getMeasureData);
				msg.setData(data);
				// 发送消息
				mHandler.sendMessage(msg);
			}
			try
			{
				// 暂停1秒
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
