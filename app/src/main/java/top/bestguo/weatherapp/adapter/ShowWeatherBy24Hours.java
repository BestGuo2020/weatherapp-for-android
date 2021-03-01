package top.bestguo.weatherapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import top.bestguo.weatherapp.R;

/**
 * Created by He Guo on 2021/2/24.
 */

public class ShowWeatherBy24Hours extends RecyclerView.Adapter<ShowWeatherBy24Hours.ViewHolderBy24Hours> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Map<String, Object>> data;

    public ShowWeatherBy24Hours(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public ShowWeatherBy24Hours(Context context, List<Map<String, Object>> data) {
        this(context);
        this.data = data;
    }

    @Override
    public ViewHolderBy24Hours onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBy24Hours(layoutInflater.inflate(R.layout.weather_hours_24, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolderBy24Hours holder, int position) {
        // {"time":"20:00","weather":"阴","temp":"16","img":"2"}
        if(data != null){
            Map curData = data.get(position);
            holder.curTime.setText((String)curData.get("time"));
            Glide.with(context).load("file:///android_asset/weathercn02/" + curData.get("img") + ".png").into(holder.curWeather);
            holder.curTemp.setText(curData.get("temp") + "℃");
        }
//        holder.curTime.setText("16:00");
//        Glide.with(context).load("file:///android_asset/weathercn02/0.png").into(holder.curWeather);
//        holder.curTemp.setText("26℃");
    }

    /**
     * 列表数量
     * @return 列表中的元素个数
     */
    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolderBy24Hours extends RecyclerView.ViewHolder {

        private TextView curTime, curTemp;
        private ImageView curWeather;

        ViewHolderBy24Hours(View itemView) {
            super(itemView);
            curTime = itemView.findViewById(R.id.cur_time);
            curTemp = itemView.findViewById(R.id.cur_temp);
            curWeather = itemView.findViewById(R.id.cur_weather_icon);
        }
    }
}
