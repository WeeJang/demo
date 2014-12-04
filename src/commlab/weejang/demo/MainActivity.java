package commlab.weejang.demo;

import java.util.HashMap;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

public class MainActivity extends Activity implements
		android.os.Handler.Callback
{

	private static final String TAG = "MainActivity";
	// 控件
	TextView umtsTextView = null;
	TextView wifiTextView = null;
	TextView trafficTextView = null;

	Button getButton = null;

	// Handler
	private final Handler mHandler = new Handler(Looper.getMainLooper(), this);
	// 测量UMTS
	private final MeasureUmts mMeasureUmts = new MeasureUmts(MainActivity.this,mHandler);
	// 测量WiFi
	private final MeasureWiFi mMeasureWiFi = new MeasureWiFi(MainActivity.this, mHandler);
	//测量Traffic
	private final MeasureTraffic mMeasureTraffic = new MeasureTraffic(mHandler);
	
	
	// 在完整生存期时开始调用
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 初始化一个Activity,并填充UI
		this.setContentView(R.layout.main_view);

		// 获取控件
		umtsTextView = (TextView) findViewById(R.id.umtsTextView);
		wifiTextView = (TextView) findViewById(R.id.wiFiTextView);
		trafficTextView = (TextView) findViewById(R.id.trafficTextView);
		getButton = (Button) findViewById(R.id.getButton);



		getButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				umtsTextView.setText("on");
				wifiTextView.setText("up");
				trafficTextView.setText("down");

				// 开启UMTS测量
				mMeasureUmts.initMeasureProc();
				//开启WiFi测量
				mMeasureWiFi.initMeasureProc();
				//开启Traffic测量
				mMeasureTraffic.initMeasureProc();
			}
		});
	}

	
//	@Override
//	public void onStop() 
//	{
//	//	mMeasureWiFi.stopMeasureProc();
//	//	mMeasureUmts.stopMeasureProc();
//	//	mMeasureTraffic.stopMeasureProc();
//	}
	
	
		
	// 发送给UI(Main-Thread)的消息，在此处理，实现android.os.Handler.Callback接口[优先级：2]
	//
	@Override
	public boolean handleMessage(Message msg)
	{
		// TODO Auto-generated method stub

		// data 格式 统一 ： timestamp + flag(omit) + datadetai
		Bundle data = msg.getData();
		if (data == null)
		{
			return false;
		}

		// 无时间戳，无效
		Long timeStamp = data.getLong("timestamp");
		if (timeStamp == null)
		{
			return false;
		}

		switch (msg.what)
		{
		// 处理UMTS
		case GlobalVar.MSG_HANDLER_MEASURE_UMTS:

			HashMap<String, String> umtsMeasureData = (HashMap<String, String>) data
					.getSerializable("data");
			// Log.v(TAG,timeStamp + ": " + measureData);
			if (umtsMeasureData != null)
			{
				umtsTextView.setText(timeStamp + ": "
						+ umtsMeasureData);
			}

			break;
		// 处理WiFi
		case GlobalVar.MSG_HANDLER_MEASURE_WIFI:

			HashMap<String, String> wifiMeasureData = (HashMap<String, String>) data
					.getSerializable("data");
			if (wifiMeasureData != null)
			{
				wifiTextView.setText(timeStamp + ": " +wifiMeasureData);
			}
			
			break;
		
		case GlobalVar.MSG_HANDLER_MEASURE_TRAFFIC:
				HashMap<String, String>  trafficMeasureData = (HashMap<String, String>)data
					.getSerializable("data");
				if (trafficMeasureData != null)
				{
					trafficTextView.setText(timeStamp + ": " + trafficMeasureData);
				}
			break;

		default:
			break;
		}

		return false;
	}

	// Handler -Main

}
