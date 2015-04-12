package commlab.weejang.demo.db;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import commlab.weejang.demo.utils.GlobalVar;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author jangwee
 * 
 *         封装所有的业务和方法
 */
public class DBManager
{
	private static final String TAG = "DBManager";

	private DBHelper dbHelper;
	private SQLiteDatabase db;

	public DBManager(Context context)
	{
		dbHelper = new DBHelper(context);
		// 因为getWritableDatabase()内部调用了mContext.openOrCreateDatabase(mName,0,mFactory);
		// 所以要确保context已经初始化，我们可以把实例化DBManager的操作放在Activity/Service等组件的onCreate()里
		db = dbHelper.getWritableDatabase();
	}

	// 初始化
	public void initDBManager()
	{
		updateTable(GlobalVar.dbTableName);
	}

	/**
	 * add measureData单个元素
	 */
	public void add(MeasureData measureData)
	{
		db.execSQL("INSERT INTO " + GlobalVar.dbTableName
				+ "  VALUES (null,?,?,?)", new Object[] {
				measureData.timeStamp, measureData.dataType,
				measureData.dataInfo });
	}

	/**
	 * add meausreData 多个元素
	 * 
	 * @param
	 */
	public void add(List<MeasureData> measureDatas)
	{
		// 开始事务
		db.beginTransaction();
		try
		{
			for (MeasureData measureData : measureDatas)
			{
				db.execSQL("INSERT INTO " + GlobalVar.dbTableName
						+ " VALUES(null,?,?,?)", new Object[] {
						measureData.timeStamp, measureData.dataType,
						measureData.dataInfo });
				Log.i(TAG, String.valueOf(measureData.timeStamp)
						+ measureData.dataType + measureData.dataInfo);
			}
			// 设置事务完成
			db.setTransactionSuccessful();
			Log.i(TAG, "insert finish");
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		} finally
		{
			// 结束事务
			db.endTransaction();
		}
	}

	/**
	 * 返回数据库中最大的_id
	 */
	public int getMaxId()
	{

		String strSql = "SELECT MAX(_id) AS maxId from "
				+ GlobalVar.dbTableName;
		Cursor mCursor = db.rawQuery(strSql, null);
		// !
		mCursor.moveToNext();
		return mCursor.getInt(mCursor.getColumnIndex("maxId"));
	}

	/**
	 * packed data 2 json
	 * 
	 * @throws JSONException
	 */
	public JSONObject packedData2JSON(long startIndex, long endIndex)
			throws JSONException
	{

		if ((startIndex < 0) || (endIndex < 0) || (startIndex > endIndex))
		{
			return null;
		}

		JSONObject retJson = new JSONObject();
		int count = 0;

		Cursor result = db.rawQuery(
				"SELECT time_stamp,data_type,data_info FROM "
						+ GlobalVar.dbTableName
						+ " where (_id >= ? and _id < ?)", new String[] {
						String.valueOf(startIndex), String.valueOf(endIndex) });
		Log.i(TAG, "find [ ] " + startIndex + " " + endIndex);

		// 检查是否为空
		if (!result.moveToFirst())
		{
			return null;
		}

		while (!result.isAfterLast())
		{
			JSONObject dataElem = new JSONObject();
			dataElem.put("timeStamp", result.getLong(0));
			dataElem.put("dataType", result.getString(1));
			dataElem.put("dataInfo", result.getString(2));
			Log.i(TAG,
					"result : " + result.getLong(0) + " " + result.getString(1)
							+ " " + result.getString(2));
			retJson.put(String.valueOf(count++), dataElem);
			// !
			result.moveToNext();
		}
		retJson.put("dataNum", count);
		// {1:{},2:{},……,dataNum:5}
		return retJson;
	}

	/**
	 * 关闭数据库
	 */
	public void closeDB()
	{
		db.close();
	}

	private void updateTable(String tableName)
	{
		// db.execSQL("DROP TABLE IF EXISTS " + tableName);
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableName
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,time_stamp VARCHAR,data_type VARCHAR,data_info TEXT)");
	}

	public String getDBInfo()
	{
		StringBuffer retString = new StringBuffer();
		retString.append("[CurrentTable]").append(GlobalVar.dbTableName);
		retString.append("[MaxId]").append(String.valueOf(getMaxId()));
		retString.append("[UploadedIn]").append(
				String.valueOf(GlobalVar.uploadedLastIndex));
		return retString.toString();
	}
}
