## BaseCaptureActivity使用说明

### 简介
`BaseCaptureActivity`是一个扫描界面的基类，如果你需要自定义扫描界面，那么你可以直接继承它实现自己的扫描界面。  
若想在你的项目中快速接入扫描功能，可使用本库`定制版扫描界面`,具体接入可参考[CaptureActivity使用说明](https://github.com/ShaoqiangPei/ScanPro/blob/master/read/CaptureActivity%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)

### 使用说明
#### 一.主要方法
##### 1.1 静态方法
`BaseCaptureActivity`包含以下几个静态方法，便于开发者在使用的过程中调用：
```
    /**默认跳转**/
    public static void startAct(Context context,Class<?>cls,int requestCode)
    
    /**获取二维码内容**/
    public static void getCodeResult(int requestCode,int resultCode, Intent data, OnScanResultListener listener)
```
- startAct(Context context,Class<?>cls,int requestCode):当你项目中某个界面(如界面A)需要使用到扫描功能的时候，你可以在界面A通过点击按钮调用此方法跳转到`定制扫描
  界面`或`自定义扫描界面`。当然， 前提是你已经在点击按钮的时候做过了扫描所需权限的处理。
- getCodeResult(int requestCode,int resultCode, Intent data, OnScanResultListener listener)：当你界面A在调用完扫描界面并在关闭扫描界面后，想在界面A中接收
  扫描数据回传的时候，你可以在A界面的`onActivityResult(int requestCode, int resultCode, @Nullable Intent data)`中调用此方法，用于接收扫描回传值
##### 1.2 其他主要方法介绍
- `handleDecode(Result rawResult, Bundle bundle)`方法,主要用于将扫描结果回传到界面A的处理，此处只做了解，具体处理已经在 `BaseCaptureActivity`基类中完成。此方法只做了解即可，无需开发者调用。
- `restartPreviewAfterDelay(long delayMS)`:重复扫描。此方法已在 `BaseCaptureActivity`内部处理，当开发这使用的是扫描完结果后仍停在扫描界面的模式时，扫描框在获取到扫描完结果的2秒之后，继续恢复扫描功能。此方法只做了解即可，无需开发者调用。
- `defaultInitCrop(ViewGroup preLayout,ViewGroup scanLayout)`:默认获取扫描二维码的尺寸(宽高),当你在自定义扫描界面的时候，可以考虑在自定义扫描界面的`initCrop()`方法中调用此方法,具体使用可参照[CaptureActivity代码](https://github.com/ShaoqiangPei/ScanPro/blob/master/scan/zxing/src/main/java/com/zxing/activity/CaptureActivity.java)中的`initCrop()`方法。当然,你也可以在自定义扫描界面的`initCrop()`方法中自己实现获取扫描二维码的尺寸。当然，你也可以在自定义的扫描界面的`initCrop()`中不做任何处理，这时，你获得的扫描结果中二维码尺寸(宽高)将为`0`。
- `Object[] getContentArray()`:虚拟方法，需要子类实现。传参及解释如下：
```
    /**
     * 传两个参数:第一个参数为int,布局id,如：R.layout.activity_capture
     *           第二个参数为boolean,true表示扫描后立即关闭扫描界面,false表示扫描后不关闭扫描界面
     *           第二个参数传null时,isScanedFinish取默认值为true,即扫描后立即返回
     * @return
     */
    protected abstract Object[] getContentArray();
```
还有以下几个虚拟方法，都是在自定义扫描界面的时候需要实现的：
```
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
```
- 这里需要解释的是，在自定义扫描的界面中必须含有一个控件:`SurfaceView`,然后在`getSurfaceView()`中返回这个`SurfaceView`对象
- 自定义扫描界面中涉及选择相册中二维码照片扫描功能的时候,若用户不授权相册权限，需要在拒绝授权的方法中调用`noAlbumPermission()`方法，
然后在`noAlbumPermission()`方法中做无相册权限的处理
- 在自定义扫描界面中，无需调用`onActivityResult(int requestCode, int resultCode, @Nullable Intent data)`方法处理相册数据回传问题，
因为在`BaseCaptureActivity`中已做处理,开发者只需当`getContentArray()`方法中第二个参数为`false`(即扫描结果在扫描界面处理)的时候，在
`scanSuccess(int requestCode,String result,int width,int height)`和`scanFailed(int requestCode,String result,int width,int height)`中做好`扫描成功`和`扫描失败`
的逻辑处理即可。  
在自定义扫描界面中也可能会使用到`BaseCaptureActivity`中的以下几个方法：
```
    /***
     * 扫描动画
     *
     * @param view 扫描线的imageView
     * @param duration 扫描时间间隔，单位毫秒，若duration<=0,则取默认时间间隔2500毫秒
     */
    public void scanAnimation(View view,int duration)
    
    /**打开相册**/
    public void selectImage() 
    
    /**开启/关闭闪光灯**/
    public void changeFlashLight()
```
#### 二.自定义扫描界面
自定义扫描界面需要继承`BaseCaptureActivity`,以自定义扫描界面`CustScanActivity`为例，你可以像下面这样开始你的自定义扫描界面：
```
/**
 * Title:自定义扫描界面
 * description:
 * autor:pei
 * created on 2020/3/28
 */
public class CustScanActivity extends BaseCaptureActivity {

    @Override
    protected Object[] getContentArray() {
        //参数1：布局文件id，如 R.layout.activity_cus_scan
        //参数2：扫描完毕后，是否立刻关闭扫描界面。true：是，false：否。
        //例如：return new Object[]{R.layout.activity_cus_scan,true};
        return new Object[]{1,true};
    }

    @Override
    protected void initView() {
       //初始化控件
    }

    @Override
    protected SurfaceView getSurfaceView() {
        return null;//返回SurfaceView对象,必须返回，不能为null
    }

    @Override
    protected void initData() {
        super.initData();
        //当你自定义扫描界面的时候，你可能需要使用到这个扫描动画的方法,具体使用可参考CaptureActivity类
//        //扫描动画
//        scanAnimation(mImvScan,0);
    }

    @Override
    protected void initCrop() {
        //在此设置扫描二维码的尺寸，如：
//        super.mCropRect=new Rect();
//        mCropRect.set(0,0,200,200);
        //不过一般我们都直接调用父类默认测量代码，
        //参考CaptureActivity中super.defaultInitCrop(mPreLayout,mScanLayout);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void noAlbumPermission() {
       //未给定打开相册权限的处理
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void scanSuccess(int requestCode,String result, int width, int height) {
        //扫描成功的处理

    }

    @Override
    protected void scanFailed(int requestCode,String result, int width, int height) {
        //扫描失败的处理
    }

}
```
假如你项目中`界面A`需要集成定义扫描界面`CustScanActivity`.那么你需要如下几步处理：
##### 2.1 在Androidmanifast.xml中注册CustScanActivity
你需要在你项目的`Androidmanifast.xml`中注册`CustScanActivity`以用于界面跳转。
##### 2.2.添加用户相机权限
在`界面A`中点击按钮时处理`打开相机`,`相册读写`权限等。涉及要修改的地方有`Androidmanifast.xml`,`fileprovider`以及`android6.0+用户手动权限`
这里就不详细说明了。 
##### 2.3 从界面A跳转到自定义扫描界面CustScanActivity
然后在具备权限的情况下，你可以在`界面A`通过以下方法跳转到自定义扫描界面`CustScanActivity`：
```
int requestCode=100;//请求code根据业务需求自定义
//跳转扫描界面
BaseCaptureActivity.startAct(Context context,Class<?>cls,requestCode);
```
##### 2.4 自定义扫描界面CustScanActivity布局及扫描结果的处理
在自定义扫描界面`CustScanActivity`中实现`getContentArray()`方法，类似如下：
```
    @Override
    protected Object[] getContentArray() {
        //参数1：布局文件id，如 R.layout.activity_cus_scan
        //参数2：扫描完毕后，是否立刻关闭扫描界面。true：是，false：否。
        return new Object[]{R.layout.activity_cus_scan,true};
    }
```
- 当第二个参数为`true`时，表示扫描出结果后会立马关闭当前扫描界面，那么扫描结果会在`界面A`中处理，若是此种情况，
你无需在`CustScanActivity`界面的`scanSuccess(int requestCode,String result, int width, int height)`和`scanFailed(int requestCode,String result, int width, int height)`
中做任何逻辑处理。你需要在`界面A`中处理扫描返回结果。在`界面A`的`onActivityResult(int requestCode, int resultCode, @Nullable Intent data)`
中做扫描结果的处理，你可以像下面这样：
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BaseCaptureActivity.getCodeResult(requestCode, resultCode,data, new OnScanResultListener() {
            @Override
            public void scanSuccess(String result, int width, int height) {
               //扫描结果成功的处理
               //.... 
            }

            @Override
            public void scanFailed(String result, int width, int height) {
                //扫描结果失败的处理
                //.... 
            }
        });
    }
```
- 当第二个参数为`false`时，表示扫描出结果后，不关闭扫描界面。这时你的扫描结果是在自定义扫描界面`CustScanActivity`中处理，而不是在`界面A`
中处理。所以你要在自定义扫描界面`CustScanActivity`的`scanSuccess(int requestCode,String result, int width, int height)`和`scanFailed(int requestCode,String result, int width, int height)`
中做扫描成功和扫描失败的处理，类似如下：
```
    @Override
    protected void scanSuccess(int requestCode,String result, int width, int height) {
        //扫描成功的处理
        //...
    }

    @Override
    protected void scanFailed(int requestCode,String result, int width, int height) {
        //扫描失败的处理
        //...
    }
```
##### 2.5 自定义扫描界面CustScanActivity其他几个方法的解释
- initView()：用于处理控件初始化
- getSurfaceView():返回`SurfaceView`对象,必须返回，不能为`null`。即你在自定义扫描界面`CustScanActivity`中必须有一个`SurfaceView`控件
  在`initView()`初始化后，在此方法中返回`SurfaceView`对象
- initData():可在此方法中做初始化时数据的解基本处理，例如在定制界面`CaptureActivity`中此处做的是扫描动画处理
- initCrop():用于处理获取扫描的二维码的尺寸逻辑，不写此方法的逻辑时，默认获取二维码尺寸为`0`
- setListener():设置控件监听
- onClick(View v):实现点击按键处理逻辑
- noAlbumPermission():用户拒绝权限的方法中调用此方法。并在此方法中处理用户拒绝授权的逻辑
- scanSuccess(int requestCode,String result, int width, int height)和scanFailed(int requestCode,String result, int width, int height)：当扫描界面在`getContentArray()`
方法中第二个参数设置为`false`(即扫描结果的处理在扫描界面处理的时候)，用于做扫描结果成功或失败的处理

