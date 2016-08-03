package com.zhao.sender.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.zhao.sender.httpconnection.*;

import com.zhao.sender.globalvariable.Globalvariable;
import com.zhao.sender.globalvariable.Utility;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

/**
 * Created by zhao on 2016/5/11.
 */
public class LocationPresenter extends BasePresenter{


    public Location getCurrentLocation(Context context){
        Location location = null;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider = null;
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        }else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
            Toast.makeText(context, "No location provider to use",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(provider,0,0,new LocationListener(){

                @Override
                public void onLocationChanged(final Location location){

                }

                @Override
                public void onProviderDisabled(String provider)  {

                }

                @Override
                public void onProviderEnabled(String provider)  {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

            });
            while(location == null) location = locationManager.getLastKnownLocation(provider);
        }catch (SecurityException e) {
            Log.d("Location",e.toString());
            Toast.makeText(context, "请开启定位权限",
                    Toast.LENGTH_SHORT).show();
        }

        return location;
    }









}
