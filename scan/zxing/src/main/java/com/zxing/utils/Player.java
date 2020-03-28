package com.zxing.utils;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Title:
 * description:
 * autor:pei
 * created on 2020/3/26
 */
public class Player {

    private MediaPlayer mediaPlayer;

    /**
     * 设置播放源为Raw文件夹
     * @param rawId:若播放文件路径为 res/raw/audio.mp3,则 rawId= R.raw.audio
     */
    public void setDataByRaw(int rawId, Context context){
        //释放资源
        release();
        //直接创建，不需要设置setDataSource
        mediaPlayer=MediaPlayer.create(context, rawId);
    }

    /** 开始播放 **/
    public void start(MediaPlayer.OnCompletionListener onCompletionListener) {
        //检测是否已经设置播放源
        if(mediaPlayer==null){
            throw new SecurityException("请先设置播放源(播放文件)");
        }
        if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
            //同步缓冲
            bufferPlayer(false);
            // 开始播放
            mediaPlayer.start();
            ScanUtil.i("=======开始播放======");
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        }else{
            ScanUtil.i("=======播放失败=====mediaPlayer="+mediaPlayer);
            if(null!=mediaPlayer){
                ScanUtil.i("=======播放失败=====mediaPlayer.isPlaying()="+mediaPlayer.isPlaying());
            }
        }
    }

    /***
     * 缓冲播放器
     *
     * @param async 是否异步缓冲(true=异步缓冲,false=同步缓冲)
     */
    private void bufferPlayer(boolean async){
        ScanUtil.i("======缓冲==1====");
        try {
            ScanUtil.i("======缓冲==2====");
            if(async){
                ScanUtil.i("======缓冲(异步)==3====");
                //异步缓冲
                mediaPlayer.prepareAsync() ;
                ScanUtil.i("======缓冲(异步)==4====");
            }else{
                ScanUtil.i("======缓冲(同步)==5====");
                //同步缓冲
                mediaPlayer.prepare();
                ScanUtil.i("======缓冲(同步)==6====");
            }
            ScanUtil.i("======缓冲==7====");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ScanUtil.i("=====缓冲播放器失败==1========"+e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ScanUtil.i("=====缓冲播放器失败==2========"+e.getMessage());
        }
    }

    /**暂停**/
    public void pause() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**是否在播放**/
    public boolean isPlaying(){
        if(mediaPlayer!=null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**停止**/
    public void stop() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**释放资源**/
    public void release() {
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            ScanUtil.i("=====mediaPlayer释放资源==========");
        }
    }


}
