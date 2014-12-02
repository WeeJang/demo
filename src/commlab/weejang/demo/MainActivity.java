package commlab.weejang.demo;

import java.util.concurrent.Callable;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author jangwee
 *	 UI 
 */

public class MainActivity extends Activity implements android.os.Handler.Callback {
	

	//控件
	TextView umtsTextView = null;
	TextView wifiTextView = null;
	TextView trafficTextView = null;
	
	Button getButton = null;
	
	SignalStrength signalStrength = null;
    MeasureUmtsThread measureUmtsThread = null; 
    
    private static  Handler mHandler = null;
  
	
	//在完整生存期时开始调用
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//初始化一个Activity,并填充UI
		this.setContentView(R.layout.main_view);
		
		//获取控件
		umtsTextView = (TextView)findViewById(R.id.umtsTextView);
		wifiTextView =(TextView)findViewById(R.id.wiFiTextView);
		trafficTextView=(TextView)findViewById(R.id.trafficTextView);	
		getButton =(Button)findViewById(R.id.getButton);
		
		//mHanler
		mHandler = new Handler(Looper.getMainLooper(), this);
		
		getButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				umtsTextView.setText("on");
				wifiTextView.setText("up");
				trafficTextView.setText("down");
				
				//测量线程  
				
				measureUmtsThread = new MeasureUmtsThread(MainActivity.this,mHandler); //MainActivity.this  not this
				//开启测量
				GlobalVar.isMeasureThreadStop = false;
				GlobalVar.isMeasureThreadWait = false;
				new Thread(measureUmtsThread).start();
			}
		});
	}


	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			umtsTextView.setText(msg.obj.toString());
			break;

		default:
			break;
		}
		
		return false;
	}

	//Handler -Main
	
	
}
