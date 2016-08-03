package com.zhao.sender.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.baidu.mapapi.SDKInitializer;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.google.gson.Gson;
import com.zhao.sender.R;
import com.zhao.sender.globalvariable.overlayutil.DrivingRouteOverlay;
import com.zhao.sender.httpconnection.HttpCallbackListener;
import com.zhao.sender.model.DeliveryAddress;
import com.zhao.sender.presenter.LocationPresenter;
import com.zhao.sender.presenter.MapPresenter;

import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MapActivity extends BaseActivity{
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private RoutePlanSearch mRoutePlanSearch;
    private String addressLocation;
    private double lat;
    private double lng;
    private   LatLng  point1;
    private  LatLng  point2;
    private OverlayOptions option1;
    private OverlayOptions option2;
    private int searchTime;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;
                case -1:

                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        addressLocation = getIntent().getStringExtra("addressLocation");
        lng = Double.parseDouble(addressLocation.split(",")[0]);
        lat = Double.parseDouble(addressLocation.split(",")[1]);
        searchTime = 0;
        initMap();
    }

    private void initMap(){
        mBaiduMap = mMapView.getMap();

//普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);

////卫星地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
//
////空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);

        point1 = new LatLng(lat,lng);
        Location location = new LocationPresenter().getCurrentLocation(MapActivity.this);
        point2 = new LatLng(location.getLatitude(),location.getLongitude());
        //LatLng point2 = new LatLng(25.286926,110.343267);
//构建Marker图标
       BitmapDescriptor bitmap1 = BitmapDescriptorFactory.fromResource(R.drawable.t5);
       BitmapDescriptor bitmap2 = BitmapDescriptorFactory.fromResource(R.drawable.t2);

//构建MarkerOption，用于在地图上添加Marker
       option1 = new MarkerOptions().position(point1).icon(bitmap1);
       option2 = new MarkerOptions().position(point2).icon(bitmap2);
//在地图上添加Marker，并显示

//定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(point2).zoom(16).build();
//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//改变地图状态
        PlanNode n1 = PlanNode.withLocation(point1);
        PlanNode n2 = PlanNode.withLocation(point2);

        mBaiduMap.setMapStatus(mMapStatusUpdate);
       //mBaiduMap.addOverlay(option1);
       // mBaiduMap.addOverlay(option2);

        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                // 获取步行线路规划结果
                if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    if(searchTime == 3){
                        Toast.makeText(MapActivity.this, "抱歉，寻路失败",Toast.LENGTH_SHORT).show();
                        mBaiduMap.addOverlay(option1);
                        mBaiduMap.addOverlay(option2);
                    }
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    if(searchTime == 3){
                    Toast.makeText(MapActivity.this, "起终点或途经点地址有岐义",
                            Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
//                    // route = result.getRouteLines().get(0);
                    searchTime = 3;
                    DrivingRouteOverlay overlay = new  DrivingRouteOverlay (mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(drivingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();

                }

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
        findRoute();

    }

    private void findRoute(){
         new Thread(new Runnable() {
            @Override
            public void run() {
                while(searchTime < 3) {
                    searchTime++;
                    try {
                        mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(PlanNode.withLocation(point2)).to(PlanNode.withLocation(point1)));
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mRoutePlanSearch.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


}
