package com.zxing.activity;

import android.Manifest;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.zxing.R;
import com.zxing.utils.ScanUtil;
import com.zxing.widget.ScanLayout;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Title:扫描界面定制扫描
 * description:
 * autor:pei
 * created on 2020/3/25
 */
@RuntimePermissions
public abstract class CaptureActivity extends BaseCaptureActivity {

    protected ImageView mImvBack;
    protected TextView mTvBack;

    protected SurfaceView mSurfaceView;
    protected ConstraintLayout mPreLayout;//预览布局,全屏幕显示
    protected ScanLayout mScanLayout;//扫描布局
    protected ImageView mImvScan;//扫描线
    protected TextView mTvMessage;//提示语
    protected ImageView mImvAlbum;//相册
    protected ImageView mImvLight;//闪光灯

    @Override
    protected Object[] getContentArray() {
        return new Object[]{R.layout.activity_capture,scanFinish()};
    }

    /**
     * 扫描结束后，是否关闭当前界面
     *
     * @return false:扫描结束后不关闭扫描界面
     *         true:扫描结束后关闭扫描界面
     *  默认为true，即扫描后关闭扫描界面
     */
    protected abstract boolean scanFinish();

    @Override
    protected void initView(){
        mImvBack = findViewById(R.id.imv_back);
        mTvBack = findViewById(R.id.tv_back);

        mSurfaceView = findViewById(R.id.capture_preview);
        mPreLayout = findViewById(R.id.capture_container);
        mScanLayout = findViewById(R.id.capture_crop_view);
        mImvScan = findViewById(R.id.capture_scan_line);
        mTvMessage = findViewById(R.id.tv_message);
        mImvAlbum = findViewById(R.id.imv_album);
        mImvLight = findViewById(R.id.imv_light);
    }

    @Override
    protected SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    @Override
    protected void initData() {
        super.initData();
        //扫描动画
        scanAnimation(mImvScan,0);
    }

    @Override
    protected void initCrop() {
       super.defaultInitCrop(mPreLayout,mScanLayout);
    }

    @Override
    protected void setListener() {
        mImvBack.setOnClickListener(this);
        mImvAlbum.setOnClickListener(this);
        mImvLight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.imv_back){//返回
           this.finish();
       }else if(v.getId()==R.id.imv_album){//相册
           //申请读写授权
           CaptureActivityPermissionsDispatcher.requestPermissionWithPermissionCheck(CaptureActivity.this);
       }else if(v.getId()==R.id.imv_light) {//闪光灯
           changeFlashLight();
       }
    }

    /**申请权限**/
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void requestPermission() {
        ScanUtil.i("=====允许授权=====");
        //打开相册
        selectImage();
    }

    /**弹授权框(第一次拒绝后,第二次打开弹出的授权框)**/
    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDialog(final PermissionRequest request){
        //继续申请权限
        request.proceed();
    }

    /**拒绝授权**/
    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void deniedPermission(){
        ScanUtil.i("=====拒绝授权的处理=====");

        //弹框提示：需要授权
        noAlbumPermission();
    }

    /**不再询问**/
    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void neverAskPermission(){
        ScanUtil.i("=====不再询问的处理=====");

        //弹框提示：需要授权
        noAlbumPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CaptureActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}

