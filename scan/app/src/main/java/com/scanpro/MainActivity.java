package com.scanpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.zxing.activity.BaseCaptureActivity;
import com.zxing.encoding.EncodingUtils;
import com.zxing.interfacer.OnScanResultListener;
import com.zxing.utils.ScanUtil;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private Button mBtnScan;
    private TextView mTvResult;
    private ImageView mImvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScanUtil.setDebug(true);

        initView();
        initData();
        setListener();
    }

    private void initView() {
        mBtnScan=findViewById(R.id.btn_scan);
        mTvResult=findViewById(R.id.tv_result);
        mImvResult=findViewById(R.id.imv);
    }

    private void initData() {
        /**申请授权**/
        MainActivityPermissionsDispatcher.requestPhoneStateWithPermissionCheck(MainActivity.this);
    }

    private void setListener() {
        mBtnScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                //跳转扫描界面
//                BaseCaptureActivity.startAct(MainActivity.this, ScanActivity.class);

//                makeCode();

                makeOp();
            }
        });
    }

    private void makeCode(){
        String input="梁晶晶";

        Bitmap bitmaoLogo= BitmapFactory.decodeResource(getResources(),R.drawable.ic_test);
//        Bitmap bitmap=EncodingUtils.createQRCode(input,mImvResult.getWidth(),mImvResult.getHeight(),bitmaoLogo);
//        Bitmap bitmap=EncodingUtils.createQRCode(input,mImvResult.getWidth(),mImvResult.getHeight(),null);
        Bitmap bitmap=EncodingUtils.createPureColorQRCode(input,mImvResult.getWidth(),mImvResult.getHeight(),0xff0c9f11,bitmaoLogo);
        mImvResult.setImageBitmap(bitmap);
    }

    private void makeOp(){

//        Bitmap bitmap=EncodingUtils.createBarCode("ABCD23456gejkek",500,200);
        Bitmap bitmap=EncodingUtils.getBarcodeBitmap("ABCD23456gejkek",500,200,false);
        mImvResult.setImageBitmap(bitmap);
    }

    /**申请权限**/
    @NeedsPermission({Manifest.permission.CAMERA})
    void requestPhoneState() {
        ScanUtil.i("=====允许授权=====");
        //获取设备号
        //......
    }

    /**弹授权框(第一次拒绝后,第二次打开弹出的授权框)**/
    @OnShowRationale({Manifest.permission.CAMERA})
    void showDialog(final PermissionRequest request){
        new AlertDialog.Builder(this)
                .setMessage("是否授权手机权限?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    /**拒绝授权**/
    @OnPermissionDenied({Manifest.permission.CAMERA})
    void deniedPermission(){
        ScanUtil.i("=====拒绝授权的处理=====");

        //弹框提示：需要授权
        //......
    }

    /**不再询问**/
    @OnNeverAskAgain({Manifest.permission.CAMERA})
    void neverAskPermission(){
        ScanUtil.i("=====不再询问的处理=====");

        //弹框提示：需要授权
        //......
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //返回结果处理
        BaseCaptureActivity.getCodeResult(resultCode, data, new OnScanResultListener() {
            @Override
            public void scanSuccess(String result,int width,int height) {
                Toast.makeText(MainActivity.this,"扫描结果: "+result,Toast.LENGTH_SHORT).show();
                mTvResult.setText(result);
            }

            @Override
            public void scanFailed(String result,int width,int height) {
                Toast.makeText(MainActivity.this,"扫描错误",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
