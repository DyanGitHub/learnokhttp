package com.dy.learnokhttp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.dy.learnokhttp.R;
import com.dy.learnokhttp.util.gson.testbean.TVViewBean;
import com.dy.learnokhttp.util.okhttp.CacheType;
import com.dy.learnokhttp.util.okhttp.OkHttpUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 作者： gzw020 on 2016/4/1 11:29
 * 测试Gson的接口解析完成。
 * Gson解析格式：
 * Gson gson=new Gson();
 * TVViewBean bean=gson.fromJson(result, TVViewBean.class);
 */
public class MainGsonActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		OkHttpUtils.requestGet(url,callbackString, CacheType.CACHED_THEN_NETWORK);
	}
	public final static String url="http://api.zhongguoguzheng.com/api.php?op=show&catid=489&id=1452";
	public final static String TAG="dy";
	public OkHttpUtils.CallbackUI callbackString=new OkHttpUtils.CallbackUI()
	{
		@Override
		public void onFailure(IOException e) {
		}

		@Override
		public void onResponse(Response response) throws IOException {
			if(response==null)
				return;
			String result =response.body().string();
			if(result==null|| TextUtils.isEmpty(result))
				return;
			Log.d(TAG,"response:"+result);
			Gson gson=new Gson();
			TVViewBean bean=gson.fromJson(result, TVViewBean.class);
			Method[] methods=bean.getList()[1].getClass().getMethods();
			for (Method method : methods) {
				String mName = method.getName();
				if (mName.startsWith("get") && !mName.startsWith("getClass")) {
					String fieldName = mName.substring(3, mName.length());
					//mList.add(fieldName);
					//System.out.println("字段名字----->" + fieldName);
					try {
						Object value = method.invoke(bean.getList()[1], null);
						Log.d(TAG,fieldName+":"+value);
						//System.out.println("执行方法返回的值：" + value);
//						if (value instanceof String) {
//							vList.add("\"" + value + "\"");
//							System.out.println("字段值------>" + value);
//						} else {
//							vList.add(value);
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};


}
