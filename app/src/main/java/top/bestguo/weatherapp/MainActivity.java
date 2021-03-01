package top.bestguo.weatherapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.widget.Toast;

import top.bestguo.weatherapp.adapter.ShowWeatherBy24Hours;
import top.bestguo.weatherapp.adapter.ShowWeatherBy7DaysAdapter;
import top.bestguo.weatherapp.adapter.TodayIndexAdapter;
import top.bestguo.weatherapp.network.RequestWeather;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView weather7Days, weather24Hours, todayIndex;

    // 气温
    private TextView temperature, tempMaxMin, weatherText,
            airStatus, lastUpdate, location, aqiLevel, aqiAffect, aqiMeasure,
            aqiPrimaryPollutant;
    // 图标温度
    private ImageView imageWeather;
    // 带进度的对话框
    private ProgressDialog progressDialog;
    // 获取位置服务
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        setContentView(R.layout.activity_main);
        // 7天的天气情况展示
        weather7Days = findViewById(R.id.weather_7days);
        weather7Days.setLayoutManager(new LinearLayoutManager(this));
        weather7Days.setAdapter(new ShowWeatherBy7DaysAdapter(this));

        // 24小时天气情况展示
        weather24Hours = findViewById(R.id.weather_24h);
        weather24Hours.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        weather24Hours.setAdapter(new ShowWeatherBy24Hours(this));
        weather24Hours.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int dp = getResources().getDimensionPixelOffset(R.dimen.margin);
                outRect.set(dp, 0, dp, 0);
            }
        });

        // 今日指数展示
        todayIndex = findViewById(R.id.today_index);
        todayIndex.setLayoutManager(new LinearLayoutManager(this));
        todayIndex.setAdapter(new TodayIndexAdapter(this));

        // 右上角菜单展示
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("BestGuo天气");
        toolbar.setTitleTextColor(Color.WHITE);
        // 使用 Java 代码设置样式
        toolbar.setPopupTheme(R.style.RightTop);
        // 设置 overflow 图标
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.touch));
        // 指定菜单栏
        toolbar.inflateMenu(R.menu.dropdown_menu);
        // 指定点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.city_manager:
                        intent = new Intent(MainActivity.this, CityManageActivity.class);
                        break;
                    case R.id.about:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("关于BestGuo天气")
                                .setMessage("BestGuo天气作为一个入门的程序已经有了python版、java" +
                                        "web版和Android版，如果喜欢我的项目，欢迎在github上给个Star\n\n" +
                                        "项目地址：https://github.com/BestGuo2020/weatherapp-for-android/")
                                .setPositiveButton("好嘞", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        break;
                    case R.id.flush:
                        MainActivity.this.onStart();
                        break;
                }
                if (intent != null)
                    startActivityForResult(intent, 1);
                return true;
            }
        });
        // 得到温度组件
        temperature = findViewById(R.id.temperature);
        // 得到最值气温
        tempMaxMin = findViewById(R.id.temp_max_min);
        // 天气文字
        weatherText = findViewById(R.id.weather_text);
        // 风力和湿度
        airStatus = findViewById(R.id.air_status);
        // 天气图标
        imageWeather = findViewById(R.id.image_weather);
        // 更新时间
        lastUpdate = findViewById(R.id.last_update);
        // 获取城市
        location = findViewById(R.id.location);
        // 获取空气质量等级
        aqiLevel = findViewById(R.id.aqi_level);
        // 获取空气质量满意度
        aqiAffect = findViewById(R.id.aqi_affect);
        // 获取空气质量适宜人群
        aqiMeasure = findViewById(R.id.aqi_measure);
        // 获取主要污染物
        aqiPrimaryPollutant = findViewById(R.id.aqi_primary_pollutant);

    }

    /**
     * 获取用户数据
     * @param cityName
     */
    private void getWeatherData(final String cityName) {

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("加载天气信息");
        progressDialog.setMessage("正在向服务器获取天气信息，请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                Gson gson = new Gson();
                Map resp = gson.fromJson(data.getString("resp"), Map.class);
                // 判断返回值状态
                int status = ((Double) resp.get("status")).intValue();
                if (status == 0) {
                    Map result = (Map) resp.get("result");
                    // 设置当前温度
                    temperature.setText((String) result.get("temp"));
                    // 设置最高和最低温度
                    tempMaxMin.setText(result.get("temphigh") + "℃ / " + result.get("templow") + "℃");
                    // 设置文字图片
                    weatherText.setText("天气：" + result.get("weather"));
                    // 设置湿度和风力
                    airStatus.setText("湿度：" + result.get("humidity") + "%，风力：" + result.get("winddirect") + result.get("windpower"));
                    // 加载天气图片
                    Glide.with(MainActivity.this).load("file:///android_asset/weathercn02/" + result.get("img") + ".png").into(imageWeather);
                    // 设置更新时间
                    lastUpdate.setText("更新时间：" + result.get("updatetime"));
                    // 设置城市位置
                    location.setText((String) result.get("city"));
                    // 设置 24 小时天气
                    List<Map<String, Object>> hourly = (List<Map<String, Object>>) result.get("hourly");
                    weather24Hours.setAdapter(new ShowWeatherBy24Hours(MainActivity.this, hourly));
                    // 设置未来 7 天天气
                    List<Map<String, Object>> daily = (List<Map<String, Object>>) result.get("daily");
                    weather7Days.setAdapter(new ShowWeatherBy7DaysAdapter(MainActivity.this, daily));
                    // 设置今日指数
                    List<Map<String, Object>> index = (List<Map<String, Object>>) result.get("index");
                    todayIndex.setAdapter(new TodayIndexAdapter(MainActivity.this, index));
                    // 设置空气质量相关信息
                    Map<String, Object> aqi = (Map<String, Object>) result.get("aqi");
                    Map<String, Object> aqiInfo = (Map<String, Object>) aqi.get("aqiinfo");
                    aqiLevel.setText((String) aqiInfo.get("level"));
                    aqiLevel.setTextColor(Color.parseColor((String) aqiInfo.get("color")));
                    String primaryPollutant = (String) aqi.get("primarypollutant");
                    // 设置空气质量的影响
                    aqiAffect.setText((String) aqiInfo.get("affect"));
                    // 设置空气质量适宜人群
                    aqiMeasure.setText("建议：" + aqiInfo.get("measure"));
                    // 设置首要污染物
                    aqiPrimaryPollutant.setText("主要污染物：" + aqi.get("primarypollutant"));
                    progressDialog.dismiss();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("出错啦");
                    builder.setMessage("天气APP出现了未知错误，正在抢救中！").show();
                }
            }
        };

        // 创建一个线程用于发送网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final RequestWeather requestWeather = new RequestWeather();
                    requestWeather.setAppCode("**********************");
                    String resp = requestWeather.sendRequest("city", cityName);
                    Log.d("resp", resp);
                    // 发送消息
                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString("resp", resp);
                    message.setData(data);
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    if(e instanceof SocketTimeoutException) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "访问超时，请点击右上角菜单栏重新刷新即可", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("---onStart---", "isRunning......");
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        // 获取数据
        String cityName = sharedPreferences.getString("cityName", "莲花县");
        getWeatherData(cityName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 判断返回码为OK
        if(resultCode == RESULT_OK){
            Log.d("---onActivityResult---", "isRunning......");
            // 由于 onActivityResult 比 onStart 先执行，所以先赋值
            Bundle bundle = data.getExtras();
            String cityName = bundle.getString("cityName");
            SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
            // 编辑
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cityName", cityName);
            // 将选中的城市保存起来
            editor.apply();
        }
    }
}
