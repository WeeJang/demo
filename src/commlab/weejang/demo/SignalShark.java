package commlab.weejang.demo;

import commlab.weejang.demo.utils.EventHandler;

import android.app.Application;

public class SignalShark extends Application
{
	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		EventHandler.getInstance();
	}
}
