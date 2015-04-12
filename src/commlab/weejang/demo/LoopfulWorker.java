package commlab.weejang.demo;

import android.os.Looper;

/**
 * A worker thread with loopful trait 参考MediaPlaybackActivity.java设计
 * 
 * @author jangwee
 */

public class LoopfulWorker implements Runnable
{
	// 锁对象
	private final Object mLock = new Object();
	// Looper
	private Looper mLooper;

	/**
	 * @param name
	 *            the name of loopful worker
	 */
	public LoopfulWorker(String name)
	{
		// 创建线程
		Thread t = new Thread(null, this, name);
		// 设置最低优先级
		t.setPriority(Thread.MIN_PRIORITY);
		// 运行线程
		t.start();

		// 同步，防止多次创建mLooper
		synchronized (mLock)
		{
			while (mLooper == null)
			{
				try
				{
					mLock.wait();
				} catch (InterruptedException e)
				{
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
