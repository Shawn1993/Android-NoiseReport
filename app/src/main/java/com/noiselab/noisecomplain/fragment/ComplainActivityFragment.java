package com.noiselab.noisecomplain.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import com.noiselab.noisecomplain.R;
import com.noiselab.noisecomplain.activity.MapLocationActivity;
import com.noiselab.noisecomplain.model.ComplainForm;
import com.noiselab.noisecomplain.model.ComplainFormDao;
import com.noiselab.noisecomplain.model.ComplainResponse;
import com.noiselab.noisecomplain.utility.AppConfig;
import com.noiselab.noisecomplain.utility.ComplainFormManager;
import com.noiselab.noisecomplain.utility.HttpUtil;
import com.noiselab.noisecomplain.utility.LocationUtil;
import com.noiselab.noisecomplain.utility.NoiseHelper;
import com.noiselab.noisecomplain.widget.ComplainCardView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class ComplainActivityFragment extends Fragment implements BDLocationListener, View.OnClickListener {

    boolean isOK_Address = false;
    boolean isOK_NoiseZone = false;
    boolean isOK_NoiseType = false;
    boolean isOK_NoisePower = false;
    private ComplainForm mComplainRequestForm;

    private FloatingActionButton mComplainRequestButton;

    private ComplainCardView mMapItem;
    private ComplainCardView mNoiseMeterItem;
    private ComplainCardView mNoiseTypeItem;
    private ComplainCardView mNoiseZoneItem;
    private ComplainCardView mNoiseCommentItem;

    // the services
    private LocationUtil mLocationUtil;
    private NoiseHelper mNoiseHelper;

    private String mCity;

    private Handler mDialogHandler = new Handler();

    private void initNoiseHelper() {
        mNoiseHelper = new NoiseHelper(new NoiseHelper.Callback() {
            ArrayList<Double> volumes = new ArrayList<>();

            @Override
            public void onUpdate(double volume, double avgVolume) {
                volumes.add(volume);
            }

            @Override
            public void onStop(double volume, double avgVolume) {
                volumes.add(volume);

                isOK_NoisePower = true;
                mNoiseMeterItem.activate();
                mNoiseMeterItem.setSubTitleText(((int) avgVolume) + " 分贝", 0);
                mNoiseMeterItem.showSubTitle(0);
                checkComplainButtonEnable();

                mComplainRequestForm.averageIntensity = avgVolume;
                mComplainRequestForm.intensities = new double[volumes.size()];
                for (int i = 0; i < volumes.size(); i++) {
                    mComplainRequestForm.intensities[i] = volumes.get(i);
                }
                volumes.clear();
            }
        });

        NoiseHelper.Callback callback =  new NoiseHelper.Callback() {

            @Override
            public void onUpdate(double volume, double avgVolume){

            }

            @Override
            public void onStop(double volume, double avgVolume) {

            }
        };
        mNoiseHelper = new NoiseHelper(callback);
        mNoiseHelper.setmDuration(5000);
        mNoiseHelper.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationUtil = new LocationUtil(getActivity());
        mLocationUtil.registerLocationListener(this);
        ComplainFormManager.clear();
        mComplainRequestForm = ComplainFormManager.getRequestForm();
        mComplainRequestForm.coord = LocationUtil.COORDINATE;

        initNoiseHelper();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complain, container, false);
        mComplainRequestButton = (FloatingActionButton) view.findViewById(R.id.complain_fab);
        mComplainRequestButton.setOnClickListener(this);
        mMapItem = (ComplainCardView) view.findViewById(R.id.map_item);
        mMapItem.setOnClickListener(this);
        mNoiseMeterItem = (ComplainCardView) view.findViewById(R.id.noise_meter_item);
        mNoiseMeterItem.setOnClickListener(this);
        mNoiseMeterItem.setSubTitleText("正在测量", 0);
        mNoiseMeterItem.showSubTitle(0);
        mNoiseTypeItem = (ComplainCardView) view.findViewById(R.id.noise_type_item);
        mNoiseTypeItem.setOnClickListener(this);
        mNoiseZoneItem = (ComplainCardView) view.findViewById(R.id.noise_zone_item);
        mNoiseZoneItem.setOnClickListener(this);
        mNoiseCommentItem = (ComplainCardView) view.findViewById(R.id.noise_comment_item);

        return view;
    }

    @Override
    public void onResume() {
        checkComplainButtonEnable();
        if (!isOK_Address) {
            mLocationUtil.start();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mLocationUtil.isStarted()) {
            mLocationUtil.stop();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mLocationUtil.unRegisterLocationListener(this);
        if (mLocationUtil.isStarted()) {
            mLocationUtil.stop();
        }
        mLocationUtil = null;
        mNoiseHelper.stop();
        mNoiseHelper = null;
        ComplainFormManager.clear();
        super.onDestroy();
    }

    // 百度定位接口
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
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            float radius = location.getRadius();
            double altitude = location.getAltitude();
            String address = location.getAddrStr();
            mCity = location.getCity();
            if (address != null && !"".equals(address)) {
                mComplainRequestForm.autoLatitude = lat;
                mComplainRequestForm.autoLongitude = lng;
                mComplainRequestForm.autoAddress = (address);
                mComplainRequestForm.autoHorizontalAccuracy = radius;
                mComplainRequestForm.autoAltitude = altitude;
                mMapItem.setSubTitleText(address, 0);
                mMapItem.showSubTitle(0);
                isOK_Address = true;
                mMapItem.activate();
                checkComplainButtonEnable();
            }

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                Log.i("Location", "gps" + ",lat=" + lat + ",lng=" + lng + ",address" + address + ",radius" + radius);
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                Log.i("Location", "net" + ",lat=" + lat + ",lng=" + lng + ",address" + address + ",radius" + radius);
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                Log.i("Location", "offline" + ",lat=" + lat + ",lng=" + lng + ",address" + address + ",radius" + radius);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complain_fab:
                pushComplainForm();
                break;
            case R.id.map_item:
                openMapLocationActivity();
                break;
            case R.id.noise_meter_item:
                // TODO: 26/3/2016
                break;
            case R.id.noise_type_item:
                openNoiseTypeView();
                break;
            case R.id.noise_zone_item:
                openNoiseZoneView();
                break;
        }
    }

    private void checkComplainButtonEnable() {
        if (isOK_Address && isOK_NoiseType && isOK_NoisePower && isOK_NoiseZone) {
            mComplainRequestButton.setEnabled(true);
        } else {
            mComplainRequestButton.setEnabled(false);
        }
    }

    // Push the form
    private void pushComplainForm() {
        // Show a snackbar
        final Snackbar snackbar = Snackbar.make(getView(), "正在为您投递表单", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        // Get the rest of form
        mComplainRequestForm.noiseType = (String) mNoiseTypeItem.getSubTitleText(0);
        mComplainRequestForm.sfaType = (String) mNoiseZoneItem.getSubTitleText(0);
        mComplainRequestForm.devId = AppConfig.getMacAddress(getActivity());
        mComplainRequestForm.date = DateFormat.format(AppConfig.DATE_FORMAT, System.currentTimeMillis()).toString();
        mComplainRequestForm.comment = ((EditText) (mNoiseCommentItem.getFootView())).getText().toString();
        // Push the form now.
        String json = new Gson().toJson(mComplainRequestForm);
        HttpUtil.postAsyncForm(AppConfig.REQUEST_URL, mComplainRequestForm, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mDialogHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                snackbar.dismiss();
                                new AlertDialog.Builder(getActivity()).setTitle("错误").setIcon(R.drawable.ic_error_black_36dp).setMessage("对不起，网络出现问题").setPositiveButton("确定", null).show();
                            }
                        });
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        mDialogHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                snackbar.dismiss();
                            }
                        });
                        // Judge the state code
                        if (!response.isSuccessful()) {
                            mDialogHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(getActivity()).setTitle("错误").setMessage("对不起，服务器发生错误").setIcon(R.drawable.ic_error_black_36dp).setPositiveButton("确定", null).show();
                                }
                            });
                            return;
                        }
                        // Judge the content type
                        String contentType = response.header("Content-Type");
                        if (!contentType.contains("json")) {
                            mDialogHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(getActivity()).setTitle("错误").setIcon(R.drawable.ic_error_black_36dp).setMessage("对不起，返回内容出错").setPositiveButton("确定", null).show();
                                }
                            });
                            return;
                        }
                        // Judge the response body content
                        Gson gson = new Gson();
                        final ComplainResponse complainResponse = gson.fromJson(response.body().string(), ComplainResponse.class);
                        if (complainResponse.formId == null) {
                            mDialogHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(getActivity()).setTitle("错误").setIcon(R.drawable.ic_error_black_36dp).setMessage("对不起，返回内容出错").setPositiveButton("确定", null).show();
                                }
                            });
                            return;
                        }
                        mDialogHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "已经成功为您提交表单", Toast.LENGTH_LONG).show();
                                // OK,it is safe and do what you like.
                                // Write the form into database
                                mComplainRequestForm.formId = complainResponse.formId;
                                ComplainFormDao dao = new ComplainFormDao(getActivity());
                                dao.insert(mComplainRequestForm);
                                // Pass the id to parent activity
                                Intent data = new Intent();
                                data.putExtra("formId", mComplainRequestForm.formId);
                                getActivity().setResult(Activity.RESULT_OK, data);
                                getActivity().finish();
                            }
                        });

                    }
                }
        );
    }

    // 打开地图界面
    private void openMapLocationActivity() {
        Intent intent = new Intent(getActivity(), MapLocationActivity.class);
        intent.putExtra("latitude", mComplainRequestForm.autoLatitude);
        intent.putExtra("longitude", mComplainRequestForm.autoLongitude);
        intent.putExtra("city", mCity);
        startActivityForResult(intent, 1);
    }

    // 打开声功能区的选择界面
    private void openNoiseZoneView() {
        final CharSequence[] texts = getResources().getTextArray(R.array.noise_zone_lists);
        Integer i = (Integer) mNoiseZoneItem.getTag();
        i = (i == null) ? -1 : i;
        new AlertDialog.Builder(getActivity())
                .setTitle("请选择所属区域")
                .setSingleChoiceItems(texts, i, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isOK_NoiseZone = true;
                        mNoiseZoneItem.activate();
                        mNoiseZoneItem.setSubTitleText(texts[which], 0);
                        mNoiseZoneItem.showSubTitle(0);
                        mNoiseZoneItem.setTag(which);
                        checkComplainButtonEnable();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("取消", null)
                .show();
    }

    // 打开噪声类型选择界面
    private void openNoiseTypeView() {
        final CharSequence[] texts = getResources().getTextArray(R.array.noise_type_lists);
        Integer i = (Integer) mNoiseTypeItem.getTag();
        i = (i == null) ? -1 : i;
        new AlertDialog.Builder(getActivity())
                .setTitle("请选择噪声类型")
                .setSingleChoiceItems(texts, i, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isOK_NoiseType = true;
                        mNoiseTypeItem.activate();
                        mNoiseTypeItem.setSubTitleText(texts[which], 0);
                        mNoiseTypeItem.showSubTitle(0);
                        mNoiseTypeItem.setTag(which);
                        checkComplainButtonEnable();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("取消", null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            mComplainRequestForm.manualAddress = data.getStringExtra("address");
            mComplainRequestForm.manualLatitude = data.getDoubleExtra("latitude", Double.NaN);
            mComplainRequestForm.manualLongitude = data.getDoubleExtra("longitude", Double.NaN);

            isOK_Address = true;
            mMapItem.activate();
            mMapItem.setSubTitleText(mComplainRequestForm.manualAddress, 1);
            mMapItem.showSubTitle(1);
            checkComplainButtonEnable();
        }
    }

}
