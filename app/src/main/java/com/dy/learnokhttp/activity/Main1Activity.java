package com.dy.learnokhttp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dy.learnokhttp.R;
import com.dy.learnokhttp.util.okhttp.CacheType;
import com.dy.learnokhttp.util.okhttp.OkHttpUtils;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;

public class Main1Activity extends AppCompatActivity {
    private String TAG = Main1Activity.this.getClass().getSimpleName();

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;
    private TextView tv7;
    private RadioGroup type;
    private boolean isGet=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);



        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);

        type= (RadioGroup) findViewById(R.id.way);
        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.get)
                {
                    Log.d(TAG,"now is get");
                    isGet=true;
                }
                if(checkedId==R.id.post)
                {
                    Log.d(TAG,"now is post");
                    isGet=false;
                }
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.ONLY_NETWORK);
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.ONLY_CACHED);

            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.NETWORK_ELSE_CACHED);
            }
        });

        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.CACHED_ELSE_NETWORK);
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.CACHED_THEN_NETWORK);

            }
        });
        tv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OkHttpUtils.requestDownload(downloadUrl, getExternalCacheDir().getAbsolutePath() + "/mydownload", downCallback);

                } catch (Exception e) {
                    Log.e(TAG, "发送下载请求中，设置目标文件目录失败");
                }
            }
        });

    }
    private String downloadUrl="http://guzhengqu.zhongguoguzheng.com/gz111212/mp3/she2013/zyyyxygzkjqj-yiji-7ziyouhua.mp3";
    private String postUrl="http://api.zhongguoguzheng.com/api.php?op=login";
    private String url = "http://api.k780.com:88/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";


    private void getData( int cacheType){
        tv5.setText("");
        if(isGet)
        {
            OkHttpUtils.requestGet(url, jsonCallback, cacheType);
        }
        else
        {
            HashMap<String,String> map=new HashMap<>();
            map.put("username","dy1111");
            map.put("password","rrrr");
            OkHttpUtils.requestPost(postUrl,jsonCallback,cacheType,map);
        }
    }
    private OkHttpUtils.CallbackString jsonCallback = new OkHttpUtils.CallbackString() {

        @Override
        public void onFailure(com.squareup.okhttp.Request request, final IOException e) {
            super.onFailure(request, e);
            tv5.post(new Runnable() {
                @Override
                public void run() {
                    tv5.setText(e.toString());
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (response != null) {
               final String result=response.body().string();
                Log.d(TAG,"response:"+result);
                tv5.post(new Runnable() {
                    @Override
                    public void run() {
                            tv5.setText(result);
//                            tv7.setText(result);//实践证明可以加上其他ui的更新语句
                    }
                });
            }
        }
    };
    private OkHttpUtils.CallbackString downCallback = new OkHttpUtils.CallbackString() {

        @Override
        public void onFailure(com.squareup.okhttp.Request request, final IOException e) {
            super.onFailure(request, e);
            tv5.post(new Runnable() {
                @Override
                public void run() {
                    tv5.setText(e.toString());
                }
            });
        }

        @Override
        public void onProgress(float progress, long total) {
            super.onProgress(progress, total);
            Log.d(TAG,progress+":"+total);
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (response != null) {
                final String result=response.body().contentLength()+"b";
                Log.d(TAG,"response:"+response.body().contentLength()+"b");
                tv5.post(new Runnable() {
                    @Override
                    public void run() {
                        tv5.setText(result);
//                            tv7.setText(result);//实践证明可以加上其他ui的更新语句
                    }
                });
            }
        }
    };
}
