package com.noiselab.noisecomplain.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.noiselab.noisecomplain.R;
import com.noiselab.noisecomplain.model.ComplainForm;
import com.noiselab.noisecomplain.utility.ComplainFormManager;
import com.noiselab.noisecomplain.utility.LocationUtil;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

public class MapLocationActivityFragment extends Fragment implements BDLocationListener {

    // the fragment initialization parameters
    private static final String ARG_CENTER_LAT = "center_lat";
    private static final String ARG_CENTER_LNG = "center_lng";
    private static final String ARG_CITY = "city";

    // the arguments
    private double mLatitude;
    private double mLongitude;
    private String mAddress;
    private float mRadius;
    private String mCity;

    // the widgets
    private MapView mMapView;

    private FloatingActionButton mMyLocationButton;
    private MaterialAutoCompleteTextView mSearchTextFeild;

    private ImageButton mBackButton;
    private Button mSureButton;
    private ArrayAdapter<String> mSearchAdapter;

    private boolean mIsSearchResult = false;
    // the services
    SuggestionSearch mSuggestionSearch;
    PoiSearch mPoiSearch;
    LocationUtil mLocationUtil;
    GeoCoder mGeoCoder;
    // results
    PoiResult poiResult;
    SuggestionResult mSuggestionResult;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lat the initial center latitude of map
     * @param lng the initial center longtitude of map
     * @return A new instance of fragment MainActivityComplainListFragment.
     */
    public static MapLocationActivityFragment newInstance(double lat, double lng, String city) {
        MapLocationActivityFragment fragment = new MapLocationActivityFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_CENTER_LAT, lat);
        args.putDouble(ARG_CENTER_LNG, lng);
        args.putString(ARG_CITY, city);
        fragment.setArguments(args);
        return fragment;

    }

    private void initSuggestionSearch() {
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult result) {
                if (result == null || result.getAllSuggestions() == null) {
                    return;
                }
                mSuggestionResult = result;
                mSearchAdapter.clear();
                for (SuggestionResult.SuggestionInfo info : result.getAllSuggestions()) {
                    mSearchAdapter.add(info.key);
                }
            }
        });
    }

    private void initPoiSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result == null || result.getAllPoi() == null) {
                    return;
                }
                poiResult = result;
                mSearchAdapter.clear();
                for (PoiInfo info : result.getAllPoi()) {
                    mSearchAdapter.add(info.address);
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        });
    }

    private void initGeoCoder() {
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                double lat = mMapView.getMap().getMapStatus().target.latitude;
                double lng = mMapView.getMap().getMapStatus().target.longitude;
                String address = result.getAddress();

                Intent data = new Intent();
                data.putExtra("latitude", lat);
                data.putExtra("longitude", lng);
                data.putExtra("address", address);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });
    }


    private void initLocationUtil() {
        mLocationUtil = new LocationUtil(getActivity());
        mLocationUtil.registerLocationListener(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPoiSearch();
        initSuggestionSearch();
        initGeoCoder();
        initLocationUtil();

        if (getArguments() != null) {
            mLatitude = getArguments().getDouble(ARG_CENTER_LAT);
            mLongitude = getArguments().getDouble(ARG_CENTER_LNG);
            mCity = getArguments().getString(ARG_CITY);
        } else {
            mLatitude = Double.NaN;
            mLatitude = Double.NaN;
            mLocationUtil.start();
        }
    }


    private void initSearchTextFeild(View view) {
        mSearchTextFeild = (MaterialAutoCompleteTextView) view.findViewById(R.id.search_text_field);
        mSearchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, new String[]{"null"});
        mSearchTextFeild.setAdapter(mSearchAdapter);
        mSearchTextFeild.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    Log.v("投诉", "城市" + mCity + "|关键字" + s.toString());
                    mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
                            .city(mCity)
                            .location(new LatLng(mLatitude, mLongitude))
                            .keyword(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchTextFeild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mIsSearchResult) {
                    LatLng latLng = poiResult.getAllPoi().get(position).location;
                    setMapCenter(latLng.latitude, latLng.longitude);
                    mIsSearchResult = false;
                } else {
                    String keyword = mSuggestionResult.getAllSuggestions().get(position).key;
                    mPoiSearch.searchInCity(new PoiCitySearchOption()
                            .city(mCity)
                            .keyword(keyword)
                            .pageNum(10));
                    mIsSearchResult = true;
                }
                mSearchAdapter.clear();
            }
        });
    }

    private void initMapView(View view) {
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        setMapCenter(mLatitude, mLongitude);

        mMyLocationButton = (FloatingActionButton) view.findViewById(R.id.complain_fab);
        mMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationUtil.isStarted()) {
                    mLocationUtil.requestLocation();
                } else {
                    mLocationUtil.start();
                }
            }
        });
    }

    private void initBackButton(View view) {
        mBackButton = (ImageButton) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });
    }

    private void initSureButton(View view) {
        mSureButton = (Button) view.findViewById(R.id.sure_button);
        mSureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mMapView.getMap().getMapStatus().target));
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_location, container, false);
        initMapView(view);
        initBackButton(view);
        initSearchTextFeild(view);
        initSureButton(view);
        return view;
    }

    private void setMapCenter(double lat, double lng) {
        if (Double.isNaN(lat) || Double.isNaN(lng))
            return;
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(lat, lng));
        mMapView.getMap().animateMapStatus(mapStatusUpdate);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        mLocationUtil.unRegisterLocationListener(this);
        if (mLocationUtil.isStarted()) {
            mLocationUtil.stop();
        }
        mLocationUtil = null;
        super.onDestroy();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {

        if (location.getLocType() == BDLocation.TypeNone) {

        } else if (location.getLocType() == BDLocation.TypeOffLineLocationFail) {

        } else if (location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail) {

        } else if (location.getLocType() == BDLocation.TypeServerError) {//服务器网络定位失败
            Log.i("Location", "serverError");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {//网络不同导致定位失败
            Log.i("Location", "networkError");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {//无法获取失败原因，一般可能是飞行模式
            Log.i("Location", "Criteria");
        } else {
            // 成功定位
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mRadius = location.getRadius();
            mAddress = location.getAddrStr();
            mCity = location.getCity();
            setMapCenter(mLatitude, mLongitude);

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            } else if (location.getLocType() == BDLocation.TypeCacheLocation) {// 缓存定位结果
            }
        }
    }
}
