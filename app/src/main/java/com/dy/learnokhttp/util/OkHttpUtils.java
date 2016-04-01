package com.dy.learnokhttp.util;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dyan on 2016/3/31.
 * 封装okhttp框架提供的api
 * get（是否缓存）\post\下载均完成测试0331
 *
 * 上传待调试
 */
public class OkHttpUtils {

	public static final OkHttpClient client = new OkHttpClient();

	public static int SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
	public static File CACHE_DIRECTORY;
	private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Response originalResponse = chain.proceed(chain.request());
			int maxAge = 60; // 缓存在1分钟内可读取
			return originalResponse.newBuilder()
					.header("Cache-Control", "public, max-age=" + maxAge)
					.build();
		}
	};

	/*
		设置，在application中调用
		 */
	public static void configClient(Context context) {
		CACHE_DIRECTORY = context.getExternalCacheDir().getAbsoluteFile();
		configClient(CACHE_DIRECTORY, SIZE_OF_CACHE, 30);
	}

	public static void configClient(File cacheDir, int cacheSize, int connectTimeOutSec) {
		client.setCache(new Cache(cacheDir, cacheSize));//设置缓存
		client.setConnectTimeout(connectTimeOutSec, TimeUnit.SECONDS);//设置连接超时,超时会在onfailed中响应
		client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);//设置与服务器间的缓存请求
	}

	/*
		 Get请求的response返回的结果有三种：字符串、字节数组、inputstream
	 */
//	1、获取json数据到本地(get/post,缓存)
	//一般get请求String返回
	public static void requestGet(String url, CallbackString callbackString, int cacheType) {
		if (url == null || callbackString == null)
			return;
		requestPost(url, callbackString, cacheType, null);
	}
    //一般Post请求String返回
	public static void requestPost(String url, final CallbackString callbackString, int cacheType,Map<String,String> map) {
		if (url == null || callbackString == null)
			return;
		final Request.Builder rBuilder=new Request.Builder();
		if(map!=null)
		{
			//Post
			FormEncodingBuilder builder=new FormEncodingBuilder();
			for(Map.Entry<String,String> entry:map.entrySet())
			{
				builder.add(entry.getKey(),entry.getValue());
			}
			rBuilder.post(builder.build());
		}
		rBuilder.url(url);
		switch (cacheType)
		{
			case CacheType.ONLY_NETWORK:
				responseFromNetwork(rBuilder,callbackString);
				break;
			case CacheType.ONLY_CACHED:
				responseFromCache(rBuilder, callbackString);
				break;
			case CacheType.CACHED_ELSE_NETWORK:
				responseFromCache(rBuilder, new CallbackString() {
					@Override
					public void onResponse(Response response) throws IOException {
						if (response.code() == 200) {
							callbackString.onResponse(response);
						} else {
							responseFromNetwork(rBuilder, callbackString);
						}
					}

					@Override
					public void onFailure(Request request, IOException e) {
						super.onFailure(request, e);
						responseFromNetwork(rBuilder, callbackString);
					}
				});
				break;
			case CacheType.NETWORK_ELSE_CACHED:
				responseFromNetwork(rBuilder, new CallbackString() {
					@Override
					public void onFailure(Request request, IOException e) {
						super.onFailure(request, e);//无网下网络请求时报异常是这里出的
						responseFromCache(rBuilder, callbackString);
					}

					@Override
					public void onResponse(Response response) throws IOException {
						if(response.code()==200){
							callbackString.onResponse(response);
						}else{
							responseFromCache(rBuilder, callbackString);
						}
					}
				});
				break;
			case CacheType.CACHED_THEN_NETWORK:
				responseFromCache(rBuilder,callbackString);
				responseFromNetwork(rBuilder, new CallbackString() {
					@Override
					public void onFailure(Request request, IOException e) {
						super.onFailure(request, e);
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

		public static void responseFromNetwork(Request.Builder rBuilder,CallbackString callbackString)
		{
			rBuilder.cacheControl(CacheControl.FORCE_NETWORK);
			client.newCall(rBuilder.build()).enqueue(callbackString);
		}
		public static void responseFromCache(Request.Builder rBuilder,CallbackString callbackString)
		{
			rBuilder.cacheControl(CacheControl.FORCE_CACHE);
			client.newCall(rBuilder.build()).enqueue(callbackString);
		}


		//	2、下载文件到本地
		public static void requestDownload(final String url ,final String  fileDir,final CallbackString callbackString)
		{
			if(url==null||callbackString==null||fileDir==null)
			return;
			Request request=new Request.Builder().url(url).build();
			client.newCall(request).enqueue(new CallbackString() {
				@Override
				public void onFailure(Request request, IOException e) {
					callbackString.onFailure(request,e);
				}

				@Override
				public void onResponse(Response response)  {
					if(response==null||response.code()!=200)
					{
						callbackString.onFailure(null,new IOException("response==null"));
					}
					saveFile(response,url,fileDir,callbackString);
				}
			});
		}
	public static void saveFile(Response response,String url,String fileDir,CallbackString callbackString)
	{
		InputStream is = null;
		byte[] buf = new byte[2048];
		int len = 0;
		FileOutputStream fos = null;
		try
		{
			is = response.body().byteStream();
			final long total = response.body().contentLength();
			long sum = 0;
			File dir = new File(fileDir);
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			File file = new File(dir, url.substring(url.lastIndexOf("/") + 1));
			Log.d(TAG,file.getAbsolutePath().toString());
			fos = new FileOutputStream(file);
			while ((len = is.read(buf)) != -1)
			{
				sum += len;
				fos.write(buf, 0, len);
				final long finalSum = sum;
				callbackString.onProgress(sum * 1.0f / total,total);
			}
			fos.close();
			is.close();
			callbackString.onResponse(response);
		}
		catch (IOException e)
		{
			callbackString.onFailure(null,e);
		}
		finally {
			if(is!=null)
			{
				try {
					is.close();
					is=null;
				} catch (IOException e) {
					callbackString.onFailure(null,e);
				}
			}
			if(fos!=null)
			{
				try {
					fos.close();
					fos=null;
				} catch (IOException e) {
					callbackString.onFailure(null, e);
				}
			}
		}

	}
    //	3、上传文件
	public static void requestUpdate()
	{
//参考以下，或者http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0106/2275.html
//		/*
//	   上传文件
//	 */
//		public static void  postFile(File file ,String url)
//		{
//
//			RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//			RequestBody requestBody = new MultipartBuilder()
//					.type(MultipartBuilder.FORM)
//					.addPart(Headers.of(
//									"Content-Disposition",
//									"form-data; name=\"username\""),
//							RequestBody.create(null, "张鸿洋"))
//					.addPart(Headers.of(
//							"Content-Disposition",
//							"form-data; name=\"mFile\"; filename=\"wjd.mp4\""), fileBody)
//					.build();
//			Request request=new Request.Builder().url(url).post(requestBody).build();
//			OkHttpClient client=new OkHttpClient();
//			client.newCall(request).enqueue(new Callback() {
//				@Override
//				public void onFailure(Request request, IOException e) {
//					e.printStackTrace();
//					Log.d(TAG,"上传失败");
//
//				}
//
//				@Override
//				public void onResponse(Response response) throws IOException {
//					Log.d(TAG,response.body().string());
//				}
//			});
//		}
	}


	public static class CallbackString implements Callback {
		@Override
		public void onFailure(Request request, IOException e) {
			httpError(e);
		}

		@Override
		public void onResponse(final Response response) throws IOException {
		}

		public void onProgress(float progress, long total) {
			//progress 0-1
		}

	}

	public static void httpError(Exception e) {
		//此处可以放统一的提示和监控
//		e.printStackTrace();调试时可以打印看下
		Log.e(TAG, e.getMessage());
	}

	public static final String TAG = "DY";

}
