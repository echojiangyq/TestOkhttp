package jyq.testokhttp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScrollingActivity extends AppCompatActivity {

    OkHttpClient mOkHttpClient;
    final String URL_GET = "https://publicobject.com/helloworld.txt";
    final String URL_IMG = "http://oacisqzry.bkt.clouddn.com/rwby_backgroud.jpg";
    final String URL_12306 = "https://kyfw.12306.cn/otn/leftTicket/init";
    private static final String TAG = "ScrollingActivity";
    @Bind(R.id.tv_content)
    TextView tvContent;
    
    @Bind(R.id.iv_content)
    ImageView ivContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                testGetSync();
//                testGetASync();
//                OkhttpUtil.getAsyn(URL_GET, new OkhttpUtil.ResultCallback<String>() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        tvContent.setText(response);
//                    }
//                });

//                OkhttpUtil.displayImage(ivContent, URL_IMG ,R.drawable.ic_launcher);
                
                
                
                
//                OkhttpUtil.downloadAsyn(URL_IMG, getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), new OkhttpUtil.ResultCallback<String>() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(ScrollingActivity.this, "下载成功！", Toast.LENGTH_SHORT).show();
//                    }
//                });
                try {
                    OkhttpUtil.init(0, 0, getAssets().open("cer/12306.cer"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                OkhttpUtil.getAsyn(URL_12306, new OkhttpUtil.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        tvContent.setText(response);
                    }
                });
            }
        });

    }

    
    private void testGetSync(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                final Request request = new Request.Builder().url(URL_GET).build();
                try {
                    final Response response = mOkHttpClient.newCall(request).execute();
                    if(response.isSuccessful()){
                        Log.d(TAG, "testGetSync: " + response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    private void testGetASync(){
        final Request request = new Request.Builder().url(URL_GET).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.d(TAG, "testGetSync: " + response.body().string());
                    tvContent.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tvContent .setText(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
}
