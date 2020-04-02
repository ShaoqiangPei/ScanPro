package com.zxing.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.Result;
import com.google.zxing.common.StringUtils;
import com.zxing.R;
import com.zxing.camera.CameraManager;
import com.zxing.decode.DecodeThread;
import com.zxing.encoding.QRCodeDecoder;
import com.zxing.interfacer.OnScanResultListener;
import com.zxing.utils.CaptureActivityHandler;
import com.zxing.utils.InactivityTimer;
import com.zxing.utils.ScanUtil;
import com.zxing.utils.Player;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Title:扫描界面父类
 * description:
 * autor:pei
 * created on 2020/3/26
 */
public abstract class BaseCaptureActivity extends AppCompatActivity implements View.OnClickListener,SurfaceHolder.Callback{

    private static final String CODE_WIDTH="width";
    private static final String CODE_HEIGHT="height";
    private static final String CODE_RESULT="result";
    private static final int SCAN_DURATION=2500;//默认扫描时间间隔,单位毫秒
    private static final long VIBRATE_DURATION = 200L;//震动时长

    private static final int REQUEST_GET_CODE=0;//扫描请求code
    public static final int REQUEST_GET_IMAGE=1;//相册code

    protected SurfaceView mSurfaceView;

    protected Camera mCamera;
    protected CameraManager cameraManager;
    protected CaptureActivityHandler handler;
    protected InactivityTimer inactivityTimer;

    protected Player mPlayer;
    protected Vibrator mVibrator;
    protected Rect mCropRect = null;
    protected boolean isHasSurface = false;

    private boolean isScanedFinish=true;//扫描后是否关闭当前界面,默认为true，即关闭。
    private static int mRequestCode;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕高亮

        Object object[]=getContentArray();
        if(object.length==2){
            try {
                if(object[0]!=null) {
                    int layoutId = Integer.valueOf(object[0].toString());
                    setContentView(layoutId);
                    initView();
                    initData();
                    setListener();
                }
                if(object[1]!=null) {
                    isScanedFinish = Boolean.valueOf(object[1].toString());
                }else{
                    isScanedFinish=true;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ScanUtil.e("=========转参数错误====="+e.getMessage());
            }
        }
    }

    protected void initData(){
        //获取mSurfaceView控件
        mSurfaceView=getSurfaceView();
        //初始化对象
        inactivityTimer = new InactivityTimer(this);
        mPlayer=new Player();
    }

    /**播放Raw文件夹下音乐文件,加震动**/
    protected void playEffect(){
        mPlayer.setDataByRaw(R.raw.code_music, BaseCaptureActivity.this);
        mPlayer.start(null);
        //震动
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(VIBRATE_DURATION);
    }

    /**停止音效震动效果**/
    protected void stopEffect() {
        if (mPlayer != null) {
            mPlayer.release();
        }
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplication());
        handler = null;
        if (isHasSurface) {
            if(mSurfaceView!=null) {
                initCamera(mSurfaceView.getHolder());
            }
        } else {
            if(mSurfaceView!=null) {
                // Install the callback and wait for surfaceCreated() to init the camera.
                mSurfaceView.getHolder().addCallback(this);
            }
        }
        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        cameraManager.closeDriver();
        if (!isHasSurface&&mSurfaceView!=null) {
            mSurfaceView.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开相册扫描的处理
        if (requestCode == BaseCaptureActivity.REQUEST_GET_IMAGE && resultCode == Activity.RESULT_OK) {
            decodePath(data);
        }
    }

    /**选择完相册返回的处理**/
    private void decodePath(Intent data){
        new AsyncTask<Void,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... voids) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap tempBitmap = BitmapFactory.decodeFile(picturePath);
                return tempBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                decode(bitmap);
            }
        }.execute();
    }

    /**解析相册返回bitmap的处理**/
    private void decode(Bitmap bitmap){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                String result= QRCodeDecoder.syncDecodeQRCode(bitmap);
                bitmap.recycle();
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                handleDecode(result,null);
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        stopEffect();
        if(mCamera!=null){
            mCamera.release();
        }
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            ScanUtil.e("*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        isHasSurface = false;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param result The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(String result, Bundle bundle) {
        inactivityTimer.onActivity();
        playEffect();

        int width=0;
        int height=0;
        Intent resultIntent = new Intent();
        if(bundle!=null) {
            //扫描实物得到结果
            width = mCropRect != null ? mCropRect.width() : width;
            height = mCropRect != null ? mCropRect.height() : height;
        }else{
            //扫描相册二维码得到结果
            bundle=new Bundle();
        }
        bundle.putInt(BaseCaptureActivity.CODE_WIDTH, width);
        bundle.putInt(BaseCaptureActivity.CODE_HEIGHT, width);
        if(!TextUtils.isEmpty(result)){
            result=result.trim();//去掉扫描出的空格
        }
        bundle.putString(BaseCaptureActivity.CODE_RESULT, result);
        resultIntent.putExtras(bundle);
        if(isScanedFinish) {
            this.setResult(RESULT_OK, resultIntent);
            this.finish();
        }else{
            if(TextUtils.isEmpty(result)){//扫描失败
                scanFailed(mRequestCode,result,width,height);
            }else{//扫描成功
                scanSuccess(mRequestCode,result,width,height);
            }
            restartPreviewAfterDelay(2000);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            ScanUtil.w("initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }
            initCrop();
        } catch (IOException ioe) {
            ScanUtil.w(ioe.getMessage());
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            ScanUtil.w("Unexpected error initializing camera: "+e.getMessage());
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Camera error");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    /**重复扫描**/
    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /***
     * 初始化截取的矩形区域
     *
     * @param preLayout 预览布局,需要全屏幕显示
     * @param scanLayout 扫描布局
     */
    public void defaultInitCrop(ViewGroup preLayout,ViewGroup scanLayout) {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanLayout.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanLayout.getWidth();
        int cropHeight = scanLayout.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = preLayout.getWidth();
        int containerHeight = preLayout.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 传两个参数:第一个参数为int,布局id,如：R.layout.activity_capture
     *           第二个参数为boolean,true表示扫描后立即关闭扫描界面,false表示扫描后不关闭扫描界面
     *           第二个参数传null时,isScanedFinish取默认值为true,即扫描后立即返回
     * @return
     */
    protected abstract Object[] getContentArray();

    protected abstract void initView();
    protected abstract SurfaceView getSurfaceView();
    /**初始化截取的矩形区域**/
    protected abstract void initCrop();
    protected abstract void setListener();

    /**没有相册权限的处理**/
    protected abstract void noAlbumPermission();

    /**扫描成功返回的处理**/
    protected abstract void scanSuccess(int requestCode,String result,int width,int height);
    /**扫描失败返回的处理**/
    protected abstract void scanFailed(int requestCode,String result,int width,int height);


    /***
     * 扫描动画
     *
     * @param view 扫描线的imageView
     * @param duration 扫描时间间隔，单位毫秒，若duration<=0,则取默认时间间隔2500毫秒
     */
    public void scanAnimation(View view,int duration){
        if(duration<0||duration==0){
            duration= BaseCaptureActivity.SCAN_DURATION;
        }
        //扫描动画
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(duration);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        view.startAnimation(animation);
    }

    /**打开相册**/
    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, BaseCaptureActivity.REQUEST_GET_IMAGE);
    }

    /**开启/关闭闪光灯**/
    public void changeFlashLight(){
        if(cameraManager!=null){
            cameraManager.flashHandler();
        }
    }

    /**默认跳转**/
    public static void startAct(Context context,Class<?>cls,int requestCode){
        mRequestCode=requestCode;
        Intent intent=new Intent(context, cls);
        ((AppCompatActivity)context).startActivityForResult(intent, requestCode);
    }

    /**获取二维码内容**/
    public static void getCodeResult(int requestCode,int resultCode, Intent data, OnScanResultListener listener) {
        ScanUtil.i("====getCodeResult======requestCode="+requestCode+"   resultCode="+resultCode);
        if (requestCode == mRequestCode && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString(BaseCaptureActivity.CODE_RESULT, null);
            int width = bundle.getInt(BaseCaptureActivity.CODE_WIDTH, 0);
            int height = bundle.getInt(BaseCaptureActivity.CODE_HEIGHT, 0);
            if (TextUtils.isEmpty(result)) {//扫描结果出错
                ScanUtil.i("=====扫描结果出错===result=" + result + "  width=" + width + "  height=" + height);
                listener.scanFailed(result, width, height);
            } else {//扫描成功
                ScanUtil.i("=====扫描成功===result=" + result + "  width=" + width + "  height=" + height);
                listener.scanSuccess(result, width, height);
            }
        }
    }


}
