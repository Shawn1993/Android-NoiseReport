package com.noiselab.noisecomplain.utility;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.noiselab.noisecomplain.application.MyApplication;

/**
 * Created by shawn on 26/3/2016.
 */
public class LocationUtil {
    public static final String COORDINATE = "bd09ll";
    private static LocationClient mLocationService;

    public LocationUtil(Context context) {
        mLocationService = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType(COORDINATE);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIsNeedLocationDescribe(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationService.setLocOption(option);

    }

    public boolean isStarted() {
        return mLocationService.isStarted();
    }

    public void start() {
        mLocationService.start();
    }

    public void stop() {
        mLocationService.stop();
    }

    public void registerLocationListener(BDLocationListener listener) {
        mLocationService.registerLocationListener(listener);


    }

    public void unRegisterLocationListener(BDLocationListener listener) {
        mLocationService.unRegisterLocationListener(listener);
    }

    public void requestLocation() {
        mLocationService.requestLocation();
    }

}
