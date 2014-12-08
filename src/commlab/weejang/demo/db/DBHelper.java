package commlab.weejang.demo.db;


/**
 * @author jangwee 
 * @ DBHelper 维护和管理数据库的基类
 * 
 * 
 */

import commlab.weejang.demo.utils.GlobalVar;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	
	private static final String DATABASE_NAME = "measuredata.db";
	
	private static final int DATABASE_VERSION = 1;
	

	public DBHelper(Context context)
	{
		// TODO Auto-generated constructor stub
		// CursorFactory设置为null,使用默认值 
		super(context,DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	//数据库第一次创建时，onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//db.execSQL("DROP TABLE IF EXISTS " + GlobalVar.dbTableName);
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS "+GlobalVar.dbTableName +
								"(_id INTEGER PRIMARY KEY AUTOINCREMENT,time_stamp VARCHAR,data_type VARCHAR,data_info TEXT)");
		
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub

	}

	
}
