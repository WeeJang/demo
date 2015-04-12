package commlab.weejang.demo.utils;

/**
 * Handler 的弱引用 
 * 声明为abstract，只能通过继承而实例化 
 */

import java.lang.ref.WeakReference;

import android.os.Handler;

public abstract class WeakHandler<T> extends Handler
{
	private WeakReference<T> mOwner;

	public WeakHandler(T owner)
	{
		mOwner = new WeakReference<T>(owner);
	}

	public T getOwner()
	{
		return mOwner.get();
	}

}
