## EncodingUtils使用说明

### 概述
`EncodingUtils`是一个生成各种样式`一维码`、`二维码`的工具。

### 使用说明
#### 一.生成一维码(条形码)
生成一维码的方法如下：
```
    /**
     * 生成一维码
     *
     * @param content 文本内容
     * @param qrWidth 条形码的宽度
     * @param qrHeight 条形码的高度
     * @param hasText 一维码底部是否显示文字。true:显示，false：不显示
     * @return bitmap
     */
    public static Bitmap createBarCode(String content, int qrWidth, int qrHeight,boolean hasText)
```
你可以在`MainActivity`中这样调用：
```
    //声明
    private ImageView mImv;
    
    //初始化
    mImv=findViewById(R.id.imv);
    
    //生成不显示底部文字的条形码
    Bitmap bitmap=EncodingUtils.createBarCode("ABCD23456gejkek",500,200,false);
    mImv.setImageBitmap(bitmap);
```
#### 二.生成二维码
##### 2.1 生成黑色二维码
生成一般的`黑色`的二维码，你可以调用以下方法：
```
    /**
     * 创建黑色二维码
     *
     * @param content   content 二维码内容
     * @param widthPix  widthPix 宽度
     * @param heightPix heightPix 高度
     * @param logoBm    logoBm 二维码中心logo，为null时显示无logo的二维码
     * @return 二维码的bitmap
     */
    public static Bitmap createQRCode(String content, int widthPix, int heightPix, Bitmap logoBm) 
```
##### 2.2 生成单色二维码
生成单色二维码，如一般使用的黑色二维码，蓝色二维码，红色二维码等，你可以调用以下方法：
```
    /**
     * 生成单色二维码
     * @param content 二维码内容
     * @param widthPix 二维码尺寸(宽度)
     * @param color 二维码颜色,格式为： 0xff000000 或 Color.RED
     * @param heightPix 二维码尺寸(高度)
     * @param logoBm 二维码logo，为null时表示二维码中无图片
     * @return
     */
    public static Bitmap createPureColorQRCode(String content, int widthPix, int heightPix, int color,Bitmap logoBm)
```
下面给出一个生成`青色`二维码的示例(注:`mImvResult`为显示二维码的`ImageView`):
```
    Bitmap bitmap=EncodingUtils.createPureColorQRCode(input,mImvResult.getWidth(),mImvResult.getHeight(),0xff0c9f11,null);
    mImvResult.setImageBitmap(bitmap);
```
##### 2.3 生成带logo/无log的二维码
无论是生成黑色的二维码方法`createQRCode(String content, int widthPix, int heightPix, Bitmap logoBm)`
还是生成单色二维码的方法`createPureColorQRCode(String content, int widthPix, int heightPix, int color,Bitmap logoBm)`
当中的最后一个参数`logoBm`都是一个`Bitmap`,当这个参数为`null`时，则生成一个不含`logo`的二维码，当这个参数为一个图片的bitmap时,
则生成一个中心含图片的二维码。  
下面以生成一个中间含图片的黑色二维码为例(注:`mImvResult`为显示二维码的`ImageView`):
```
     Bitmap bitmaoLogo= BitmapFactory.decodeResource(getResources(),R.drawable.ic_test);
     Bitmap bitmap=EncodingUtils.createQRCode(input,mImvResult.getWidth(),mImvResult.getHeight(),bitmaoLogo);
     mImvResult.setImageBitmap(bitmap);
```
生成单色含`logo`的二维码方式雷同，此处不做赘述。

