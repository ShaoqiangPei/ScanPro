package com.zxing.encoding;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 */
public class EncodingUtils {

    /**
     * 创建黑色二维码
     *
     * @param content   content
     * @param widthPix  widthPix
     * @param heightPix heightPix
     * @param logoBm    logoBm
     * @return 二维码
     */
    public static Bitmap createQRCode(String content, int widthPix, int heightPix, Bitmap logoBm) {
        return createPureColorQRCode(content,widthPix,heightPix,Color.BLACK,logoBm);
    }

    /**
     * 生成单色二维码
     * @param content 二维码内容
     * @param widthPix 二维码尺寸(宽度)
     * @param color 二维码颜色,格式为： 0xff000000 或 Color.RED
     * @param heightPix 二维码尺寸(高度)
     * @param logoBm 二维码logo，为null时表示二维码中无图片
     * @return
     */
    public static Bitmap createPureColorQRCode(String content, int widthPix, int heightPix, int color,Bitmap logoBm) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }
            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix,
                    heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = color;//其他颜色,格式为0xff000000 或 Color.RED
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;//白色
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }
            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**在二维码中间添加Logo图案**/
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/4
        float scaleFactor = srcWidth * 1.0f / 4 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }


    /**
     * 生成条形码图片
     * @param str 要往二维码中写入的内容,需要utf-8格式
     * @param width 图片的宽
     * @param height 图片的高
     * @return 返回一个条形bitmap
     */
    public static Bitmap createBarCode(String str,int width, int height){
        Bitmap bmapp= null;
        try {
            //条形码CODE_128
            BarcodeFormat fomt=BarcodeFormat.CODE_128;
            BitMatrix matrix=new MultiFormatWriter().encode(str, fomt, width, height);
            int[] pixel=new int[matrix.getWidth()*matrix.getHeight()];
            for(int i=0;i<height;i++){
                for(int j=0;j<width;j++){
                    if(matrix.get(j,i))
                        pixel[i*width+j]=0xff000000;
                }
            }
            bmapp = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            bmapp.setPixels(pixel, 0, width, 0, 0, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bmapp;
    }


    /**
     * 生成一维码
     *
     * @param content 文本内容
     * @param qrWidth 条形码的宽度
     * @param qrHeight 条形码的高度
     * @param hasText 一维码底部是否显示文字。true:显示，false：不显示
     * @return bitmap
     */
    public static Bitmap getBarcodeBitmap(String content, int qrWidth, int qrHeight,boolean hasText) {
        content = content.trim();
        //文字的高度
        int mHeight = qrHeight / 5;
        try {
            Map<EncodeHintType, Object> hints = new EnumMap(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix result;
            try {
                int barCodeHeight=qrHeight;
                if(hasText){
                    barCodeHeight=qrHeight-mHeight;
                }
                result = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, qrWidth, barCodeHeight, hints);
            } catch (IllegalArgumentException iae) {
                return null;
            }
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0;
                }
            }
            Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            qrBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //大的bitmap
            Bitmap bigBitmap = Bitmap.createBitmap(width, qrHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bigBitmap);
            Rect srcRect = new Rect(0, 0, width, height);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(qrBitmap, srcRect, dstRect, null);
            if(hasText) {
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setFilterBitmap(true);
                //字体大小
                paint.setTextSize(mHeight);
                //开始绘制文本的位置
                canvas.translate(width / 2, mHeight);
                int textWidth= (int) paint.measureText(content);
                canvas.drawText(content, 0, content.length(), -textWidth/2, height, paint);
            }
            return bigBitmap;
        } catch (Exception e) {
            return null;
        }
    }

}
