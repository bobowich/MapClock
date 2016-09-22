package com.zy.mapclock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.zy.mapclock.R;
import com.zy.mapclock.baidu.mapapi.overlayutil.PoiOverlay;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener{
    public static final String TAG = MainActivity.class.getSimpleName();
    private String[] array = {"定位", "设置", "关于", "设置", "关于", "设置", "设置", "关于","设置", "设置","定位", "设置", "关于"};
    ListView mListView;
    MapView mMapView;
    //SearchView mSearchView;
    Button mButton;
    LocationClient mLocationClient;
    BaiduMap baiduMap;
    boolean isFirstLoc = true;
    EditText city_edit;
    AutoCompleteTextView searchText;
    ArrayList<String> searchHint = new ArrayList<String>();

    //提供搜索功能的类
    PoiSearch mPoiSearch;
    SuggestionSearch mSuggestionSearch;
    ArrayAdapter<String> searchAdapter;
    BDLocationListener mLocationListener = new MyLocationListener();
    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d(TAG, "onReceiveLocation: received");
            if (bdLocation == null) {
                Log.d(TAG, "onReceiveLocation: bdlocation == null");
                return;
            }
            // 开启定位图层


// 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
// 设置定位数据
            baiduMap.setMyLocationData(locData);
            /*
            if (isFirstLoc) {
                isFirstLoc = false;
                */
                LatLng ll = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            mLocationClient.stop();
            /*
// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_launcher);
            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
            //baiduMap.setMyLocationConfiguration(config);
            baiduMap.setMyLocationConfigeration(config);

// 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);
            Log.d(TAG, "onReceiveLocation: mLocationClient stop");
            //mMapView.requestLayout();
            //mLocationClient.stop();
            */
        }
    }

    private class MyPoiOverlay extends PoiOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {
            super.onPoiClick(i);
            //PoiInfo poi = getPoiResult().getAllPoi().get(i);
            PoiInfo info = getPoiResult().getAllPoi().get(i);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(info.uid));
            // }
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.mapview);
        baiduMap = mMapView.getMap();

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.getAllPoi() == null) {
                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    baiduMap.clear();
                    PoiOverlay overlay = new MyPoiOverlay(baiduMap);
                    baiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(poiResult);
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null && suggestionResult.getAllSuggestions() == null) {
                    return;
                }
                Log.d(TAG, "onGetSuggestionResult: suggest");
                for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                    Log.d(TAG, "onGetSuggestionResult: info.key--"+info.key);
                    searchHint.add(info.key);
                }
                //Todo:这里没有弄明白为什么searchAdapter的初始化要在这里
                searchAdapter = new ArrayAdapter<String>(MainActivity.this
                        , android.R.layout.simple_list_item_1, searchHint);
                searchText.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
            }
        });
        mListView = (ListView) findViewById(R.id.listview1);
        //mSearchView = (SearchView)findViewById(R.id.search_view1);
        mButton = (Button) findViewById(R.id.button1);
        mButton.setOnClickListener(this);

        city_edit = (EditText) findViewById(R.id.city_edit);
        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //searchType = 1;
                String citystr = city_edit.getText().toString();
                String keystr = searchText.getText().toString();
                 mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(citystr).keyword(keystr).pageNum(0));
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    return;
                }
                Log.d(TAG, "onTextChanged: suggest");
                mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().keyword(s.toString()
                ).city(city_edit.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchText.setThreshold(1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, array);
        mListView.setAdapter(adapter);


        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);
        initLocation();
        baiduMap.setMyLocationEnabled(true);
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        /*
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        */
        mLocationClient.setLocOption(option);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.unRegisterLocationListener(mLocationListener);
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 重写onBackPressed()方法，弹出退出提示框
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG, "onBackPressed: access");
        AlertDialog dialog = createDialog();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * @return 创建退出dialog
     */
    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle("确定要退出吗？")
                .setMessage("点击确定退出当前应用")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: here");
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                Log.d(TAG, "onClick: start");
                mLocationClient.start();
        }
    }
}
