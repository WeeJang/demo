package commlab.weejang.demo;

import android.app.Notification.Action;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver
{
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(ACTION))
		{
			Intent startService = new Intent(context,MeasureService.class);
			startService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startService(startService);
		}
	}

}
