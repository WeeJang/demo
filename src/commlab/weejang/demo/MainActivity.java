package commlab.weejang.demo;

import commlab.weejang.demo.utils.EventHandler;
import commlab.weejang.demo.utils.GlobalVar;
import commlab.weejang.demo.utils.WeakHandler;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 前期辅助UI
 * 
 * @author jangwee
 */

public class MainActivity extends Activity implements View.OnClickListener
{
	// log tag
	private static final String TAG = "MainActivity";
	//
	private SignalShark signalShark;
	//
	private Intent intent;

	// UI控件
	Button startBtn;
	Button pauseBtn;
	Button continusBtn;
	Button stopBtn;
	Button uploadBtn;
	Button refreshInfoBtn;

	TextView statuTextView;
	TextView infoTextView;

	// 在完整生存期时开始调用
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.service_view);

		signalShark = (SignalShark) this.getApplication();

		startBtn = (Button) findViewById(R.id.startBtn);
		pauseBtn = (Button) findViewById(R.id.pauseBtn);
		continusBtn = (Button) findViewById(R.id.continueBtn);
		stopBtn = (Button) findViewById(R.id.stopBtn);
		uploadBtn = (Button) findViewById(R.id.uploadBtn);
		refreshInfoBtn = (Button) findViewById(R.id.refreshInfoBtn);

		statuTextView = (TextView) findViewById(R.id.statuTextView);
		infoTextView = (TextView) findViewById(R.id.infoTextView);

		startBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		continusBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
		uploadBtn.setOnClickListener(this);
		refreshInfoBtn.setOnClickListener(this);

		startBtn.setEnabled(true);
		pauseBtn.setEnabled(false);
		continusBtn.setEnabled(false);
		stopBtn.setEnabled(false);

		// 注册handler
		EventHandler.getInstance().addHandler(mHandler);

	}

	public void onClick(View v)
	{

		intent = new Intent("commlab.weejang.demo.MeasureService");
		int operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_INVALID;

		switch (v.getId())
		{
		// 启动测量
		case R.id.startBtn:
			operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_START;
			statuTextView.setText("started");

			startBtn.setEnabled(false);
			continusBtn.setEnabled(false);
			pauseBtn.setEnabled(true);
			stopBtn.setEnabled(true);
			Log.i(TAG, "startBtn");

			break;
		// 暂停测量
		case R.id.pauseBtn:
			operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_PAUSE;
			statuTextView.setText("paused");

			pauseBtn.setEnabled(false);
			continusBtn.setEnabled(true);
			stopBtn.setEnabled(false);
			startBtn.setEnabled(false);

			Log.i(TAG, "pauseBtn");
			break;
		// 重启测量

		case R.id.continueBtn:
			operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_CONTIUE;
			statuTextView.setText("continued");

			startBtn.setEnabled(false);
			pauseBtn.setEnabled(true);
			continusBtn.setEnabled(false);
			stopBtn.setEnabled(true);

			Log.i(TAG, "continueBtn");
			break;
		// 终止测量
		case R.id.stopBtn:
			// 终止服务
			operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_STOP;

			startBtn.setEnabled(true);
			pauseBtn.setEnabled(false);
			continusBtn.setEnabled(false);
			stopBtn.setEnabled(false);

			statuTextView.setText("stoped");
			Log.i(TAG, "stopBtn");
			break;

		// 上传数据
		case R.id.uploadBtn:
			operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_UPLOAD;

			statuTextView.setText("uploading data ...");

			pauseBtn.setEnabled(false);
			continusBtn.setEnabled(true);
			stopBtn.setEnabled(false);
			startBtn.setEnabled(false);

			break;

		// 更新当前数据库状态
		case R.id.refreshInfoBtn:
			operatorFlag = GlobalVar.SERVICE_OPERATOR_FLAG_REFRESHINFO;
			Log.i(TAG, "refreshInfo");
			break;

		default:
			break;
		}
		// 向Service发送消息
		Bundle bundle = new Bundle();
		bundle.putInt("OPF", operatorFlag);
		intent.putExtras(bundle);
		// 启动服务
		startService(intent);
	}

	// 设置状态
	private void setStatue(String statue)
	{
		this.statuTextView.setText(statue);
	}

	// 设置信息
	private void setInfo(String info)
	{
		this.infoTextView.setText(info);
	}

	// MainActivity Handler
	public Handler mHandler = new MainActivityHandler(this);

	// 注：static : 不需要将静态内部类的实例绑定在外部类的实例上 否则 @SuppressLint("HandlerLeak")
	private static class MainActivityHandler extends WeakHandler<MainActivity>
	{
		public MainActivityHandler(MainActivity owner)
		{
			super(owner);
		}

		@Override
		public void handleMessage(Message msg)
		{
			// 获取Owner引用
			MainActivity mainActivity = this.getOwner();
			Log.i(TAG, "MainActivityHandler Got Event!");
			// 检查是否被GC
			if (mainActivity == null)
			{
				Log.v(TAG, "MainActivity is null!");
				return;
			}
			// 获取数据
			Bundle data = msg.getData();
			int eventType = data.getInt("eventType");
			// 消息匹配
			switch (eventType)
			{
			case GlobalVar.MAIN_EVENT_FLAG_UPLOAD_FINISHED:
				mainActivity.setStatue("upload Finished!");
				break;
			case GlobalVar.MAIN_EVENT_FLAG_UPLOAD_ERROR:
				mainActivity.setStatue("upload Error!");
				break;
			case GlobalVar.MAIN_EVENT_FLAG_REFRESH_INFO:
				mainActivity.setInfo(data.getString("dataInfo"));
				break;
			default:
				break;
			}
		}
	}
}
