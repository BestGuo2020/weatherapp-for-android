package top.bestguo.weatherapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import top.bestguo.weatherapp.R;

/**
 * Created by He Guo on 2021/2/25.
 *
 *
 */
public class TodayIndexAdapter extends RecyclerView.Adapter<TodayIndexViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Map<String, Object>> data;

    public TodayIndexAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public TodayIndexAdapter(Context context, List<Map<String, Object>> data) {
        this(context);
        this.data = data;
    }

    @Override
    public TodayIndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TodayIndexViewHolder(inflater.inflate(R.layout.today_index, parent, false));
    }

    @Override
    public void onBindViewHolder(TodayIndexViewHolder holder, int position) {
        // 获取当前数据
        if(data != null) {
            Map<String, Object> curData = data.get(position);
            holder.getiDetail().setText((String) curData.get("detail"));
            // 指数类型和指数值
            String name = (String) curData.get("iname");
            String value = (String) curData.get("ivalue");
            holder.getiNameValue().setText(name + "：" + value);
        }
        // 判断是否到达最后一个
        if(position == getItemCount() - 1) {
            holder.getiSep().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}

class TodayIndexViewHolder extends RecyclerView.ViewHolder {

    private TextView iNameValue, iDetail;
    private View iSep;

    TodayIndexViewHolder(View itemView) {
        super(itemView);
        iNameValue = itemView.findViewById(R.id.i_name_value);
        iDetail = itemView.findViewById(R.id.i_detail);
        iSep = itemView.findViewById(R.id.i_sep);
    }

    TextView getiNameValue() {
        return iNameValue;
    }

    TextView getiDetail() {
        return iDetail;
    }

    View getiSep() {
        return iSep;
    }
}
