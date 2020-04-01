package com.zxing.interfacer;

/**
 * Title:
 * description:
 * autor:pei
 * created on 2020/3/27
 */
public interface OnScanResultListener {

    void scanSuccess(String result,int width,int height);//扫描成功
    void scanFailed(String result,int width,int height);//扫描失败
}
