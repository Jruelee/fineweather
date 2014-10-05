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
package org.jrue.fineweather.activities;

import java.util.ArrayList;
import java.util.List;

import org.jrue.fineweather.activities.R;
import org.jrue.fineweather.db.FineWeatherDB;
import org.jrue.fineweather.model.City;
import org.jrue.fineweather.model.County;
import org.jrue.fineweather.model.Province;
import org.jrue.fineweather.util.HttpCallbackListener;
import org.jrue.fineweather.util.HttpUtil;
import org.jrue.fineweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author jruelee
 *
 */

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private FineWeatherDB fineWeatherDB;
	private List<String> dataList = new ArrayList<String>(); 
	
	private List<Province> provinceList; //省列表
	private List<City> cityList;  //市列表
	private List<County> countyList;   //县列表
	private Province selectedProvinces;  //选中的省份
	private City selectedCity;   //选中的城市
	private int currentLevel;   //选中的级别
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.choose_area);  
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
     
		fineWeatherDB = FineWeatherDB.getInstance(this);  //获取FineWeather实例
       
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvinces = provinceList.get(position);  //定位到某省份
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);           //定位到某城市
					queryCounties();
				}
			}
        });
        queryProvinces();
	}

	/**
	 * 查询所有的省，优先从数据库中查询，如果没有查询到再去服务器中查询（此举节省流量）
	 */
	private void queryProvinces() {
		provinceList = fineWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}

	/**
	 * 查询所选中的省内的所以的城市，优先从数据库中查询，如果没有再从服务器中去查询
	 */
	private void queryCities() {
		cityList = fineWeatherDB.loadCities(selectedProvinces.getId());  //通过选中的省份遍历出对应的城市
		if(cityList.size() > 0){
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvinces.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvinces.getProvinceCode(),"city");
		}
	}
	/**
	 * 查询所选中的市内的所以的县，优先从数据库中查询，如果没有再从服务器中去查询
	 */
	private void queryCounties() {
		countyList = fineWeatherDB.loadCountries(selectedCity.getId());  //通过选中的城市遍历出对应的县
		if(countyList.size() > 0){
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	
	/**
	 * 根据传入的代号和类型从服务器上查询省市县的数据(因为数据库没有信息，才调用服务器的信息的)
	 * @param object
	 * @param string
	 */
	private void queryFromServer(final String code, final String type) {
		String address = null;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(fineWeatherDB, response);
				}else if("city".equals(type)){
					result = Utility.handleCitiesResponse(fineWeatherDB, response, selectedProvinces.getId());
				}else if("county".equals(type)){
					result = Utility.handleCountiesResponse(fineWeatherDB, response, selectedCity.getId());
				}
				if(result){
					//通过runOnUiThread()方法实现回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							//网上服务器获取的信息已存入数据库，从中再次调用
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread( new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败，请重试！", 0).show();
					}
				});
			}
		});
	}

	/**
	 * 显示加载的进度对话框
	 */

	private void showProgressDialog() {
		if(progressDialog == null){
		    progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("数据加载中...");
			progressDialog.setCanceledOnTouchOutside(false);    //务必加上去，否则对话框在点击屏幕时会关闭
		}
		progressDialog.show();
	}
	/**
	 * 关闭加载的进度对话框,表示加载完毕
	 */
	private void closeProgressDialog() {

		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该是返回市列表，省列表，还是直接退出
	 */

	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
}
