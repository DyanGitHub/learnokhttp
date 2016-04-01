package com.dy.learnokhttp.okhttp;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dyan on 2016/3/28.
 * 自己对原装okhttp学习，其中也有对鸿洋github上api的测试
 */

public class LearnBaseOkHttp {
	public static final String TAG="dy";
	public static final String WEBURL="http://api.zhongguoguzheng.com/api.php?op=dongtai";
	public static final String DOWNLOADURL="http://pic.zhongguoguzheng.com/qupu/123caicai/langtaosha1_88.gif";
	public static final int GET_FINISHED=101;
	public static final String POSTURL="http://api.zhongguoguzheng.com/api.php?op=login";
    public int kind=0;
	/**
	 * http请求模式
	 * @param handler 与主线程交互
	 * @param url    请求url
	 * @param kind   请求类型//0获取返回字符串 1获取返回的二进制数组 2获取返回的inputstream
	 */
	public static void testHttpGet(final Handler handler,String url, final int kind)
	{
		//		测试
		if(url==null)
		{
			switch (kind)
			{
				case 0:
					url=WEBURL;
					break;
//				case 1:
//					url=DOWNLOADURL;
//					break;
				case 2:
					url=DOWNLOADURL;
					break;
			}
		}
		httpGet(handler,url,kind);
	}

	public static  void httpGet(final Handler handler,final String url, final int kind)
	{
//		创建一个okhttpclient对象
		OkHttpClient mOkHttpClient=new OkHttpClient();
//		创建一个Request
		Request request=new Request.Builder().url(url).build();
//		创建一个任务
		Call call=mOkHttpClient.newCall(request);
//		请求加入调度 （实现异步的方式去执行请求--推荐） 也可直接执行excute（实现阻塞的方式去执行请求）
		Log.d(TAG,"mystart===========");
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Request request, IOException e) {
				Log.d(TAG, "获取数据失败");
			}

			@Override
			public void onResponse(Response response) throws IOException {
				switch (kind) {
					case 0:
						//获取返回的字符串
						String text = response.body().string();
						Log.d(TAG, "获取返回的字符串:" + text);
						Message msg = Message.obtain();
						msg.what = GET_FINISHED;
						Bundle bundle = new Bundle();
//				        bundle.putString("data",response.body().string());//再次获取是空的
						bundle.putString("data", text);
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					case 2:
						//可用于下载大文件
						InputStream is = response.body().byteStream();
						File file = new File(Environment.getExternalStorageDirectory() + "/" + url.substring(url.lastIndexOf("/") + 1));
						if (file.exists()) {
							file.delete();
						}
						file.createNewFile();

						FileOutputStream out = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int byteRead = 0;
						while ((byteRead = is.read(buffer)) > 0) {
							out.write(buffer, 0, byteRead);
						}
						out.close();
						Log.d(TAG, "myfinished===========");

						break;
					default:
						Log.d(TAG, "获取返回的二进制字节数组:" + response.body().bytes());
						break;

				}
			}
		});


	}
	public static void testHYGet()
	{
		Log.d(TAG, "hystart===========");
		String url=DOWNLOADURL;
		OkHttpUtils
				.get()
				.url(url)
				.build()//
				.execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "hy.mp3")//
				{


					@Override
					public void inProgress(float v, long l) {
//						Log.d(TAG,"当前进度："+v);
					}

					@Override
					public void onError(okhttp3.Call call, Exception e) {

					}

					@Override
					public void onResponse(File file) {
						Log.d(TAG, "hyfinished===========");
					}
				});

	}

	public static void testHYPost()
	{
		String url=POSTURL;
		OkHttpUtils
				.post()
				.url(url)
				.addParams("username", "dy1111")
				.addParams("password", "rrrr")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(okhttp3.Call call, Exception e) {

					}

					@Override
					public void onResponse(String s) {
						Log.d(TAG, "hypost:" + s);
					}
				});

	}
	public static void testHYPostFile()
	{
//		上传采用哪种方式还未确定
//		1、表单形式
//		OkHttpUtils.post()//
//				.addFile("mFile", "messenger_01.png", file)//
//				.addFile("mFile", "test1.txt", file2)//
//				.url(url)
//				.params(params)//
//				.headers(headers)//
//				.build()//
//				.execute(new MyStringCallback());
//       2、请求体的形式
//		OkHttpUtils
//				.postFile()
//				.url(url)
//				.file(file)
//				.build()
//				.execute(new MyStringCallback());
	}

    public static void testHttpPost(final Handler handler,final String url,HashMap<String,String> map)
	{
		if(map==null)
		{
			map=new HashMap<>();
			map.put("username","dy1111");
			map.put("password","rrrr");
		}

		httpPost(handler,url,map);
	}

	/**
	 * httpPost请求
	 * @param handler
	 * @param url
	 * @param map
	 */
    public static void httpPost(final Handler handler,final String url, HashMap<String,String> map)
	{
		FormEncodingBuilder builder=new FormEncodingBuilder();
		for(Map.Entry<String,String> entry:map.entrySet())
		{
			builder.add(entry.getKey(),entry.getValue());
		}
		Request request=new Request.Builder().url(POSTURL).post(builder.build()).build();
		OkHttpClient client=new OkHttpClient();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {

			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.d(TAG,response.body().string());
			}
		});
	}

	/**
	 * @param file 目标文件的绝对路径
	 */
	/*
	   上传文件
	 */
	public static void  postFile(File file ,String url)
	{

		RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
				.addPart(Headers.of(
								"Content-Disposition",
								"form-data; name=\"username\""),
						RequestBody.create(null, "张鸿洋"))
				.addPart(Headers.of(
						"Content-Disposition",
						"form-data; name=\"mFile\"; filename=\"wjd.mp4\""), fileBody)
				.build();
		Request request=new Request.Builder().url(url).post(requestBody).build();
		OkHttpClient client=new OkHttpClient();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.printStackTrace();
				Log.d(TAG,"上传失败");

			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.d(TAG,response.body().string());
			}
		});
	}

}
