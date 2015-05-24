package commlab.weejang.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.nfc.Tag;
import android.util.Log;
import commlab.weejang.demo.interfaces.IMeasurable;
import commlab.weejang.demo.utils.GlobalVar;

public class PingInfo implements IMeasurable
{
	
	//Ping Tag
	private static final String TAG = "PingInfo";
	
	//
	private HashMap<String, String> infoMap = new HashMap<String, String>();

	@Override
	public int IdentifyMeasurable()
	{
		return GlobalVar.MSG_HANDLER_MEASURE_PING;
	}

	@Override
	public HashMap<String, String> MeasureParameters() 
	{
		// TODO Auto-generated method stub
		Process p ;
		int result = 0 ;
		try
		{
			infoMap.clear();
			//每次ping 10 次．超时　10s 
			p = Runtime.getRuntime().exec("ping -c  10 -w 10 202.112.146.103");
			result = p.waitFor();
			
			InputStream inputStream = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer stringBuffer = new StringBuffer();
			String line = "";
			while((line = in.readLine()) != null){
				stringBuffer.append(line).append("\n");
			}
			in.close();
			inputStream.close();
			
			infoMap.put("pingInfo", stringBuffer.toString());
		
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return infoMap;
	}

	@Override
	public void initDevice()
	{
	}

}
