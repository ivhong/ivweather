package com.ivhong.ivweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends CheckPermission {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    public MapView mapView = null;
    protected String[] needPermissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    AMap aMap;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                    int ltype = amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    String ltypes;
                    StringBuilder stringBuilder = new StringBuilder();
                    switch (ltype){
                        case 0:
                            ltypes = "定位失败";
                            break;
                        case 1:
                            ltypes = "GPS定位结果";
                            break;
                        case 2:
                            ltypes = "前次定位结果";
                            break;
                        case 3:
                            ltypes = "未知3";
                            break;
                        case 4:
                            ltypes = "缓存定位结果";
                            break;
                        case 5:
                            ltypes = "Wifi定位结果";
                            break;
                        case 6:
                            ltypes = "基站定位结果";
                            break;
                        case 7:
                            ltypes = "未知7";
                            break;
                        case 8:
                            ltypes = "离线定位结果";
                            break;
                        default:
                            ltypes = "未知";
                            break;
                    }
                    stringBuilder.append("定位类型:" + ltypes + "\n");
                    stringBuilder.append("纬度:" + amapLocation.getLatitude() + "\n");//获取纬度
                    stringBuilder.append("经度:" + amapLocation.getLongitude() + "\n");//获取经度
                    stringBuilder.append("精度信息:" + amapLocation.getAccuracy() + "\n");//获取精度信息
                    stringBuilder.append("地址:" + amapLocation.getAddress() + "\n");//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    stringBuilder.append("国家信息:" + amapLocation.getCountry() + "\n");//国家信息
                    stringBuilder.append("省信息:" + amapLocation.getProvince() + "\n");//省信息
                    stringBuilder.append("城市信息:" + amapLocation.getCity() + "\n");//城市信息
                    stringBuilder.append("城区信息:" + amapLocation.getDistrict() + "\n");//城区信息
                    stringBuilder.append("街道信息:" + amapLocation.getStreet() + "\n");//街道信息
                    stringBuilder.append("街道信息:" + amapLocation.getStreetNum() + "\n");//街道门牌号信息
                    stringBuilder.append("街道门牌号信息:" + amapLocation.getCityCode() + "\n");//城市编码
                    stringBuilder.append("地区编码:" + amapLocation.getAdCode() + "\n");//地区编码
                    stringBuilder.append("当前定位点的AOI信息:" + amapLocation.getAoiName() + "\n");//获取当前定位点的AOI信息
                    stringBuilder.append("室内定位的建筑物Id:" + amapLocation.getBuildingId() + "\n");//获取当前室内定位的建筑物Id
                    stringBuilder.append("室内定位的楼层:" + amapLocation.getFloor() + "\n");//获取当前室内定位的楼层
//                    amapLocation.getGpsStatus();//获取GPS的当前状态
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    stringBuilder.append("定位时间:" + df.format(date) + "\n");


                    TextView textView = (TextView) findViewById(R.id.location);
                    textView.setText(stringBuilder.toString());

                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.d("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( !_checkpermission() ){
            return ;
        }
//获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
        }

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);

        initLBS();

    }

    private void refreshMap(){

    }

    private void initLBS() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
//设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stopLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.startLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();

        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void requestPermissionsCallback() {
        initLBS();
    }
}
