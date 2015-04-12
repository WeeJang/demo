package commlab.weejang.demo.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.nfc.Tag;
import android.util.Log;

/**
 * http utils
 * 
 * @author jangwee
 * 
 */
public class HttpUtils
{

	private static final String TAG = "HttpUtils";

	@SuppressWarnings("finally")
	public static boolean postJSONData(JSONObject jsonObject) throws Exception
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(GlobalVar.uploadDataUrl);
		// add http header
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

		httpPost.setEntity(new StringEntity("measureData="
				+ jsonObject.toString()));
		HttpResponse httpResponse = httpClient.execute(httpPost);

		Log.i(TAG, httpResponse.getStatusLine().toString());

		if (httpResponse.getStatusLine().getStatusCode() == 200)
		{
			return true;
		} else
		{
			return false;
		}
	}
}
