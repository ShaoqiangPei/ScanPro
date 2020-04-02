## CaptureActivity使用说明

### 简介
`CaptureActivity`是一个`定制版扫描界面`的基类，其继承于`BaseCaptureActivity`。当你想快速使用一个`定制版扫描界面`的时候，你可以写一个类继承此类。  若想自定义扫描界面，请参考[BaseCaptureActivity使用说明](https://github.com/ShaoqiangPei/ScanPro/blob/master/read/BaseCaptureActivity%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)

### 使用说明
#### 一. CaptureActivity 特点
`CaptureActivity`几乎实现了一个扫描界面需要的所有功能。包括`直接扫描二维码`,`闪光灯`,`选择相册照片扫描`。当然，`CaptureActivity`也添加了`打开相册`
的`权限申请`。`CaptureActivity`实现了一个`定制版的扫描界面`。
#### 二. 调用定制版的扫描界面为啥要多写个类继承CaptureActivity
**Q：**`CaptureActivity`功能已经很完美了，那为啥还要多写个子类去继承它才能调用`定制版的扫描界面`,你秀你妹呢?  
**A：** 呃,`CaptureActivity`虽然功能完美,可以加快开发者接入一个扫描界面，但是介于以下几点考虑：
- 扫描后是直接关闭扫描界面，还是扫描后直接在扫描界面显示扫描结果并错逻辑处理
- 用户未授权`相册权限`的处理
- 扫描界面各控件`Pading`，`margin`调整
- 扫描界面各控件`文字内容`,`文字大小`,`文字颜色`调整
- 扫描界面各控件`图标切换`,`图标大小`调整  

基于以上几点，则需要开发者写一个子类继承于`CaptureActivity`,当有需求时用于微调`定制版扫描界面`参数。以达到最快接入且具备一定灵活性的特点。
#### 三. 定制版扫描界面示例
继承于`CaptureActivity`，你可以像下面这样写一个`定制版扫描界面`(以`ScanActivity`为例)：
```
/**
 * Title:扫描界面
 * description:
 * autor:pei
 * created on 2020/3/28
 */
public class ScanActivity extends CaptureActivity {

    @Override
    protected boolean scanFinish() {
        //扫描完毕后，是否立刻关闭扫描界面。true：是，false：否。
        return false;
    }

    @Override
    protected void noAlbumPermission() {

    }

    @Override
    protected void scanSuccess(int requestCode,String result, int width, int height) {
        LogUtil.i("======扫描success的结果====result="+result);
    }

    @Override
    protected void scanFailed(int requestCode,String result, int width, int height) {
        LogUtil.i("======扫描failed的结果====result="+result);
    }
}
```
假如你项目中`界面A`需要集成定制版扫描界面`ScanActivity`，那么你需要如下几步处理：
##### 3.1 在Androidmanifast.xml中注册ScanActivity
你需要在你项目的`Androidmanifast.xml`中注册`ScanActivity`以用于界面跳转。
##### 3.2.添加用户相机权限
在`界面A`中点击按钮时处理`打开相机`,`相册读写`权限等。涉及要修改的地方有`Androidmanifast.xml`,`fileprovider`以及`android6.0+用户手动权限`
这里就不详细说明了。 
##### 3.3 从界面A跳转到定制版扫描界面ScanActivity
然后在具备权限的情况下，你可以在`界面A`通过以下方法跳转到定制版扫描界面`ScanActivity`：
```
//跳转扫描界面
BaseCaptureActivity.startAct(Context context,Class<?>cls,int requestCode);
```
##### 3.4 定制版扫描界面ScanActivity中几个方法的解释
- scanFinish():返回`true`表示扫描出结果后会立马关闭当前扫描界面，那么扫描结果会在`界面A`中处理，若是此种情况，
你无需在`ScanActivity`界面的`scanSuccess(int requestCode,String result, int width, int height)`和`scanFailed(int requestCode,String result, int width, int height)`
中做任何逻辑处理。你需要在`界面A`中处理扫描返回结果。在`界面A`的`onActivityResult(int requestCode, int resultCode, @Nullable Intent data)`
中做扫描结果的处理，你可以像下面这样：
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BaseCaptureActivity.getCodeResult(requestCode,resultCode,data, new OnScanResultListener() {
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
返回`false`表示扫描出结果后，不关闭扫描界面。这时你的扫描结果是在定制版扫描界面`ScanActivity`中处理，而不是在`界面A`
中处理。所以你要在定制版扫描界面`ScanActivity`的`scanSuccess(int resultCode,String result, int width, int height)`和`scanFailed(int resultCode,String result, int width, int height)`中做扫描成功和扫描失败的处理，类似如下：
```
    @Override
    protected void scanSuccess(int resultCode,String result, int width, int height) {
        //扫描成功的处理
        //...
    }

    @Override
    protected void scanFailed(int resultCode,String result, int width, int height) {
        //扫描失败的处理
        //...
    }
```
- noAlbumPermission():当用户拒绝授权`打开相册`权限的处理
- scanSuccess(String result, int width, int height)和scanFailed(String result, int width, int height):当`scanFinish()`返回参数为
`false`(即扫描到结果后不立即关闭扫描界面)时，扫描结果成功和失败的处理逻辑。
#### 四. 微调定制版扫描界面参数
以修改扫描界面返回键文字为例，可以在`ScanActivity`中重载其父类`CaptureActivity`的`initData()`方法，并在其中修改返回键文字，类似下面这样:
```
/**
 * Title:扫描界面
 * description:
 * autor:pei
 * created on 2020/3/28
 */
public class ScanActivity extends CaptureActivity {
    
    //其他方法，在此省略
    //......
    
    @Override
    protected void initData() {
        super.initData();

        super.mTvBack.setText("大家好");
    }

}
```
其他参数的微调类似，必要时也可重写父类`CaptureActivity`中的一些方法，用以微调其他参数。
