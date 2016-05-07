package com.noiselab.noisecomplain.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noiselab.noisecomplain.R;
import com.noiselab.noisecomplain.utility.NoiseHelper;
import com.noiselab.noisecomplain.widget.NoiseGraphView;
import com.noiselab.noisecomplain.widget.NoiseMeterView;

public class MainActivityNoiseMeterFragment extends Fragment {

    private NoiseHelper mNoiseHelper;

    private NoiseMeterView mNoiseMeterView;
    private NoiseGraphView mNoiseGraphView;

    public MainActivityNoiseMeterFragment() {
        mNoiseHelper = new NoiseHelper(new NoiseHelper.Callback() {
            @Override
            public void onUpdate(double volume, double avgVolume) {
                mNoiseMeterView.setValue(volume);
                mNoiseGraphView.addValue(volume);
            }

            @Override
            public void onStop(double volume, double avgVolume) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_fragment_noise_meter);
        }

        View view = inflater.inflate(R.layout.fragment_noise_meter, container, false);
        mNoiseMeterView = (NoiseMeterView) view.findViewById(R.id.noise_meter_view);
        mNoiseGraphView = (NoiseGraphView) view.findViewById(R.id.noise_graph_view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNoiseHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mNoiseHelper.stop();
    }
}
