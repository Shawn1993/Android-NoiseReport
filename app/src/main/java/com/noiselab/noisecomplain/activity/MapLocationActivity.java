package com.noiselab.noisecomplain.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.BoringLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
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
import com.noiselab.noisecomplain.fragment.MapLocationActivityFragment;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import java.lang.annotation.ElementType;
import java.util.ArrayList;

public class MapLocationActivity extends AppCompatActivity {

    double longitude;
    double latitude;
    String city;

    private void initData() {
        latitude = getIntent().getDoubleExtra("latitude", Double.NaN);
        longitude = getIntent().getDoubleExtra("longitude", Double.NaN);
        String c = getIntent().getStringExtra("city");
        if (c != null && "".equals(c)) {
            city = c;
        } else {
            city = "广州";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_map_location);

        initData();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, MapLocationActivityFragment.newInstance(latitude, longitude, city))
                .commit();
    }
}
