package com.rainm.gaodedemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapActivity extends AppCompatActivity implements LocationSource, AMapLocationListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener {

    private MapView mMapView;
    private AMap mAMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private boolean isFirstLoc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.map_view);
        assert mMapView != null;
        mMapView.onCreate(savedInstanceState);
        initAMap();
        initLocation();
        initMyLocationPoint();

    }

    private boolean isInstallPackage() {
        if (new File("/data/data/" + "com.autonavi.minimap").exists()) {
            return true;
        } else {
            Snackbar.make(mMapView, "您未安装高德地图App", Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }

    private void openGaoDeMap(double lon, double lat) {
        try {
            StringBuilder loc = new StringBuilder();
            loc.append("androidamap://navi?sourceApplication=CheSir");
//            loc.append("&poiname=");
//            loc.append("电子科大");
            loc.append("&lat=");
            loc.append(lat);
            loc.append("&lon=");
            loc.append(lon);
            loc.append("&dev=0");
            Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(loc.toString()));
            intent.setPackage("com.autonavi.minimap");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMyLocationPoint() {

        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //自定义定位蓝点图标，不设置显示系统小蓝点
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
//                fromResource(R.drawable.i5));
        //自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.BLACK);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth((float) 0.1);
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
    }

    private void initLocation() {
        if (mLocationClient == null) {
            //声明mLocationOption对象
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //返回经纬度的同时会返回地址描述。注意：模式为仅设备模式(Device_Sensors)时无效
            mLocationOption.setNeedAddress(true);
            //
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔时间为1000ms
            mLocationOption.setInterval(1000);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        }
    }

    private void initAMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.setOnMarkerClickListener(this);//设置Marker点击事件
        mAMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        //mAMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式

        //开启右上角定位按钮
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        //设置可以点击
        mAMap.setMyLocationEnabled(true);

        //设置为定位模式，还有旋转等
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        //开启指南针
        mAMap.getUiSettings().setCompassEnabled(true);
        //开启比例尺
        mAMap.getUiSettings().setScaleControlsEnabled(true);
        float scale = mAMap.getScalePerPixel();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {

            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
                Log.d("DemoLocation", "Location Success");
                Log.d("DemoLocation", aMapLocation.getAddress());

                if (isFirstLoc) {
                    //设置缩放级别
                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                    //将地图移动到定位点
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));

                    mListener.onLocationChanged(aMapLocation);
                    //增加自己设置的定位点
//                    mAMap.addMarker(getMarkerOptions(aMapLocation, 0.0003, R.drawable.i3));
                    isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
//        if (isInstallPackage("GaoDeMap")) {
//            if (aMapLocation != null) {
//                openGaoDeMap(103.924, 30.742, "电子科大", "这是一个描述");
//            }
//        }
    }

    public void render(Marker marker, View view) {

    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation, double d, int a) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(a));
        //允许用户可以自由移动标记
        options.draggable(true);
        //位置
        options.position(new LatLng(amapLocation.getLatitude() + d, amapLocation.getLongitude() + d));
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        //标题
        options.title(buffer.toString());
        //子标题
        options.snippet("\n这里来加货小伙子");
        //设置多少帧刷新一次图片资源
        options.period(60);

        return options;

    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;

    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    //点击标记事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("DemoLocation", "被点击了点击");
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "信息窗口被点击了", Toast.LENGTH_SHORT).show();
    }
}
