package commlab.weejang.demo;



import java.text.SimpleDateFormat;
import java.util.Date;

import commlab.weejang.demo.utils.GlobalVar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 前期辅助UI
 * @author jangwee
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
	Button continusBtn;
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
		continusBtn =(Button)findViewById(R.id.continueBtn);
		stopBtn = (Button)findViewById(R.id.stopBtn);
		
		statuTView = (TextView)findViewById(R.id.statuTextView);
		
		startBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		continusBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
		
		startBtn.setEnabled(true);
		pauseBtn.setEnabled(false);
		continusBtn.setEnabled(false);
		stopBtn.setEnabled(false);
		
		
		

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
					statuTView.setText("started");
					
					startBtn.setEnabled(false);
					continusBtn.setEnabled(false);
					pauseBtn.setEnabled(true);
					stopBtn.setEnabled(true);
					Log.i(TAG, "startBtn");
					
				break;
			//暂停测量
			case R.id.pauseBtn:
					operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_PAUSE;
					statuTView.setText("paused");
						
					pauseBtn.setEnabled(false);
					continusBtn.setEnabled(true);
					stopBtn.setEnabled(false);	
					startBtn.setEnabled(false);
					
					
					Log.i(TAG, "pauseBtn");
				break;
			//重启测量
			
			case R.id.continueBtn:
					operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_CONTIUE;
					statuTView.setText("continued");
					
					startBtn.setEnabled(false);
					pauseBtn.setEnabled(true);
					continusBtn.setEnabled(false);
					stopBtn.setEnabled(true);
					
					
					Log.i(TAG, "continueBtn");
					break;
			//终止测量
			case R.id.stopBtn:
					//终止服务
				 	operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_STOP;
				 	
				 	startBtn.setEnabled(true);
				 	pauseBtn.setEnabled(false);
				 	continusBtn.setEnabled(false);
				 	stopBtn.setEnabled(false);
				 	
				 	
				 	statuTView.setText("stoped");
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
