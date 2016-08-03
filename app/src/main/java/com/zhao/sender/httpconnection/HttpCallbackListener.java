package com.zhao.sender.httpconnection;

import android.graphics.Bitmap;

import java.io.InputStream;

/**
 * Created by zhao on 2016/4/16.
 */
public interface HttpCallbackListener {
    void onFinish(String response); //字符数据回调处理
    void onFinish(InputStream in); //数据流回调处理
    void onFinish(Bitmap bm); //位图文件数据回调处理
    void onError(Exception e); //获取数据失败处理
}
