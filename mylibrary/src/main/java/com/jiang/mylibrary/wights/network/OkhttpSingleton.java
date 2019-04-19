package com.jiang.mylibrary.wights.network;

import com.google.gson.GsonBuilder;
import com.jiang.mylibrary.BuildConfig;
import com.jiang.mylibrary.constans.NetConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongliJiang on 2019/4/19 11:40
 * 描述：配置okhttp
 */
public class OkhttpSingleton {
    private final OkHttpClient.Builder mBuilder;
    private final OkHttpClient mClient;
    private final Retrofit retrofit;
    private static volatile OkhttpSingleton mOkhttpSingleton;

    private OkhttpSingleton(){
        mBuilder = new OkHttpClient().newBuilder();
        // 设置连接、读、写超时
        mBuilder.connectTimeout(60, TimeUnit.SECONDS);
        mBuilder.readTimeout(60, TimeUnit.SECONDS);
        mBuilder.writeTimeout(60, TimeUnit.SECONDS);
        //错误重连
        mBuilder.retryOnConnectionFailure(true);
        // 加入自定义Interceptor 可以设置通用参数等
//        mBuilder.addInterceptor(new BaseInterceptor());
//        mBuilder.addInterceptor(new RedirectInterceptor());

        // Debug模式打印log
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mBuilder.addInterceptor(interceptor);
        }
        mClient = mBuilder.build();
        retrofit = new Retrofit.Builder().
                baseUrl(NetConstants.BASE_URL). //设置baseUrl
                addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()
        )).
                client(mClient).
                build();
    }
    // 单例
    public static OkhttpSingleton getSingleton(){
        if (mOkhttpSingleton==null){
            synchronized (OkhttpSingleton.class){
                if (mOkhttpSingleton==null){
                    mOkhttpSingleton = new OkhttpSingleton();
                }
            }
        }
        return mOkhttpSingleton;
    }

    public OkHttpClient getClient() {
        return mClient;
    }
}
