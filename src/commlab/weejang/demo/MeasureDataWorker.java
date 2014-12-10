package commlab.weejang.demo;

import android.os.Looper;

/**
 * 参考MediaPlaybackActivity.java
 *  维护测量消息队列
 * @author jangwee
 *
 */

public class MeasureDataWorker implements Runnable
{

	private final Object mLock = new Object();
	private Looper mLooper;
	
	/**
	 * create measuer thread with the given name.
	 */
		
	public  MeasureDataWorker(String name)
	{
		Thread t = new Thread(null,this,name);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
		
		synchronized (mLock)
		{
			while (mLooper == null)
			{
				try
				{
					mLock.wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public Looper getLooper()
	{
		return this.mLooper;
	}
	
	@Override
	public void run()
	{
		synchronized (mLock)
		{
			Looper.prepare();
			mLooper = Looper.myLooper();
			mLock.notifyAll();
		}
		Looper.loop();
	}
	
	public void quit()
	{
		mLooper.quit();
	}
}
