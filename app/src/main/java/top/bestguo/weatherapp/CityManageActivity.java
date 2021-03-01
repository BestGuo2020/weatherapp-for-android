package top.bestguo.weatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.bestguo.weatherapp.adapter.AddedCityAdapter;
import top.bestguo.weatherapp.network.HttpClient;
import top.bestguo.weatherapp.network.RequestWeather;
import top.bestguo.weatherapp.utils.CollectionConvertUtils;

public class CityManageActivity extends AppCompatActivity {

    private TextView textView;
    private Toolbar toolbarWeatherManager;
    private RecyclerView addedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manage);

        textView = findViewById(R.id.add);
        // 设置点击事件，跳转到添加城市的页面
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CityManageActivity.this, AddCityActivity.class));
            }
        });

        // 后退到上一级
        toolbarWeatherManager = findViewById(R.id.toolbar_city_manager);
        toolbarWeatherManager.setNavigationIcon(R.drawable.touch_return);
        toolbarWeatherManager.setTitle("管理城市");
        toolbarWeatherManager.setTitleTextColor(Color.WHITE);

        // 已添加的城市
        addedCity = findViewById(R.id.added_city);
        addedCity.setLayoutManager(new LinearLayoutManager(this));
        addedCity.setAdapter(new AddedCityAdapter(this));

        setSupportActionBar(toolbarWeatherManager);
        getSupportActionBar();
    }

    /**
     * 获取天气的相关数据
     */
    private void getCityData() {

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // 获取消息
                Bundle bundle = msg.getData();
                String resp = bundle.getString("resp");
                // 转成可操作的集合类
                List<Map<String, Object>> data = new Gson().fromJson(resp, List.class);
                AddedCityAdapter cityAdapter = new AddedCityAdapter(CityManageActivity.this, data);
                // 设置短按监听事件
                cityAdapter.setItemOnClickListener(new AddedCityAdapter.ItemOnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 获取城市名
                        TextView cityName = view.findViewById(R.id.city_name);
                        // 传过来的数据返回
                        Intent intent = new Intent();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("cityName", cityName.getText().toString());
                        intent.putExtras(bundle1);
                        // 设置返回结果
                        setResult(RESULT_OK, intent);
                        // 结束当前界面
                        finish();
                    }
                });
                // 设置长按监听事件
                cityAdapter.setItemOnLongClickListener(new AddedCityAdapter.ItemOnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CityManageActivity.this);
                        builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                                Set<String> strings = preferences.getStringSet("cities", null);
                                // 找到城市名称
                                TextView cityName = view.findViewById(R.id.city_name);
                                // 删除
                                if(strings.remove(cityName.getText().toString())){
                                    Toast.makeText(CityManageActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    getCityData();
                                }
                            }
                        }).show();
                        return true;
                    }
                });
                addedCity.setAdapter(cityAdapter);
            }
        };

        // 创建一个线程用于发送网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 读取城市数据
                Set<String> cities = getSharedPreferences("data", MODE_PRIVATE).getStringSet("cities", null);
                Log.d("cities", cities + "");
                // 读取的数据是否为空
                if(cities != null) {
                    List<String> list = new ArrayList<>();
                    CollectionConvertUtils.convertSetToList(cities, list);
                    // 声明城市数据列表
                    List<Map<String, Object>> maps = new ArrayList<>();
                    // 每循环一次，发送一次请求
                    int length = list.size();
                    for (int i = 0; i < length; i++) {
                        // 创建请求对象
                        final RequestWeather requestWeather = new RequestWeather();
                        requestWeather.setAppCode("****************");
                        try {
                            String cityName = list.get(i);
                            // 发送请求
                            String resp = requestWeather.sendRequest("city", cityName);
                            Log.d("resp", resp);
                            // 解析数据
                            Gson gson = new Gson();
                            Map<String, Object> map2 = gson.fromJson(resp, Map.class);
                            // 判断数据是否存在
                            String status = map2.get("status").toString();
                            if (status.equals("0.0")) {
                                Map<String, Object> map = (Map<String, Object>) map2.get("result");
                                // 添加城市信息到 List 集合中
                                addCityToList(maps, map, i);
                                // 可修改城市名称
                                reviseCityNameAvail(cityName, map, cities);
                            } else {
                                // 城市名不存在删除城市
                                cities.remove(cityName);
                                addCityToList(maps, null, i);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if(e instanceof SocketTimeoutException) {
                                Toast.makeText(CityManageActivity.this, "访问超时，请进入或返回到任意界面重新打开", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    // 发送消息
                    Message message = new Message();
                    // 保存数据
                    Bundle data = new Bundle();
                    Gson gson = new Gson();
                    data.putString("resp", gson.toJson(maps));
                    message.setData(data);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 城市名称是否可纠正的方法
     *
     * @param cityName 城市名
     * @param map 请求的数据
     * @param cities 已存储的城市名集合
     */
    private void reviseCityNameAvail(String cityName, Map<String, Object> map, Set<String> cities) {
        // 获取城市名
        String city = (String) map.get("city");
        // 判断从接口获取的城市名和输入的城市名是否一致
        if(!cityName.equals(city)){
            // 添加现有的城市
            cities.add(city);
            // 删除输入的城市
            cities.remove(cityName);
        }
    }

    /**
     * 添加城市信息到 List 集合中
     *
     * @param maps 被添加的 List 集合
     * @param map 需要添加的 Map 集合
     * @param index 位置
     */
    private void addCityToList(List<Map<String, Object>> maps, Map<String, Object> map, int index) {
        if(map != null) {
            // 获取城市名
            String city = (String) map.get("city");
            // 获取图片
            String img = (String) map.get("img");
            // 获取温度
            String temp = (String) map.get("temp");
            // 获取天气
            String weather = (String) map.get("weather");
            // 添加到列表中
            Map<String, Object> map1 = new HashMap<>();
            map1.put("city", city);
            map1.put("img", img);
            map1.put("temp", temp);
            map1.put("weather", weather);
            maps.add(index, map1);
        } else {
            maps.add(index, null);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 获取城市数据
        getCityData();
    }
}
