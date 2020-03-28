## CaptureActivity使用说明

### 简介
`CaptureActivity`是一个`定制版扫描界面`的基类，其继承于`BaseCaptureActivity`。当你想快速使用一个`定制版扫描界面`的时候，你可以写一个类继承此类。  
若想自定义扫描界面，请参考[BaseCaptureActivity使用说明](https://github.com/ShaoqiangPei/ScanPro/blob/master/read/BaseCaptureActivity%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)

### 使用说明
#### 一. CaptureActivity 特点
`CaptureActivity`几乎实现了一个扫描界面需要的所有功能。包括`直接扫描二维码`,`闪光灯`,`选择相册照片扫描`。当然，`CaptureActivity`也添加了`打开相册`
的`权限申请`。`CaptureActivity`实现了一个`定制版的扫描界面`。
#### 二. 调用定制版的扫描界面为啥要多写个类继承CaptureActivity
**Q：**`CaptureActivity`功能已经很完美了，那为啥还要写个子类去继承它才能调用`定制版的扫描界面`?
**A：**`CaptureActivity`虽然功能完美,可以加快开发者接入一个扫描界面，但是介于以下几点考虑：
- 扫描后是直接关闭扫描界面，还是扫描后直接在扫描界面显示扫描结果并错逻辑处理
- 扫描界面各控件`Pading`，`margin`调整
- 扫描界面各控件`文字内容`,`文字大小`，,`文字颜色`调整
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
    protected void scanSuccess(String result, int width, int height) {
        LogUtil.i("======扫描success的结果====result="+result);
    }

    @Override
    protected void scanFailed(String result, int width, int height) {
        LogUtil.i("======扫描failed的结果====result="+result);
    }
}
```
- scanFinish():返回`true`表示扫描完毕后立刻关闭扫描界面




