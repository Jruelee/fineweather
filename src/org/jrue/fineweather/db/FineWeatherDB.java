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

import java.util.ArrayList;
import java.util.List;

import org.jrue.fineweather.model.City;
import org.jrue.fineweather.model.County;
import org.jrue.fineweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 创建一个FineWeatherDB类，这个类将会把一些常用的数据库操作封装起来，以方便我们后面的使用
 *
 */
public class FineWeatherDB {
	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "fine_weather";
	/**
	 * 数据库版本
	 */
	public static final int VERSON = 1;
	
	private static FineWeatherDB fineWeatherDB;
	private SQLiteDatabase db;
	
	/**
	 * 私有化构造函数
	 */
	private FineWeatherDB(Context context){
		DBOpenhelper dbHelper = new DBOpenhelper(context, DB_NAME, null, VERSON);
		db = dbHelper.getWritableDatabase();
	}
	/**
	 * 获取FineWeatherDB实例
	 */
	public synchronized static FineWeatherDB getInstance(Context context){
		if(fineWeatherDB == null){
			fineWeatherDB = new FineWeatherDB(context);
		}
		return fineWeatherDB;
	}
	/**
	 * 获取province实例储存到数据库
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code",province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	/**
	 * 从数据库读取全国所有的省份信息
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		return list;
	}
	/**
	 * 将city实例储存到数据库中(id是自增长的，所有不需要put id)
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("city", null, values);
		}
	}
	/**
	 * 从数据库中读取某省下所有城市的信息
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	/**
	 * 将County实例储存到数据库
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	/**
	 * 从数据库中读取某城市下所有的县信息
	 */
	public List<County> loadCountries(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
			County county = new County();
			county.setId(cursor.getInt(cursor.getColumnIndex("id")));
			county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
			county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
			list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
	}
}