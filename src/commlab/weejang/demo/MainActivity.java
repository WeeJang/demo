package commlab.weejang.demo;

import java.util.HashMap;
import java.util.concurrent.Callable;

import commlab.weejang.demo.utils.GlobalVar;
import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author jangwee UI
 */

public class MainActivity extends Activity implements View.OnClickListener
{

	private static final String TAG = "MainActivity";
	
	private Intent intent;
	
	// 控件
//	TextView umtsTextView = null;
//	TextView wifiTextView = null;
//	TextView trafficTextView = null;
//	Button getButton = null;
		
	Button startBtn;
	Button pauseBtn;
	Button stopBtn;
	TextView statuTView;
	
	// 在完整生存期时开始调用
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

//		// 初始化一个Activity,并填充UI
//		this.setContentView(R.layout.main_view);
//		// 获取控件
//		umtsTextView = (TextView) findViewById(R.id.umtsTextView);
//		wifiTextView = (TextView) findViewById(R.id.wiFiTextView);
//		trafficTextView = (TextView) findViewById(R.id.trafficTextView);
//		getButton = (Button) findViewById(R.id.getButton);
		
		this.setContentView(R.layout.service_view);
		startBtn = (Button)findViewById(R.id.startBtn);
		pauseBtn =(Button)findViewById(R.id.pauseBtn);
		stopBtn = (Button)findViewById(R.id.stopBtn);
		
		
		startBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);

	}
		
		public void onClick(View v)
		{
			
			intent = new Intent("commlab.weejang.demo.MeasureService");
			int operatorFlag  = GlobalVar.SERVICE_OPERATOR_FLAG_INVALID;
			
			switch (v.getId())
			{
			//启动测量
			case R.id.startBtn:
					operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_START;
					
					Log.i(TAG, "startBtn");
					
				break;
			//暂停测量
			case R.id.pauseBtn:
					operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_PAUSE;
					
					Log.i(TAG, "pauseBtn");
				break;
				
			//终止测量
			case R.id.stopBtn:
					//终止服务
				 	operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_STOP;
				
				 	Log.i(TAG, "stopBtn");
				 break;
			default:
				break;
			}
			
			Bundle bundle = new Bundle();
			bundle.putInt("OPF",operatorFlag);
			
			intent.putExtras(bundle);
			//启动服务
			startService(intent);

		}

	


}
