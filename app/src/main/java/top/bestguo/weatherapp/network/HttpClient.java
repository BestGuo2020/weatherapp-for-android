package top.bestguo.weatherapp.network;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by He Guo on 2021/2/25.
 */

public class HttpClient {

    private static OkHttpClient httpClient = new OkHttpClient();
    private HttpUrl.Builder httpUrl;
    private Request.Builder builder;

    private HttpClient() {
        builder = new Request.Builder();
    }

    public static HttpClient newInstance() {
        return new HttpClient();
    }

    // 解析 url
    public void setUrl(String url){
        this.httpUrl = HttpUrl.parse(url).newBuilder();
    }

    // 查询参数
    public void queryByCity(String key, String value) {
        httpUrl.addQueryParameter(key, value);
    }

    // 添加请求头
    public void setHeader(String key, String value) {
        builder.addHeader(key, value);
    }

    // 返回数据
    public String getResponseData() throws IOException {
        /*request.url("http://jisutqybmf.market" +
                ".alicloudapi.com/weather/query?city=%E5%AE%89%E9%A1%BA")
                .addHeader("Authorization", "APPCODE 1303692fd97f4e848b1334924fc7af98")
                .addHeader("Content-Type", "application/json; charset=utf-8").build();*/

        Request request = builder.url(httpUrl.toString()).build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();

        return response.body().string();
    }

}
