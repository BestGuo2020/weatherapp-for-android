package top.bestguo.weatherapp.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import top.bestguo.weatherapp.R;

/**
 * 获取7天的天气展示
 *
 * Created by He Guo on 2021/2/7.
 */

public class ShowWeatherBy7DaysAdapter extends RecyclerView.Adapter<ShowWeatherBy7DaysAdapter.ViewHolderByDays> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Map<String, Object>> daily;

    public ShowWeatherBy7DaysAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public ShowWeatherBy7DaysAdapter(Context context, List<Map<String, Object>> daily) {
        this(context);
        this.daily = daily;
    }

    @Override
    public ViewHolderByDays onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderByDays(layoutInflater.inflate(R.layout.weather_days_7, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolderByDays holder, int position) {
        if(daily != null) {
            // 获取当前项的天气数据
            Map curData = daily.get(position);
            // 获取最高气温相关数据
            Map highData = (Map) curData.get("day");
            String tempHigh = (String) highData.get("temphigh");
            // 获取最低气温相关数据
            Map lowData = (Map) curData.get("night");
            String tempLow = (String) lowData.get("templow");
            holder.days.setText((String) curData.get("date"));
            holder.tempMaxAndMin.setText(tempHigh + "℃ / " + tempLow + "℃");
            Glide.with(context).load("file:///android_asset/weathercn02/" + highData.get("img") + ".png").into(holder.weatherIcon);
        }
//        holder.days.setText("2月8日明天");
//        holder.tempMaxAndMin.setText("20℃ / 11℃");
//        Glide.with(context).load("file:///android_asset/weathercn02/0.png").into(holder.weatherIcon);
    }

    @Override
    public int getItemCount() {
        return daily == null ? 0 : daily.size();
    }

    static class ViewHolderByDays extends RecyclerView.ViewHolder {
        TextView days, tempMaxAndMin;
        ImageView weatherIcon;

        ViewHolderByDays(View itemView) {
            super(itemView);
            days = itemView.findViewById(R.id.date_time);
            weatherIcon = itemView.findViewById(R.id.weather_icon);
            tempMaxAndMin = itemView.findViewById(R.id.temp_max_min);
        }
    }
}
