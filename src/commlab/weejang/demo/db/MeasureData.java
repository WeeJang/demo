package commlab.weejang.demo.db;

import android.R.string;

/**
 * 
 * @author jangwee
 * 定义传输的数据格式
 */

public class MeasureData
{
	//时间戳
	public long timeStamp;
	//数据类型: 'wifi' '3G','Traffic'
	public String dataType;
	//数据内容
	public String dataInfo;
	
	public MeasureData(long timeStamp,String dataType,String dataInfo)
	{
		this.timeStamp = timeStamp;
		this.dataType = dataType;
		this.dataInfo = dataInfo;
	}
	
}
