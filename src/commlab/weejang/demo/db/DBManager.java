package commlab.weejang.demo.db;

import java.util.List;

import commlab.weejang.demo.utils.GlobalVar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author jangwee
 * 
 * DBManager 封装所有的业务和方法
 */
public class DBManager
{
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	
	public DBManager(Context context)
	{
		dbHelper = new DBHelper(context);
		//因为getWritableDatabase()内部调用了mContext.openOrCreateDatabase(mName,0,mFactory);
		//所以要确保context已经初始化，我们可以把实例化DBManager的操作放在Activity/Service等组件的onCreate()里
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * add meausreData
	 * @param 
	 */
	public void add (List<MeasureData> measureDatas)
	{
		//开始事务
		db.beginTransaction();
		
		try
		{
			for (MeasureData measureData : measureDatas)
			{
				db.execSQL("INSERT INTO" +GlobalVar.dbTableName + "VALUES(null,?,?,?)", 
										new Object[]{measureData.timeStamp,measureData.dataType,measureData.dataInfo});
			}
			//设置事务完成
			db.setTransactionSuccessful();
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			//结束事务
			db.endTransaction();
		}
	}
	
	public void closeDB()
	{
		db.close();
	}
	
}
