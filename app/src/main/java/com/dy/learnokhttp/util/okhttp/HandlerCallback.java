package com.guzheng.android.util.okhttp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

/**
 * 作者： Dyan on 2016/4/11 10:04
 * 描述：
 */
public class HandlerCallback implements Callback {
	public static final String TAG="dy";
	public static final int CALL_SUCCESS=200;
	public static final int CALL_FAILED=400;
	public static final int CALL_PROGRESS=300;

    public HandlerCallback(OkHttpUtils.CallbackUI callback)
	{
		mHandler=new UIHandler(callback);
	}

	@Override
	public void onFailure(Request request, IOException e) {
		sendFailedMessage(e);
	}
	public static void httpError(Exception e) {
		//此处可以放统一的提示和监控
//		Log.e(TAG, e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onResponse(final Response response) throws IOException {
		sendSuccMessage(response);
	}
	public void onProgress(float progress, long total,File file) {
		//progress 0-1
		sendProMessage(progress,total,file);

	}
	public  class UIHandler extends Handler {
		private OkHttpUtils.CallbackUI callback;
		public UIHandler(OkHttpUtils.CallbackUI callback){
			super(Looper.getMainLooper());
			this.callback=callback;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what){
				case CALL_SUCCESS: {
					Response response= (Response) msg.obj;
					if (response!=null&&response.code()==200&&callback != null) {
						try {
							callback.onResponse(response);
						} catch (Exception e) {
							sendFailedMessage(e);
						}
					}
					else
					{
						IOException e=new IOException("response为null或者code不等于200");
						sendFailedMessage(e);
					}
					break;
				}
				case CALL_FAILED: {
					Exception e = (Exception) msg.obj;
					if (callback != null) {
						httpError(e);//打印或者提示错误
						callback.onFailure(e);
					}
					break;
				}
				case CALL_PROGRESS: {
					Bundle bundle=msg.getData();
					float progress=bundle.getFloat("progress");
					long total =bundle.getLong("total");
					File file = (File) bundle.getSerializable("file");
					if (callback != null) {
						callback.onProgress(progress,total,file);
					}
					break;
				}
				default:
					super.handleMessage(msg);
					break;
			}
		}
	}
	private  UIHandler mHandler;
	public  void sendFailedMessage(Exception e)
	{
		Message msg= Message.obtain();
		msg.what=CALL_FAILED;
		msg.obj=e;
		mHandler.sendMessage(msg);
	}
	public  void sendSuccMessage(Response response)
	{
		Message msg= Message.obtain();
		msg.what=CALL_SUCCESS;
		msg.obj=response;
		mHandler.sendMessage(msg);
	}
	public  void sendProMessage(float progress, long total,File file )
	{
		Message msg= Message.obtain();
		msg.what=CALL_PROGRESS;
		Bundle bundle=new Bundle();
		bundle.putFloat("progress",progress);
		bundle.putLong("total", total);
		bundle.putSerializable("file", file);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

}
