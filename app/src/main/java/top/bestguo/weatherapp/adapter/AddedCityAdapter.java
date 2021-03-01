package top.bestguo.weatherapp.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import top.bestguo.weatherapp.R;

/**
 * Created by He Guo on 2021/2/28.
 */

public class AddedCityAdapter extends RecyclerView.Adapter<AddedCityAdapter.AddedCityViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Map<String, Object>> data;
    // 短按事件
    private ItemOnClickListener itemOnClickListener;
    // 长按事件
    private ItemOnLongClickListener itemOnLongClickListener;

    public AddedCityAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public AddedCityAdapter(Context context, List<Map<String, Object>> data) {
        this(context);
        this.data = data;
    }

    @Override
    public AddedCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(data != null)
            return new AddedCityViewHolder(inflater.inflate(R.layout.added_city, parent, false));
        else
            return new AddedCityViewHolder(inflater.inflate(R.layout.added_city_loading, parent, false));
    }

    // 设置短按事件
    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    // 设置长按事件
    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    @Override
    public void onBindViewHolder(AddedCityViewHolder holder, int position) {
        if(data != null) {
            Map<String, Object> map = data.get(position);
            Log.d("itemData", map + "");
            if(map != null) {
                // 设置名字
                holder.cityName.setText((String) map.get("city"));
                // 设置图标
                Glide.with(context).load("file:///android_asset/weathercn02/" + map.get("img") + ".png").into(holder.weatherIcon);
                // 设置温度
                holder.temperature.setText((String) map.get("temp"));
                // 设置天气
                holder.weatherText.setText((String) map.get("weather"));
                if(itemOnClickListener != null) {
                    // 设置短按点击事件
                    holder.cityItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemOnClickListener.onClick(v);
                        }
                    });
                }
                if(itemOnLongClickListener != null){
                    // 设置长按点击事件
                    holder.cityItem.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return itemOnLongClickListener.onLongClick(v);
                        }
                    });
                }
            } else {
                // 永远不显示
                holder.cityItem.setVisibility(View.GONE);
            }
        }
        // 判断是否到达最后一个
        if(position == getItemCount() - 1) {
            holder.sep.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class AddedCityViewHolder extends RecyclerView.ViewHolder {

        private TextView cityName, temperature, weatherText;
        private ImageView weatherIcon;
        private View sep;
        private LinearLayout cityItem;

        AddedCityViewHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_name);
            temperature = itemView.findViewById(R.id.temperature);
            weatherText = itemView.findViewById(R.id.weather_text);
            weatherIcon = itemView.findViewById(R.id.weather_icon);
            sep = itemView.findViewById(R.id.sep);
            cityItem = itemView.findViewById(R.id.city_item);
        }
    }

    /**
     * 短按接口类
     */
    public interface ItemOnClickListener {
        void onClick(View view);
    }

    /**
     * 长按接口类
     */
    public interface ItemOnLongClickListener {
        boolean onLongClick(View view);
    }
}
