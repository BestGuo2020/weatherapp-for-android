package top.bestguo.weatherapp.network;

import java.io.IOException;

/**
 * 发送天气请求
 *
 * Created by He Guo on 2021/3/1.
 */

public class RequestWeather {

    private final String url = "http://jisutqybmf.market.alicloudapi.com/weather/query";
    private final HttpClient httpClient = HttpClient.newInstance();
    private String appCode;

    /**
     * 设置app code，不要将 APPCODE 带进来
     * @param appCode app code
     */
    public void setAppCode(String appCode) {
        this.appCode = "APPCODE " + appCode;
    }

    /**
     * 发送请求
     *
     * @param field 请求字段
     * @param value 请求值
     * @return 返回数据
     * @throws IOException 可能会导致IO异常
     */
    public String sendRequest(String field, String value) throws IOException {
        httpClient.setUrl(url);
        httpClient.setHeader("Authorization", appCode);
        httpClient.setHeader("Content-Type", "application/json; charset=utf-8");
        httpClient.queryByCity(field, value);

        return httpClient.getResponseData();
    }

}
