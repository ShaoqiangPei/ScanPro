package com.zxing.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Title:自定义扫描框布局
 * description:
 * autor:pei
 * created on 2020/3/25
 */
public class ScanLayout extends ConstraintLayout {

    private int mWidth;
    private int mHeight;

    private float mSideLength=40f;//四角单边长
    private float mSideWidth=20f;//角边长宽度

    public ScanLayout(Context context) {
        this(context,null);
    }

    public ScanLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScanLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        //宽
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        //高
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制方框
        drawRect(canvas);
    }

    /**绘制方框**/
    private void drawRect(Canvas canvas){
        //设置paint
        Paint paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        //画矩形
        RectF rectF=new RectF();
        rectF.set(0,0,mWidth,mHeight);
        //画矩形
        canvas.drawRect(rectF,paint);

        //画四个直角
        paint.setStrokeWidth(mSideWidth);
        Path path=new Path();
        //左上直角
        path.moveTo(mSideLength,0f);//设置path的起点
        path.lineTo(0f,0f);
        path.lineTo(0f,mSideLength);
        //右上直角
        path.moveTo(mWidth-mSideLength,0f);//设置path的起点
        path.lineTo(mWidth,0f);
        path.lineTo(mWidth,mSideLength);
        //左下直角
        path.moveTo(0f,mHeight-mSideLength);//设置path的起点
        path.lineTo(0f,mHeight);
        path.lineTo(mSideLength,mHeight);
        //右下直角
        path.moveTo(mWidth,mHeight-mSideLength);//设置path的起点
        path.lineTo(mWidth,mHeight);
        path.lineTo(mWidth-mSideLength,mHeight);

        //绘制
        canvas.drawPath(path,paint);
    }

}
