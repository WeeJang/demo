package commlab.weejang.demo;

/**
 * 开机自动激活测量服务
 * @author jangwee
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver
{
	
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
/*
		if (intent.getAction().equals(ACTION))
		{
			Intent startService = new Intent(context,MeasureService.class);
			startService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(startService);
		}
*/
	}
}
