package top.bestguo.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddCityActivity extends AppCompatActivity {

    private GridView recommend_city;
    private Button buttonSearch;
    private EditText editText;
    private Toolbar toolbarAddCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        // 获取布局
        recommend_city =  findViewById(R.id.recommend_city);
        editText = findViewById(R.id.search_city);
        buttonSearch = findViewById(R.id.button_search);

        // 点击事件
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 添加城市
                addCity(editText.getText().toString());
            }
        });

        // 后退到上一级
        toolbarAddCity = findViewById(R.id.toolbar_add_city);
        toolbarAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 布局网格
        // 推荐城市设置
        final String[] cities = {"北京", "上海", "广州", "深圳",
                "天津", "武汉", "沈阳", "重庆", "杭州", "南京", "哈尔滨",
                "长春", "呼和浩特", "石家庄", "银川", "乌鲁木齐", "拉萨", "西宁", "西安", "兰州",
                "太原", "昆明", "南宁", "成都", "长沙", "济南", "南昌", "合肥", "郑州", "福州",
                "贵阳", "海口", "秦皇岛", "桂林", "三亚", "香港", "澳门"
        };
        recommend_city.setAdapter(new CityRecommendAdapter(this, cities));
        // 网格布局中的点击设置
        recommend_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView item = view.findViewById(R.id.city_name);
//                Toast.makeText(AddCityActivity.this, item.getText(), Toast.LENGTH_SHORT).show();
                String city = item.getText().toString();
                addCity(city);
            }
        });

        // 获取 toolbar
        toolbarAddCity = findViewById(R.id.toolbar_add_city);
        toolbarAddCity.setTitle("添加城市");
        toolbarAddCity.setNavigationIcon(R.drawable.touch_return);
        toolbarAddCity.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbarAddCity);
        getSupportActionBar();

    }

    // 添加城市
    private void addCity(String cityName){
        if(cityName != null) {
            if(cityName.length() > 0) {
                // 获取 cities 数据
                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                Set<String> cities = sharedPreferences.getStringSet("cities", null);
                // 编辑
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(cities != null){
                    if(cities.contains(cityName)){
                        Toast.makeText(AddCityActivity.this, "该城市已经添加", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        cities.add(cityName);
                    }
                } else {
                    cities = new HashSet<>();
                    cities.add(cityName);
                }
                editor.putStringSet("cities", cities);
                // 提交
                editor.apply();
                Toast.makeText(AddCityActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddCityActivity.this, "请输入城市名称", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddCityActivity.this, "请输入城市名称", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * 将城市信息展示到网格布局中
     */
    private class CityRecommendAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private String[] cities;

        CityRecommendAdapter(AddCityActivity addCityActivity, String[] cities) {
            context = addCityActivity;
            inflater = LayoutInflater.from(context);
            this.cities = cities;
        }

        @Override
        public int getCount() {
            return cities.length;
        }

        @Override
        public Object getItem(int position) {
            return cities[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView city;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.recommend_city, null);
                // 找到组件
                city = convertView.findViewById(R.id.city_name);
                // 放入其对应的位置
                convertView.setTag(city);
            } else {
                city = (TextView) convertView.getTag();
            }
            // 设置城市名
            city.setText(cities[position]);
            return convertView;
        }
    }
}
