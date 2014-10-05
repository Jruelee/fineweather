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
                     ���汣��    ����BUG                        
                       @author jrue                     
////////////////////////////////////////////////////////////////*/
package org.jrue.fineweather.util;

import org.jrue.fineweather.db.FineWeatherDB;
import org.jrue.fineweather.model.City;
import org.jrue.fineweather.model.County;
import org.jrue.fineweather.model.Province;

import android.text.TextUtils;

/**
 * @author jruelee
 *
 */
public class Utility {
	/**
	 * �����ʹ�����������ص�ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(FineWeatherDB fineWeatherDB,
			String response)
	{
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");  //��ȡʡ������
			if(allProvinces != null && allProvinces.length > 0){
				for(String p : allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					fineWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * �����ʹ�����������ص��м�����
	 */
	public  static boolean handleCitiesResponse(FineWeatherDB fineWeatherDB,
			String response,int provinceId)
	{
		if(!TextUtils.isEmpty(response)){
			String[] allcities = response.split(",");  //��ȡʡ������
			if(allcities != null&& allcities.length > 0){
				for(String c : allcities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					fineWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * �����ʹ�����������ص��ؼ�����
	 */
	public  static boolean handleCountiesResponse(FineWeatherDB fineWeatherDB,
			String response,int cityId)
	{
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");  //��ȡʡ������
			if(allCounties != null && allCounties.length > 0){
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					fineWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
}