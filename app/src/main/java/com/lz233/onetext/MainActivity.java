package com.lz233.onetext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.tools.FileUtils;
import com.lz233.onetext.tools.SaveBitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import pub.devrel.easypermissions.EasyPermissions;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.AbstractCrashesListener;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.crashes.model.ErrorReport;
import com.microsoft.appcenter.utils.async.AppCenterConsumer;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.zqc.opencc.android.lib.ChineseConverter;

import static com.zqc.opencc.android.lib.ConversionType.S2HK;
import static com.zqc.opencc.android.lib.ConversionType.S2T;
import static com.zqc.opencc.android.lib.ConversionType.S2TW;
import static com.zqc.opencc.android.lib.ConversionType.S2TWP;
import static com.zqc.opencc.android.lib.ConversionType.T2S;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    private int onetext_code;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private LinearLayout pic_layout;
    private TextView onetext_quote1_textview;
    private TextView onetext_text_textview;
    private TextView onetext_quote2_textview;
    private TextView onetext_by_textview;
    private TextView onetext_from_textview;
    private TextView onetext_time_textview;
    private ImageView seal_imageview;
    private LinearLayout request_permissions_layout;
    private AppCompatButton request_permissions_button;
    private AppCompatButton save_button;
    private AppCompatButton refresh_button;
    private AppCompatButton seal_button;
    private IndicatorSeekBar onetext_text_size_seekbar;
    private AppCompatButton onetext_text_size_button;
    private IndicatorSeekBar onetext_by_size_seekbar;
    private AppCompatButton onetext_by_size_button;
    private IndicatorSeekBar onetext_time_size_seekbar;
    private AppCompatButton onetext_time_size_button;
    private IndicatorSeekBar onetext_from_size_seekbar;
    private AppCompatButton onetext_from_size_button;
    /*static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sp
        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getString("interface_style","md2").equals("default")){
            setContentView(R.layout.activity_main);
        }else if(sharedPreferences.getString("interface_style","md2").equals("md2")){
            setContentView(R.layout.activity_main_2);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //fb
        pic_layout = findViewById(R.id.pic_layout);
        progressBar = findViewById(R.id.progressBar);
        onetext_quote1_textview = findViewById(R.id.onetext_quote1_textview);
        onetext_text_textview = findViewById(R.id.onetext_text_textview);
        onetext_quote2_textview = findViewById(R.id.onetext_quote2_textview);
        onetext_by_textview = findViewById(R.id.onetext_by_textview);
        onetext_from_textview = findViewById(R.id.onetext_from_textview);
        onetext_time_textview = findViewById(R.id.onetext_time_textview);
        seal_imageview=findViewById(R.id.seal_imageview);
        request_permissions_layout = findViewById(R.id.request_permissions_layout);
        request_permissions_button = findViewById(R.id.request_permissions_button);
        save_button = findViewById(R.id.save_button);
        refresh_button = findViewById(R.id.refresh_button);
        seal_button = findViewById(R.id.seal_button);
        onetext_text_size_seekbar = findViewById(R.id.onetext_text_size_seekbar);
        onetext_text_size_button = findViewById(R.id.onetext_text_size_button);
        onetext_by_size_seekbar = findViewById(R.id.onetext_by_size_seekbar);
        onetext_by_size_button = findViewById(R.id.onetext_by_size_button);
        onetext_time_size_seekbar = findViewById(R.id.onetext_time_size_seekbar);
        onetext_time_size_button = findViewById(R.id.onetext_time_size_button);
        onetext_from_size_seekbar = findViewById(R.id.onetext_from_size_seekbar);
        onetext_from_size_button = findViewById(R.id.onetext_from_size_button);
        //申请权限
        final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions);
        //初始化
        AppCenter.start(getApplication(), "2bd0575c-79d2-45d9-97f3-95e6a81e34e0", Analytics.class, Crashes.class);
        AppCenterFuture<ErrorReport> future = Crashes.getLastSessionCrashReport();
        future.thenAccept(new AppCenterConsumer<ErrorReport>() {
            @Override
            public void accept(ErrorReport errorReport) {
                try {
                    //Toast.makeText(MainActivity.this,errorReport.getStackTrace(),Toast.LENGTH_SHORT).show();
                    Snackbar.make(getWindow().getDecorView(), errorReport.getStackTrace(), Snackbar.LENGTH_LONG).setAction(R.string.report_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent().setClass(MainActivity.this, CrashReportActivity.class));
                        }
                    }).setActionTextColor(getResources().getColor(R.color.colorText2)).show();
                }catch (Exception e){
                }
            }
        });
        //Analytics.trackEvent("My custom event");
        //Crashes.generateTestCrash();
        switch (sharedPreferences.getInt("interface_daynight",0)){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
        onetext_quote1_textview.setTextSize(sharedPreferences.getInt("onetext_text_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_text_textview.setTextSize(sharedPreferences.getInt("onetext_text_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_quote2_textview.setTextSize(sharedPreferences.getInt("onetext_text_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_by_textview.setTextSize(sharedPreferences.getInt("onetext_by_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.text_size))));
        onetext_time_textview.setTextSize(sharedPreferences.getInt("onetext_time_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_from_textview.setTextSize(sharedPreferences.getInt("onetext_from_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        if(sharedPreferences.getBoolean("seal_enabled",false)){
            seal_button.setText(R.string.seal_button_ison);
            seal_imageview.setVisibility(View.VISIBLE);
        }else {
            seal_button.setText(R.string.seal_button_isoff);
            seal_imageview.setVisibility(View.GONE);
        }
        onetext_text_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_text_size_text)+" ${PROGRESS} SP");
        onetext_by_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_by_size_text)+" ${PROGRESS} SP");
        onetext_time_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_time_size_text)+" ${PROGRESS} SP");
        onetext_from_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_from_size_text)+" ${PROGRESS} SP");
        onetext_text_size_seekbar.setProgress(sharedPreferences.getInt("onetext_text_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_by_size_seekbar.setProgress(sharedPreferences.getInt("onetext_by_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.text_size))));
        onetext_time_size_seekbar.setProgress(sharedPreferences.getInt("onetext_time_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_from_size_seekbar.setProgress(sharedPreferences.getInt("onetext_from_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        //监听器
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EasyPermissions.hasPermissions(MainActivity.this, permissions)){
                    String pic_file_name = "OneText "+System.currentTimeMillis()+".jpg";
                    String pic_file_path = Environment.getExternalStorageDirectory()+"/Pictures/OneText/";
                    if(!FileUtils.isDirectory(pic_file_path)) {
                        File file = new File(pic_file_path);
                        file.mkdirs();
                    }
                    Boolean isSuccess = SaveBitmap.saveBitmapToSdCard(MainActivity.this,SaveBitmap.getCacheBitmapFromView(pic_layout), pic_file_path+pic_file_name);
                    if(isSuccess) {
                        Snackbar.make(view, getString(R.string.save_succeed)+" "+pic_file_name, Snackbar.LENGTH_SHORT).show();
                    }else {
                        Snackbar.make(view, getString(R.string.save_fail), Snackbar.LENGTH_SHORT).show();
                    }
                }else {
                    Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                run(true);
                Analytics.trackEvent("Refreshed");
            }
        });
        seal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedPreferences.getBoolean("seal_enabled",false)){
                    seal_imageview.setVisibility(View.GONE);
                    editor.putBoolean("seal_enabled",false);
                    editor.apply();
                    seal_button.setText(R.string.seal_button_isoff);
                }else {
                    seal_imageview.setVisibility(View.VISIBLE);
                    editor.putBoolean("seal_enabled",true);
                    editor.apply();
                    seal_button.setText(R.string.seal_button_ison);
                }
            }
        });
        onetext_text_size_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onetext_quote1_textview.setTextSize(seekParams.progress);
                onetext_text_textview.setTextSize(seekParams.progress);
                onetext_quote2_textview.setTextSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("onetext_text_size",seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_text_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.onetext_size));
                onetext_quote1_textview.setTextSize(text_size);
                onetext_text_textview.setTextSize(text_size);
                onetext_quote2_textview.setTextSize(text_size);
                onetext_text_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_text_size",text_size);
                editor.apply();
            }
        });
        onetext_by_size_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onetext_by_textview.setTextSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("onetext_by_size",seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_by_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.text_size));
                onetext_by_textview.setTextSize(text_size);
                onetext_by_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_by_size",text_size);
                editor.apply();
            }
        });
        onetext_time_size_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onetext_time_textview.setTextSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("onetext_time_size",seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_time_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.small_text_size));
                onetext_time_textview.setTextSize(text_size);
                onetext_time_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_time_size",text_size);
                editor.apply();
            }
        });
        onetext_from_size_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onetext_from_textview.setTextSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("onetext_from_size",seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_from_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.small_text_size));
                onetext_from_textview.setTextSize(text_size);
                onetext_from_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_from_size",text_size);
                editor.apply();
            }
        });
    }
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        run(false);
        //Toast.makeText(MainActivity.this,"test",Toast.LENGTH_SHORT).show();
    }
    public void run(final Boolean forcedRefresh) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Random random = new Random();
                    String feed_type = sharedPreferences.getString("feed_type","remote");
                    long currentTimeMillis = System.currentTimeMillis();
                    boolean shouldUpdate = false;
                    if((currentTimeMillis-sharedPreferences.getLong("feed_latest_refresh_time",0))>(sharedPreferences.getLong("feed_refresh_time",1)*3600000)){
                        if(feed_type.equals("remote")) {
                            if(!FileUtils.isFile(getFilesDir().getPath()+"/OneText/OneText-Library.json")){
                                progressBar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });
                                FileUtils.downLoadFileFromURL(sharedPreferences.getString("feed_URL","https://github.com/lz233/OneText-Library/raw/master/OneText-Library.json"),getFilesDir().getPath()+"/OneText/","OneText-Library.json",true);
                            } else{
                                shouldUpdate = true;
                            }
                        }
                        editor.putLong("feed_latest_refresh_time",currentTimeMillis);
                        //editor.commit();
                    }
                    JSONArray jsonArray = null;
                    if(feed_type.equals("remote")) {
                        jsonArray = new JSONArray(FileUtils.readTextFromFile(getFilesDir().getPath()+"/OneText/OneText-Library.json"));
                    }
                    if(feed_type.equals("local")) {
                        jsonArray = new JSONArray(FileUtils.readTextFromFile(sharedPreferences.getString("feed_local_path",getFilesDir().getPath()+"/OneText/OneText-Library.json")));
                    }
                    if(((((currentTimeMillis-sharedPreferences.getLong("widget_latest_refresh_time",0))>(sharedPreferences.getLong("widget_refresh_time",30)*60000))&(!sharedPreferences.getBoolean("widget_enabled",false)))|(sharedPreferences.getInt("onetext_code",-1) == -1))|(forcedRefresh)){
                        onetext_code = random.nextInt(jsonArray.length());
                        editor.putInt("onetext_code",onetext_code);
                        editor.putLong("widget_latest_refresh_time",currentTimeMillis);
                        //editor.apply();
                    }else {
                        onetext_code = sharedPreferences.getInt("onetext_code", random.nextInt(jsonArray.length()));
                    }
                    editor.apply();
                    String language =Locale.getDefault().getLanguage();
                    String country =Locale.getDefault().getCountry();
                    JSONObject jsonObject = new JSONObject(jsonArray.optString(onetext_code));
                    String text;
                    String by;
                    String from;
                    if(language.equals("zh")&country.equals("CN")) {
                        text = jsonObject.optString("text");
                        by = jsonObject.optString("by");
                        from = jsonObject.optString("from");
                    }else if(language.equals("zh")&country.equals("HK")) {
                        text = ChineseConverter.convert(jsonObject.optString("text"), S2HK, MainActivity.this);
                        by = ChineseConverter.convert(jsonObject.optString("by"),S2HK,MainActivity.this);
                        from = ChineseConverter.convert(jsonObject.optString("from"),S2HK,MainActivity.this);
                    }else if(language.equals("zh")&country.equals("MO")){
                        text = ChineseConverter.convert(jsonObject.optString("text"), S2T, MainActivity.this);
                        by = ChineseConverter.convert(jsonObject.optString("by"),S2T,MainActivity.this);
                        from = ChineseConverter.convert(jsonObject.optString("from"),S2T,MainActivity.this);
                    }else if(language.equals("zh")&country.equals("TW")){
                        text = ChineseConverter.convert(jsonObject.optString("text"), S2TWP, MainActivity.this);
                        by = ChineseConverter.convert(jsonObject.optString("by"),S2TWP,MainActivity.this);
                        from = ChineseConverter.convert(jsonObject.optString("from"),S2TWP,MainActivity.this);
                    }else {
                        text = jsonObject.optString("text");
                        by = jsonObject.optString("by");
                        from = jsonObject.optString("from");
                    }
                    final String final_text = text;
                    final String final_by = by;
                    final String final_from = from;
                    JSONArray timeJsonArray = new JSONArray(jsonObject.optString("time"));
                    final String timeOfRecord = timeJsonArray.optString(0);
                    final String timeOfCreation = timeJsonArray.optString(1);
                    final String time;
                    if (!timeOfCreation.equals("")) {
                        time = getString(R.string.record_time)+"："+timeOfRecord+" "+getString(R.string.created_time)+"："+timeOfCreation;
                    }else {
                        time = getString(R.string.record_time)+"："+timeOfRecord;
                    }
                    onetext_text_textview.post(new Runnable() {
                        @Override
                        public void run() {
                            //progressBar.setVisibility(View.GONE);
                            onetext_text_textview.setText(final_text);
                            if(!final_by.equals("")) {
                                onetext_by_textview.setText("—— "+final_by);
                                onetext_by_textview.setVisibility(View.VISIBLE);
                            }else {
                                onetext_by_textview.setVisibility(View.GONE);
                            }
                            if(!final_from.equals("")) {
                                onetext_from_textview.setText(final_from);
                                onetext_from_textview.setVisibility(View.VISIBLE);
                            }else {
                                onetext_from_textview.setVisibility(View.GONE);
                            }
                            onetext_time_textview.setText(time);
                        }
                    });
                    //更新小部件
                    Intent intent = new Intent("com.lz233.onetext.widget");
                    intent.setPackage(getPackageName());
                    MainActivity.this.sendBroadcast(intent);
                    if(shouldUpdate) {
                        runUpdate();
                    }else {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void runUpdate () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                FileUtils.downLoadFileFromURL(sharedPreferences.getString("feed_URL","https://github.com/lz233/OneText-Library/raw/master/OneText-Library.json"),getFilesDir().getPath()+"/OneText/","OneText-Library.json",true);
                editor.remove("widget_request_download");
                editor.apply();
                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        startActivity(new Intent().setClass(MainActivity.this, SettingActivity.class));
        return true;
        }

        return super.onOptionsItemSelected(item);
        }
    public void requestPermissions(final String[] permissions){
        //申请权限
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            //Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
            request_permissions_layout.setVisibility(View.GONE);
        } else {
            //没有打开相关权限、申请权限
            request_permissions_layout.setVisibility(View.VISIBLE);
            request_permissions_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EasyPermissions.requestPermissions(MainActivity.this,getString(R.string.request_permissions_text), 1, permissions);
                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //下面两个方法是实现EasyPermissions的EasyPermissions.PermissionCallbacks接口
    //分别返回授权成功和失败的权限
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Log.i(TAG, "获取成功的权限" + perms);
        request_permissions_layout.setVisibility(View.GONE);
    }

    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //Log.i(TAG, "获取失败的权限" + perms);
    }
}
