package com.scanpro;

import android.widget.Toast;
import com.zxing.activity.CaptureActivity;
import com.zxing.utils.ScanUtil;

/**
 * Title: 扫描界面,对ScanActivity界面进行补参修改
 * description:
 * autor:pei
 * created on 2020/3/26
 */
public class ScanActivity extends CaptureActivity {

    @Override
    protected boolean scanFinish() {
        return false;
    }

    @Override
    protected void initData() {
        super.initData();

//        //设置相册按钮不显示
//        setImvAlbumVisibility(false);
    }

    @Override
    protected void noAlbumPermission() {
        ScanUtil.i("=====无权限=====");
    }

    @Override
    protected void scanSuccess(int requestCode,String result,int width,int height) {
        ScanUtil.i("=====扫描成功哈哈哈====result="+result);
        ScanUtil.i("=====扫描成功哈哈哈====result.length()="+result.length());

        Toast.makeText(this,"扫描界面成功的结果: "+result,Toast.LENGTH_SHORT).show();

       //finish();
    }

    @Override
    protected void scanFailed(int requestCode,String result,int width,int height) {
        ScanUtil.i("=====扫描失pppppp败哈哈哈====result="+result);

        Toast.makeText(this,"扫描界面失败的结果: "+result,Toast.LENGTH_SHORT).show();
        //finish();
    }
}
