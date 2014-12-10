package commlab.weejang.demo.interfaces;

/**
 * @author jangwee 
 * 定义可测量的接口
 */

import java.util.HashMap;




public interface Measurable
{
	//身份验证
	abstract int IdentifyMeasurable();
	//返回测量参数
	abstract HashMap<String, String> MeasureParameters();
	//初始化必要组件
	abstract void  initDevice();
}
