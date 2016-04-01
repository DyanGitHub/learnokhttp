package com.dy.learnokhttp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.dy.learnokhttp.R;
import com.dy.learnokhttp.okhttp.GetCache;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*
   对okhttp中的缓存机制探索
 */
public class TestActivity extends ActionBarActivity {

    private final static String TAG = "TestActivity";

    private final OkHttpClient client = new OkHttpClient();
    public TextView show;
//    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
//        @Override public Response intercept(Chain chain) throws IOException {
//            Response originalResponse = chain.proceed(chain.request());
//            if (Utils.isNetworkAvailable(context)) {
//                int maxAge = 60; // 在线缓存在1分钟内可读取
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", "public, max-age=" + maxAge)
//                        .build();
//            } else {
//                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                        .build();
//            }
//        }};
    Activity activity;
    public void again()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    execute();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        show= (TextView) findViewById(R.id.show);
        activity=this;
        findViewById(R.id.again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                again();
            }
        });
        //拦截器只负责和服务器的交互和本地缓存有效性无直接关系
        Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (GetCache.Utils.isNetworkAvailable(activity)) {
                int maxAge = 60; // 在线缓存在1分钟内可读取
                System.out.println("在线缓存在1分钟内可读取");
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
//                这段不会被执行。。。。
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                System.out.println("离线时缓存保存4周");
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }};
        client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        File sdcache =getExternalCacheDir();
        System.out.println("缓存保存地址：" + sdcache.getAbsolutePath().toString());
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        client.setCache(new Cache(getExternalCacheDir().getAbsoluteFile(), cacheSize));
        again();


//        try {
//            String test= GetCache.getFromCache("http://publicobject.com/helloworld.txt");
//            System.out.println("本地缓存获取结果："+test);
//        } catch (Exception e) {
//            System.out.println("获取本地缓存失败:"+e.getMessage());
//        }
    }
    public  void execute0() throws Exception {
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

    }
    public  void execute() throws Exception {
        Request request = new Request.Builder()
                .url("http://api.k780.com:88/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json")
                .build();
//        request.newBuilder().cacheControl(new CacheControl.Builder().maxAge()).build();
        if(GetCache.Utils.isNetworkAvailable(activity))
        {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
            System.out.println("走网络请求");
        }
        else
        {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        }

        Response response1 = client.newCall(request).execute();
        if (!response1.isSuccessful())
        {System.out.println("error: " + response1);}

        String response1Body = response1.body().string();
        System.out.println("Response 1 response:          " + response1);
        System.out.println("Response 1 response:          " + response1.code());//只要有数据，不论缓存还是网络code为200
        System.out.println("Response 1 cache response:    " + response1.cacheResponse());
        System.out.println("Response 1 network response:  " + response1.networkResponse());

//        OkHttpClient client1 = new OkHttpClient();
//        System.out.println("is client equals client1:"+(client==client1));
//        Response response2 = client1.newCall(request).execute();
        //1、验证必须是用一个client对象才能保证缓存有效
        Response response2 = client.newCall(request).execute();
        if (!response2.isSuccessful()) throw new IOException("Unexpected code " + response2);

        final String response2Body = response2.body().string();
        System.out.println("Response 2 response:          " + response2);
        System.out.println("Response 2 cache response:    " + response2.cacheResponse());
        System.out.println("Response 2 network response:  " + response2.networkResponse());

        System.out.println("Response 2 equals Response 1? " + response1Body.equals(response2Body));
        show.post(new Runnable() {
            @Override
            public void run() {
               show.setText(response2Body);
            }
        });
    }
    public void execute1() throws Exception {
        Request request = new Request.Builder()
                .url("http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0106/2275.html")
                .build();
        request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        Response response1 = client.newCall(request).execute();
        if (!response1.isSuccessful())
        {
            System.out.println("error:" + response1);
            throw new IOException("Unexpected code " + response1);
        }

        String response1Body = response1.body().string();
        System.out.println("Response 1 response:          " + response1);
        System.out.println("Response 1 cache response:    " + response1.cacheResponse());
        System.out.println("Response 1 network response:  " + response1.networkResponse());

//       2、 验证可以进行request设置来灵活改变缓存加载机制
        request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
//       3、尚未验证可以灵活改变缓存有效时间,考虑到“同一个缓存目录有多个缓存访问时错误的”的说明
        Response response2 = client.newCall(request).execute();
        if (!response2.isSuccessful()) throw new IOException("Unexpected code " + response2);
        String response2Body = response2.body().string();
//        show.setText(response2Body);
        System.out.println("Response 2 response:          " + response2);
        System.out.println("Response 2 cache response:    " + response2.cacheResponse());
        System.out.println("Response 2 network response:  " + response2.networkResponse());

//        System.out.println("Response 2 equals Response 1? " + response1Body.equals(response2Body));

    }
//   4、验证 excute换成enqueue,缓存同样生效
    public void execute2() throws Exception {
        Request request = new Request.Builder()
                .url("http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0106/2275.html")
                .build();
        request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("Response  ioexception  " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {

                if (!response.isSuccessful())
                {
                    onFailure(null,new IOException("Unexpected code " + response));
                }


                System.out.println("Response  response:          " + response);
                System.out.println("Response  cache response:    " + response.cacheResponse());
                System.out.println("Response  network response:  " + response.networkResponse());

            }
        });
    }
    //    验证鸿洋框架是否支持缓存--okhttp3暂没找设置缓存的方法，
    //    验证鸿洋框架中的client不是同一个client
    public void testHYClient() {
        OkHttpUtils.getInstance().setConnectTimeout(100000, TimeUnit.MILLISECONDS);
        okhttp3.OkHttpClient one = OkHttpUtils.getInstance().getOkHttpClient();
        okhttp3.OkHttpClient two = OkHttpUtils.getInstance().getOkHttpClient();
        System.out.println("鸿洋系统中的client是否是同一个client：" + (one == two));//one和two中间调用set会false ，方向一致为true，因为setconnectiontimeout这些都做了重构，所以要放在application中。
        System.out.println("clone与本身是否一致:"+(client==client.clone()));//不一致
    }




}