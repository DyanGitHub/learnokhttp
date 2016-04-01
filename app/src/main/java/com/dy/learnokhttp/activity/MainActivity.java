package com.dy.learnokhttp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.widget.TextView;

import com.dy.learnokhttp.R;
import com.dy.learnokhttp.okhttp.LearnBaseOkHttp;


public class MainActivity extends Activity {
    public TextView hello;
	public static final String TAG="dy";
	public Handler handler=new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what)
			{
				case LearnBaseOkHttp.GET_FINISHED :
//					Log.d(TAG,"handler收到的东西:"+msg.getData().getString("data"));
					hello.setText(Html.fromHtml(msg.getData().getString("data")));
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hello = (TextView) findViewById(R.id.hello);
//		LearnBaseOkHttp.testHttpGet(handler, null, 2);
//		LearnBaseOkHttp.testHYPost();
//		LearnBaseOkHttp.testHttpPost(null, null, null);
//		OkHttpUtil.httpGet("http://api.zhongguoguzheng.com/api.php?op=dongtai",this, new OkHttpUtil.CallbackUtil<String>() {
//			@Override
//			public void onAfter(String response) {
//				hello.setText(response);
//			}
//		});
		final Activity activity = this;
//        new Thread(){
//			@Override
//			public void run() {
//				OkHttpUtil.httpGet("http://api.zhongguoguzheng.com/api.php?op=dongtai", activity, new OkHttpUtil.CallbackUtil<String>() {
//					@Override
//					public void onAfter(String response) {
//						hello.setText(response);
//					}
//				});
//			}
//		}.start();



	}


}
