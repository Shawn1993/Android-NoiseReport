package com.noiselab.noisecomplain.model;

import java.io.Serializable;

/**
 * Created by shawn on 23/3/2016.
 */
public class ComplainForm {
    // 表单ID
    public String formId;
    // 用户ID
    public String devId;
    // 用户投诉使用的系统 1:android 2:ios 3:browser 4:wechat
    public String devType = "android";
    // 时间
    public String date;
    // 噪声值
    public double averageIntensity;
    // 噪声数组
    public double[] intensities;
    // 坐标系 bd09ll/gcj02/bd09
    public String coord;
    // 定位经度
    public double autoLatitude;
    // 定位纬度
    public double autoLongitude;
    // 定位高度
    public double autoAltitude;
    // 定位水平精度
    public double autoHorizontalAccuracy;
    // 定位垂直精度
    public double autoVerticalAccuracy;
    // 定位地址
    public String autoAddress;
    // 人工经度
    public double manualLatitude;
    // 人工纬度
    public double manualLongitude;
    // 人工地址
    public String manualAddress;
    // 声功能区
    public String sfaType;
    // 噪声类型
    public String noiseType;
    // 用户描述信息
    public String comment;
}
