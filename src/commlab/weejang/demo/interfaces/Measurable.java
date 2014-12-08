package commlab.weejang.demo.interfaces;

import java.util.HashMap;

import android.R.integer;
import android.os.Bundle;
import commlab.weejang.demo.MeasureService;


public interface Measurable
{
	//身份验证
	abstract int IdentifyMeasurable();
	//返回测量参数
	abstract HashMap<String, String> MeasureParameters();
	//初始化必要组件
	abstract void  initDevice();
}
