package com.example.weather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.ListViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.example.weather.db.City;
import com.example.weather.db.County;
import com.example.weather.db.Province;
import com.example.weather.gson.Weather;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.LogUtil;
import com.example.weather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的市
     */
    private City selectedCity;
    /**
     * 选中项目的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_content);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        LogUtil.logInfo("onCreateView is Running");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.logInfo("onActivityCreated is Running");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String weather_id = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getContext(),WeatherActivity.class);
                    intent.putExtra("weather_id",weather_id);
                    startActivity(intent);
                    getActivity().finish();
                    LogUtil.logError("城市选择结束，根据intent开启新的Activity，传入weather_id");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (currentLevel ==LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });

        queryProvinces();
        LogUtil.logInfo("第一次查询省级数据");
    }

    /**
     * 查询全国省份，优先数据库，然后服务器
     */
    private void queryProvinces(){
        LogUtil.logInfo("查询省份信息");
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvincename());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel =LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china/";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询省内市，优先数据库，然后服务器
     */
    private void queryCities(){
        LogUtil.logInfo("查询城市信息");
        titleText.setText(selectedProvince.getProvincename());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?",String.valueOf(selectedProvince.getId())).find(City.class);
//        cityList = DataSupport.where("provinceId = 9").find(City.class);

        LogUtil.logDebug(String.valueOf(selectedProvince.getId()));
//        LogUtil.logDebug(String.valueOf(cityList.size()));
//        LogUtil.logDebug(this.cityList.toString());

        if (cityList.size() > 0){
            dataList.clear();
            for(City city: this.cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address;
            if (provinceCode <= 6){
                address = "http://guolin.tech/api/china/"+provinceCode+"/"+provinceCode;
            }
            address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询市内县，优先数据库，然后服务器
     */
    private void queryCounties(){
        LogUtil.logInfo("查询县级信息");
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityId = ?",String.valueOf(selectedCity.getId())).find(County.class);
        LogUtil.logDebug("CityId is :"+selectedCity.getId());
        LogUtil.logDebug("countyList Size:"+countyList.size());
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
//            LogUtil.logDebug(""+);
            queryFromServer(address,"county");
        }
    }

    /**
     * 根据地址和类型在服务器查询数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type){
        LogUtil.logInfo("queryFromServer is Running");
        LogUtil.logInfo("address ："+address);

        showProgressDialog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HttpUtil.sendOkHttpRequest(address,new Callback(){

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LogUtil.logInfo("开始发送网络请求");
                    String responseText = response.body().string();
                    boolean result = false;
                    if ("province".equals(type)){
                        result = Utility.handleProvinceReponse(responseText);
                    }else if ("city".equals(type)){
                        result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                    }else if ("county".equals(type)){
                        result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                    }
                    if (result){
                        getActivity().runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                if ("province".equals(type)){
                                    queryProvinces();
                                }else if ("city".equals(type)){
                                    queryCities();
                                }else if ("county".equals(type)){
                                    queryCounties();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }


    /**
     * 开启进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
