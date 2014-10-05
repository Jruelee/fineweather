/*/////////////////////////////////////////////////////////////////
                          _ooOoo_                              
                         o8888888o                             
                         88" . "88                             
                         (| ^_^ |)                             
                         O\  =  /O                             
                      ____/`---'\____                           
                    .'  \\|     |//  `.                         
                   /  \\|||  :  |||//  \                       
                  /  _||||| -:- |||||-  \                      
                  |   | \\\  -  /// |   |                      
                  | \_|  ''\---/''  |   |                      
                  \  .-\__  `-`  ___/-. /                       
                ___`. .'  /--.--\  `. . ___                     
              ."" '<  `.___\_<|>_/___.'  >'"".               
            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 
            \  \ `-.   \_ __\ /__ _/   .-` /  /                
      ========`-.____`-.___\_____/___.-`____.-'========         
                           `=---='                              
      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^       
                     佛祖保佑    永无BUG                        
                       @author jrue                     
////////////////////////////////////////////////////////////////*/
package org.jrue.fineweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author jruelee
 *
 */
public class DBOpenhelper extends SQLiteOpenHelper {

	//创建Province表建表语句
	public static final String TABLE_PROVINCE ="create table Province("
       +"id integer primary key autoincrement,"
       +"province_name text,"
       +"province_code text)";
	
	//创建City表建表语句
	public static final String TABLE_CITY = "create table City("
       +"id integer primary key autoincrement,"
       +"city_name text,"
       +"city_code text,"
       +"province_id integer)";
	
	//创建County表建表语句
	public static final String TABLE_COUNTY = "create table County("
      +"id integer primary key autoincrement,"
      +"county_name text,"
      +"county_code text,"
      +"city_id integer)";
	
	
	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DBOpenhelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_PROVINCE);
		db.execSQL(TABLE_CITY);
		db.execSQL(TABLE_COUNTY);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
