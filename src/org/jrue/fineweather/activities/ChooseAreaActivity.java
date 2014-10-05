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
	
	private List<Province> provinceList; //ʡ�б�
	private List<City> cityList;  //���б�
	private List<County> countyList;   //���б�
	private Province selectedProvinces;  //ѡ�е�ʡ��
	private City selectedCity;   //ѡ�еĳ���
	private int currentLevel;   //ѡ�еļ���
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.choose_area);  
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
     
		fineWeatherDB = FineWeatherDB.getInstance(this);  //��ȡFineWeatherʵ��
       
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvinces = provinceList.get(position);  //��λ��ĳʡ��
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);           //��λ��ĳ����
					queryCounties();
				}
			}
        });
        queryProvinces();
	}

	/**
	 * ��ѯ���е�ʡ�����ȴ����ݿ��в�ѯ�����û�в�ѯ����ȥ�������в�ѯ���˾ٽ�ʡ������
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
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}

	/**
	 * ��ѯ��ѡ�е�ʡ�ڵ����Եĳ��У����ȴ����ݿ��в�ѯ�����û���ٴӷ�������ȥ��ѯ
	 */
	private void queryCities() {
		cityList = fineWeatherDB.loadCities(selectedProvinces.getId());  //ͨ��ѡ�е�ʡ�ݱ�������Ӧ�ĳ���
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
	 * ��ѯ��ѡ�е����ڵ����Ե��أ����ȴ����ݿ��в�ѯ�����û���ٴӷ�������ȥ��ѯ
	 */
	private void queryCounties() {
		countyList = fineWeatherDB.loadCountries(selectedCity.getId());  //ͨ��ѡ�еĳ��б�������Ӧ����
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ���ص�����(��Ϊ���ݿ�û����Ϣ���ŵ��÷���������Ϣ��)
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
					//ͨ��runOnUiThread()����ʵ�ֻص����̴߳����߼�
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							//���Ϸ�������ȡ����Ϣ�Ѵ������ݿ⣬�����ٴε���
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ�ܣ������ԣ�", 0).show();
					}
				});
			}
		});
	}

	/**
	 * ��ʾ���صĽ��ȶԻ���
	 */

	private void showProgressDialog() {
		if(progressDialog == null){
		    progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ݼ�����...");
			progressDialog.setCanceledOnTouchOutside(false);    //��ؼ���ȥ������Ի����ڵ����Ļʱ��ر�
		}
		progressDialog.show();
	}
	/**
	 * �رռ��صĽ��ȶԻ���,��ʾ�������
	 */
	private void closeProgressDialog() {

		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	/**
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ���Ƿ������б�ʡ�б�����ֱ���˳�
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
