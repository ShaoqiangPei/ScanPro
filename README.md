## ScanPro

### 简介
`ScanPro`是一个利用`zxing`封装实现二维码扫描及生成的工具库。

### 使用说明
#### 一.库依赖
在你project对应的buid.gradle中添加如下代码：
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
在你要使用的module对应的buid.gradle中添加如下代码(以0.0.1版本为例)：
```
	dependencies {
	        implementation 'com.github.ShaoqiangPei:ScanPro:0.0.1'
	}
```
#### 二. 主要功能类
[BaseCaptureActivity](https://github.com/ShaoqiangPei/ScanPro/blob/master/read/BaseCaptureActivity%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ———— 自定义扫描界面基类,需要自定义扫描界面的时候需要继承此类   
[CaptureActivity](https://github.com/ShaoqiangPei/ScanPro/blob/master/read/CaptureActivity%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md
) ———— 定制版扫描界面基类,需要快速接入定制版扫描界面需要继承此类  
[EncodingUtils](https://github.com/ShaoqiangPei/ScanPro/blob/master/read/EncodingUtils%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ———— 一维码、二维码生成工具类  
ScanUtil ———— log调试工具类  
#### 三. 显示本库调试log
开启本库调试log(默认情况下为`false`,即为关闭状态):
```
   //显示库内部调试log     
   ScanUtil.setDebug(true);
```
本库`log`显示的`tag=scan`,`log`等级一般为i,如:`ScanUtil.i("");`。
  
  
