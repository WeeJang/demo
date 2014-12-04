package commlab.weejang.demo;

/**
 * 后台记录服务，后期将所有DataStream定向到这里，然后记录到本地，最终定时上传到服务器，服务器汇总
 * 其实还有一个思路，就是本地不断在线学习，这个得到后期模型建立出来，进行参数优化的时候做
 */

/**
 * 这里利用SQLite数据库存储本地数据，然后导出
 */
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.R.integer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class NoteService extends Service
{

	private static final String TAG = "NoteService";
	
	//日志文件名中的时间格式
	private static SimpleDateFormat logFileLogFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate()
	{
		 TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		//外部写入
		 String sDPath = FileUtils.getSDPath();
		 //得到当前时间
		 Date nowTime = new Date();
		 String logFileString = telephonyManager.getDeviceId()+"-"+logFileLogFormat.format(nowTime)+".log";

		 //创建log文件
		 try
		{
			FileUtils.createSDFile("measureLog", logFileString);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	
	@Override
	public void onStart(Intent intent,int startId)
	{
		
	}
	
	@Override
	public void onDestroy()
	{
		
	}
	

	
}


