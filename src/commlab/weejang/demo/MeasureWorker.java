package commlab.weejang.demo;

/**
 * 测量线程类，驱动测量
 * @author jangwee
 * 
 */

import java.util.HashMap;

import commlab.weejang.demo.interfaces.Measurable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class MeasureWorker implements Runnable
{
	
	private  Measurable mMeasurable ;
	private Handler mHandler;
	
	
	
	private boolean isProcExist = false;
	private boolean isProcPause = false;
	
	private Bundle data = new Bundle();
	
	public MeasureWorker(Measurable mMeasurable,Handler mHandler)
	{
		this.mMeasurable = mMeasurable;
		this.mHandler = mHandler;
	}
	
	public Measurable getMeasurable()
	{
		return this.mMeasurable;
	}
	
	public Handler getHandler()
	{
		return this.mHandler;
	}
	
	public void setMeasurable(Measurable measurable)
	{
		this.mMeasurable = measurable;
	}
	
	public void setHandler(Handler handler)
	{
		this.mHandler = handler;
	}
	
	
	
	public  void initMeasureProc()
	{
		isProcExist  =  true;
		this.mMeasurable.initDevice();
		
	}
	
	public void startMeasureProc()
	{
		new Thread(this).start();
	}
	
	public  void pauseMeasureProc()
	{
		isProcPause = true;
	}
	
	public void  continueMeasureProc()
	{
		isProcPause = false;
	}
	
	
	public void stopMeasureProc()
	{
		isProcExist = false;
	}
	
	
	
	@Override
	public void run()
	{
		while (isProcExist)
		{
			if (!isProcPause)
			{
				HashMap<String, String> getMeasureData = mMeasurable.MeasureParameters();
				Message msg = mHandler.obtainMessage(mMeasurable.IdentifyMeasurable());
				
				data.clear();
				data.putLong("timestamp", System.currentTimeMillis());
				data.putSerializable("data", getMeasureData);
				msg.setData(data);
				
				mHandler.sendMessage(msg);	
			}
			try
			{
				Thread.currentThread().sleep(3000);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
}
