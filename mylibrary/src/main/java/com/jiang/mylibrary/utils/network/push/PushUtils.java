package com.jiang.mylibrary.utils.network.push;

import android.util.Log;


import com.jiang.mylibrary.constans.Constants;
import com.jiang.mylibrary.wights.network.OkhttpSingleton;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by hongliJiang on 2019/4/19 11:10
 * 描述：上传功能
 */
public class PushUtils {
    public static void postFile(String url, final ProgressListener listener, Callback callback, File...files){

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Log.i("huang","files[0].getName()=="+files[0].getName());
        //第一个参数要与Servlet中的一致
        builder.addFormDataPart(Constants.PUSH_DYNAMIC_FILE,files[0].getName(), RequestBody.create(MediaType.parse("application/octet-stream"),files[0]));

        MultipartBody multipartBody = builder.build();

        Request request  = new Request.Builder().url(url).post(new ProgressRequestBody(multipartBody,listener)).build();
        OkhttpSingleton.getSingleton().getClient().newCall(request).enqueue(callback);
    }
}
