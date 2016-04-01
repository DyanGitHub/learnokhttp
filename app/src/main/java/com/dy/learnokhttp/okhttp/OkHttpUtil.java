package com.dy.learnokhttp.okhttp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dy.learnokhttp.util.okhttp.CacheType;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;



/**
 * Created by Dyan on 2016/3/29.
 * 对外
 * 20160330 完成get\post出String的模块
 */
public class OkHttpUtil {
	public static final OkHttpClient client=new OkHttpClient();
	public static final int SIZE_OF_CACHE=10 * 1024 * 1024; // 10 MiB
	public static File CACHE_DIRECTORY;
    /*
    	设置，在application中调用
    	 */
	public static void configClient(Context context)
	{
		try {
			CACHE_DIRECTORY = context.getExternalCacheDir().getAbsoluteFile();
			Log.d(TAG, context.getExternalCacheDir().getAbsoluteFile().toString());
			client.setCache(new Cache(CACHE_DIRECTORY, SIZE_OF_CACHE));//设置缓存
			client.setConnectTimeout(30, TimeUnit.SECONDS);//设置连接超时,超时会在onfailed中响应
		}catch (Exception e)
		{
			Log.e(TAG,"缓存及网络相关初始化设置失败");
			return;
		}

	}

    /*
         Get请求的response返回的结果有三种：字符串、字节数组、inputstream
     */
//	1、获取json数据到本地(考虑缓存问题)
	public static void requestString(String url, CallbackString callbackString,int cacheType)
	{
		if (url==null||callbackString==null)
			return;
		Request request=new Request.Builder().url(url).build();
		responseFrom(url,cacheType,request,callbackString);
	}
	public static void requestString(String url,int maxAgeSec,int maxStale, CallbackString callbackString)
	{
		if (url==null||callbackString==null)
			return;
		Request request = new Request.Builder().url(url).cacheControl(new CacheControl.Builder().maxAge(5, TimeUnit.SECONDS)
				.maxStale(5, TimeUnit.SECONDS).build()).build();
		responseFrom(url, CacheType.ONLY_NETWORK,request,callbackString);

	}
	public static void responseFrom(final String url,int cacheType,final Request request,final CallbackString callbackString)
	{
		switch (cacheType)
		{
			case CacheType.ONLY_NETWORK:
				responseFromNetwork(request,callbackString);
				break;
			case CacheType.ONLY_CACHED:
				responseFromCache(url, callbackString);
				break;
			case CacheType.CACHED_ELSE_NETWORK:
				responseFromCache(url, new CallbackString() {
					@Override
					public void onResponse(String response) throws IOException {
						if (response == null) {
							responseFromNetwork(request, callbackString);
						} else {
							callbackString.onResponse(response);
						}
					}

					@Override
					public void onFailure(Exception e) {
						responseFromNetwork(request, callbackString);
					}
				});
				break;
			case CacheType.NETWORK_ELSE_CACHED:
				responseFromNetwork(request, new CallbackString() {
					@Override
					public void onFailure(Exception e) {
						responseFromCache(url, callbackString);
					}

					@Override
					public void onResponse(Response response) throws IOException {
						if(response.code()==200){
							callbackString.onResponse(response);
						}else{
							responseFromCache(url, callbackString);
						}
					}
				});
				break;
			case CacheType.CACHED_THEN_NETWORK:
				responseFromCache(url,callbackString);
				responseFromNetwork(request, new CallbackString() {
					@Override
					public void onFailure(Exception e) {
					}

					@Override
					public void onResponse(Response response) throws IOException {
						if (response.code() == 200) {
							callbackString.onResponse(response);
						}
					}
				});
				break;
		}
	}
	public static void responseFromNetwork(Request request,CallbackString callbackString)
	{
		if(callbackString!=null)
		client.newCall(request).enqueue(callbackString);
	}
	public static void responseFromCache(String url,CallbackString callbackString)
	{
		try {
			String response=GetCache.getFromCache(url);
			callbackString.onResponse(response);
		}catch (Exception e)
		{
			callbackString.onFailure(e);
		}
	}


//	2、下载文件到本地
	public static void httpStream()
	{

	}

	static Handler handler=new Handler(Looper.getMainLooper());
	public static  class CallbackString implements Callback
	{

		@Override
		public void onFailure(Request request, IOException e) {
			onFailure(e);
		}
		public void onFailure(final Exception e) {
			httpError(e);
		}
		@Override
		public void onResponse(final Response response) throws IOException {
			onResponse(response.body().string());
		}
		public void onResponse(final String response) throws IOException {
			//重写这个即可
		}
	}

    public static void httpError(Exception e)
	{
		e.printStackTrace();
		Log.e(TAG,e.getMessage());
	}
	public static final String TAG="DY";


}
